import { createClient } from "https://esm.sh/@supabase/supabase-js@2.49.1";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
};

function ttsFallback(reason: string, detail?: string) {
  return new Response(
    JSON.stringify({ url: null, cached: false, fallback: "tts", reason, detail }),
    { status: 200, headers: { ...corsHeaders, "Content-Type": "application/json" } },
  );
}

async function resolveVoiceId(apiKey: string): Promise<string | null> {
  const fromEnv = Deno.env.get("ELEVENLABS_VOICE_ID")?.trim();
  if (fromEnv) return fromEnv;

  const res = await fetch("https://api.elevenlabs.io/v1/voices", {
    headers: { "xi-api-key": apiKey },
  });
  if (!res.ok) return null;

  const body = await res.json();
  const voices = body.voices as Array<{ voice_id: string; category?: string }> | undefined;
  if (!voices?.length) return null;

  // На Free нельзя library-голоса через API — берём первый из «своих» (не premade library).
  const own = voices.find((v) => v.category !== "premade") ?? voices[0];
  return own?.voice_id ?? null;
}

Deno.serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const { lessonId, beatId, text } = await req.json();
    if (!lessonId || !beatId || !text) {
      return new Response(JSON.stringify({ error: "lessonId, beatId, text required" }), {
        status: 400,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    const supabaseUrl = Deno.env.get("SUPABASE_URL")!;
    const serviceKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!;
    const elevenKey = Deno.env.get("ELEVENLABS_API_KEY");

    const supabase = createClient(supabaseUrl, serviceKey);
    const path = `${lessonId}/${beatId}.mp3`;

    const { data: cachedFile, error: downloadErr } = await supabase.storage
      .from("falstaff-audio")
      .download(path);

    if (!downloadErr && cachedFile) {
      const { data: existing } = supabase.storage.from("falstaff-audio").getPublicUrl(path);
      return new Response(JSON.stringify({ url: existing.publicUrl, cached: true }), {
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    if (!elevenKey) {
      return ttsFallback("no_api_key");
    }

    const voiceId = await resolveVoiceId(elevenKey);
    if (!voiceId) {
      return ttsFallback(
        "no_voice_id",
        "Задайте ELEVENLABS_VOICE_ID или добавьте голос в кабинете ElevenLabs (Voices).",
      );
    }

    const modelId = Deno.env.get("ELEVENLABS_MODEL_ID") ?? "eleven_flash_v2_5";

    const ttsRes = await fetch(`https://api.elevenlabs.io/v1/text-to-speech/${voiceId}`, {
      method: "POST",
      headers: {
        "xi-api-key": elevenKey,
        "Content-Type": "application/json",
        Accept: "audio/mpeg",
      },
      body: JSON.stringify({
        text,
        model_id: modelId,
        voice_settings: { stability: 0.45, similarity_boost: 0.75 },
      }),
    });

    if (!ttsRes.ok) {
      const errText = await ttsRes.text();
      let reason = "elevenlabs_error";
      if (errText.includes("paid_plan_required") || errText.includes("payment_required")) {
        reason = "paid_plan_required";
      }
      return ttsFallback(
        reason,
        "На Free нельзя premade-голоса. Укажите ELEVENLABS_VOICE_ID своего голоса из Voices → My Voices.",
      );
    }

    const audioBytes = new Uint8Array(await ttsRes.arrayBuffer());
    const { error: uploadError } = await supabase.storage
      .from("falstaff-audio")
      .upload(path, audioBytes, { contentType: "audio/mpeg", upsert: true });

    if (uploadError) {
      return new Response(JSON.stringify({ error: uploadError.message, fallback: "tts" }), {
        status: 500,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    const { data: uploaded } = supabase.storage.from("falstaff-audio").getPublicUrl(path);
    return new Response(JSON.stringify({ url: uploaded.publicUrl, cached: false }), {
      headers: { ...corsHeaders, "Content-Type": "application/json" },
    });
  } catch (e) {
    return new Response(JSON.stringify({ error: String(e), fallback: "tts" }), {
      status: 500,
      headers: { ...corsHeaders, "Content-Type": "application/json" },
    });
  }
});
