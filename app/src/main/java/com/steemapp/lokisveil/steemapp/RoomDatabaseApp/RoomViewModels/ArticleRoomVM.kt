package com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomViewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomRepos.ArticleRoomRepo
import android.arch.paging.LivePagedListBuilder


/**
 * the article viewmodel class
 */
class ArticleRoomVM(application: Application):AndroidViewModel(application) {
    //fetch the db
    private val articleRepo = ArticleRoomRepo(application)
    var firstFiveList: LiveData<List<FeedArticleDataHolder.FeedArticleHolder>>? = null
    private var fetchedItem: LiveData<FeedArticleDataHolder.FeedArticleHolder>? = null
    private var pagedUpdatedList : LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>>? = null
    private var lastDbKey:LiveData<Int>? = null

    /**
     * the first five are to be got
     */
    fun getFirstFive(): LiveData<List<FeedArticleDataHolder.FeedArticleHolder>> {
        firstFiveList = articleRepo.getFirstTwenty()
        return firstFiveList!!
    }

    /**
     * get a single article
     */
    fun getFetchedItem(id:Int): LiveData<FeedArticleDataHolder.FeedArticleHolder> {
        fetchedItem = articleRepo.getFetchedItem(id)
        return fetchedItem!!
    }

    /**
     * get the last db id inserted
     */
    fun getLastDbKey():LiveData<Int>{
        lastDbKey = articleRepo.getLastDbKey()
        return lastDbKey!!
    }

    /**
     * gets a paged data list from the db
     */
    fun getPagedUpdatedList(isBlog:Boolean = false): LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>> {
        pagedUpdatedList = articleRepo.getPagedUpdatedList(isBlog)
        return pagedUpdatedList!!
    }

    /**
     * gets a paged data list from the db also does not load all the old data
     */
    fun getPagedUpdatedList(dbKey:Int,isBlog:Boolean = false): LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>> {
        pagedUpdatedList = articleRepo.getPagedUpdatedList(dbKey,isBlog)
        return pagedUpdatedList!!
    }

    /**
     * insert a list of items
     */
    fun insert(data:List<FeedArticleDataHolder.FeedArticleHolder>){
        articleRepo.insert(data)
    }

    /**
     * insert a single item
     */
    fun insert(data:FeedArticleDataHolder.FeedArticleHolder){
        articleRepo.insert(data)
    }

    /**
     * delete all articles
     */
    fun deleteAll(){
        articleRepo.deleteAll()
    }
}