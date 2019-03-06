package com.insteem.ipfreely.steem.Interfaces

import com.insteem.ipfreely.steem.DataHolders.FeedArticleDataHolder

/**
 * Created by boot on 2/4/2018.
 */
interface AllRecyclerViewAdapterInterface {
    fun SetMenuItemVisibility(MenuItemId: Int, visibility: Boolean)
    fun SetData(data: FeedArticleDataHolder.FeedArticleHolder)
}