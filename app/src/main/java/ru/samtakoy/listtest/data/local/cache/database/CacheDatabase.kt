package ru.samtakoy.listtest.data.local.cache.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [EmployeeEntity::class],
    version = 1
)
abstract class CacheDatabase : RoomDatabase() {

    abstract fun employeeData(): EmployeeDao
}