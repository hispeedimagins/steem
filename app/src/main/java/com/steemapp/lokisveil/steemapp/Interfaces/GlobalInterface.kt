package com.steemapp.lokisveil.steemapp.Interfaces

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.widget.CheckBox
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder

/**
 * Created by boot on 3/21/2018.
 */
interface GlobalInterface {
     //fun add(`object`: Any)

     /*fun notifydatachanged()
     fun notifyitemcinserted(position: Int)
     fun notifyitemcchanged(position: Int)
     fun notifyitemcchanged(position: Int,payload:Any)*/

    fun notifyRequestMadeSuccess()
    fun notifyRequestMadeError()
     fun getObjectMine(): Any
     //fun getSize(): Int

     //fun SetMenuItemVisibility(MenuItemId: Int, visibility: Boolean)
     //fun SetData(data: FeedArticleDataHolder.FeedArticleHolder)
    //ct fun SetData(data: JsonTenorTrendingMedium)
     //fun getResources(): Resources
     fun getContextMine(): Context
    fun getActivityMine(): Activity
    fun followersDone(){

    }
    fun followingDone(){

    }

    // for attaching listeners
    fun attachCheckboxListner(box:CheckBox?){

    }



    //fun AddItemDivider()
}