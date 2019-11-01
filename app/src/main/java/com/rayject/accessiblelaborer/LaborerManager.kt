package com.rayject.accessiblelaborer

import android.accessibilityservice.AccessibilityService
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

object LaborerManager {
    private val laborers = mutableListOf<Laborer?>()

    fun init(service: AccessibilityService) {
        if(laborers.isEmpty()) {
            createLaborers(service)

        }

    }

    fun destroy() {
        laborers.clear()
    }

    private fun createLaborers(service: AccessibilityService) {
        val LaborerClasses = listOf<KClass<out Laborer>>(
            TaobaoLaborer::class,
            SuningLaborer::class
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
            it?.getPackageName() == packageName
        }
    }

    fun registerLabor(laborer: Laborer) {
        laborers.add(laborer)

    }

    fun removeLabor(laborer: Laborer) {
        laborers.remove(laborer)
    }
}