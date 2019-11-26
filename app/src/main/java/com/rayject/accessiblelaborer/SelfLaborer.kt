package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.suspendCoroutine
import kotlin.system.measureTimeMillis

class SelfLaborer(service: AccessibilityService): TaskLaborer(service) {

    override fun getPackageName(): String {
        return "com.rayject.accessiblelaborer"
    }

    override fun getHomeClassName(): String {
        return "com.rayject.accessiblelaborer.MainActivity"
    }

    override fun canHandleCurrentNode(): Boolean {
        return isHomeClass()
    }

    override fun isActive(): Boolean {
        return true
    }

    override fun triggerWork(event: AccessibilityEvent) {
    }

    override fun handleHome() {

    }

    override fun handleBrowse() {
    }

    override fun handleReturnHome() {
    }

    override fun onViewClick(event: AccessibilityEvent) {
        val c = 0
        runBlocking {  }
        MainScope().launch {
            delay(1000)
            Log.d(TAG, "aaa")
        }
        val a = 0
        var b = 1
        b += a
//        val channel = Channel<Int>()
        runBlocking<Unit> {
            launch {
                coroutineScope {

                }
            }
        }
        val ms = MainScope()
        ms.cancel()

        runBlocking {
            GlobalScope.launch {  }
        }


    }
}