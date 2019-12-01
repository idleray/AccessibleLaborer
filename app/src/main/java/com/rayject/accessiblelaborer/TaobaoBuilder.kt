package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

fun buildTbLaborer(service: AccessibilityService):Laborer {
    val laborer = StateLaborer(service)
    laborer.pkgName = "com.taobao.taobao"
//    laborer.pkgName = "com.tencent.mm"
    laborer.className = "com.taobao.browser.BrowserActivity"
    laborer.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_VIEW_CLICKED// or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
    laborer.initStateName = "home"
    laborer.handleDelay = 5000
    laborer.text = "双12全民寻宝"

    var state: State
    state = State()
    state.name = "home"
    state.trigger = "${AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED}:${AccessibilityEvent.TYPE_VIEW_CLICKED}"

    var task: Task
    task = Task()
    task.name="去浏览任务"
    task.next = "shop"
    task.timeLimit = true
//    task.limitTextContain = "关注店铺"
    task.actionText = "去浏览"
    task.actionDelay = 2000
    task.action = "click"
    task.parentLevel = 0
    state.tasks.add(task)

    task = Task()
    task.name = "去搜索任务"
    task.next = "search"
    task.timeLimit = true
//    task.limitTextContain = "逛逛会场"
    task.actionText = "去搜索"
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
    task.actionDelay = 24000
    state.completeTask = task

    laborer.states.add(state)

    state = State()
    state.name = "search"
    state.trigger = "${AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED}"

    task = Task()
    task.name = "返回任务中心"
    task.next = "home"
    task.action = "back"
    task.actionDelay = 20000
    state.completeTask = task

    laborer.states.add(state)

    return laborer
}