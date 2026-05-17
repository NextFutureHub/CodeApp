-- Всегда student при регистрации; admin только вручную в Dashboard/SQL
create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
    insert into public.profiles (id, display_name, role)
    values (
        new.id,
        coalesce(new.raw_user_meta_data ->> 'display_name', 'Ученик'),
        'student'
    );

    insert into public.user_progress (user_id)
    values (new.id);

    return new;
end;
$$;
