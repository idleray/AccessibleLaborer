package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
fun Log.ff(msg: String) {

}
class LaborerAccessibilityService: AccessibilityService() {
    var TAG = "LaborerAccessibilityService"

    var currentLaborer : Laborer? = null
    val jobs = mutableListOf<Job>()

    public override fun onServiceConnected() {
        super.onServiceConnected()
        logd("----onServiceConnected----")

        LaborerManager.init(this)

        val info = serviceInfo
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.packageNames = LaborerManager.laborers.map {
            it.getPackageName()
        }.toTypedArray()

        serviceInfo = info

    }

    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if(BuildConfig.DEBUG) {
//            printEvent(this, event)
//            printCurrentNodes(this)
            logd(AccessibilityEvent.eventTypeToString(event.eventType))
//            logd("contentChangeTypes: ${event.contentChangeTypes}")
        }

        if(event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            logd("in TYPE_WINDOW_STATE_CHANGED")
            if(!event.packageName.equals(currentLaborer?.getPackageName())) {
                logd("clear current laborer: ${currentLaborer?.getPackageName()}")
                currentLaborer?.finish()
                currentLaborer = null

                cancelJobs()
            }
            if(currentLaborer == null) {
                val candidates = LaborerManager.chooseLaborersByEvent(event)
                logd("candidates: $candidates")
                val candidate = candidates?.find {
                    it.handleDelayMillis() == 0L
                }
                if(candidate != null) {
                    currentLaborer = candidate
                    logd("found immediate laborer: ${currentLaborer?.getPackageName()}")
                    currentLaborer?.init()
//                    currentLaborer?.start()
//                    currentLaborer?.handleEvent(event)
                } else {
                    candidates?.forEach {
                        logd("delay to found")
                        val job = runDelay(it.handleDelayMillis()){
                            if(it.canHandleCurrentNode()) {
                                logd("now to found delay laborer")
                                //TODO: Use Mutex to deal concurrency
                                if(currentLaborer == null) {
                                    currentLaborer = it
                                    logd("found delay laborer: ${currentLaborer?.getPackageName()}")
                                    currentLaborer?.init()
                                    //异步执行，因此在此处理事件
                                    currentLaborer?.handleEventByType(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
                                }
                            }

                        }
                        jobs.add(job)
                    }

                }
            }
        }

        currentLaborer?.handleEvent(event)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        LaborerManager.destroy()
        return super.onUnbind(intent)
    }

    override fun onGesture(gestureId: Int): Boolean {
        logd("onGesture: gestureId = $gestureId")
        return super.onGesture(gestureId)

    }

    private fun cancelJobs() {
        jobs.forEach {
            it.cancel()
//            it.join()
        }
    }

}