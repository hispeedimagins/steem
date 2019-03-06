package com.insteem.ipfreely.steem.Interfaces

import com.google.android.material.floatingactionbutton.FloatingActionButton

interface TagsInterface {
    fun okclicked(originalval:String,tag:String,limit:String,request:String)
    //return a fab button
    fun getFab(): FloatingActionButton?{
        return null
    }
}