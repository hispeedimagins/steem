package com.steemapp.lokisveil.steemapp.Interfaces

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder

/**
 * Created by boot on 2/4/2018.
 */
interface arvdinterface {
    abstract fun add(`object`: Any)

    abstract fun notifydatachanged()
    abstract fun notifyitemcinserted(position: Int)
    abstract fun notifyitemcchanged(position: Int)
    abstract fun notifyitemcchanged(position: Int,payload:Any)

    abstract fun getObject(position: Int): Any
    abstract fun getSize(): Int

    abstract fun SetMenuItemVisibility(MenuItemId: Int, visibility: Boolean)
    abstract fun SetData(data: FeedArticleDataHolder.FeedArticleHolder)
    //abstract fun SetData(data: JsonTenorTrendingMedium)
    abstract fun getResources(): Resources
    abstract fun getContext(): Context
    fun getActivity(): Activity
    abstract fun AddItemDivider()
    fun GetGlobalInterface() : GlobalInterface?
}