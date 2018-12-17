package com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomConverters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import org.json.JSONArray
import java.util.*

/**
 * converters class is used to convert data to and from the db
 */
class Converters {

    /**
     * converts list to string via Gson
     * @param value list of items
     */
    @TypeConverter
    fun listToJson(value: List<String>?): String {

        return Gson().toJson(value)
    }

    /**
     * converts a string to a list of string
     * @param value string to convert via gson
     */
    @TypeConverter
    fun jsonToList(value: String): List<String>? {
        if(value == null)return emptyList()
        val objects = Gson().fromJson(value, Array<String>::class.java)
        if(objects == null)return emptyList()
        return objects.toList()
    }

    /**
     * enum to string
     */
    @TypeConverter
    fun enumToString(enum:MyOperationTypes):String{
        return enum.toString()
    }

    /**
     * string to enum
     */
    @TypeConverter
    fun enumFromString(enum:String):MyOperationTypes{
        return MyOperationTypes.valueOf(enum)
    }

    /**
     * json array to string
     */
    @TypeConverter
    fun jsonArrayToString(jsonArray:JSONArray):String{
        return jsonArray.toString()
    }

    /**
     * string to jsonarray
     */
    @TypeConverter
    fun jsonArrayFromString(jsonArray:String):JSONArray{
        return JSONArray(jsonArray)
    }

    /**
     * date to long
     */
    @TypeConverter
    fun dateToLong(date: Date):Long{
        return date.time
    }

    /**
     * long to date
     */
    @TypeConverter
    fun dateFromLong(date:Long):Date{
        return Date(date)
    }
}