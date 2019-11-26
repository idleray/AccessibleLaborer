package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.view.accessibility.AccessibilityEvent
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

@SuppressLint("StaticFieldLeak")
object LaborerManager {
    var service: AccessibilityService? = null
    val laborers = mutableListOf<Laborer>()

    fun init(service: AccessibilityService) {
        this.service = service
        if(laborers.isEmpty()) {
//            createLaborers(service)
            buildLaborers(service)

        }

    }

    fun destroy() {
        service = null
        laborers.clear()
    }

    private fun buildLaborers(service: AccessibilityService) {
        laborers.add(buildSampleLaborer(service))
        laborers.add(buildSuningHomeLaborer(service))

    }

    private fun createLaborers(service: AccessibilityService) {
        var LaborerClasses = listOf<KClass<out Laborer>>(
//            SelfLaborer::class,
            TaobaoLaborer::class,
            JdLaborer::class,
            SuningLaborer::class,
            WechatLaborer::class
        )

        for(cls in LaborerClasses) {
            val laborer = cls.primaryConstructor?.call(service)
            if(laborer != null) {
                laborers.add(laborer)
            }

        }
    }

    fun getLaborerByPkgName(packageName: CharSequence): Laborer? {
        return laborers.find {
            it.getPackageName() == packageName
        }
    }

    fun chooseLaborersByEvent(event: AccessibilityEvent): List<Laborer>? {
        return laborers?.filter {
            if(event.packageName.equals(it.getPackageName())
                //TODO: 是否需要限制到className
                && event.className.equals(it.getHomeClassName())) {
                if(it.handleDelayMillis() != 0L) {
                    //需要延迟确认时，先认为可以处理
                    true
                } else {
                    it.canHandleCurrentNode()
                }
            } else {
                false
            }
        }
    }

    fun registerLabor(laborer: Laborer) {
        laborers.add(laborer)

    }

    fun removeLabor(laborer: Laborer) {
        laborers.remove(laborer)
    }
}