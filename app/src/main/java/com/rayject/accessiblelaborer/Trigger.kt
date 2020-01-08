package com.rayject.accessiblelaborer

import android.view.accessibility.AccessibilityEvent

data class Trigger(var eventType: Int, var triggerType: Int = 0, var contentChangeTypes: Int = 0)

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

    return triggers
}