package com.insteem.ipfreely.steem.Interfaces

import android.app.Activity
import android.content.Context

import android.widget.CheckBox
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Created by boot on 3/21/2018.
 */
interface GlobalInterface {
     //fun add(`object`: Any)

     /*fun notifydatachanged()
     fun notifyitemcinserted(position: Int)
     fun notifyitemcchanged(position: Int)
     fun notifyitemcchanged(position: Int,payload:Any)*/

    fun notifyRequestMadeSuccess(){}
    fun notifyRequestMadeError(){}
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


    /**
     * reports the follower progress
     * @param got the number got till now
     * @param total the total number of people to get
     */
    fun followerProgress(got:Int,total:Int){

    }

    /**
     * reports the following progress
     * @param got the number got till now
     * @param total the total number of people to get
     */
    fun followingProgress(got:Int,total:Int){

    }

    /**
     * called when the db count does not match the count from the server
     */
    fun followHasChanged(){

    }

    // for attaching listeners
    fun attachCheckboxListner(box:CheckBox?){

    }
    //returns a FAB button
    fun getFab(): FloatingActionButton?{
        return null
    }
    //returns a FAB menu from the library
    fun getFabM(): FloatingActionMenu?{
        return null
    }

    fun objectClicked(data:Any?){}


    //fun AddItemDivider()
}