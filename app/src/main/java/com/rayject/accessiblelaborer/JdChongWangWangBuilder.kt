package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

fun buildJdCwwLaborer(service: AccessibilityService):Laborer {
    val laborer = StateLaborer(service)
    laborer.pkgName = "com.jingdong.app.mall"
//    laborer.pkgName = "com.tencent.mm"
    laborer.className = "com.jingdong.app.mall.WebActivity"
    laborer.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
    laborer.initStateName = "home"
    laborer.handleDelay = if(BuildConfig.DEBUG) 10000 else 10000
    laborer.text = "宠汪汪"

    var state: State
    state = State()
    state.name = "home"
    state.triggers = builderDefaultTriggers()

    var task: Task
//    task = Task()
//    task.name="关注店铺任务"
//    task.next = "shops"
//    task.timeLimit = true
//    task.limitTextContain = "关注店铺"
//    task.actionText = "去关注"
//    task.actionDelay = 2000
//    task.action = "click"
//    task.parentLevel = 1
//    state.tasks.add(task)

    task = Task()
    task.name="关注商品任务"
    task.next = "browse"
    task.timeLimit = true
    task.limitTextContain = "关注商品"
    task.actionSiblingIndex = 3
//    task.actionText = "去关注"
    task.actionDelay = 2000
    task.action = "click"
    task.parentLevel = 0
    state.tasks.add(task)
//
//    task = Task()
//    task.name="去浏览任务"
//    task.next = "shop"
//    task.timeLimit = true
//    task.limitTextContain = "看京东推荐官直播"
//    task.actionText = "去完成"
//    task.actionDelay = 2000
//    task.action = "click"
//    task.parentLevel = 0
//    state.tasks.add(task)


    laborer.states.add(state)

    state = State()
    state.name = "shops"
    state.triggers = builderTriggers2()

    task = Task()
    task.name="进店并关注"
    task.next = "shop"
    task.timeLimit = true
//    task.limitTextContain = "关注商品"
    task.actionText = "进店并关注"
    task.actionDelay = 3000
    task.action = "click"
    task.parentLevel = 0
    state.tasks.add(task)

    //结束task
    task = Task()
    task.name = "从店铺集合返回"
    task.next = "home"
    task.action = "back"
    task.actionDelay = 2000
    state.completeTask = task


    state = State()
    state.name = "shop"
    state.triggers = builderTriggers1()

    task = Task()
    task.name = "返回任务中心"
    task.next = "shops"
    task.action = "back"
    task.actionDelay = 10000
    state.completeTask = task

    laborer.states.add(state)

    state = State()
    state.name = "browse"
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