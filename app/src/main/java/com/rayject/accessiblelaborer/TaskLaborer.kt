package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class TaskLaborer(service: AccessibilityService): BaseLaborer(service) {
    protected val TAG = "TaskLaborer"

    var state = STATE.IDLE
        set(value) {
            Log.d(TAG, "set state: $value")
            field = value
        }
    abstract fun triggerWork(event: AccessibilityEvent)
    abstract fun handleHome()
    abstract fun handleBrowse()
    abstract fun handleReturnHome()

    override fun init() {
        state = STATE.IDLE
        val info = service.serviceInfo
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_VIEW_CLICKED
        service.serviceInfo = info
    }

    override fun finish() {
    }

    override fun handleEvent(event: AccessibilityEvent) {
        when(event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> onWindowChanged(event)
            AccessibilityEvent.TYPE_VIEW_CLICKED -> onViewClick(event)
        }
    }

    open fun onWindowChanged(event: AccessibilityEvent) {
        Log.d(TAG, "onWindowChange")
        Log.d(TAG, "state: $state")

        when (state) {
            STATE.IDLE -> {
                if(getHomeClassName() == event.className) {
                    state = STATE.HOME
                    handleHome()
//                    MainScope().launch {
//                        Log.d(TAG, "start to print")
//                        delay(20000)
//                        printCurrentNodes(service)
//                    }

                }
            }
            STATE.HOME -> {
//                MainScope().launch {
//                    Log.d(TAG, "start to print")
//                    delay(20000)
//                    printCurrentNodes(service)
//                }
            }
            STATE.BROWSE -> {
                handleBrowse()
//                    MainScope().launch {
//                        Log.d(TAG, "start to print")
//                        delay(20000)
//                        printCurrentNodes(service)
//                    }

            }
            STATE.RETURN_HOME -> {
                handleReturnHome()

            }
        }
    }

    fun onViewClick(event: AccessibilityEvent) {
        Log.d(TAG, "onViewClick")
        if(state == STATE.HOME) {
            triggerWork(event)
        }
//        MainScope().launch {
//            delay(1000)
//            printCurrentNodes(service)
//
//        }

    }
}