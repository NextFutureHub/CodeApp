# 📚 CodeApp — Интерактивная платформа обучения программированию

**CodeApp** — это полнофункциональная платформа для интерактивного обучения программированию с поддержкой мобильного приложения, веб-админки и облачного бэкенда на Supabase.

---

## 🎯 О проекте

CodeApp позволяет ученикам:
- 📖 Изучать курсы программирования через интерактивные уроки
- 🎮 Получать XP, повышать уровень и отслеживать прогресс
- 🔥 Накапливать "стрик" (серия тренировок подряд)
- ⭐ Достигать достижений и использовать жизни
- 🎙️ Слушать озвучку персонажа Фальстафа (с голосом ElevenLabs)
- 🔄 Синхронизировать прогресс между устройствами в реальном времени

Администраторы могут:
- 📝 Управлять курсами и уроками
- 👥 Просматривать профили и прогресс пользователей
- 📊 Анализировать статистику обучения

---

## 🏗️ Архитектура проекта

```
CodeApp/
├── app/                      # 📱 Android приложение (Kotlin + Compose)
│   ├── src/main/kotlin/     # Исходный код на Kotlin
│   ├── src/main/res/        # Ресурсы (иконки, строки)
│   └── build.gradle.kts     # Конфигурация Gradle
│
├── admin/                    # 🌐 Веб-админка (React + TypeScript)
│   ├── src/
│   │   ├── pages/           # Страницы: Dashboard, Courses, Users
│   │   ├── components/      # Компоненты UI
│   │   └── lib/             # Утилиты и API
│   └── package.json
│
├── supabase/                 # 🗄️ Бэкенд (Supabase/PostgreSQL)
│   ├── migrations/          # Схема БД, RLS, Realtime
│   ├── functions/           # Edge Functions (озвучка)
│   ├── seed/                # Начальные данные
│   └── config.toml
│
├── scripts/                  # 🔧 Утилиты и скрипты
├── gradle/                   # Gradle обёртка
└── .env                      # Переменные окружения
```

### Языки и технологии:
- **Kotlin** (77.4%) — Android приложение с Jetpack Compose
- **JavaScript** (8.2%) — Скрипты и утилиты
- **TypeScript** (7.7%) — Веб-админка на React
- **PL/pgSQL** (4.6%) — Хранимые процедуры и RLS политики
- **CSS** (1%) — Стили веб-админки

---

## 📱 Android приложение (`/app`)

### Требования:
- **Android 10** (API 29) и выше
- **Android Studio** Ladybug или позже

### Технологический стек:
- **Jetpack Compose** — модерный UI фреймворк
- **Supabase SDK** — аутентификация и Real-time БД
- **Ktor Client** — HTTP запросы с WebSocket
- **DataStore** — локальное хранилище настроек
- **Kotlin Coroutines** — асинхронное программирование

### Запуск:

1. **Откройте проект в Android Studio:**
   ```bash
   # Или просто откройте папку в IDE
   open -a "Android Studio" .
   ```

2. **Настройте учетные данные Supabase:**
   
   Отредактируйте `gradle.properties`:
   ```properties
   SUPABASE_URL=https://xxx.supabase.co
   SUPABASE_ANON_KEY=eyJ...
   ```
   
   Или используйте локальный Supabase (см. ниже).

3. **Выберите эмулятор или устройство и нажмите Run** ▶️

### Архитектура приложения:

```kotlin
com.codelingo.app/
├── MainActivity              # Точка входа
├── ui/                       # Экраны и компоненты
│   ├── auth/                # Авторизация
│   ├── home/                # Главный экран
│   ├── courses/             # Список курсов
│   └── lesson/              # Экран урока
│
├── data/                    # Слой данных
│   ├── repository/          # Работа с API/БД
│   ├── model/               # Data классы
│   └── local/               # DataStore
│
└── viewmodel/               # ViewModel слой
```

### Важно: Offline режим

Если `SUPABASE_URL` и `SUPABASE_ANON_KEY` не установлены:
- Приложение работает **полностью офлайн**
- Данные берутся из локального JSON
- Real-time синхронизация недоступна

---

## 🌐 Веб-админка (`/admin`)

### Требования:
- **Node.js** 18+ и **npm** или **yarn**

### Технологический стек:
- **React 19** — UI фреймворк
- **TypeScript** — типизированный JavaScript
- **Vite** — быстрый сборщик
- **React Router** — навигация
- **Supabase JS SDK** — работа с БД

