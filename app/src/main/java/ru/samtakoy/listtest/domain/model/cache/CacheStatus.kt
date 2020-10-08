package ru.samtakoy.listtest.domain.model.cache

enum class CacheStatus(
    val isNetworkBusy: Boolean
) {

    NOT_INITIALIZED(false),
    DATA_RETRIEVING(true),
    UNCOMPLETED(false),
    SYNCHRONIZED(false)

}