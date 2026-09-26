package com.grape.client

import android.app.Activity
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.grape.client.announcement.AnnouncementRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : Activity(), SurfaceHolder.Callback {
    private lateinit var announcementView: TextView
    private lateinit var gameSurface: SurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameSurface = SurfaceView(this).apply {
            holder.addCallback(this@MainActivity)
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        val title = TextView(this).apply {
            text = "Grape Client"
            textSize = 28f
        }

        val status = TextView(this).apply {
            text = "Native core: ${nativeVersion()}\nARM64 host ready"
            textSize = 16f
        }

        val launchButton = Button(this).apply {
            text = "Launch Minecraft"
            setOnClickListener {
                Toast.makeText(
                    this@MainActivity,
                    "Native game surface is ready. Minecraft runtime is not bundled yet.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        announcementView = TextView(this).apply {
            textSize = 16f
            setPadding(0, 32, 0, 0)
            visibility = TextView.GONE
        }

        layout.addView(title)
        layout.addView(status)
        layout.addView(gameSurface, LinearLayout.LayoutParams(-1, 0, 1f))
        layout.addView(launchButton)
        layout.addView(announcementView)
        setContentView(layout)

        loadAnnouncement()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        nativeAttachSurface(holder.surface)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        nativeAttachSurface(holder.surface)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        nativeDetachSurface()
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
    private external fun nativeAttachSurface(surface: Surface)
    private external fun nativeDetachSurface()

    companion object {
        init {
            System.loadLibrary("grape")
        }
    }
}
