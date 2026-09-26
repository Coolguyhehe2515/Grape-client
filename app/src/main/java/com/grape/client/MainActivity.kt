package com.grape.client

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Space
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
            setPadding(32, 40, 32, 32)
        }

        val title = TextView(this).apply {
            text = "Grape Client"
            textSize = 30f
            setTypeface(typeface, Typeface.BOLD)
        }

        val status = TextView(this).apply {
            text = "Native core: ${nativeVersion()}\nARM64 runtime host ready"
            textSize = 16f
            setPadding(0, 8, 0, 0)
        }

        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 28, 0, 0)
        }

        val launchButton = Button(this).apply {
            text = "Launch Minecraft"
            setOnClickListener { launchMinecraftHost() }
        }

        val aboutButton = Button(this).apply {
            text = "About"
            setOnClickListener { showAboutDialog() }
        }

        actions.addView(launchButton, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        actions.addView(Space(this), LinearLayout.LayoutParams(12, 1))
        actions.addView(aboutButton, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.45f))

        announcementView = TextView(this).apply {
            textSize = 16f
            setPadding(0, 28, 0, 0)
            visibility = View.GONE
        }

        layout.addView(title)
        layout.addView(status)
        layout.addView(actions)
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

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("About Grape Client")
            .setMessage(
                "Version: Alpha 0.0.1\n\n" +
                    "Grape Client is an independent Minecraft client project.\n\n" +
                    "Credits are managed through the project's remote JSON configuration.\n\n" +
                    "Grape Client is not an official Microsoft product and is not affiliated with Microsoft."
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun loadAnnouncement() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching { AnnouncementRepository(this@MainActivity).fetch() }.getOrNull()
            withContext(Dispatchers.Main) {
                if (result?.enabled == true) {
                    announcementView.text = "${result.title}\n\n${result.message}"
                    announcementView.visibility = View.VISIBLE
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
