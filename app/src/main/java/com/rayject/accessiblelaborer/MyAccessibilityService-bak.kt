package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

//enum class STATE {
//    IDLE,
//    HOME,
//    RETURN_HOME,
//    SHOP
//}

class MyAccessibilityServiceBak: AccessibilityService() {
    var TAG = "common"
    var homeClassName = "com.taobao.browser.BrowserActivity"
    var JD_PKG = "com.jingdong.app.mall"
    var TB_PKG = "com.taobao.taobao"

    var MSG_RETURN_HOME = 100
    var MSG_BROWSE = 101
    var homeNodeInfo : AccessibilityNodeInfo? = null
    var state = STATE.IDLE
    lateinit var handler: Handler

    public override fun onServiceConnected() {
        super.onServiceConnected()
        homeNodeInfo = null
        handler = object: Handler() {
            override fun handleMessage(msg: Message?) {
                when(msg?.what) {
                    MSG_RETURN_HOME -> {
                        performGlobalAction(GLOBAL_ACTION_BACK)
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

        when(event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> onWindowChanged(event)
        }

        when(state) {
            STATE.HOME -> getMiaobi(event)
            STATE.SHOP -> browseShop(event)
            STATE.RETURN_HOME -> getMiaobiAgain(event)
        }
    }

    fun onWindowChanged(event: AccessibilityEvent) {
        Log.d(TAG, "onWindowChange")
        if(homeNodeInfo == null) {
            if(event.className == homeClassName) {
                Log.d(TAG, "set home node")
                //首次进入主页
                state = STATE.HOME
                homeNodeInfo = rootInActiveWindow
            }
        } else {
            //返回主页
            Log.d(TAG, "homeNodeInfo.hashCode: ${Integer.toHexString(homeNodeInfo?.hashCode() ?: 0)}")
            Log.d(TAG, "rootInActiveWindow.hashCode: ${Integer.toHexString(rootInActiveWindow?.hashCode() ?: 0)}")
//            if(event.className == homeClassName && homeNodeInfo?.hashCode() == rootInActiveWindow?.hashCode()) {
            if(event.className == homeClassName) {
                Log.d(TAG, "return home node")
                state = STATE.RETURN_HOME
            }
            //TODO: homeNodeInfo的hashCode是否会变?
            //真的会变。。。
        }
    }

    private fun browseShop(event: AccessibilityEvent) {
        if(event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }
        Log.d(TAG, "browseShop")
        //不需要滚动也行
//        rootInActiveWindow?.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
        handler.sendEmptyMessageDelayed(MSG_RETURN_HOME, 23000)

    }

    private fun getMiaobi(event: AccessibilityEvent) {
//        if(event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
//            && event.eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) {
//            return
//        }

        if(event.eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            return
        }

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

    private fun signIn() {
    }

    private fun goShop(): Boolean {
        Log.d(TAG, "go shop")
        val node = rootInActiveWindow ?: return false
//        val node = event.source

        val shopNode = findNodeByText(node,"去进店")
        val performed = shopNode?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        if(performed != null && performed) {
            Log.d(TAG, "set state : shop")
            state = STATE.SHOP
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
            state = STATE.SHOP
        }

        return shopNode != null
    }

    //findAccessibilityNodeInfosByText找不到，不知道原因
    private fun findNodeByText(node: AccessibilityNodeInfo, text: String): AccessibilityNodeInfo?{
        var retNode: AccessibilityNodeInfo? = null
        if(node.text == text) {
            retNode = node
            return retNode
        }
        if(node.childCount == 0) {
            return retNode
        }
        for(index in 0 until node.childCount) {
            val child = node.getChild(index)
            if(child != null) {
                val aNode = findNodeByText(child, text)
                if(aNode != null) {
                    retNode = aNode
                    break
                }
                aNode?.recycle()
            }
        }

        node.recycle()

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