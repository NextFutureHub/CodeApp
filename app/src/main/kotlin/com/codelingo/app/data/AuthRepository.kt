package com.codelingo.app.data

import com.codelingo.app.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

data class AuthUser(
    val id: String,
    val email: String?,
)

class AuthRepository {
    val isConfigured: Boolean get() = SupabaseProvider.isConfigured

    val sessionStatus: Flow<SessionStatus>?
        get() = if (isConfigured) SupabaseProvider.client.auth.sessionStatus else null

    val currentUser: Flow<AuthUser?> =
        sessionStatus?.map { status ->
            when (status) {
                is SessionStatus.Authenticated -> status.session.user?.toAuthUser()
                else -> null
            }
        } ?: kotlinx.coroutines.flow.flowOf(null)

    suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        requireConfigured()
        SupabaseProvider.client.auth.signInWith(Email) {
            this.email = email.trim()
            this.password = password
        }
    }

    suspend fun signUp(email: String, password: String, displayName: String): Result<Unit> = runCatching {
        requireConfigured()
        SupabaseProvider.client.auth.signUpWith(Email) {
            this.email = email.trim()
            this.password = password
            data = buildJsonObject {
                put("display_name", displayName.trim().ifBlank { "Ученик" })
            }
        }
    }

    suspend fun signOut(): Result<Unit> = runCatching {
        requireConfigured()
        SupabaseProvider.client.auth.signOut()
    }

    fun currentUserId(): String? =
        if (isConfigured) SupabaseProvider.client.auth.currentSessionOrNull()?.user?.id else null

    private fun requireConfigured() {
        if (!isConfigured) error("Supabase не настроен. Добавьте SUPABASE_URL и SUPABASE_ANON_KEY в gradle.properties")
    }

    private fun UserInfo.toAuthUser() = AuthUser(id = id, email = email)
}
