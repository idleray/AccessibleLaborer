package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.*
import java.util.regex.Pattern

interface Laborer {
    val service: AccessibilityService
    fun init()
    fun start()
    fun finish()

    fun getPackageName(): String
    fun getHomeClassName(): String
    fun canHandleCurrentNode(): Boolean
    fun handleDelayMillis(): Long
    fun isActive(): Boolean
    fun handleEvent(event: AccessibilityEvent)
    fun handleEventByType(eventType: Int)
}

abstract class BaseLaborer(override val service: AccessibilityService): Laborer {
    override fun start() {

    }

    override fun handleEventByType(eventType: Int) {
    }

    override fun handleDelayMillis(): Long {
        return 0
    }

    protected fun isHomeClass(): Boolean {
        return service.rootInActiveWindow.className == getHomeClassName()
    }
}
fun findNodeByWhatEver(node: AccessibilityNodeInfo?, text: String): AccessibilityNodeInfo? {
    var node = findNode(node, text, 0)
    if(node == null) {
        node = findNode(node, text, 1)
    }

    return node
}

//findAccessibilityNodeInfosByText找不到，不知道原因
fun findNodeByText(node: AccessibilityNodeInfo?, text: String): AccessibilityNodeInfo?{
    return findNode(node, text, 0)
}

fun findNodeByContentDescription(node: AccessibilityNodeInfo?, text: String): AccessibilityNodeInfo?{
    return findNode(node, text, 1)
}

fun findNode(node: AccessibilityNodeInfo?, text: String, type: Int): AccessibilityNodeInfo?{
//    Log.d(TAG, "findNode: $text")
//        printSources(node!!, 0)
    var retNode: AccessibilityNodeInfo? = null
    var nodeText : String? = null
    if(type == 0) {
        nodeText = node?.text?.toString()
    } else if (type == 1) {
        nodeText = node?.contentDescription?.toString()
    }
//    Log.d(TAG, "text: $nodeText")
    if(nodeText?.contains(text) == true) {
        retNode = node
        return retNode
    }
    if(node?.childCount == 0) {
        return retNode
    }
    for(index in 0 until (node?.childCount ?: 0)) {
        val child = node?.getChild(index)
        if(child != null) {
            val aNode = findNode(child, text, type)
            if(aNode != null) {
                retNode = aNode
                break
            }
            aNode?.recycle()
        }
    }

    node?.recycle()

    return retNode
}


fun printEvent(service: AccessibilityService, event: AccessibilityEvent) {
    Log.d("eventType", AccessibilityEvent.eventTypeToString(service.serviceInfo.eventTypes))
    Log.d("event", event.toString())
    Log.d("root", service.rootInActiveWindow?.toString() ?: "no root")

    val node = event.source
    if(node == null) {
        Log.e("event", "event.source is null")
        return
    }
    printSources(node, 0)

}

fun printCurrentNodes(service: AccessibilityService) {
    Log.d("eventType", AccessibilityEvent.eventTypeToString(service.serviceInfo.eventTypes))

    printSources(service.rootInActiveWindow, 0)

}

fun printSources(source: AccessibilityNodeInfo, depth: Int) {
    val tag = "depth-$depth"
    Log.d(tag, source.toString())
    for(index in 0 until source.childCount) {
        val dp = depth + 1
        val child = source.getChild(index)
        if(child == null) {
            Log.e("err------", "WTFFFFFFFFFFFF")
            break
        }
        printSources(child, dp)
    }

    //不要recycle，会导致node被回收。。。
//    source.recycle()


}

fun logd(msg: String) {
    Log.d("StateLaborer", msg)
}

fun AccessibilityService.back() {
    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
}

fun hasTaskRemain(text: String?): Boolean {
    var ret = false
    val pos = text?.indexOf("/") ?: -1
    if(pos != -1) {
        val remainP = Pattern.compile("\\d+(?=/)")
        val m1 = remainP.matcher(text)
        var remainCount = -1
        if(m1.find()) {
            remainCount = m1.group().toInt()
        }

        val tp = Pattern.compile("(?<=/)\\d+")
        val m2 = tp.matcher(text)
        var totalCount = -1
        if(m2.find()) {
            totalCount = m2.group().toInt()
        }
        if(remainCount != -1 && totalCount != -1 && remainCount < totalCount) {
            Log.d("taskRemain", "remain: $remainCount, total: $totalCount")
            ret = true
        }
    }

    return ret
}

fun runDelay(timeMillis: Long, block: () -> Unit ): Job {
    return MainScope().launch {
        delay(timeMillis)
        block()
    }
}

fun runBlockingDelay(timeMillis: Long, block: () -> Unit ) {
    //TODO: 对吗？
    runBlocking {
        delay(timeMillis)
        block()
    }
}
