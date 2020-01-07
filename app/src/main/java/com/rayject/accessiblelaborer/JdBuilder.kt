package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

fun buildJdLaborer(service: AccessibilityService):Laborer {
    val laborer = StateLaborer(service)
    laborer.pkgName = "com.jingdong.app.mall"
//    laborer.pkgName = "com.tencent.mm"
    laborer.className = "com.jingdong.app.mall.WebActivity"
    laborer.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
    laborer.initStateName = "home"
    laborer.handleDelay = if(BuildConfig.DEBUG) 20000 else 10000
    laborer.text = "全民炸年兽"

    var state: State
    state = State()
    state.name = "home"
    state.trigger = "${AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED}:${AccessibilityEvent.TYPE_VIEW_CLICKED}"

    var task: Task
    task = Task()
    task.name="去浏览任务"
    task.next = "shop"
    task.timeLimit = true
    task.limitTextContain = "逛逛好店"
    task.actionText = "去完成"
    task.actionDelay = 2000
    task.action = "click"
    task.parentLevel = 0
    state.tasks.add(task)

    task = Task()
    task.name="去浏览任务"
    task.next = "shop"
    task.timeLimit = true
    task.limitTextContain = "逛逛热卖会场"
    task.actionText = "去完成"
    task.actionDelay = 2000
    task.action = "click"
    task.parentLevel = 0
    state.tasks.add(task)

    task = Task()
    task.name="去浏览任务"
    task.next = "shop"
    task.timeLimit = true
    task.limitTextContain = "看京东推荐官直播"
    task.actionText = "去完成"
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
    task.actionDelay = 2000
    state.completeTask = task

    laborer.states.add(state)

    return laborer
}