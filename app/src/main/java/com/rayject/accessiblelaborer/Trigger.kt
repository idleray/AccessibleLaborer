package com.rayject.accessiblelaborer

import android.view.accessibility.AccessibilityEvent

/**
 * triggerType // 0: 不限制次数 1: 仅一次
 * status // 0:可用 1：不可用
 */
val TRIGGER_TYPE_NORMALL = 0
val TRIGGER_TYPE_ONE_TIME = 1
val TRIGGER_STATUS_VALID = 0
val TRIGGER_STATUS_INVALID = 1
data class Trigger(var eventType: Int, var triggerType: Int = 0, var contentChangeTypes: Int = 0, var status: Int = 0) {
    fun hasContentChangeType(contentType: Int): Boolean {
        return contentChangeTypes or contentType != 0

    }
}

fun builderDefaultTriggers(): List<Trigger> {
    val triggers = mutableListOf<Trigger>()
    triggers.add(Trigger(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED))
    triggers.add(Trigger(AccessibilityEvent.TYPE_VIEW_CLICKED))

    return triggers
}

fun builderTriggers1(): List<Trigger> {
    val triggers = mutableListOf<Trigger>()
    triggers.add(Trigger(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED))

    return triggers
}

fun builderTriggers2(): List<Trigger> {
    val triggers = mutableListOf<Trigger>()
    triggers.add(Trigger(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED))
    triggers.add(Trigger(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED, 1, AccessibilityEvent.CONTENT_CHANGE_TYPE_SUBTREE))

    return triggers
}