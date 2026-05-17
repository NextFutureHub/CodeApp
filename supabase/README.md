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

## CLI на Windows

Глобальный `npm install -g supabase` **не поддерживается**. В проекте CLI уже в devDependencies:

```powershell
cd CodeApp
npm install          # один раз
npx supabase login
npx supabase link --project-ref wawqxhxgbxexpwimupxy
npx supabase db push
```

Или через npm-скрипты: `npm run login`, `npm run db:push`.

Альтернатива: [Scoop](https://scoop.sh) → `scoop install supabase`, или `winget install Supabase.CLI`.

## Запуск локально

```bash
cd CodeApp
npx supabase start
npx supabase db reset   # миграции + seed
```

Скопируйте URL и anon key в `gradle.properties` и `admin/.env`.

## Роли

| `profiles.role` | Кто |
|-----------------|-----|
| `student` | Ученик (ставится **автоматически** при регистрации) |
| `admin` | Доступ к веб-админке и управлению курсами |

Имя «Ученик» в приложении — это `display_name`, не роль. Роль в UI не выбирается.

## Первый администратор

1. Зарегистрируйтесь в приложении или Studio.
2. В SQL Editor:

```sql
update public.profiles set role = 'admin' where id = 'ваш-uuid';
```

3. Войдите в админку: `cd admin && npm i && npm run dev`

## Realtime

Таблица `user_progress` в publication `supabase_realtime` — прогресс синхронизируется между устройствами.

## Фальстаф: озвучка (ElevenLabs)

1. Создайте API-ключ на [elevenlabs.io](https://elevenlabs.io).
2. Задайте секреты в Supabase: **Project Settings → Edge Functions → Secrets**:
   - `ELEVENLABS_API_KEY`
   - `ELEVENLABS_VOICE_ID` (опционально, иначе дефолтный голос)
3. Деплой функции (с `verify_jwt = false` в `functions/falstaff-tts/config.toml`):
   `npx supabase functions deploy falstaff-tts`
4. Примените миграцию `20250517000006_lesson_story.sql` (`npx supabase db push`).

Без ключа ElevenLabs приложение использует **Android TextToSpeech** (fallback). На экране теории подсказка появится, если играет системный голос.

Проверка: в Logcat фильтр `FalstaffVoice` — ошибки HTTP или «ElevenLabs unavailable».

## Android

Для **Realtime** нужен Ktor-движок с WebSocket (`ktor-client-okhttp`), не `ktor-client-android`.

В `gradle.properties`:

```properties
SUPABASE_URL=https://xxx.supabase.co
SUPABASE_ANON_KEY=eyJ...
```

Без ключей приложение работает **офлайн** (локальный JSON + DataStore).
