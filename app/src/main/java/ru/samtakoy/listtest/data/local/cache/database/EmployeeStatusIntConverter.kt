package ru.samtakoy.listtest.data.local.cache.database

import androidx.room.TypeConverter
import ru.samtakoy.listtest.domain.model.EmployeeStatus


class EmployeeStatusIntConverter {

    @TypeConverter
    fun fromInt(statusId: Int): EmployeeStatus {
        return EmployeeStatus.getById(statusId)!!
    }

    @TypeConverter
    fun fromEmployeeStatus(status: EmployeeStatus): Int = status.id

}