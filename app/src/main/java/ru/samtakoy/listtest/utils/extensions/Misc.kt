package ru.samtakoy.listtest.utils.extensions

import ru.samtakoy.listtest.domain.model.Employee

val <T> T.exhaustive: T
    get() = this

fun List<Employee>.positionOf(employeeId: Int): Int {
    for(i in this.indices){
        if(this[i].id == employeeId){
            return i
        }
    }
    return -1
}