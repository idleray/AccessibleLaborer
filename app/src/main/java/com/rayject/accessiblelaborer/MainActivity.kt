package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AccessibilityManager.AccessibilityStateChangeListener {
    override fun onAccessibilityStateChanged(enabled: Boolean) {
        updateServiceStatus()
    }

    private lateinit var accessibilityManager: AccessibilityManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        accessibilityManager.addAccessibilityStateChangeListener(this)
        updateServiceStatus()
        start.setOnClickListener{
            openAccessibility()
        }

    }

    override fun onResume() {
        super.onResume()
        updateServiceStatus()
    }

    fun openAccessibility() {
        try {
            val accessibleIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(accessibleIntent)
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.turn_on_error_toast), Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }
    private fun updateServiceStatus() {
        if (isServiceEnabled()) {
            start.text = "关闭"
        } else {
            start.text = "开启"
        }
    }

    private fun isServiceEnabled(): Boolean {
        val accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_GENERIC
        )
        for (info in accessibilityServices) {
            if (info.getId() == "$packageName/.services.HongbaoService") {
                return true
            }
        }
        return false
    }
}
