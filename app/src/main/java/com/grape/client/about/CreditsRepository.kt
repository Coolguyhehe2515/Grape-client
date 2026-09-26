package com.grape.client.about

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class CreditsRepository(private val context: Context) {
    companion object {
        private const val REMOTE_URL = "https://raw.githubusercontent.com/Coolguyhehe2515/Grape-client/main/credits.json"
    }

    suspend fun fetch(): Credits? = withContext(Dispatchers.IO) {
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
                val people = json.optJSONArray("credits")
                val credits = buildList {
                    if (people != null) {
                        for (i in 0 until people.length()) {
                            val item = people.optJSONObject(i) ?: continue
                            add(Credit(
                                name = item.optString("name"),
                                role = item.optString("role"),
                                link = item.optString("link")
                            ))
                        }
                    }
                }
                Credits(
                    version = json.optString("version", "Alpha 0.0.1"),
                    credits = credits,
                    notice = json.optString("notice", "Grape Client is not an official Microsoft product and is not affiliated with or endorsed by Microsoft.")
                )
            } finally {
                connection.disconnect()
            }
        }.getOrNull()
    }
}
