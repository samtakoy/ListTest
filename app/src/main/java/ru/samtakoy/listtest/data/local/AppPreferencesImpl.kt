package ru.samtakoy.listtest.data.local

import android.content.Context
import android.content.SharedPreferences
import ru.samtakoy.listtest.domain.Locals
import javax.inject.Inject

private const val APP_PREFERENCES = "mysettings"
private const val KEY_PAGES_LOADED = "PAGES_LOADED"
private const val KEY_PAGES_TOTAL = "PAGES_TOTAL"
private const val KEY_CACHE_TIMESTAMP = "CACHE_TIMESTAMP"
private const val KEY_CACHE_VALID = "CACHE_VALID"


class AppPreferencesImpl @Inject constructor(
    private val context: Context
) : Locals{

    var mPrefs: SharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

    /** есть запись о кеше */
    override var hasCacheRecord: Boolean
        get() = getBoolean(KEY_CACHE_VALID, false)
        set(value){ putBoolean(KEY_CACHE_VALID, value) }

    override var pagesLoaded: Int
        get() = getInt(KEY_PAGES_LOADED, 0)
        set(value){ putInt(KEY_PAGES_LOADED, value) }

    override var pagesTotal: Int
        get() = getInt(KEY_PAGES_TOTAL, 0)
        set(value){ putInt(KEY_PAGES_TOTAL, value) }

    /** время сосздания записи океше */
    override var cacheWriteTimestampSeconds: Long
        get() = getLong(KEY_CACHE_TIMESTAMP, 0)
        set(value){ putLong(KEY_CACHE_TIMESTAMP, value) }


    private fun putInt(key:String, value:Int){
        val editor = mPrefs.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    private fun getInt(key: String, defValue: Int) = mPrefs.getInt(key, defValue)!!


    private fun putLong(key:String, value:Long){
        val editor = mPrefs.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    private fun getLong(key: String, defValue: Long) = mPrefs.getLong(key, defValue)!!

    private fun putBoolean(key:String, value:Boolean){
        val editor = mPrefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    private fun getBoolean(key: String, defValue: Boolean) = mPrefs.getBoolean(key, defValue)!!

}