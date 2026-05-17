# CodeLingo — Supabase Backend

## Структура

| Компонент | Описание |
|-----------|----------|
| `migrations/` | Схема БД, RLS, Realtime |
| `seed/courses.sql` | Курсы из `courses.json` |
| `../admin/` | Веб-админка (курсы, пользователи) |

## Таблицы

- **profiles** — профиль пользователя (`student` / `admin`)
- **user_progress** — XP, уровень, streak, жизни, уроки, достижения
- **courses**, **course_levels**, **lessons** — каталог (задания в `tasks` JSONB)

## RLS

- Ученик видит и меняет **только свой** `user_progress` и `profiles`
- Ученик читает **опубликованные** курсы (`is_published = true`)
- **admin** (поле `profiles.role`) — полный CRUD курсов и просмотр всех пользователей

## Запуск локально

```bash
# Установите Supabase CLI: https://supabase.com/docs/guides/cli
cd CodeApp
supabase start
supabase db reset   # миграции + seed
```

Скопируйте URL и anon key в `gradle.properties` и `admin/.env`.

## Первый администратор

1. Зарегистрируйтесь в приложении или Studio.
2. В SQL Editor:

```sql
update public.profiles set role = 'admin' where id = 'ваш-uuid';
```

3. Войдите в админку: `cd admin && npm i && npm run dev`

## Realtime

Таблица `user_progress` в publication `supabase_realtime` — прогресс синхронизируется между устройствами.

## Android

В `gradle.properties`:

```properties
SUPABASE_URL=https://xxx.supabase.co
SUPABASE_ANON_KEY=eyJ...
```

Без ключей приложение работает **офлайн** (локальный JSON + DataStore).
