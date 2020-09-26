package ru.samtakoy.listtest.data.local.cache.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {

    @Query("SELECT * FROM employee")
    fun getAllEmployees(): Flow<List<EmployeeEntity>>

    @Query("SELECT count(*) FROM employee")
    fun getEmployeeCount(): Flow<Int>

    @Insert
    fun addEmployees(employees: List<EmployeeEntity>)

    @Query("DELETE FROM employee")
    fun clearEmployees()

}