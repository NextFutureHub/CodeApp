import { useEffect, useState } from "react";
import { supabase, type Profile, type UserProgressRow } from "../lib/supabase";

type Row = Profile & { progress?: UserProgressRow };

export default function UsersPage() {
  const [rows, setRows] = useState<Row[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function load() {
      const { data: profiles } = await supabase
        .from("profiles")
        .select("id, display_name, role, avatar_url, created_at")
        .order("created_at", { ascending: false });

      const { data: progress } = await supabase.from("user_progress").select("*");

      const byUser = new Map((progress as UserProgressRow[] | null)?.map((p) => [p.user_id, p]));
      setRows(
        ((profiles as Profile[]) ?? []).map((p) => ({
          ...p,
          progress: byUser.get(p.id),
        })),
      );
      setLoading(false);
    }
    load();
  }, []);

  return (
    <>
      <h2>Пользователи</h2>
      {loading ? (
        <p>Загрузка…</p>
      ) : (
        <div className="card">
          <table>
            <thead>
              <tr>
                <th>Имя</th>
                <th>Роль</th>
                <th>XP</th>
                <th>Уровень</th>
                <th>Streak</th>
                <th>Уроков</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((r) => (
                <tr key={r.id}>
                  <td>{r.display_name}</td>
                  <td>
                    <span className={`badge ${r.role}`}>{r.role}</span>
                  </td>
                  <td>{r.progress?.xp ?? 0}</td>
                  <td>{r.progress?.level ?? 1}</td>
                  <td>{r.progress?.streak ?? 0}</td>
                  <td>{r.progress?.completed_lessons?.length ?? 0}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  );
}
