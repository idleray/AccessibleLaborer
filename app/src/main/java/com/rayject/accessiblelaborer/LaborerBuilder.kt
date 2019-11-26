package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

fun buildSampleLaborer(service: AccessibilityService):Laborer {
    val laborer = StateLaborer(service)
    laborer.pkgName = "com.suning.mobile.ebuy"
    laborer.className = "com.suning.mobile.ucwv.ui.WebViewActivity"
    laborer.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_VIEW_CLICKED
    laborer.initStateName = "home"
    laborer.handleDelay = 5000
    laborer.text = "省钱攻略"
    laborer.textType = 0

    var state: State
    state = State()
    state.name = "home"
    state.trigger = "${AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED}:${AccessibilityEvent.TYPE_VIEW_CLICKED}"

    var task: Task
    task = Task()
    task.next = "second"
    task.actionText = "进行中"
    task.actionTextType = 0
    task.action = "click"
    state.tasks.add(task)

    laborer.states.add(state)

    //------------state 2
    state = State()
    state.name = "second"
    state.trigger = "${AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED}:${AccessibilityEvent.TYPE_VIEW_CLICKED}"

    task = Task()
    task.next = "home"
    task.action = "back"
    state.tasks.add(task)

    laborer.states.add(state)


    return laborer
}


fun buildSuningHomeLaborer(service: AccessibilityService):Laborer {
    val laborer = StateLaborer(service)
    laborer.pkgName = "com.suning.mobile.ebuy"
    laborer.className = "com.suning.mobile.ebuy.host.MainActivity"
    laborer.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_VIEW_CLICKED
    laborer.initStateName = "home"
    laborer.handleDelay = 3000
    laborer.text = "首页"
    laborer.textType = 0

    var state: State
    state = State()
    state.name = "home"
    state.trigger = "${AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED}:${AccessibilityEvent.TYPE_VIEW_CLICKED}"

    var task: Task
    task = Task()
    task.next = "second"
    task.actionText = "苏宁超市"
    task.actionTextType = 0
    task.actionDelay = 3000
    task.action = "click"
    state.tasks.add(task)

    laborer.states.add(state)

    //------------state 2
    state = State()
    state.name = "second"
    state.trigger = "${AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED}:${AccessibilityEvent.TYPE_VIEW_CLICKED}"

    task = Task()
    task.next = "home"
    task.action = "back"
    task.actionDelay = 1500
    state.tasks.add(task)

    laborer.states.add(state)


    return laborer
}