import { useEffect, useState } from "react";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { supabase, type Profile } from "./lib/supabase";
import LoginPage from "./pages/LoginPage";
import DashboardPage from "./pages/DashboardPage.tsx";
import CoursesPage from "./pages/CoursesPage.tsx";
import UsersPage from "./pages/UsersPage.tsx";
import Layout from "./components/Layout";

export default function App() {
  const [profile, setProfile] = useState<Profile | null>(null);
  const [loading, setLoading] = useState(true);
  const location = useLocation();

  useEffect(() => {
    supabase.auth.getSession().then(({ data }) => {
      if (data.session) loadProfile(data.session.user.id);
      else {
        setProfile(null);
        setLoading(false);
      }
    });

    const { data: sub } = supabase.auth.onAuthStateChange((_e, session) => {
      if (session) loadProfile(session.user.id);
      else {
        setProfile(null);
        setLoading(false);
      }
    });
    return () => sub.subscription.unsubscribe();
  }, []);

  async function loadProfile(userId: string) {
    const { data, error } = await supabase
      .from("profiles")
      .select("id, display_name, role, avatar_url, created_at")
      .eq("id", userId)
      .single();
    if (error || !data) {
      setProfile(null);
    } else {
      setProfile(data as Profile);
    }
    setLoading(false);
  }

  if (loading) {
    return (
      <div className="auth-page">
        <p>Загрузка…</p>
      </div>
    );
  }

  if (!profile) {
    return location.pathname === "/login" ? (
      <LoginPage />
    ) : (
      <Navigate to="/login" replace />
    );
  }

  if (profile.role !== "admin") {
    return (
      <div className="auth-page">
        <div className="card auth-form">
          <h2>Доступ запрещён</h2>
          <p>
            Для админки в таблице <code>profiles</code> нужна роль <strong>admin</strong> (не
            «Ученик»). Ученики при регистрации получают <code>student</code> автоматически.
          </p>
          <button type="button" className="primary" onClick={() => supabase.auth.signOut()}>
            Выйти
          </button>
        </div>
      </div>
    );
  }

  return (
    <Layout profile={profile}>
      <Routes>
        <Route path="/" element={<DashboardPage />} />
        <Route path="/courses" element={<CoursesPage />} />
        <Route path="/users" element={<UsersPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Layout>
  );
}
