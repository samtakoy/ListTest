package ru.samtakoy.listtest.domain.reps


import kotlinx.coroutines.flow.Flow
import ru.samtakoy.listtest.domain.model.Employee

interface EmployeeCacheRepository {

    fun getEmployees(): Flow<List<Employee>>
    fun getEmployee(employeeId: Int): Flow<List<Employee>>
    fun getEmployeesCount(): Flow<Int>
    suspend fun addData(employees: List<Employee>)
    suspend fun clearEmployees()

}