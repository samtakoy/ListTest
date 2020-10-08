package ru.samtakoy.listtest.domain.reps

import ru.samtakoy.listtest.domain.model.dto.EmployeePack

interface RemoteEmployeeRepository {

    suspend fun retrieveMoreEmployees(nextPageNum: Int): Result<EmployeePack>

}