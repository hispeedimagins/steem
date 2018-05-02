package com.steemapp.lokisveil.steemapp.Interfaces

import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder

/**
 * Created by boot on 2/4/2018.
 */
interface AllRecyclerViewAdapterInterface {
    abstract fun SetMenuItemVisibility(MenuItemId: Int, visibility: Boolean)
    abstract fun SetData(data: FeedArticleDataHolder.FeedArticleHolder)
   // fun GetFragmentManager()
    //abstract fun SetData(data: JsonTenorTrendingMedium)
}