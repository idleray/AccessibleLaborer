package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

fun buildSuningLaborer(service: AccessibilityService):Laborer {
    val laborer = StateLaborer(service)
    laborer.pkgName = "com.suning.mobile.ebuy"
//    laborer.pkgName = "com.tencent.mm"
    laborer.className = "com.suning.mobile.ucwv.ui.WebViewActivity"
    laborer.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_VIEW_CLICKED// or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
    laborer.initStateName = "home"
    laborer.handleDelay = 10000
    laborer.text = "天天发现鲸"

    var state: State
    state = State()
    state.name = "home"
    state.trigger = "${AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED}:${AccessibilityEvent.TYPE_VIEW_CLICKED}"

    var task: Task
    task = Task()
    task.name="逛互动城店铺"
    task.next = "shop"
    task.timeLimit = false
//    task.limitTextContain = "关注店铺"
    task.actionText = "+6000"
    task.actionDelay = 2000
    task.action = "click"
    task.parentLevel = 0
    state.tasks.add(task)

    task = Task()
    task.name = "去逛逛的任务"
    task.next = "guangguang"
    task.timeLimit = true
    task.limitTextContain = "逛腊八主会场"
    task.actionText = "去逛逛"
    task.actionDelay = 2000
    task.action = "click"
    task.parentLevel = 0
    state.tasks.add(task)

    laborer.states.add(state)

    task = Task()
    task.name = "看榴莲视频的任务"
    task.next = "guangguang"
    task.timeLimit = true
    task.limitTextContain = "看榴莲视频"
    task.actionText = "去看看"
    task.actionDelay = 2000
    task.action = "click"
    task.parentLevel = 0
    state.tasks.add(task)

    laborer.states.add(state)

    task = Task()
    task.name = "去逛逛的任务"
    task.next = "guangguang"
    task.timeLimit = true
    task.limitTextContain = "逛腊八主会场"
    task.actionText = "去逛逛"
    task.actionDelay = 2000
    task.action = "click"
    task.parentLevel = 0
    state.tasks.add(task)

    laborer.states.add(state)

    state = State()
    state.name = "shop"
    state.trigger = "${AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED}"

    task = Task()
    task.name = "返回任务中心"
    task.next = "home"
    task.action = "back"
    task.actionDelay = 15000
    state.completeTask = task

    laborer.states.add(state)

    state = State()
    state.name = "guangguang"
    state.trigger = "${AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED}"

    task = Task()
    task.name = "返回任务中心"
    task.next = "home"
    task.action = "back"
    task.actionDelay = 15000
    state.completeTask = task

    laborer.states.add(state)

    return laborer
}