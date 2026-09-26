package com.grape.client

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Space
import android.widget.TextView
import android.widget.Toast
import com.grape.client.about.CreditsRepository
import com.grape.client.announcement.AnnouncementRepository
import com.grape.client.minecraft.MinecraftHostActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : Activity() {
    private lateinit var content: LinearLayout
    private lateinit var homeButton: TextView
    private lateinit var settingsButton: TextView
    private lateinit var aboutButton: TextView

    private val background = 0xFF161216.toInt()
    private val surface = 0xFF211B21.toInt()
    private val surfaceStrong = 0xFF2A222A.toInt()
    private val accent = 0xFFB000FF.toInt()
    private val textPrimary = 0xFFF4EDF4.toInt()
    private val textSecondary = 0xFFBEB4BE.toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
    }

    private fun showHome() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(background)
        }
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(20), dp(16), dp(12))
        }
        root.addView(ScrollView(this).apply { addView(content) }, LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(createNavigation())
        setContentView(root)
        buildHome()
        selectNavigation(homeButton)
    }

    private fun buildHome() {
        content.removeAllViews()
        val header = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL }
        val titleBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        titleBox.addView(text("Grape Client", 30f, textPrimary).apply { setTypeface(typeface, Typeface.BOLD) })
        titleBox.addView(text("Minecraft client for Android", 14f, textSecondary))
        header.addView(titleBox, LinearLayout.LayoutParams(0, -2, 1f))
        header.addView(text("ALPHA 0.0.1", 11f, accent).apply {
            gravity = Gravity.CENTER
            setTypeface(typeface, Typeface.BOLD)
            background = rounded(surfaceStrong, 50)
            setPadding(dp(12), dp(7), dp(12), dp(7))
        })
        content.addView(header)
        gap(22)
        content.addView(text("Welcome back", 27f, textPrimary).apply { setTypeface(typeface, Typeface.BOLD) })
        content.addView(text("Native core: ${nativeVersion()}", 15f, textSecondary))
        gap(20)
        content.addView(text("Minecraft", 16f, textSecondary).apply { setTypeface(typeface, Typeface.BOLD) })

        val runtime = card()
        runtime.addView(text("ARM64 runtime", 19f, textPrimary).apply { setTypeface(typeface, Typeface.BOLD) })
        runtime.addView(text("Runtime host ready", 14f, textSecondary))
        runtime.addView(Button(this).apply {
            text = "Launch Minecraft"
            textSize = 16f
            isAllCaps = false
            setTextColor(textPrimary)
            background = rounded(accent, 22)
            setOnClickListener { launchMinecraft() }
        }, LinearLayout.LayoutParams(-1, dp(54)).apply { topMargin = dp(16) })
        content.addView(runtime)
        gap(16)

        val announcement = card()
        announcement.addView(text("Announcements", 18f, textPrimary).apply { setTypeface(typeface, Typeface.BOLD) })
        val announcementText = text("Checking for announcements...", 14f, textSecondary)
        announcement.addView(announcementText, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(7) })
        content.addView(announcement)
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching { AnnouncementRepository(this@MainActivity).fetch() }.getOrNull()
            withContext(Dispatchers.Main) {
                announcementText.text = if (result?.enabled == true) "${result.title}\n\n${result.message}" else "No new announcements."
            }
        }
        gap(18)
        content.addView(text("Grape Client is currently in development.", 14f, textSecondary))
    }

    private fun showSettings() {
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(background) }
        val page = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(24), dp(20), dp(20)) }
        page.addView(text("Settings", 30f, textPrimary).apply { setTypeface(typeface, Typeface.BOLD) })
        page.addView(card().apply {
            addView(text("Grape Client", 18f, textPrimary).apply { setTypeface(typeface, Typeface.BOLD) })
            addView(text("More settings will be added here.", 14f, textSecondary))
        }, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(22) })
        root.addView(ScrollView(this).apply { addView(page) }, LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(createNavigation())
        setContentView(root)
        selectNavigation(settingsButton)
    }

    private fun showAbout() {
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(background) }
        val page = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(24), dp(20), dp(20)) }
        page.addView(text("About", 30f, textPrimary).apply { setTypeface(typeface, Typeface.BOLD) })
        page.addView(text("Grape Client", 19f, accent).apply { setTypeface(typeface, Typeface.BOLD) })
        page.addView(text("Alpha 0.0.1", 14f, textSecondary))
        page.addView(card().apply {
            addView(text("About this project", 17f, textPrimary).apply { setTypeface(typeface, Typeface.BOLD) })
            addView(text("Grape Client is an independent Minecraft client project for Android.", 14f, textSecondary))
        }, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(18) })
        page.addView(card().apply {
            addView(text("Notice", 17f, textPrimary).apply { setTypeface(typeface, Typeface.BOLD) })
            addView(text("Grape Client is not an official Microsoft product and is not affiliated with or endorsed by Microsoft.", 14f, textSecondary))
        }, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(10) })
        page.addView(text("Credits", 21f, textPrimary).apply { setTypeface(typeface, Typeface.BOLD) }, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(24) })
        val creditsBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        page.addView(creditsBox, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(10) })
        root.addView(ScrollView(this).apply { addView(page) }, LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(createNavigation())
        setContentView(root)
        selectNavigation(aboutButton)

        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching { CreditsRepository(this@MainActivity).fetch() }.getOrNull()
            withContext(Dispatchers.Main) {
                creditsBox.removeAllViews()
                if (result == null || result.credits.isEmpty()) {
                    creditsBox.addView(text("No credits available.", 14f, textSecondary))
                } else {
                    result.credits.forEach { credit ->
                        val item = card()
                        item.addView(text(credit.name, 17f, textPrimary).apply { setTypeface(typeface, Typeface.BOLD) })
                        item.addView(text(credit.role, 14f, textSecondary))
                        if (credit.link.isNotBlank()) item.addView(text("Open profile", 14f, accent).apply {
                            setTypeface(typeface, Typeface.BOLD)
                            setPadding(0, dp(9), 0, 0)
                            setOnClickListener { openUrl(credit.link) }
                        })
                        creditsBox.addView(item, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(10) })
                    }
                }
            }
        }
    }

    private fun createNavigation(): View {
        val nav = LinearLayout(this).apply {
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(8), dp(8), dp(8))
            background = rounded(surface, 24)
        }
        homeButton = navItem("⌂\nBeranda")
        settingsButton = navItem("⚙\nPengaturan")
        aboutButton = navItem("ⓘ\nTentang")
        homeButton.setOnClickListener { showHome() }
        settingsButton.setOnClickListener { showSettings() }
        aboutButton.setOnClickListener { showAbout() }
        nav.addView(homeButton, LinearLayout.LayoutParams(0, dp(62), 1f))
        nav.addView(settingsButton, LinearLayout.LayoutParams(0, dp(62), 1f))
        nav.addView(aboutButton, LinearLayout.LayoutParams(0, dp(62), 1f))
        return nav
    }

    private fun navItem(value: String) = text(value, 12f, textSecondary).apply { gravity = Gravity.CENTER }

    private fun selectNavigation(selected: TextView) {
        listOf(homeButton, settingsButton, aboutButton).forEach { it.setTextColor(textSecondary); it.background = null }
        selected.setTextColor(textPrimary)
        selected.background = rounded(accent, 22)
    }

    private fun card() = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(16), dp(16), dp(16), dp(16))
        background = rounded(surface, 20)
    }

    private fun text(value: String, size: Float, color: Int) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
    }

    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply { setColor(color); cornerRadius = dp(radius).toFloat() }

    private fun gap(size: Int) { content.addView(Space(this), LinearLayout.LayoutParams(1, dp(size))) }

    private fun launchMinecraft() = runCatching {
        startActivity(Intent(this, MinecraftHostActivity::class.java))
    }.onFailure { Toast.makeText(this, it.message ?: "Unable to start Minecraft host.", Toast.LENGTH_LONG).show() }

    private fun openUrl(url: String) = runCatching {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }.onFailure { Toast.makeText(this, "Unable to open profile.", Toast.LENGTH_SHORT).show() }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    private external fun nativeVersion(): String

    companion object { init { System.loadLibrary("grape") } }
}
