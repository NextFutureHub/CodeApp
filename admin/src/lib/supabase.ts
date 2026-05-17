import { createClient } from "@supabase/supabase-js";

const url =
  import.meta.env.VITE_SUPABASE_URL ?? import.meta.env.SUPABASE_URL ?? "";
const key =
  import.meta.env.VITE_SUPABASE_ANON_KEY ??
  import.meta.env.SUPABASE_ANON_KEY ??
  "";

if (!url || !key) {
  throw new Error(
    "Задайте SUPABASE_URL и SUPABASE_ANON_KEY в CodeApp/.env (или VITE_SUPABASE_* в admin/.env), затем перезапустите npm run dev",
  );
}

export const supabase = createClient(url, key);

export type Profile = {
  id: string;
  display_name: string;
  role: "student" | "admin";
  avatar_url: string | null;
  created_at: string;
};

export type CourseRow = {
  id: string;
  title: string;
  icon: string;
  color: string;
  sort_order: number;
  is_published: boolean;
};

export type UserProgressRow = {
  user_id: string;
  xp: number;
  level: number;
  streak: number;
  lives: number;
  completed_lessons: string[];
  achievements: string[];
  updated_at: string;
};