### Структура:
```
admin/src/
├── pages/
│   ├── LoginPage.tsx         # Вход (email + пароль)
│   ├── DashboardPage.tsx     # Статистика
│   ├── CoursesPage.tsx       # Управление курсами
│   └── UsersPage.tsx         # Список пользователей
│
├── components/
│   └── Layout.tsx            # Основная раскладка
│
├── lib/
│   └── supabase.ts           # Клиент Supabase
│
└── App.tsx                   # Маршруты и авторизация
```

### Запуск:

1. **Установите зависимости:**
   ```bash
   cd admin
   npm install
   ```

2. **Настройте переменные окружения** (`.env`):
   ```env
   SUPABASE_URL=https://xxx.supabase.co
   SUPABASE_ANON_KEY=eyJ...
   ```

3. **Запустите dev-сервер:**
   ```bash
   npm run dev
   ```
   
   Откроется на `http://localhost:5173`

4. **Сборка для production:**
   ```bash
   npm run build
   ```

### Роли доступа:

| Роль | Доступ |
|------|--------|
| **admin** | Полный доступ к админке |
| **student** | Только приложение (без админки) |

Роль назначается в БД таблице `profiles` (поле `role`).

---

## 🗄️ Бэкенд — Supabase (`/supabase`)

### Основные таблицы:

#### **profiles** — Профили пользователей
```sql
id              UUID PRIMARY KEY (Supabase Auth)
display_name    TEXT            -- Имя для отображения
role            TEXT            -- 'student' или 'admin'
avatar_url      TEXT            -- Ссылка на аватар
created_at      TIMESTAMP       -- Дата регистрации
```

#### **user_progress** — Прогресс ученика
```sql
id              UUID PRIMARY KEY
user_id         UUID FOREIGN KEY (profiles)
xp              INT             -- Опыт (XP)
level           INT             -- Уровень
streak          INT             -- Серия тренировок подряд
lives           INT             -- Количество жизней
lessons_done    INT[]           -- IDs завершённых уроков
achievements    JSONB           -- Полученные достижения
updated_at      TIMESTAMP
```

#### **courses** — Каталог курсов
```sql
id              UUID PRIMARY KEY
title           TEXT
description     TEXT
is_published    BOOLEAN         -- Видимость для учеников
order           INT
created_at      TIMESTAMP
```

#### **course_levels** — Уровни курса (1, 2, 3...)
```sql
id              UUID PRIMARY KEY
course_id       UUID FOREIGN KEY
level_number    INT
title           TEXT
```

#### **lessons** — Уроки с заданиями
```sql
id              UUID PRIMARY KEY
course_level_id UUID FOREIGN KEY
title           TEXT
tasks           JSONB           -- Структурированные задания
order           INT
```

### Row-Level Security (RLS):

Политики автоматически ограничивают доступ:

✅ **Ученик видит:**
- Только свой профиль
- Только свой прогресс
- Только опубликованные курсы (`is_published = true`)

✅ **Admin видит:**
- Все профили и прогресс
- Полный CRUD для курсов
- Все таблицы для управления

### Real-time синхронизация:

Таблица `user_progress` опубликована для Real-time:
- Прогресс синхронизируется между устройствами в реальном времени
- Изменения в админке сразу видны в приложении

### Настройка локально:

**1. Установите Supabase CLI:**

```bash
# На Windows (рекомендуется Scoop или winget)
scoop install supabase
# или
winget install Supabase.CLI

# На macOS
brew install supabase/tap/supabase

# На Linux
brew install supabase/tap/supabase
```

**2. Инициализируйте локальный Supabase:**

```bash
cd CodeApp
npx supabase start          # Запустит локальный контейнер
npx supabase db reset       # Применит миграции + seed
```

**3. Скопируйте локальные учетные данные:**

После `npx supabase start` вы увидите:
```
DB URL:      postgresql://postgres:postgres@127.0.0.1:54322/postgres
ANON KEY:    eyJ...
```

Обновите:
- `gradle.properties` (для Android)
- `admin/.env` (для веб-админки)

**4. Работайте локально:**
```bash
npx supabase db push        # Применить новые миграции
npx supabase db pull        # Скачать схему с сервера
```

---

## 🎤 Озвучка персонажа (ElevenLabs)

В приложении есть персонаж Фальстаф, который может озвучивать текст.

### Запуск без озвучки:

Если у вас нет API-ключа ElevenLabs, приложение автоматически использует **Android TextToSpeech**.

### Настройка озвучки:

