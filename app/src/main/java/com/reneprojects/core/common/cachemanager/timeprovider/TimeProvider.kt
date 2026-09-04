package com.reneprojects.core.common.cachemanager.timeprovider

import javax.inject.Inject

internal interface TimeProvider {
    fun currentTimeMillis(): Long
}

internal class TimeProviderImpl @Inject constructor() : TimeProvider {
    override fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

}