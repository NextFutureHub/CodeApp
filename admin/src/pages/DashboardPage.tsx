import { useEffect, useState } from "react";
import { supabase } from "../lib/supabase";

export default function DashboardPage() {
  const [stats, setStats] = useState({ users: 0, courses: 0, lessons: 0 });

  useEffect(() => {
    Promise.all([
      supabase.from("profiles").select("id", { count: "exact", head: true }),
      supabase.from("courses").select("id", { count: "exact", head: true }),
      supabase.from("lessons").select("id", { count: "exact", head: true }),
    ]).then(([u, c, l]) => {
      setStats({
        users: u.count ?? 0,
        courses: c.count ?? 0,
        lessons: l.count ?? 0,
      });
    });
  }, []);

  return (
    <>
      <h2>Дашборд</h2>
      <div className="stats">
        <div className="stat">
          <strong>{stats.users}</strong>
          Пользователей
        </div>
        <div className="stat">
          <strong>{stats.courses}</strong>
          Курсов
        </div>
        <div className="stat">
          <strong>{stats.lessons}</strong>
          Уроков
        </div>
      </div>
      <div className="card" style={{ marginTop: 24 }}>
        <h3>Realtime</h3>
        <p style={{ color: "#a8b5c9" }}>
          Таблица <code>user_progress</code> подключена к Supabase Realtime — прогресс
          синхронизируется между устройствами ученика.
        </p>
      </div>
    </>
  );
}
