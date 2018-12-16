package com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomViewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomRepos.ArticleRoomRepo
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomRepos.WidgetRepo

class WidgetVM(application: Application): AndroidViewModel(application) {
    private val articleRepo = WidgetRepo(application)
    var firstFiveList: LiveData<List<FeedArticleDataHolder.FeedArticleHolder>>? = null
    private var fetchedItem: LiveData<FeedArticleDataHolder.FeedArticleHolder>? = null
    private var pagedUpdatedList : LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>>? = null
    private var lastDbKey: LiveData<Int>? = null
    fun getFirstFive(): LiveData<List<FeedArticleDataHolder.FeedArticleHolder>> {
        firstFiveList = articleRepo.getFirstTwenty()
        return firstFiveList!!
    }

    fun getFetchedItem(id:Int): LiveData<FeedArticleDataHolder.FeedArticleHolder> {
        fetchedItem = articleRepo.getFetchedItem(id)
        return fetchedItem!!
    }

    /*fun getLastDbKey(): LiveData<Int> {
        lastDbKey = articleRepo.getLastDbKey()
        return lastDbKey!!
    }*/

    fun getPagedUpdatedList(isBlog:Boolean = false): LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>> {
        pagedUpdatedList = articleRepo.getPagedUpdatedList(isBlog)
        return pagedUpdatedList!!
    }

    fun getPagedUpdatedList(dbKey:Int,isBlog:Boolean = false): LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>> {
        pagedUpdatedList = articleRepo.getPagedUpdatedList(dbKey,isBlog)
        return pagedUpdatedList!!
    }

    fun insert(data:List<FeedArticleDataHolder.FeedArticleHolder>){
        articleRepo.insert(data)
    }

    fun insert(data: FeedArticleDataHolder.FeedArticleHolder){
        articleRepo.insert(data)
    }

    fun deleteAll(){
        articleRepo.deleteAll()
    }
}