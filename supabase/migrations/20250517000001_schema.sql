-- CodeLingo: profiles, progress, course catalog

create extension if not exists "pgcrypto";

-- ---------------------------------------------------------------------------
-- Profiles (extends auth.users)
-- ---------------------------------------------------------------------------
create table public.profiles (
    id uuid primary key references auth.users (id) on delete cascade,
    display_name text not null default 'Ученик',
    avatar_url text,
    role text not null default 'student' check (role in ('student', 'admin')),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index profiles_role_idx on public.profiles (role);

-- ---------------------------------------------------------------------------
-- User progress (game state)
-- ---------------------------------------------------------------------------
create table public.user_progress (
    user_id uuid primary key references public.profiles (id) on delete cascade,
    xp integer not null default 0 check (xp >= 0),
    level integer not null default 1 check (level >= 1),
    streak integer not null default 0 check (streak >= 0),
    lives integer not null default 5 check (lives >= 0 and lives <= 10),
    max_lives integer not null default 5 check (max_lives >= 1 and max_lives <= 10),
    last_active_date date,
    completed_lessons text[] not null default '{}',
    achievements text[] not null default '{}',
    updated_at timestamptz not null default now()
);

-- ---------------------------------------------------------------------------
-- Course catalog (managed via admin)
-- ---------------------------------------------------------------------------
create table public.courses (
    id text primary key,
    title text not null,
    icon text not null default '📚',
    color text not null default '232 78% 55%',
    sort_order integer not null default 0,
    is_published boolean not null default true,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table public.course_levels (
    id text primary key,
    course_id text not null references public.courses (id) on delete cascade,
    title text not null,
    sort_order integer not null default 0
);

create index course_levels_course_id_idx on public.course_levels (course_id);

create table public.lessons (
    id text primary key,
    level_id text not null references public.course_levels (id) on delete cascade,
    title text not null,
    description text not null default '',
    xp_reward integer not null default 10 check (xp_reward > 0),
    theory text,
    tasks jsonb not null default '[]'::jsonb,
    sort_order integer not null default 0
);

create index lessons_level_id_idx on public.lessons (level_id);

-- ---------------------------------------------------------------------------
-- Helpers
-- ---------------------------------------------------------------------------
create or replace function public.set_updated_at()
returns trigger
language plpgsql
as $$
begin
    new.updated_at = now();
    return new;
end;
$$;

create trigger profiles_updated_at
    before update on public.profiles
    for each row execute function public.set_updated_at();

create trigger courses_updated_at
    before update on public.courses
    for each row execute function public.set_updated_at();

create trigger user_progress_updated_at
    before update on public.user_progress
    for each row execute function public.set_updated_at();

-- Admin check (security definer — used in RLS)
create or replace function public.is_admin()
returns boolean
language sql
stable
security definer
set search_path = public
as $$
    select exists (
        select 1
        from public.profiles
        where id = auth.uid()
          and role = 'admin'
    );
$$;

grant execute on function public.is_admin() to authenticated, anon;

-- New user bootstrap
create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
    insert into public.profiles (id, display_name)
    values (
        new.id,
        coalesce(new.raw_user_meta_data ->> 'display_name', 'Ученик')
    );

    insert into public.user_progress (user_id)
    values (new.id);

    return new;
end;
$$;

create trigger on_auth_user_created
    after insert on auth.users
    for each row execute function public.handle_new_user();

-- Published courses as nested JSON (matches Android Course model)
create or replace function public.get_published_courses()
returns jsonb
language sql
stable
security invoker
set search_path = public
as $$
    select coalesce(
        jsonb_agg(
            jsonb_build_object(
                'id', c.id,
                'title', c.title,
                'icon', c.icon,
                'color', c.color,
                'levels', (
                    select coalesce(
                        jsonb_agg(
                            jsonb_build_object(
                                'id', cl.id,
                                'title', cl.title,
                                'lessons', (
                                    select coalesce(
                                        jsonb_agg(
                                            jsonb_build_object(
                                                'id', l.id,
                                                'title', l.title,
                                                'description', l.description,
                                                'xpReward', l.xp_reward,
                                                'theory', l.theory,
                                                'tasks', l.tasks
                                            )
                                            order by l.sort_order
                                        ),
                                        '[]'::jsonb
                                    )
                                    from public.lessons l
                                    where l.level_id = cl.id
                                )
                            )
                            order by cl.sort_order
                        ),
                        '[]'::jsonb
                    )
                    from public.course_levels cl
                    where cl.course_id = c.id
                )
            )
            order by c.sort_order
        ),
        '[]'::jsonb
    )
    from public.courses c
    where c.is_published = true;
$$;

grant execute on function public.get_published_courses() to authenticated, anon;
