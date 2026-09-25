package com.grape.client

import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
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
            text = "Native core: ${nativeVersion()}"
            textSize = 16f
        }

        layout.addView(title)
        layout.addView(status)
        setContentView(layout)
    }

    private external fun nativeVersion(): String

    companion object {
        init {
            System.loadLibrary("grape")
        }
    }
}
