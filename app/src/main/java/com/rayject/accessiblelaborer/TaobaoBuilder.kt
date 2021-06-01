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
    laborer.handleDelay = 7000
    laborer.text = "天猫618超级星秀猫"

    var state: State
    state = State()
    state.name = "home"
    state.triggers = builderDefaultTriggers()

    var task: Task

    task = Task()
    task.name="去浏览店铺领喵币任务"
    task.next = "shopList"
    task.timeLimit = true
//    task.anchorText = "去浏览店铺领喵币"
//    task.anchorParentLevel = 2
    task.limitTextContain = "去浏览店铺领喵币"
    task.timeLimit = true
    task.actionText = "去浏览店铺领喵币"
//    task.actionTextFuzzy = false
    task.actionDelay = 3000
    task.action = "click"
    task.parentLevel = 0
    state.tasks.add(task)

    task = Task()
    task.name="去浏览任务"
    task.next = "shop"
    task.timeLimit = true
//    task.limitTextContain = "关注店铺"
    task.actionText = "去浏览"
    task.actionTextFuzzy = false
    task.actionDelay = 3000
    task.action = "click"
    task.parentLevel = 0
    state.tasks.add(task)


    //任务
    task = Task()
    task.name = "去完成任务"
    task.next = "search"
    task.timeLimit = true
//    task.limitTextContain = "逛逛会场"
    task.actionText = "去完成"
    task.actionDelay = 3000
    task.action = "click"
    task.parentLevel = 0
    state.tasks.add(task)

    laborer.states.add(state)

    //state shopList
    state = State()
    state.name = "shopList"
    state.triggers = builderTriggers1()

    task = Task()
    task.name="浏览店铺任务"
    task.next = "shopFromList"
    task.timeLimit = true
    task.limitTextContain = "逛满 10 家最多额外可得30000喵币"
    task.actionText = "逛店最多"
    task.actionDelay = 3000
    task.action = "click"
    task.parentLevel = 0
    state.tasks.add(task)

    laborer.states.add(state)

    //从shopList进入的浏览店铺任务
    state = State()
    state.name = "shopFromList"
    state.triggers = builderTriggers1()

    task = Task()
    task.name = "返回店铺列表"
    task.next = "shopList"
    task.action = "back"
    task.actionDelay = 24000
    state.completeTask = task

    laborer.states.add(state)

    //state shop
    state = State()
    state.name = "shop"
    state.triggers = builderTriggers1()

    task = Task()
    task.name = "返回任务中心"
    task.next = "home"
    task.action = "back"
    task.actionDelay = 27000
    state.completeTask = task

    laborer.states.add(state)

    //state search
    state = State()
    state.name = "search"
    state.triggers = builderTriggers1()

    task = Task()
    task.name = "返回任务中心"
    task.next = "home"
    task.action = "back"
    task.actionDelay = 30000
    state.completeTask = task

    laborer.states.add(state)

    return laborer
}