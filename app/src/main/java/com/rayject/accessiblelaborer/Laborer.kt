package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

interface Laborer {
    fun init()
    fun finish()

    fun getPackageName(): String
    fun getHomeClassName(): String
    fun isActive(): Boolean
    fun handleEvent(event: AccessibilityEvent)
}

abstract class BaseLaborer(val service: AccessibilityService): Laborer

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

    source.recycle()


}

fun AccessibilityService.back() {
    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
}