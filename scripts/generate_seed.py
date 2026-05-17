import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
courses = json.loads((ROOT / "app/src/main/assets/courses.json").read_text(encoding="utf-8"))


def esc(s):
    if s is None:
        return "null"
    return "'" + str(s).replace("'", "''") + "'"


lines = ["-- Auto-generated from courses.json"]
for ci, course in enumerate(courses):
    lines.append(
        f"INSERT INTO public.courses (id, title, icon, color, sort_order) "
        f"VALUES ({esc(course['id'])}, {esc(course['title'])}, {esc(course['icon'])}, "
        f"{esc(course['color'])}, {ci}) "
        f"ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title, icon = EXCLUDED.icon, "
        f"color = EXCLUDED.color, sort_order = EXCLUDED.sort_order;"
    )
    for li, level in enumerate(course["levels"]):
        lines.append(
            f"INSERT INTO public.course_levels (id, course_id, title, sort_order) "
            f"VALUES ({esc(level['id'])}, {esc(course['id'])}, {esc(level['title'])}, {li}) "
            f"ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title, sort_order = EXCLUDED.sort_order;"
        )
        for si, lesson in enumerate(level["lessons"]):
            tasks = json.dumps(lesson["tasks"], ensure_ascii=False).replace("'", "''")
            theory = esc(lesson.get("theory"))
            lines.append(
                f"INSERT INTO public.lessons (id, level_id, title, description, xp_reward, theory, tasks, sort_order) "
                f"VALUES ({esc(lesson['id'])}, {esc(level['id'])}, {esc(lesson['title'])}, "
                f"{esc(lesson['description'])}, {lesson['xpReward']}, {theory}, '{tasks}'::jsonb, {si}) "
                f"ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title, description = EXCLUDED.description, "
                f"xp_reward = EXCLUDED.xp_reward, theory = EXCLUDED.theory, tasks = EXCLUDED.tasks, "
                f"sort_order = EXCLUDED.sort_order;"
            )

out = ROOT / "supabase" / "seed" / "courses.sql"
out.parent.mkdir(parents=True, exist_ok=True)
out.write_text("\n".join(lines) + "\n", encoding="utf-8")
print(f"Wrote {len(lines)} lines to {out}")
