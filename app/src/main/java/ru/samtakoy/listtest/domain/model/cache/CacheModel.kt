package ru.samtakoy.listtest.domain.model.cache

import kotlinx.coroutines.flow.Flow
import ru.samtakoy.listtest.domain.model.Employee

interface CacheModel {

    fun init()
    fun observeCacheStatus(): Flow<CacheStatus>
    fun getEmployees(): Flow<List<Employee>>
    fun retrieveMoreEmployees()
}