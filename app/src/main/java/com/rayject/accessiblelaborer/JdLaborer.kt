package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlin.system.measureTimeMillis

class JdLaborer(service: AccessibilityService): TaskLaborer(service) {
    private var isFirst = true

    override fun init() {
        super.init()
        isFirst = true
        val info = service.serviceInfo
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        service.serviceInfo = info
    }

    override fun getPackageName(): String {
        return "com.jingdong.app.mall"
    }

    override fun getHomeClassName(): String {
        return "com.suning.mobile.ucwv.ui.WebViewActivity"
    }

    override fun canHandleCurrentNode(): Boolean {
        return isHomeClass()
    }

    override fun isActive(): Boolean {
        return true
    }

    override fun onWindowChanged(event: AccessibilityEvent) {
//        super.onWindowChanged(event)
        MainScope().launch {
            delay(3000)
            printCurrentNodes(service)
        }
    }

    override fun onViewClick(event: AccessibilityEvent) {


    }
}