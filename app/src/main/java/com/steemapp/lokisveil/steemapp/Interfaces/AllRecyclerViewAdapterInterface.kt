package com.steemapp.lokisveil.steemapp.Interfaces

import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder

/**
 * Created by boot on 2/4/2018.
 */
interface AllRecyclerViewAdapterInterface {
    fun SetMenuItemVisibility(MenuItemId: Int, visibility: Boolean)
    fun SetData(data: FeedArticleDataHolder.FeedArticleHolder)
}