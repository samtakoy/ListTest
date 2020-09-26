package ru.samtakoy.listtest.data.local.cache

import ru.samtakoy.listtest.domain.model.Employee
import ru.samtakoy.listtest.domain.reps.EmployeeCacheRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.samtakoy.listtest.data.local.cache.database.CacheDatabase
import ru.samtakoy.listtest.data.local.cache.database.EmployeeEntity
import javax.inject.Inject

class EmployeeCacheRepositoryImpl @Inject constructor(
    val db: CacheDatabase
) : EmployeeCacheRepository {

    override fun getEmployees(): Flow<List<Employee>> =
        db.employeeData()
            .getAllEmployees()
            .map { it -> it.map {it.toDomainEntity()}}

    override fun getEmployeesCount(): Flow<Int> =
        db.employeeData().getEmployeeCount()

    override fun addData(employees: List<Employee>) {
        db.employeeData().addEmployees(employees.map {
            EmployeeEntity.fromDomainEntity(it)
        })
    }

    override fun clearEmployees() {
        db.employeeData().clearEmployees()
    }
}