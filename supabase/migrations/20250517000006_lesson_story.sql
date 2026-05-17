-- Story beats and mini-scenes on lessons

alter table public.lessons
    add column if not exists story_intro jsonb,
    add column if not exists story_outro jsonb,
    add column if not exists mini_scene jsonb;

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
                                                'tasks', l.tasks,
                                                'storyIntro', l.story_intro,
                                                'storyOutro', l.story_outro,
                                                'miniScene', l.mini_scene
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

-- Public read for cached TTS audio
insert into storage.buckets (id, name, public)
values ('falstaff-audio', 'falstaff-audio', true)
on conflict (id) do update set public = true;

create policy "falstaff_audio_public_read"
    on storage.objects for select
    to public
    using (bucket_id = 'falstaff-audio');

create policy "falstaff_audio_service_write"
    on storage.objects for insert
    to authenticated
    with check (bucket_id = 'falstaff-audio');
