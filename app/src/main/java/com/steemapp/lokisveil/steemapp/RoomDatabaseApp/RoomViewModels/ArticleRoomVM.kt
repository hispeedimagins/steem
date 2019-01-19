package com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomViewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Interfaces.ArticleVmRepoInterface
import com.steemapp.lokisveil.steemapp.Interfaces.JsonRpcResultInterface
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomRepos.ArticleRoomRepo
import java.util.*


/**
 * the article viewmodel class
 */
class ArticleRoomVM(application: Application):AndroidViewModel(application),ArticleVmRepoInterface {
    override fun updateSaveTime(sTime: Long) {
        timeOfSave = sTime
    }

    //time of saving the data is kept up to date
    private var timeOfSave = Calendar.getInstance().timeInMillis
    private val articleRepo = ArticleRoomRepo(application)
    var firstFiveList: LiveData<List<FeedArticleDataHolder.FeedArticleHolder>>? = null
    private var fetchedItem: LiveData<FeedArticleDataHolder.FeedArticleHolder>? = null
    private var fetchedItemId : LiveData<FeedArticleDataHolder.FeedArticleHolder>? = null
    private var pagedUpdatedList : LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>>? = null
    private var pagedUpdatedListTime : LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>>? = null
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
     * get an article
     * @param id the steem id
     */
    fun getFetchedItemId(id:Long): LiveData<FeedArticleDataHolder.FeedArticleHolder> {
        fetchedItem = articleRepo.getFetchedItemId(id)
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
     * gets a paged data list from the db
     */
    fun getPagedUpdatedListTime(isBlog:Boolean = false): LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>> {
        pagedUpdatedListTime = articleRepo.getPagedUpdatedListTime(isBlog)
        return pagedUpdatedListTime!!
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
        timeOfSave = articleRepo.insert(data,timeOfSave,this)
    }

    /**
     * insert a single item
     */
    fun insert(data:FeedArticleDataHolder.FeedArticleHolder){
        timeOfSave = articleRepo.insert(data,timeOfSave,this)
    }

    /**
     * delete all articles
     */
    fun deleteAll(){
        articleRepo.deleteAll()
    }

    /**
     * delete all articles
     */
    fun deleteAll(isBlog: Boolean,jni:JsonRpcResultInterface? = null){
        articleRepo.deleteAll(isBlog,jni)
    }
}