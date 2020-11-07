package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

fun buildJdLaborer(service: AccessibilityService):Laborer {
    val laborer = StateLaborer(service)
    laborer.pkgName = "com.jingdong.app.mall"
//    laborer.pkgName = "com.tencent.mm"
    laborer.className = "com.jingdong.app.mall.WebActivity"
    laborer.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_VIEW_CLICKED// or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
    laborer.initStateName = "home"
    laborer.handleDelay = if(BuildConfig.DEBUG) 10000 else 10000
    laborer.text = "叠蛋糕"

    var state: State
    state = State()
    state.name = "home"
    state.triggers = builderDefaultTriggers()

    var task: Task
    task = Task()
    task.name="去逛xxx"
    task.next = "shop"
    task.timeLimit = true
    task.limitTextContain = "去逛"
//    task.actionSiblingIndex = 2
    task.anchorText = task.limitTextContain
    task.anchorParentLevel = 2
    task.actionText = "去完成"
    task.actionDelay = 3000
    task.action = "click"
    task.parentLevel = 0
    state.tasks.add(task)

    task = Task()
    task.name="去玩xxx"
    task.next = "shop"
    task.timeLimit = true
    task.limitTextContain = "去玩"
//    task.actionSiblingIndex = 2
    task.anchorText = task.limitTextContain
    task.anchorParentLevel = 2
    task.actionText = "去完成"
    task.actionDelay = 3000
    task.action = "click"
    task.parentLevel = 0
    state.tasks.add(task)


    laborer.states.add(state)

    state = State()
    state.name = "shop"
    state.triggers = builderTriggers1()

    task = Task()
    task.name = "返回任务中心"
    task.next = "home"
    task.action = "back"
    task.actionDelay = 10000
    state.completeTask = task

    laborer.states.add(state)

    return laborer
}