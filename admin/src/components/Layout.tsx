import { NavLink } from "react-router-dom";
import { supabase, type Profile } from "../lib/supabase";

export default function Layout({
  profile,
  children,
}: {
  profile: Profile;
  children: React.ReactNode;
}) {
  return (
    <div className="layout">
      <aside className="sidebar">
        <h1>CodeLingo Admin</h1>
        <nav>
          <NavLink to="/" end>
            Дашборд
          </NavLink>
          <NavLink to="/courses">Курсы</NavLink>
          <NavLink to="/users">Пользователи</NavLink>
        </nav>
        <p style={{ marginTop: 32, fontSize: "0.85rem", color: "#6b7a90" }}>
          {profile.display_name}
        </p>
        <button type="button" className="link" onClick={() => supabase.auth.signOut()}>
          Выйти
        </button>
      </aside>
      <main className="main">{children}</main>
    </div>
  );
}
