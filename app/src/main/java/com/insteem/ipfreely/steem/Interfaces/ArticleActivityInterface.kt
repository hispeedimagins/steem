package com.insteem.ipfreely.steem.Interfaces

import android.content.Context
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.insteem.ipfreely.steem.DataHolders.FeedArticleDataHolder

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
    //interface to return the fabbutton
    fun getFab(): FloatingActionButton?
    fun getDbData(): FeedArticleDataHolder.FeedArticleHolder?
    fun loadDataNow(){}
    //fun GetDisplayMetrics() : DisplayMetrics
}