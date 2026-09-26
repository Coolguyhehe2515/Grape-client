package com.grape.client

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.grape.client.announcement.AnnouncementRepository
import com.grape.client.minecraft.MinecraftHostActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : Activity() {
    private lateinit var announcementView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        val title = TextView(this).apply {
            text = "Grape Client"
            textSize = 28f
        }

        val status = TextView(this).apply {
            text = "Native core: ${nativeVersion()}\nARM64 runtime host ready"
            textSize = 16f
        }

        val launchButton = Button(this).apply {
            text = "Launch Minecraft"
            setOnClickListener { launchMinecraftHost() }
        }

        announcementView = TextView(this).apply {
            textSize = 16f
            setPadding(0, 32, 0, 0)
            visibility = TextView.GONE
        }

        layout.addView(title)
        layout.addView(status)
        layout.addView(launchButton)
        layout.addView(announcementView)
        setContentView(layout)

        loadAnnouncement()
    }

    private fun launchMinecraftHost() {
        runCatching {
            startActivity(Intent(this, MinecraftHostActivity::class.java))
        }.onFailure {
            Toast.makeText(
                this,
                it.message ?: "Unable to start Minecraft host.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun loadAnnouncement() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching { AnnouncementRepository(this@MainActivity).fetch() }.getOrNull()
            withContext(Dispatchers.Main) {
                if (result?.enabled == true) {
                    announcementView.text = "${result.title}\n\n${result.message}"
                    announcementView.visibility = TextView.VISIBLE
                }
            }
        }
    }

    private external fun nativeVersion(): String

    companion object {
        init {
            System.loadLibrary("grape")
        }
    }
}
