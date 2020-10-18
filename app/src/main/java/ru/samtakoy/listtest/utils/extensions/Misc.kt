package ru.samtakoy.listtest.utils.extensions

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.transition.Transition
import androidx.transition.TransitionSet
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

@RequiresApi(Build.VERSION_CODES.KITKAT)
inline fun TransitionSet.doOnEndOnce(crossinline block:()->Unit): TransitionSet {

    var listener:Transition.TransitionListener? = null

    listener = object: Transition.TransitionListener {
        override fun onTransitionStart(transition: Transition) {}
        override fun onTransitionEnd(transition: Transition) {
            this@doOnEndOnce.removeListener(listener!!)
            block()
        }
        override fun onTransitionCancel(transition: Transition) {}
        override fun onTransitionPause(transition: Transition) {}
        override fun onTransitionResume(transition: Transition) {}
    }
    this.addListener(listener!!)

    return this
}