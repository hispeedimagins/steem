package com.steemapp.lokisveil.steemapp.Interfaces

import android.support.design.widget.FloatingActionButton

interface TagsInterface {
    fun okclicked(originalval:String,tag:String,limit:String,request:String)
    //return a fab button
    fun getFab(): FloatingActionButton?{
        return null
    }
}