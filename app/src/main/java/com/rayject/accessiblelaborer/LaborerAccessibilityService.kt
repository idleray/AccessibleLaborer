package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class LaborerAccessibilityService: AccessibilityService() {
    var TAG = "LaborerAccessibilityService"

    var currentLaborer : Laborer? = null

    public override fun onServiceConnected() {
        super.onServiceConnected()
        val info = serviceInfo
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        serviceInfo = info

        LaborerManager.init(this)

    }

    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if(BuildConfig.DEBUG) {
            printEvent(this, event)
        }

        if(!event.packageName.equals(currentLaborer?.getPackageName())) {
            currentLaborer?.finish()
            currentLaborer = LaborerManager.getLaborerByPkgName(event.packageName)
            currentLaborer?.init()
        }
        currentLaborer?.handleEvent(event)


    }

    override fun onUnbind(intent: Intent?): Boolean {
        LaborerManager.destroy()
        return super.onUnbind(intent)
    }

}