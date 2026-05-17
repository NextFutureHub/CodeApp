import { useEffect, useState } from "react";
import { supabase, type CourseRow } from "../lib/supabase";

export default function CoursesPage() {
  const [courses, setCourses] = useState<CourseRow[]>([]);
  const [loading, setLoading] = useState(true);

  async function load() {
    setLoading(true);
    const { data } = await supabase
      .from("courses")
      .select("*")
      .order("sort_order");
    setCourses((data as CourseRow[]) ?? []);
    setLoading(false);
  }

  useEffect(() => {
    load();
  }, []);

  async function togglePublish(course: CourseRow) {
    await supabase
      .from("courses")
      .update({ is_published: !course.is_published })
      .eq("id", course.id);
    load();
  }

  return (
    <>
      <h2>Курсы</h2>
      <p style={{ color: "#a8b5c9" }}>
        Публикация управляет видимостью для учеников (RLS: is_published).
      </p>
      {loading ? (
        <p>Загрузка…</p>
      ) : (
        <div className="card">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Название</th>
                <th>Порядок</th>
                <th>Статус</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {courses.map((c) => (
                <tr key={c.id}>
                  <td>
                    {c.icon} {c.id}
                  </td>
                  <td>{c.title}</td>
                  <td>{c.sort_order}</td>
                  <td>
                    {c.is_published ? (
                      <span className="badge">Опубликован</span>
                    ) : (
                      <span className="badge">Скрыт</span>
                    )}
                  </td>
                  <td>
                    <button type="button" className="primary" onClick={() => togglePublish(c)}>
                      {c.is_published ? "Скрыть" : "Опубликовать"}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  );
}
