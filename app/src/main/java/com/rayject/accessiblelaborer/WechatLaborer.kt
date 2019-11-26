package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlin.system.measureTimeMillis

class WechatLaborer(service: AccessibilityService): TaskLaborer(service) {
    private var isFirst = true

    override fun init() {
        super.init()
        isFirst = true
    }

    override fun getPackageName(): String {
        return "com.tencent.mm"
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

//    override fun handleEvent(event: AccessibilityEvent) {
//        super.handleEvent(event)
//        printEvent(service, event)
////        val node = findNodeByText(service.rootInActiveWindow, "随机逛")
////        if(node != null) {
////            Log.d(TAG, "found node")
////        }
//        MainScope().launch {
//            delay(4000)
//            val node = findNodeByText(service.rootInActiveWindow, "随机逛")
//            if(node != null) {
//                Log.d(TAG, "found node")
//
//                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
//            }
//        }
//    }

    override fun onWindowChanged(event: AccessibilityEvent) {
        if(state == STATE.IDLE) {
            MainScope().launch {
                delay(4000)
                checkHome()
            }

        } else {
            super.onWindowChanged(event)
        }
//        super.onWindowChanged(event)
//        MainScope().launch {
//            delay(4000)
////            printCurrentNodes(service)
//            browse()
//        }
    }

    fun checkHome() {
        val node = findNodeByText(service.rootInActiveWindow, "随机逛")

        if(node != null) {
            state = STATE.HOME
            browse()
        }

    }

    fun browse() {
        Log.d(TAG, "browse")
        MainScope().launch {
            delay(2000)
            val node = findNodeByText(service.rootInActiveWindow, "随机逛")
            val remianShop = hasShop()
            if(remianShop) {
                val ret = node?.performAction(AccessibilityNodeInfo.ACTION_CLICK) ?: false
                if(ret) {
//                state = STATE.BROWSE
                    launch {
                        delay(2000)
                        service.back()
                        browse()
                    }
                }

            }



        }


    }

    fun hasShop(): Boolean {
        var ret = false
        val node = findNodeByText(service.rootInActiveWindow, "浏览品牌店铺")
        if(node != null) {
            Log.d(TAG, "found 浏览品牌店铺")
            ret = hasTaskRemain(node.text?.toString())
        } else {
            Log.d(TAG, "no shop remain")
        }

        return ret

    }

    override fun triggerWork(event: AccessibilityEvent) {
        browseTask()
    }

    fun browseTask() {

    }

    override fun handleHome() {
    }

    override fun handleBrowse() {
//        Log.d(TAG, "handle browse")
//        MainScope().launch {
//            delay(1500)
//            service.back()
//            state = STATE.RETURN_HOME
//
//        }

    }

    override fun onViewClick(event: AccessibilityEvent) {
//        if(state == STATE.BROWSE) {
//            MainScope().launch {
//                state = STATE.RETURN_HOME
//                delay(4000)
//                service.back()
//
//            }
//
//        } else if(state == STATE.RETURN_HOME) {
//            handleReturnHome()
//
//        }
    }

    override fun handleReturnHome() {
        Log.d(TAG, "return home")
//        browse()
    }
}