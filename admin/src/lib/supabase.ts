import { createClient } from "@supabase/supabase-js";

const url = import.meta.env.VITE_SUPABASE_URL;
const key = import.meta.env.VITE_SUPABASE_ANON_KEY;

if (!url || !key) {
  console.warn("VITE_SUPABASE_URL / VITE_SUPABASE_ANON_KEY not set");
}

export const supabase = createClient(url ?? "", key ?? "");

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
