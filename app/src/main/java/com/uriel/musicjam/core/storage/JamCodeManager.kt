package com.uriel.musicjam.core.storage

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JamCodeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val prefs: SharedPreferences = context.getSharedPreferences("jam_code_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val ACTIVE_JOIN_CODE = "active_join_code"
    }

    fun saveActiveJoinCode(joinCode: String?) {
        prefs.edit().putString(ACTIVE_JOIN_CODE, joinCode).apply()
    }

    fun getActiveJoinCode(): String? {
        return prefs.getString(ACTIVE_JOIN_CODE, null)
    }

    fun clearActiveJoinCode() {
        prefs.edit().remove(ACTIVE_JOIN_CODE).apply()
    }
}