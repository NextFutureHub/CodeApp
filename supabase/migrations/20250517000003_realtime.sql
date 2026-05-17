-- Realtime: sync user_progress across devices

alter table public.user_progress replica identity full;

alter publication supabase_realtime add table public.user_progress;
