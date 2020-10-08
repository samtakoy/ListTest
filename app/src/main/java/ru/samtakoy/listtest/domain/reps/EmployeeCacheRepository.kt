package ru.samtakoy.listtest.domain.reps


import ru.samtakoy.listtest.domain.model.Employee
import kotlinx.coroutines.flow.Flow

interface EmployeeCacheRepository {

    fun getEmployees(): Flow<List<Employee>>
    fun getEmployeesCount(): Flow<Int>
    suspend fun addData(employees: List<Employee>)
    suspend fun clearEmployees()
}