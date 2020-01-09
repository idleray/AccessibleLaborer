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
//    laborer.textType = 0

    var state: State
    state = State()
    state.name = "home"
    state.triggers = builderDefaultTriggers()

    var task: Task
    task = Task()
    task.next = "second"
    task.actionText = "进行中"
//    task.actionTextType = 0
    task.action = "click"
    state.tasks.add(task)

    laborer.states.add(state)

    //------------state 2
    state = State()
    state.name = "second"
    state.triggers = builderDefaultTriggers()

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
//    laborer.textType = 0

    var state: State
    state = State()
    state.name = "home"
    state.triggers = builderDefaultTriggers()

    var task: Task
    task = Task()
    task.next = "second"
    task.actionText = "苏宁超市"
//    task.actionTextType = 0
    task.actionDelay = 3000
    task.action = "click"
    task.parentLevel = 1
    state.tasks.add(task)

    laborer.states.add(state)

    //------------state 2
    state = State()
    state.name = "second"
    state.triggers = builderDefaultTriggers()

    task = Task()
    task.next = "home"
    task.action = "back"
    task.actionDelay = 3000
    state.tasks.add(task)

    laborer.states.add(state)


    return laborer
}

fun buildJdQpsLaborer(service: AccessibilityService):Laborer {
    val laborer = StateLaborer(service)
    laborer.pkgName = "com.jingdong.app.mall"
    laborer.className = "com.jingdong.app.mall.WebActivity"
//    laborer.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
    laborer.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
    laborer.initStateName = "home"
    laborer.handleDelay = 5000
    laborer.text = "奇葩说"

    var state: State
    state = State()
    state.name = "home"
    state.triggers = builderDefaultTriggers()

    laborer.states.add(state)

    return laborer
}

fun buildJdWangLaborer(service: AccessibilityService):Laborer {
    val laborer = StateLaborer(service)
    laborer.pkgName = "com.jingdong.app.mall"
//    laborer.pkgName = "com.tencent.mm"
    laborer.className = "com.jingdong.app.mall.WebActivity"
    laborer.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
    laborer.initStateName = "home"
    laborer.handleDelay = 5000
    laborer.text = "宠汪汪"

    var state: State
    state = State()
    state.name = "home"
    state.triggers = builderDefaultTriggers()

    var task: Task
    task = Task()
    task.name="去关注店铺集合页面"
    task.next = "shops"
    task.timeLimit = true
    task.limitTextContain = "关注店铺"
    task.actionText = "去关注"
    task.actionDelay = 3000
    task.action = "click"
    task.parentLevel = 0
    state.tasks.add(task)

    //TODO:问题：有时候TYPE_WINDOW_STATE_CHANGED事件，会找不到文字"去逛逛"，是否和阻塞有关？
    task = Task()
    task.name = "去会场页面"
    task.next = "market"
    task.timeLimit = true
    task.limitTextContain = "逛逛会场"
    task.actionText = "去逛逛"
    task.actionDelay = 2000
    task.action = "click"
    task.parentLevel = 0
    state.tasks.add(task)

    task = Task()
    task.name = "去商品页面"
    task.next = "goods"
    task.timeLimit = true
    task.limitTextContain = "关注商品"
    task.actionText = "去关注"
    task.actionDelay = 2000
    task.action = "click"
    task.parentLevel = 0
    state.tasks.add(task)

    laborer.states.add(state)

    //------------state shops
    state = State()
    state.name = "shops"
//    state.nextWhenComplete = "home"
    state.triggers = builderTriggers1()
    //结束task
    task = Task()
    task.name = "从店铺集合返回"
    task.next = "home"
    task.action = "back"
    task.actionDelay = 2000
    state.completeTask = task

    task = Task()
    task.name = "关注店铺"
    task.next = "shop"
//    task.nextWhenComplete = "home"//TODO: 应该放在state里,表示此状态的任务已完成
    task.actionText = "进店并关注"
    task.action = "click"
    task.parentLevel = 0
    task.actionDelay = 2000
    state.tasks.add(task)

    laborer.states.add(state)

    //------------state shop
    state = State()
    state.name = "shop"
    state.triggers = builderTriggers1()

    task = Task()
    task.name = "返回店铺集合页面"
    task.next = "shops"
    task.action = "back"
    task.actionDelay = 7000
    state.completeTask = task

    laborer.states.add(state)

    //------------state market
    state = State()
    state.name = "market"
    //TODO: mix2s的京东，webview的url跳转不是TYPE_WINDOW_STATE_CHANGED。但后来试了又会发来，似乎是漏掉了？是否和task的阻塞运行有关
    //更新：后来发来的应该是跳转了一个新页面，而不是url跳转
    //所以对于webview的url，要改为TYPE_WINDOW_CONTENT_CHANGED，但由于TYPE_WINDOW_CONTENT_CHANGED会出现多次，
    //需要triggerType。triggerType放在state还是task呢？还需要state的status来配合
    state.triggers = builderTriggers1()

    task = Task()
    task.name = "会场页返回"
    task.next = "home"
    task.action = "back"
    task.actionDelay = 7000
    state.completeTask = task

    laborer.states.add(state)

    //------------state goods
    state = State()
    state.name = "goods"
    state.triggers = builderTriggers1()

    task = Task()
    task.name = "商品页返回"
    task.next = "home"
    task.action = "back"
    task.actionDelay = 7000
    state.completeTask = task

    laborer.states.add(state)

    return laborer
}