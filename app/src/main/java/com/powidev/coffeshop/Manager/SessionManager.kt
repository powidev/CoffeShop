package com.powidev.coffeshop.manager

import android.content.Context
import android.content.SharedPreferences
import com.powidev.coffeshop.data.local.User

object SessionManager {
    private const val PREFS_NAME = "user_session"
    private const val KEY_EMAIL = "email"
    private const val KEY_ROLE = "role"

    fun setCurrentUser(context: Context, user: User) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().apply {
            putString(KEY_EMAIL, user.email)
            putString(KEY_ROLE, user.role)
            apply() // Usamos apply() para operación asíncrona
        }
    }

    fun isAdmin(context: Context): Boolean {  // <- Requiere Context
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ROLE, null) == "admin"
    }

    // Versión segura de getCurrentUser
    fun getCurrentUser(context: Context): User? {
        return with(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)) {
            val email = getString(KEY_EMAIL, null)
            val role = getString(KEY_ROLE, null)
            if (!email.isNullOrEmpty() && !role.isNullOrEmpty()) {
                User(email, "", role) // Password no se guarda por seguridad
            } else {
                null
            }
        }
    }

    // Añade este método para cerrar sesión
    fun logout(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .clear()
            .apply()
    }
}