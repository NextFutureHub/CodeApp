import { readFileSync, writeFileSync } from "node:fs";
import { join, dirname } from "node:path";
import { fileURLToPath } from "node:url";

const root = join(dirname(fileURLToPath(import.meta.url)), "..");
const coursesPath = join(root, "app/src/main/assets/courses.json");
const courses = JSON.parse(readFileSync(coursesPath, "utf8"));

function intro(lessonId, lines) {
  return lines.map((text, i) => ({
    id: `${lessonId}-intro-${i + 1}`,
    speaker: "falstaff",
    text,
    emotion: i === 0 ? "happy" : i === lines.length - 1 ? "think" : "neutral",
  }));
}

function outro(lessonId, lines) {
  return lines.map((text, i) => ({
    id: `${lessonId}-outro-${i + 1}`,
    speaker: "falstaff",
    text,
    emotion: i === lines.length - 1 ? "celebrate" : "happy",
    ...(i === lines.length - 1
      ? {
          choices: [
            { text: "Спасибо, Фальстаф!", response: "Всегда пожалуйста. До следующего урока!" },
            { text: "Было сложно!", response: "Сложности закаляют кодера. Ты справился!" },
          ],
        }
      : {}),
  }));
}

function miniScene(lessonId, title, subtitle, hotspots) {
  return {
    title,
    subtitle,
    background: "room",
    hotspots: hotspots.map((h, i) => ({
      id: `${lessonId}-spot-${i + 1}`,
      label: h.label,
      x: h.x,
      y: h.y,
      width: 0.16,
      height: 0.14,
      task: h.task,
      doneLabel: "Готово",
    })),
  };
}

const storyByLesson = {
  "html-1": {
    intro: ["Привет! Я Фальстаф, твой проводник в мир HTML.", "Сегодня узнаем, что такое теги и зачем они нужны."],
    outro: ["Отлично! Ты освоил основы HTML.", "Впереди ещё много приключений в коде!"],
    mini: {
      title: "Лаборатория Фальстафа",
      subtitle: "Нажми на объекты и закрепи урок",
      spots: [
        { label: "Тег", x: 0.22, y: 0.55, task: { id: "m1", type: "quiz", question: "HTML — это язык…", options: ["разметки", "программирования"], correctAnswer: "разметки" } },
        { label: "Книга", x: 0.5, y: 0.42, task: { id: "m2", type: "quiz", question: "Параграф создаёт тег…", options: ["<p>", "<div>"], correctAnswer: "<p>" } },
        { label: "Дверь", x: 0.78, y: 0.58, task: { id: "m3", type: "blocks", question: "Собери <p>Привет</p>", blocks: ["<p>", "Привет", "</p>"], correctOrder: ["<p>", "Привет", "</p>"] } },
      ],
    },
  },
};

for (const course of courses) {
  for (const level of course.levels) {
    for (const lesson of level.lessons) {
      const custom = storyByLesson[lesson.id];
      const topic = lesson.title;
      const firstTask = lesson.tasks[0];
      const introLines = custom?.intro ?? [
        `Я Фальстаф. Урок «${topic}» — поехали!`,
        lesson.description || "Сейчас потренируемся на практике.",
      ];
      const outroLines = custom?.outro ?? [
        `Урок «${topic}» пройден. Молодец!`,
        "Готов к следующему вызову?",
      ];
      lesson.storyIntro = intro(lesson.id, introLines);
      lesson.storyOutro = outro(lesson.id, outroLines);

      const spotTask =
        firstTask ??
        ({
          id: `${lesson.id}-mini-q`,
          type: "quiz",
          question: `Что запомнил из «${topic}»?`,
          options: ["Главное понял", "Нужно повторить"],
          correctAnswer: "Главное понял",
        });

      const spots = custom?.mini?.spots ?? [
        { label: "А", x: 0.25, y: 0.5, task: { ...spotTask, id: `${lesson.id}-ma` } },
        { label: "Б", x: 0.5, y: 0.45, task: { ...spotTask, id: `${lesson.id}-mb`, question: `Повтор: ${topic}?`, options: ["Да", "Нет"], correctAnswer: "Да" } },
        { label: "В", x: 0.75, y: 0.55, task: { ...spotTask, id: `${lesson.id}-mc`, type: "quiz", question: "Готов дальше?", options: ["Да!", "Ещё чуть"], correctAnswer: "Да!" } },
      ];

      lesson.miniScene = miniScene(
        lesson.id,
        custom?.mini?.title ?? `Мини-мир: ${topic}`,
        custom?.mini?.subtitle ?? "Исследуй точки на карте",
        spots,
      );
    }
  }
}

writeFileSync(coursesPath, JSON.stringify(courses, null, 2) + "\n", "utf8");
console.log("Patched", coursesPath);
