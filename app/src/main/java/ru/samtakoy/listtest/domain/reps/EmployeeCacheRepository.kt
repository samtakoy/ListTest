package ru.samtakoy.listtest.domain.reps


import ru.samtakoy.listtest.domain.model.Employee
import kotlinx.coroutines.flow.Flow

interface EmployeeCacheRepository {

    fun getEmployees(): Flow<List<Employee>>
    fun getEmployeesCount(): Flow<Int>
    fun addData(employees: List<Employee>)

}