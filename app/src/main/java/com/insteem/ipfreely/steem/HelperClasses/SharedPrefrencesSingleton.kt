package com.insteem.ipfreely.steem.HelperClasses

import android.content.Context
import android.content.SharedPreferences
import com.insteem.ipfreely.steem.CentralConstants
import java.lang.IllegalArgumentException

class SharedPrefrencesSingleton {
    companion object {

        var sharedPreferences : SharedPreferences? = null

        @Volatile
        var sharedPreferencesSingleton : SharedPrefrencesSingleton? = null
        var sharedPreferencesEdit: SharedPreferences.Editor? = null


        fun getInstance(context: Context?, prefName:String = ""):SharedPrefrencesSingleton{
            if(context == null && sharedPreferencesSingleton != null) return sharedPreferencesSingleton!!

            if(sharedPreferencesSingleton != null && sharedPreferences != null){
                return sharedPreferencesSingleton!!
            } else if(sharedPreferencesSingleton == null){
                synchronized(SharedPrefrencesSingleton::class.java){
                    if(sharedPreferencesSingleton == null){
                        sharedPreferencesSingleton = SharedPrefrencesSingleton()
                        sharedPreferences = context?.getSharedPreferences(if(prefName.isNotBlank()) prefName else CentralConstants.sharedprefname, Context.MODE_PRIVATE)
                    }
                }
                return sharedPreferencesSingleton!!
            }
            throw IllegalArgumentException("Must provide context atleast once")
        }
    }




    fun edit(){
        sharedPreferencesEdit = sharedPreferences!!.edit()
    }

    fun commit(){
        sharedPreferencesEdit!!.commit()
        sharedPreferencesEdit = null
    }

    fun put(key:String,value:Any,commit:Boolean = false){
        if(sharedPreferencesEdit == null) edit()
        when(value){
            is String -> sharedPreferencesEdit!!.putString(key,value)
            is Boolean -> sharedPreferencesEdit!!.putBoolean(key,value)
            is Int -> sharedPreferencesEdit!!.putInt(key,value)
            is Long -> sharedPreferencesEdit!!.putLong(key,value)
            is Float -> sharedPreferencesEdit!!.putFloat(key,value)
            is Double-> sharedPreferencesEdit!!.putFloat(key,value.toFloat())
        }
        if(commit) commit()
    }

    fun remove(key:String,commit:Boolean = false){
        if(sharedPreferencesEdit == null) edit()
        sharedPreferencesEdit?.remove(key)
        if(commit) commit()
    }

    fun getString(key:String):String{
        return sharedPreferences!!.getString(key,"")!!
    }

    fun getBoolean(key:String):Boolean{
        return sharedPreferences!!.getBoolean(key,false)
    }

    fun getInt(key:String):Int{
        return sharedPreferences!!.getInt(key,0)
    }

    fun getLong(key:String):Long{
        return sharedPreferences!!.getLong(key,0L)
    }

    fun getFloat(key:String):Float{
        return sharedPreferences!!.getFloat(key,0F)
    }
}