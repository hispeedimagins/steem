package com.steemapp.lokisveil.steemapp.Interfaces

import android.content.Context
import android.util.DisplayMetrics

/**
 * Created by boot on 3/25/2018.
 */
interface ArticleActivityInterface {
    fun UserClicked(name : String)
    fun ReloadData()
    fun getContextMine(): Context
    //fun GetDisplayMetrics() : DisplayMetrics
}