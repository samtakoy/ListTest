package ru.samtakoy.listtest.domain

import javax.inject.Inject

class TimestampHolderImpl @Inject constructor(): TimestampHolder{

    override val timestampSeconds: Long
        get() = System.currentTimeMillis()/1000
}