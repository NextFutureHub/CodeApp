import { readFileSync, writeFileSync, mkdirSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const root = join(dirname(fileURLToPath(import.meta.url)), "..");
const courses = JSON.parse(
  readFileSync(join(root, "app/src/main/assets/courses.json"), "utf8"),
);

function esc(s) {
  if (s == null) return "null";
  return `'${String(s).replace(/'/g, "''")}'`;
}

const lines = ["-- Auto-generated from courses.json"];
courses.forEach((course, ci) => {
  lines.push(
    `INSERT INTO public.courses (id, title, icon, color, sort_order) VALUES (${esc(course.id)}, ${esc(course.title)}, ${esc(course.icon)}, ${esc(course.color)}, ${ci}) ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title, icon = EXCLUDED.icon, color = EXCLUDED.color, sort_order = EXCLUDED.sort_order;`,
  );
  course.levels.forEach((level, li) => {
    lines.push(
      `INSERT INTO public.course_levels (id, course_id, title, sort_order) VALUES (${esc(level.id)}, ${esc(course.id)}, ${esc(level.title)}, ${li}) ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title, sort_order = EXCLUDED.sort_order;`,
    );
    level.lessons.forEach((lesson, si) => {
      const tasks = JSON.stringify(lesson.tasks).replace(/'/g, "''");
      const storyIntro = lesson.storyIntro ? `'${JSON.stringify(lesson.storyIntro).replace(/'/g, "''")}'::jsonb` : "null";
      const storyOutro = lesson.storyOutro ? `'${JSON.stringify(lesson.storyOutro).replace(/'/g, "''")}'::jsonb` : "null";
      const miniScene = lesson.miniScene ? `'${JSON.stringify(lesson.miniScene).replace(/'/g, "''")}'::jsonb` : "null";
      lines.push(
        `INSERT INTO public.lessons (id, level_id, title, description, xp_reward, theory, tasks, story_intro, story_outro, mini_scene, sort_order) VALUES (${esc(lesson.id)}, ${esc(level.id)}, ${esc(lesson.title)}, ${esc(lesson.description)}, ${lesson.xpReward}, ${esc(lesson.theory)}, '${tasks}'::jsonb, ${storyIntro}, ${storyOutro}, ${miniScene}, ${si}) ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title, description = EXCLUDED.description, xp_reward = EXCLUDED.xp_reward, theory = EXCLUDED.theory, tasks = EXCLUDED.tasks, story_intro = EXCLUDED.story_intro, story_outro = EXCLUDED.story_outro, mini_scene = EXCLUDED.mini_scene, sort_order = EXCLUDED.sort_order;`,
      );
    });
  });
});

const outDir = join(root, "supabase/seed");
mkdirSync(outDir, { recursive: true });
const out = join(outDir, "courses.sql");
writeFileSync(out, lines.join("\n") + "\n", "utf8");
console.log(`Wrote ${lines.length} lines to ${out}`);
