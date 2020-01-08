package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class Task {
    var name =""
    var next = ""
//    var nextWhenComplete = ""
    var timeLimit = false
    var limitTextContain: String? = null
//    var limitTextType = 0
    var actionText: String? = null
//    var actionTextType = 0
    var action: String? = null
    var actionDelay: Long = 0
    var parentLevel = 0
    var completed = false

    fun run(): Boolean {
        logd("--------- run task: ${name}, action = $action")
        val ret  = when(action) {
            "click" -> runClick()
            "back" -> runBack()
            else -> true
        }

        logd("--------- run task end------")
        return ret
    }

    fun runClick(): Boolean {
        var ret = false
        runBlockingDelay(actionDelay) {
            logd("after ${actionDelay}ms, start to run click")
//            printCurrentNodes(LaborerManager.service!!)
            var hasRemain = true
            if(timeLimit && !TextUtils.isEmpty(limitTextContain)) {
                val node = findNodeByWhatEver(LaborerManager.service?.rootInActiveWindow, limitTextContain!!)
                if(node != null) {
                    var text = node.text
                    if(TextUtils.isEmpty(text)) {
                        text = node.contentDescription
                    }
                    hasRemain = hasTaskRemain(text.toString())
                }
            }

            if(hasRemain) {
//            printCurrentNodes(LaborerManager.service!!)
                var actionNode = findNodeByWhatEver(LaborerManager.service?.rootInActiveWindow, actionText!!)
                if(actionNode != null) {
                    logd("click $actionText")
                    for( i in 0 until parentLevel ) {
                        actionNode = actionNode?.parent
                    }
//                    if(actionNode != null) {
//                        printSources(actionNode, 0)
//                    }
                    ret = actionNode?.performAction(AccessibilityNodeInfo.ACTION_CLICK) ?: false
                } else {
                    logd("can't find $actionText")
                    //目前在没有数量限制的时候，找不到node，认为已完成
                    //TODO: 需要确定如何才算完成，以及是否需要移除task
                    if(!timeLimit) {
                        completed = true
                    }

                }
            } else {
                completed = true
            }
        }

        logd("runClick end. completed: $completed")
        return ret
    }

    fun runBack(): Boolean {
        logd("delay ${actionDelay}ms to back")
        runBlockingDelay(actionDelay) {
            logd("now back")
            LaborerManager.service?.back()
        }
        return true
    }
}

class State {
    var name = ""
//    var nextWhenComplete = ""
    var status = 0 //判断是否已进入此状态，配合triggerType用，比如triggerType是1时判断后不用重复调用task,主要是TYPE_WINDOW_CONTENT_CHANGED事件会发生多次。 0: 未进入， 1：已进入
    var triggers: List<Int>? = null
    var trigger = ""
        set(value) {
            field = value
            triggers = value.split(":").map {
                it.toInt()
            }
        }
    var triggerType = 0 // 0: 不限制次数 1: 仅一次
    var tasks: MutableList<Task> = mutableListOf()
//    var completedTasks: MutableList<Task> = mutableListOf()
    var completeTask: Task? = null

    fun enter() {
        status = 1

    }

    fun exit() {
        status = 0
    }

    fun runTask(): String? {
        logd("runTask")
        var nextState: String? = null
        for( task in tasks) {
            logd("task[${task.name}] completed: ${task.completed}")
            if(task.completed) {
                continue
            }
            val ret = task.run()
            if(ret) {
                nextState = task.next
                break
            }
        }
        //认为所有task已完成，转移到另一个state
        if(nextState == null) {
            logd("complete to state: $nextState")
            val ret = completeTask?.run() ?: false
            if(ret) {
                nextState = completeTask?.next
            }
//            nextState = nextWhenComplete
        }

        return nextState
    }

    private fun firstTask(): Task? {
        return tasks.find {
            !it.completed
        }
    }

//    private fun removeTask(task: Task) {
//        logd("removeTask: ")
//        tasks.remove(task)
//        completedTasks.add(task)
//
//    }
}

class StateLaborer(override val service: AccessibilityService): Laborer{
    var pkgName = ""
    var className = ""
    var eventTypes = AccessibilityEvent.TYPES_ALL_MASK
    var initStateName = ""
    var handleDelay = 0L
    var text = ""
//    var textType = 0
    var states: MutableList<State> = mutableListOf()

    var active = true
    var currentState: State? = null

    override fun init() {
        logd("init --> $text")
        logd("eventTypes: " + AccessibilityEvent.eventTypeToString(eventTypes))
        val info = service.serviceInfo
        info.eventTypes = if(eventTypes > 0) eventTypes else AccessibilityEvent.TYPES_ALL_MASK
        service.serviceInfo = info

        transitionState(initStateName)
    }

    override fun start() {
        if(canTrigger(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)) {
            runTask()
        }
    }

    override fun finish() {
    }

    override fun getName(): String {
        return text
    }

    override fun getPackageName(): String {
        return pkgName
    }

    override fun getHomeClassName(): String {
        return className
    }

    override fun canHandleCurrentNode(): Boolean {
        val node = service.rootInActiveWindow ?: return false
//        printCurrentNodes(service)
        return findNodeByWhatEver(node, text) != null
    }

    override fun isActive(): Boolean {
        return active
    }

    override fun handleDelayMillis(): Long{
        return handleDelay
    }

    override fun handleEventByType(eventType: Int) {
        if(canTrigger(eventType)) {
            runTask()
        }
    }

    override fun handleEvent(event: AccessibilityEvent) {
//        when(event.eventType) {
//            AccessibilityEvent.TYPE_VIEW_CLICKED -> printEvent(service, event)
//            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> printEvent(service, event)
//        }
//        printCurrentNodes(service)
        handleEventByType(event.eventType)
    }

    private fun canTrigger(eventType: Int): Boolean {
        return currentState?.triggers?.contains(eventType) ?: false
    }

    private fun runTask() {
        val nextState = currentState?.runTask()
        //TODO：从这里转移状态不对，因为有些是延迟task，需要task真正执行完才能转移状态。所以。。。上状态机？
        //TODO: 目前是阻塞模式，状态是正确的
        if(!TextUtils.isEmpty(nextState)) {
            transitionState(nextState)
        }
    }

    private fun transitionState(statName: String?) {
        logd("transition to: $statName")
        if(TextUtils.isEmpty(statName)) {
            logd("don't transition state")
            return
        }
        currentState = states?.find {
            it.name == statName
        }
    }
}