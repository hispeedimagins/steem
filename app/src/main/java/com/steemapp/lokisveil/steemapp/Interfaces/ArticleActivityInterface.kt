package com.steemapp.lokisveil.steemapp.Interfaces

import android.content.Context
import android.util.DisplayMetrics

/**
 * Created by boot on 3/25/2018.
 */
interface ArticleActivityInterface {
    fun UserClicked(name : String)
    //interface function for tag clicks
    fun TagClicked(tag:String)
    fun ReloadData()
    fun getContextMine(): Context
    fun linkClicked(tag:String,name:String,link:String)
    //interface function for returning the body
    fun getBody():String{
        return ""
    }
    //fun GetDisplayMetrics() : DisplayMetrics
}