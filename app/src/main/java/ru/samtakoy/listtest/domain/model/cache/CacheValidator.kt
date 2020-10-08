package ru.samtakoy.listtest.domain.model.cache

import ru.samtakoy.listtest.domain.Locals

class CacheValidator (
    val cacheExpireIntervalSeconds: Int,
    val locals: Locals
){

    var pagesLoaded: Int
        private set
    var pagesTotal: Int
        private set
    var hasCacheRecord: Boolean
        private set


    init{
        pagesLoaded = locals.pagesLoaded
        pagesTotal = locals.pagesTotal
        hasCacheRecord = locals.hasCacheRecord
    }

    fun validateByTimestamp(currentTimestampSeconds: Long){
        if(hasCacheRecord ){
            val intervalSeconds = (currentTimestampSeconds - locals.cacheWriteTimestampSeconds)
            if(intervalSeconds > cacheExpireIntervalSeconds) {
                invalidate()
            }
        }
    }

    fun verifyPageNumbers(inputPage: Int, inputTotalPages: Int): Boolean{
        if(hasCacheRecord ){
            return (inputTotalPages == pagesTotal) && (inputPage == (pagesLoaded+1))
        }
        return true
    }

    fun invalidate(){
        if(hasCacheRecord){
            hasCacheRecord = false
            pagesLoaded = 0
            pagesTotal = 0
            flushDataToLocals(0)
        }
    }

    fun isSynchronized() = hasCacheRecord && pagesLoaded == pagesTotal

    fun onNewData(page: Int, totalPages: Int, timestampSeconds: Long) {
        hasCacheRecord = true
        pagesLoaded = page
        pagesTotal = totalPages

        flushDataToLocals(timestampSeconds)
    }

    private fun flushDataToLocals(timestampSeconds: Long) {
        locals.hasCacheRecord = hasCacheRecord
        locals.pagesLoaded = pagesLoaded
        locals.pagesTotal = pagesTotal
        if(hasCacheRecord && pagesLoaded == 1){
            locals.cacheWriteTimestampSeconds = timestampSeconds
        }
    }

}