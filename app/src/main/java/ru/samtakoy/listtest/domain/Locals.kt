package ru.samtakoy.listtest.domain

interface Locals {


    /** есть запись о кеше */
    var hasCacheRecord: Boolean
    var pagesLoaded: Int
    var pagesTotal: Int
    /** время сосздания записи океше */
    var cacheWriteTimestampSeconds: Long

}