package ru.samtakoy.listtest.domain.model.cache

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.samtakoy.listtest.domain.model.Employee

interface CacheModel {

    fun checkForInitialization()
    fun retrieveMoreEmployees()

    fun observeNetworkBusyStatus(): StateFlow<Boolean>
    fun observeErrors(): Flow<CacheError>
    fun observeEmployees(): Flow<List<Employee>>

    fun invalidateDbCache()
    fun clearDbCache(): Deferred<Boolean>

}