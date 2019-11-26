package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class Task {
    //TODO：2个next的状态转移使用状态机
    var next = ""
    var nextWhenComplete = ""
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
        logd("run task: $action")
        return when(action) {
            "click" -> runClick()
            "back" -> runBack()
            else -> true
        }

    }

    fun runClick(): Boolean {
        var ret = false
        runBlockingDelay(actionDelay) {
            logd("after ${actionDelay}ms, start to run click")
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
//                printSources(actionNode, 0)
                    logd("click $actionText")
                    for( i in 0 until parentLevel ) {
                        actionNode = actionNode?.parent
                    }
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

        logd("runClick end")
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
    var triggers: List<Int>? = null
    var trigger = ""
        set(value) {
            field = value
            triggers = value.split(":").map {
                it.toInt()
            }
        }
    var tasks: MutableList<Task> = mutableListOf()
    var completedTasks: MutableList<Task> = mutableListOf()

    fun runTask(): String? {
        val task = firstTask()
        if(task != null) {
            val ret = task.run()
            if(task.completed) {
                removeTask(task)
                return task.nextWhenComplete
            }
            return if(ret) {
                task.next
            } else {
                null
            }
        } else {
            return null
        }
    }

    private fun firstTask(): Task? {
        return if(tasks.isNotEmpty()) {
            tasks[0]
        } else {
            null
        }
    }

    private fun removeTask(task: Task) {
        tasks.remove(task)
        completedTasks.add(task)

    }
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
        val info = service.serviceInfo
        info.eventTypes = eventTypes
        service.serviceInfo = info

        currentState = states.find {
            it.name == initStateName
        }
    }

    override fun start() {
        if(canTrigger(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)) {
            runTask()
        }
    }

    override fun finish() {
    }

    override fun getPackageName(): String {
        return pkgName
    }

    override fun getHomeClassName(): String {
        return className
    }

    override fun canHandleCurrentNode(): Boolean {
        val node = service.rootInActiveWindow ?: return false
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
//        }
        handleEventByType(event.eventType)
    }

    private fun canTrigger(eventType: Int): Boolean {
        return currentState?.triggers?.contains(eventType) ?: false
    }

    private fun runTask() {
        val nextState = currentState?.runTask()
        //TODO：从这里转移状态不对，因为有些是延迟task，需要task真正执行完才能转移状态。所以。。。上状态机？
        if(!TextUtils.isEmpty(nextState)) {
            transferState(nextState)
        }
    }

    private fun transferState(statName: String?) {
        currentState = states?.find {
            it.name == statName
        }
    }
}