package com.grape.client.announcement

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class AnnouncementRepository(private val context: Context) {
    companion object {
        private const val REMOTE_URL = "https://raw.githubusercontent.com/Coolguyhehe2515/Grape-client/main/announcement.json"
    }

    suspend fun fetch(): Announcement? = withContext(Dispatchers.IO) {
        runCatching {
            val connection = (URL(REMOTE_URL).openConnection() as HttpURLConnection).apply {
                connectTimeout = 5000
                readTimeout = 5000
                requestMethod = "GET"
                useCaches = false
            }
            try {
                if (connection.responseCode !in 200..299) return@runCatching null
                val json = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
                Announcement(
                    enabled = json.optBoolean("enabled", false),
                    title = json.optString("title"),
                    message = json.optString("message"),
                    type = json.optString("type", "info"),
                    version = json.optString("version"),
                    url = json.optString("url"),
                    dismissible = json.optBoolean("dismissible", true)
                )
            } finally {
                connection.disconnect()
            }
        }.getOrNull()
    }
}
