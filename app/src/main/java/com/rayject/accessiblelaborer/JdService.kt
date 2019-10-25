package com.rayject.accessiblelaborer

import android.view.accessibility.AccessibilityEvent


fun MyAccessibilityService.onJdEvent(event: AccessibilityEvent) {
    when(event.eventType) {
        AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> onWindowChanged(event)
    }

}