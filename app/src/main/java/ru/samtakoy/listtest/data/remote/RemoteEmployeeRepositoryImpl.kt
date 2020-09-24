package ru.samtakoy.listtest.data.remote

import ru.samtakoy.listtest.data.remote.api.RequestApi
import ru.samtakoy.listtest.domain.model.Employee
import ru.samtakoy.listtest.domain.reps.RemoteEmployeeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.samtakoy.listtest.domain.model.dto.EmployeePack
import javax.inject.Inject

class RemoteEmployeeRepositoryImpl @Inject constructor(): RemoteEmployeeRepository {

    @Inject lateinit var api: RequestApi

    override suspend fun retrieveMoreEmployees(nextPageNum: Int): EmployeePack {
        return api.getEmployeers(nextPageNum).toDomainModel()
    }
}