1. **Создайте аккаунт на [elevenlabs.io](https://elevenlabs.io)**

2. **Получите API-ключ:**
   - Profile → API Key → Copy

3. **Создайте или добавьте голос:**
   - Важно на Free плане: **нельзя использовать готовые голоса через API**
   - Voices → Create a New Voice (Voice Design или Instant Voice Clone)
   - Скопируйте **Voice ID** (иконка ⋯ → Copy voice ID)

4. **Установите секреты в Supabase Edge Functions:**
   
   Project Settings → Edge Functions → Secrets:
   ```
   ELEVENLABS_API_KEY=sk_xxx...
   ELEVENLABS_VOICE_ID=abc123...      (ID вашего голоса)
   ELEVENLABS_MODEL_ID=eleven_flash_v2_5  (опционально)
   ```

5. **Разверните функцию:**
   ```bash
   npx supabase functions deploy falstaff-tts
   npx supabase db push              # Обновите бакет
   ```

### Как это работает:

1. Приложение отправляет текст в Edge Function
2. Функция отправляет запрос в ElevenLabs API
3. MP3 сохраняется в Storage Supabase
4. Приложение воспроизводит аудио

Если что-то пошло не так, проверьте **Logcat** в Android Studio (строка `FalstaffVoice`).

---

## 🚀 Запуск полного стека

### Вариант 1: С облачным Supabase (production)

```bash
# 1. Настройте учетные данные в gradle.properties и admin/.env
# 2. Запустите Android приложение из Android Studio
# 3. Запустите админку
cd admin && npm install && npm run dev
```

### Вариант 2: Локально (для разработки)

```bash
# 1. Запустите локальный Supabase
npx supabase start
npx supabase db reset

# 2. Скопируйте локальные учетные данные в gradle.properties и admin/.env

# 3. Запустите Android приложение из Android Studio

# 4. Запустите админку
cd admin && npm install && npm run dev
```

---

## 📊 Таблица технологий

| Компонент | Технология | Назначение |
|-----------|-----------|-----------|
| **Android приложение** | Kotlin + Jetpack Compose | Интерфейс для учеников |
| **Веб-админка** | React 19 + TypeScript + Vite | Управление контентом |
| **Аутентификация** | Supabase Auth | Регистрация и вход |
| **База данных** | PostgreSQL (Supabase) | Хранилище данных |
| **Real-time** | Supabase Realtime | Синхронизация в реальном времени |
| **Озвучка** | ElevenLabs API | Голос Фальстафа |
| **Сборка Android** | Gradle + Kotlin DSL | Управление зависимостями |

---

## 🔑 Переменные окружения

### `gradle.properties` (Android):
```properties
SUPABASE_URL=https://xxx.supabase.co
SUPABASE_ANON_KEY=eyJ...
```

### `admin/.env` (Веб-админка):
```env
SUPABASE_URL=https://xxx.supabase.co
SUPABASE_ANON_KEY=eyJ...
```

### `supabase/config.toml` (Локальный Supabase):
```toml
[api]
enabled = true
port = 54321

[db]
port = 54322
```

---

## 📚 Workflow разработчика

### Добавить новую функцию в Android:

1. Создайте ViewModel в `com.codelingo.app.viewmodel`
2. Добавьте UI компоненты в `com.codelingo.app.ui`
3. Подключите к Supabase репозиторию
4. Протестируйте на эмуляторе

### Добавить новый экран в админку:

1. Создайте компонент в `admin/src/pages/`
2. Подключите Supabase клиент из `lib/supabase.ts`
3. Добавьте маршрут в `App.tsx`
4. Стилизуйте в `styles.css`

### Изменить схему БД:

1. Создайте миграцию: `npx supabase migration new название`
2. Напишите SQL в `supabase/migrations/`
3. Протестируйте локально: `npx supabase db reset`
4. Примените на production: `npx supabase db push`

---

## 🆘 Частые вопросы

**Q: Как стать администратором?**  
A: Выполните SQL запрос в Supabase Studio:
```sql
update public.profiles set role = 'admin' where id = 'ваш-uuid';
```

**Q: Приложение работает офлайн?**  
A: Да, если не установлены `SUPABASE_URL` и `SUPABASE_ANON_KEY`, приложение использует локальные данные из JSON.

**Q: Как синхронизировать прогресс между устройствами?**  
A: Таблица `user_progress` подключена к Real-time Supabase. Изменения автоматически синхронизируются.

**Q: Почему озвучка не работает?**  
A: 
- Проверьте наличие API-ключа ElevenLabs в Edge Functions Secrets
- Откройте Logcat в Android Studio и поищите `FalstaffVoice`
- Приложение автоматически переключится на Android TextToSpeech

**Q: На каком языке написано приложение?**  
A: Основной язык курсов обучения — **русский** (видно из UI текстов).

---

## 📝 Лицензия

Не указана. Уточните у владельца проекта.

---

## 👥 Контакты

**GitHub:** [NextFutureHub/CodeApp](https://github.com/NextFutureHub/CodeApp)

---

**Создано:** 2026  
**Последнее обновление:** 19 мая 2026
