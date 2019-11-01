package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.util.regex.Pattern

class MyAccessibilityService: AccessibilityService() {
    var TAG = "common"
    var homeClassName = "com.taobao.browser.BrowserActivity"
    var jdHomeClassName = "com.jingdong.app.mall.WebActivity"
    var JD_PKG = "com.jingdong.app.mall"
    var TB_PKG = "com.taobao.taobao"

    var MSG_RETURN_HOME = 100
    var MSG_BROWSE = 101
    var homeNodeInfo : AccessibilityNodeInfo? = null
    var state = STATE.IDLE
        set(value) {
            Log.d(TAG, "set state: $value")
            field = value
        }
    lateinit var handler: Handler
    var currentPkg = ""

    public override fun onServiceConnected() {
        super.onServiceConnected()

        homeNodeInfo = null
        state = STATE.IDLE

        handler = object: Handler() {
            override fun handleMessage(msg: Message?) {
                when(msg?.what) {
                    MSG_RETURN_HOME -> {
                        back()
                        state = STATE.RETURN_HOME
                    }
                    MSG_BROWSE -> {
                        if(!goShop()) {
                            goBrowse()
                        }
                    }
                }
            }
        }
    }

    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {

        Log.d("aa", event.toString())
        Log.d(TAG , rootInActiveWindow?.toString() ?: "no root")
        Log.d(TAG, "current state: $state")
        //"com.uc.webkit.ay"
//        if(event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
//            return
//        }
//        if(event.className != "android.webkit.WebView") {
//            return
//        }


        val node = event.source
        if(node == null) {
            Log.e(TAG, "event.source is null")
            return
        }
        printSources(node, 0)

//        if(event.packageName == JD_PKG) {
//            onJdEvent(event)
//            return
//        }
        if(currentPkg != event.packageName) {
            state = STATE.IDLE
            currentPkg = event.packageName.toString()
        }

        when(event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> onWindowChanged(event)
            AccessibilityEvent.TYPE_VIEW_CLICKED -> onViewClick(event)
        }

//        when(state) {
//            STATE.HOME -> getMiaobi(event)
//            STATE.BROWSE -> browseShop(event)
//            STATE.RETURN_HOME -> getMiaobiAgain(event)
//        }
    }

