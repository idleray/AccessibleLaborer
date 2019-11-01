package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlin.system.measureTimeMillis

class SuningLaborer(service: AccessibilityService): TaskLaborer(service) {
    //nexus 7 is too slow
    private var isFirst = true

    override fun init() {
        super.init()
        isFirst = true
    }

    override fun getPackageName(): String {
        return "com.suning.mobile.ebuy"
    }

    override fun getHomeClassName(): String {
        return "com.suning.mobile.ucwv.ui.WebViewActivity"
    }

    override fun isActive(): Boolean {
        return true
    }

    override fun triggerWork(event: AccessibilityEvent) {
        browseTask()
    }

    fun browseTask() {

    }

    fun goShop() {
        Log.d(TAG, "goShop")
        MainScope().launch {
            val delayTime: Long = if(BuildConfig.DEBUG && isFirst) 20000 else 3000
            delay(delayTime)
            val node = service.rootInActiveWindow
            val shopNode = findNodeByText(node, "+6000")
            if(shopNode != null) {
                Log.d(TAG, "found shop node")
            } else {
                Log.d(TAG, "no shop node")
            }
            val ret = shopNode?.performAction(AccessibilityNodeInfo.ACTION_CLICK) ?: false
            if(ret) {
                state = STATE.BROWSE
                isFirst = false
            } else {
//                collectPoint()
            }
        }

    }

    fun collectPoint() {
        MainScope().launch {
            delay(2000)
        }
    }

    override fun handleHome() {
        goShop()
    }

    override fun handleBrowse() {
        MainScope().launch {
            delay(4000)
            val node = findNodeByText(service.rootInActiveWindow, "任务完成")
            val time = if(node == null){
                15000L
            } else{
                Log.d(TAG, "mission completed")
                1500
            }
            MainScope().launch {
                delay(time)
                service.back()
                state = STATE.RETURN_HOME
            }
        }

    }

    override fun handleReturnHome() {
        goShop()
    }
}