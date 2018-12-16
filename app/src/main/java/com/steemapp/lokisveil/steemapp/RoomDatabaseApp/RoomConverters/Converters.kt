package com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomConverters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import org.json.JSONArray
import java.util.*

class Converters {
    @TypeConverter
    fun listToJson(value: List<String>?): String {

        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): List<String>? {
        if(value == null)return emptyList()
        val objects = Gson().fromJson(value, Array<String>::class.java)
        if(objects == null)return emptyList()
        return objects.toList()
    }

    @TypeConverter
    fun enumToString(enum:MyOperationTypes):String{
        return enum.toString()
    }

    @TypeConverter
    fun enumFromString(enum:String):MyOperationTypes{
        return MyOperationTypes.valueOf(enum)
    }

    @TypeConverter
    fun jsonArrayToString(jsonArray:JSONArray):String{
        return jsonArray.toString()
    }

    @TypeConverter
    fun jsonArrayFromString(jsonArray:String):JSONArray{
        return JSONArray(jsonArray)
    }

    @TypeConverter
    fun dateToLong(date: Date):Long{
        return date.time
    }

    @TypeConverter
    fun dateFromLong(date:Long):Date{
        return Date(date)
    }
}