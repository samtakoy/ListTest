package ru.samtakoy.listtest.data.local.cache

import ru.samtakoy.listtest.domain.model.Employee
import ru.samtakoy.listtest.domain.reps.EmployeeCacheRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.samtakoy.listtest.app.misc.AppCoroutineDispatchers
import ru.samtakoy.listtest.data.local.cache.database.CacheDatabase
import ru.samtakoy.listtest.data.local.cache.database.EmployeeEntity
import javax.inject.Inject

class EmployeeCacheRepositoryImpl @Inject constructor(
    val db: CacheDatabase,
    val dispatchers: AppCoroutineDispatchers
) : EmployeeCacheRepository {

    override fun getEmployees(): Flow<List<Employee>> =
        db.employeeData()
            .getAllEmployees()
            .flowOn(dispatchers.database)
            .map { it -> it.map {it.toDomainEntity()}}

    override fun getEmployeesCount(): Flow<Int> =
        db.employeeData().getEmployeeCount()
            .flowOn(dispatchers.database)

    override suspend fun addData(employees: List<Employee>) {
        withContext(dispatchers.database) {
            db.employeeData().addEmployees(employees.map {
                EmployeeEntity.fromDomainEntity(it)
            })
        }
    }

    override suspend fun clearEmployees() {
        withContext(dispatchers.database) {
            db.employeeData().clearEmployees()
        }
    }
}