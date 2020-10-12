package ru.samtakoy.listtest.data.local.cache.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {

    @Query("SELECT * FROM employee")
    fun getAllEmployees(): Flow<List<EmployeeEntity>>

    @Query("SELECT id FROM employee ")
    fun getAllEmployeeIds(): Flow<List<Int>>

    @Query("SELECT * FROM employee WHERE id = :employeeId")
    fun getEmployee(employeeId: Int): Flow<List<EmployeeEntity>>

    @Query("SELECT count(*) FROM employee")
    fun getEmployeeCount(): Flow<Int>

    @Insert
    suspend fun addEmployees(employees: List<EmployeeEntity>)

    @Query("DELETE FROM employee")
    suspend fun clearEmployees()



}