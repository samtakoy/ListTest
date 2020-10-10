package ru.samtakoy.listtest.presentation

import android.view.View
import androidx.core.view.ViewCompat
import ru.samtakoy.listtest.domain.model.Employee


fun Employee.getAvatarTransitionName(): String = "avatar:${this.id}"
fun Employee.getFirstNameTransitionName(): String = "fName:${this.id}"
fun Employee.getLastNameTransitionName(): String = "lName:${this.id}"
fun Employee.getContainerTransitionName(): String = "cont:${this.id}"

fun View.transitionPair() = this to ViewCompat.getTransitionName(this)!!