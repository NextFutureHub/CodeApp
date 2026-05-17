package com.codelingo.app.data

object AuthErrorMessages {
    fun fromThrowable(error: Throwable): String {
        val raw = error.message.orEmpty()
        val code = extractCode(raw)
        return when (code) {
            "email_not_confirmed" ->
                "Подтвердите email по ссылке из письма или отключите подтверждение в Supabase (Authentication → Email)."
            "invalid_credentials", "invalid_login_credentials" ->
                "Неверный email или пароль."
            "user_already_registered" ->
                "Этот email уже зарегистрирован. Войдите или восстановите пароль."
            "weak_password" ->
                "Пароль слишком простой. Используйте не менее 6 символов."
            "signup_disabled" ->
                "Регистрация отключена в настройках Supabase."
            else -> when {
                raw.contains("email_not_confirmed", ignoreCase = true) ->
                    "Подтвердите email по ссылке из письма."
                raw.contains("Invalid login", ignoreCase = true) ->
                    "Неверный email или пароль."
                raw.length > 120 -> "Ошибка входа. Проверьте email и пароль."
                raw.isNotBlank() -> raw.lines().first().trim()
                else -> "Не удалось выполнить вход. Попробуйте снова."
            }
        }
    }

    private fun extractCode(raw: String): String? {
        val known = listOf(
            "email_not_confirmed",
            "invalid_credentials",
            "invalid_login_credentials",
            "user_already_registered",
            "weak_password",
            "signup_disabled",
        )
        return known.firstOrNull { raw.contains(it, ignoreCase = true) }
    }
}
