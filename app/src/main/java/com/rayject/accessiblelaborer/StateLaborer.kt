package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class Task {
    var next = ""
    var timeLimit = false
    var limitTextContain: String? = null
    var limitTextType = 0
    var actionText: String? = null
    var actionTextType = 0
    var action: String? = null
    var actionDelay: Long = 0
    var completed = false

    fun run(): Boolean {
        logd("run task: $action")
        //TODO: 根据actionType来执行不同流程
        return when(action) {
            "click" -> runClick()
            "back" -> runBack()
            else -> true
        }

    }

    fun runClick(): Boolean {
        var ret = true
        var hasRemain = true
        if(timeLimit && !TextUtils.isEmpty(limitTextContain)) {
            val node = findNodeByWhatEver(LaborerManager.service?.rootInActiveWindow, limitTextContain!!)
            if(node != null) {
                val text = if(limitTextType == 0) node.text else node.contentDescription
                hasRemain = hasTaskRemain(text.toString())
            }
        }

        if(hasRemain) {
//            printCurrentNodes(LaborerManager.service!!)
            val actionNode = findNodeByWhatEver(LaborerManager.service?.rootInActiveWindow, actionText!!)
            if(actionNode != null) {
                logd("delay to click node")
                printSources(actionNode, 0)

                //TODO: 需要改成阻塞模式，返回结果
                runBlockingDelay(actionDelay) {
                    //TODO: 根据action来处理，目前认为只处理click
                    logd("click $actionText")
                    actionNode.parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }
            } else {
                //找不到node，认为已完成
                completed = true
            }
        } else {
            completed = true
        }

        return ret
    }

    fun runBack(): Boolean {
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
    var textType = 0
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
        return findNode(node, text, textType) != null
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
        when(event.eventType) {
            AccessibilityEvent.TYPE_VIEW_CLICKED -> printEvent(service, event)
        }
        handleEventByType(event.eventType)
    }

    private fun canTrigger(eventType: Int): Boolean {
        return currentState?.triggers?.contains(eventType) ?: false
    }

    private fun runTask() {
        val nextState = currentState?.runTask()
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