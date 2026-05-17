-- Row Level Security for CodeLingo

alter table public.profiles enable row level security;
alter table public.user_progress enable row level security;
alter table public.courses enable row level security;
alter table public.course_levels enable row level security;
alter table public.lessons enable row level security;

-- ---------------------------------------------------------------------------
-- profiles
-- ---------------------------------------------------------------------------
create policy "profiles_select_own_or_admin"
    on public.profiles
    for select
    to authenticated
    using (id = auth.uid() or public.is_admin());

create policy "profiles_update_own_or_admin"
    on public.profiles
    for update
    to authenticated
    using (id = auth.uid() or public.is_admin())
    with check (
        (id = auth.uid() and role = (select p.role from public.profiles p where p.id = auth.uid()))
        or public.is_admin()
    );

-- Students cannot promote themselves to admin
create policy "profiles_insert_self"
    on public.profiles
    for insert
    to authenticated
    with check (id = auth.uid() and role = 'student');

-- ---------------------------------------------------------------------------
-- user_progress
-- ---------------------------------------------------------------------------
create policy "progress_select_own_or_admin"
    on public.user_progress
    for select
    to authenticated
    using (user_id = auth.uid() or public.is_admin());

create policy "progress_insert_own"
    on public.user_progress
    for insert
    to authenticated
    with check (user_id = auth.uid());

create policy "progress_update_own"
    on public.user_progress
    for update
    to authenticated
    using (user_id = auth.uid())
    with check (user_id = auth.uid());

-- ---------------------------------------------------------------------------
-- courses (read published for all authenticated; full CRUD for admin)
-- ---------------------------------------------------------------------------
create policy "courses_select_published"
    on public.courses
    for select
    to authenticated
    using (is_published = true or public.is_admin());

create policy "courses_admin_insert"
    on public.courses
    for insert
    to authenticated
    with check (public.is_admin());

create policy "courses_admin_update"
    on public.courses
    for update
    to authenticated
    using (public.is_admin())
    with check (public.is_admin());

create policy "courses_admin_delete"
    on public.courses
    for delete
    to authenticated
    using (public.is_admin());

-- ---------------------------------------------------------------------------
-- course_levels
-- ---------------------------------------------------------------------------
create policy "levels_select_published_course"
    on public.course_levels
    for select
    to authenticated
    using (
        exists (
            select 1 from public.courses c
            where c.id = course_id
              and (c.is_published = true or public.is_admin())
        )
    );

create policy "levels_admin_insert"
    on public.course_levels
    for insert
    to authenticated
    with check (public.is_admin());

create policy "levels_admin_update"
    on public.course_levels
    for update
    to authenticated
    using (public.is_admin())
    with check (public.is_admin());

create policy "levels_admin_delete"
    on public.course_levels
    for delete
    to authenticated
    using (public.is_admin());

-- ---------------------------------------------------------------------------
-- lessons
-- ---------------------------------------------------------------------------
create policy "lessons_select_published"
    on public.lessons
    for select
    to authenticated
    using (
        exists (
            select 1
            from public.course_levels cl
            join public.courses c on c.id = cl.course_id
            where cl.id = level_id
              and (c.is_published = true or public.is_admin())
        )
    );

create policy "lessons_admin_insert"
    on public.lessons
    for insert
    to authenticated
    with check (public.is_admin());

create policy "lessons_admin_update"
    on public.lessons
    for update
    to authenticated
    using (public.is_admin())
    with check (public.is_admin());

create policy "lessons_admin_delete"
    on public.lessons
    for delete
    to authenticated
    using (public.is_admin());
