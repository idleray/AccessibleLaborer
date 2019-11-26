package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlin.system.measureTimeMillis

class TaobaoLaborer(service: AccessibilityService): TaskLaborer(service) {

    override fun getPackageName(): String {
        return "com.taobao.taobao"
    }

    override fun getHomeClassName(): String {
        return "com.taobao.browser.BrowserActivity"
    }

    override fun canHandleCurrentNode(): Boolean {
        return isHomeClass()
    }

    override fun isActive(): Boolean {
        return true
    }

    override fun triggerWork(event: AccessibilityEvent) {
//        flow {
//            emit(1)
//
//        }.collect()
        getMiaobi()
    }

    override fun handleHome() {
    }

    override fun handleBrowse() {
        MainScope().launch {
            delay(23000)
            service.back()
            state = STATE.RETURN_HOME

        }
    }

    override fun handleReturnHome() {
        getMiaobi()
    }

    private fun getMiaobi() {
        MainScope().launch {
            delay(2000)
            if(!goShop()) {
                goBrowse()
            }
        }

    }

    private fun goShop(): Boolean {
        Log.d(TAG, "go shop")
        val node = service.rootInActiveWindow ?: return false

        val shopNode = findNodeByText(node,"去进店")
        val performed = shopNode?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        if(performed != null && performed) {
            Log.d(TAG, "set state : shop")
            state = STATE.BROWSE
        }

        return shopNode != null
    }

    private fun goBrowse(): Boolean {
        Log.d(TAG, "goBrowse")
        val node = service.rootInActiveWindow ?: return false

        val shopNode = findNodeByText(node,"去浏览")
        val performed = shopNode?.performAction(AccessibilityNodeInfo.ACTION_CLICK)

        if(performed != null && performed) {
            Log.d(TAG, "set state : shop(browse)")
            state = STATE.BROWSE
        }

        return shopNode != null
    }
}