    fun back() {
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    fun onWindowChanged(event: AccessibilityEvent) {
        Log.d(TAG, "onWindowChange")

        when (state) {
            STATE.IDLE -> {
                if(event.className == homeClassName ||
                    event.className == jdHomeClassName) {
                    Log.d(TAG, "set home node")
                    state = STATE.HOME
                }
            }
            STATE.BROWSE -> {
                handleShopState(event)
            }
            STATE.RETURN_HOME -> {
                handleReturnHome(event)

            }
        }
    }

    fun handleShopState(event: AccessibilityEvent) {
        Log.d(TAG, "handleShopState")
        when(event.packageName) {
            TB_PKG -> {
                browseShop(event, 23000)
            }
            JD_PKG -> {
                browseShop(event, 3000)
            }
        }

    }

    fun handleReturnHome(event: AccessibilityEvent) {
        Log.d(TAG, "handleReturnHome")
        when(event.packageName) {
            TB_PKG -> {
                getMiaobiAgain(event)
            }
            JD_PKG -> {
                jdAgain(event)
            }
        }
    }

    fun onViewClick(event: AccessibilityEvent) {
        Log.d(TAG, "onViewClick")
        if(state == STATE.HOME) {
            startLabor(event)
        }

    }

    fun startLabor(event: AccessibilityEvent) {
        Log.d(TAG, "startLabor")
        when(event.packageName) {
            TB_PKG -> {
                handler.postDelayed({
                    getMiaobi()
                }, 1000)
            }
            JD_PKG -> {
                handler.postDelayed({
                    jdTask()
                }, 1500)
            }
        }
    }

    fun jdTask() {
        Log.d(TAG, "jdTask")
        var node = findNodeByContentDescription(rootInActiveWindow, "逛逛好店（")
        var ret = doJdTask(node)
        if(!ret) {
            node = findNodeByContentDescription(rootInActiveWindow, "精彩会场（")
            ret = doJdTask(node)
        }

        if(!ret) {
            node = findNodeByContentDescription(rootInActiveWindow, "精选好物（")
            ret = doJdTask(node)
        }
        if(!ret) {
            node = findNodeByContentDescription(rootInActiveWindow, "更多好玩互动（")
            ret = doJdTask(node)
        }
        if(!ret) {
            node = findNodeByContentDescription(rootInActiveWindow, "看京品推荐官直播/视频")
            ret = doJdTask(node)
        }
    }

    fun doJdTask(node: AccessibilityNodeInfo?): Boolean {
        Log.d(TAG, "doJdTask")
        var ret = false
        if(node == null) {
            return false
        }

        val text = node.contentDescription
        val pos = text?.indexOf("/") ?: -1
        if(pos != -1) {
            val remainP = Pattern.compile("\\d+(?=/)")
            val m1 = remainP.matcher(text)
            var remainCount = -1
            if(m1.find()) {
                remainCount = m1.group().toInt()
            }

            val tp = Pattern.compile("(?<=/)\\d+")
            val m2 = tp.matcher(text)
            var totalCount = -1
            if(m2.find()) {
                totalCount = m2.group().toInt()
            }
            if(remainCount != -1 && totalCount != -1 && remainCount < totalCount) {
                Log.d(TAG, "remain: $remainCount, total: $totalCount")
                ret = browseJd(node)
            }
        }

        return ret
    }

    fun jdAgain(event: AccessibilityEvent) {

        handler.postDelayed({
            val node = findNodeByContentDescription(rootInActiveWindow, "朕知道了")
            node?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            jdTask()
        }, 1500)
    }

    fun browseJd(node: AccessibilityNodeInfo): Boolean {
        Log.d(TAG, "browseJd")
        val parent = node.parent
        val ret = parent?.performAction(AccessibilityNodeInfo.ACTION_CLICK) ?: false
        if(ret) {
            Log.d(TAG, "set state shop")
            state = STATE.BROWSE
        }

        return ret

    }

    private fun browseShop(event: AccessibilityEvent, delay: Long) {
        if(event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }
        Log.d(TAG, "browseShop")
        //不需要滚动也行
//        rootInActiveWindow?.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
        handler.sendEmptyMessageDelayed(MSG_RETURN_HOME, delay)

    }

    private fun getMiaobi() {
//        if(event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
//            && event.eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) {
//            return
//        }

//        if(event.eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) {
//            return
//        }

//        signIn()
        if(!goShop()) {
            goBrowse()
        }

    }

    private fun getMiaobiAgain(event: AccessibilityEvent) {
        Log.d(TAG, "getMiaobiAgain ")
        if(event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        Log.d(TAG, "getMiaobiAgain in")
        handler.sendEmptyMessageDelayed(MSG_BROWSE, 1500)

    }

    private fun goShop(): Boolean {
        Log.d(TAG, "go shop")
        val node = rootInActiveWindow ?: return false
//        val node = event.source

        val shopNode = findNodeByText(node,"去进店")
        val performed = shopNode?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        if(performed != null && performed) {
            Log.d(TAG, "set state : shop")
            state = STATE.BROWSE
        }

        return shopNode != null
    }

    private fun goBrowse(): Boolean {
        Log.d(TAG, "goBrowse")
        val node = rootInActiveWindow ?: return false
//        val node = event.source

        val shopNode = findNodeByText(node,"去浏览")
        val performed = shopNode?.performAction(AccessibilityNodeInfo.ACTION_CLICK)

        if(performed != null && performed) {
            Log.d(TAG, "set state : shop(browse)")
            state = STATE.BROWSE
        }

        return shopNode != null
    }

    //findAccessibilityNodeInfosByText找不到，不知道原因
    private fun findNodeByText(node: AccessibilityNodeInfo?, text: String): AccessibilityNodeInfo?{
        return findNode(node, text, 0)
    }

    private fun findNodeByContentDescription(node: AccessibilityNodeInfo?, text: String): AccessibilityNodeInfo?{
        return findNode(node, text, 1)
    }

    private fun findNode(node: AccessibilityNodeInfo?, text: String, type: Int): AccessibilityNodeInfo?{
        Log.d(TAG, "findNode: $text")
//        printSources(node!!, 0)
        var retNode: AccessibilityNodeInfo? = null
        var nodeText : String? = null
        if(type == 0) {
            nodeText = node?.text?.toString()
        } else if (type == 1) {
            nodeText = node?.contentDescription?.toString()
        }
        Log.d(TAG, "text: $nodeText")
        if(nodeText?.contains(text) == true) {
            retNode = node
            return retNode
        }
        if(node?.childCount == 0) {
            return retNode
        }
        for(index in 0 until (node?.childCount ?: 0)) {
            val child = node?.getChild(index)
            if(child != null) {
                val aNode = findNode(child, text, type)
                if(aNode != null) {
                    retNode = aNode
                    break
                }
                aNode?.recycle()
            }
        }

        node?.recycle()

        return retNode
    }

    private fun printSources(source: AccessibilityNodeInfo, depth: Int) {
        val tag = "depth-$depth"
        Log.d(tag, source.toString())
        for(index in 0 until source.childCount) {
            val dp = depth + 1
            val child = source.getChild(index)
            if(child == null) {
                Log.e("err------", "WTFFFFFFFFFFFF")
                break
            }
            printSources(child, dp)
        }

        source.recycle()


    }
}