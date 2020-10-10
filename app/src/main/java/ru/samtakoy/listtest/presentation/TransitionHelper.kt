package ru.samtakoy.listtest.presentation

import ru.samtakoy.listtest.domain.model.Employee


fun Employee.getAvatarTransitionName(): String = "avatar:${this.id}"
fun Employee.getFirstNameTransitionName(): String = "fName:${this.id}"
fun Employee.getLastNameTransitionName(): String = "lName:${this.id}"
fun Employee.getContainerTransitionName(): String = "cont:${this.id}"