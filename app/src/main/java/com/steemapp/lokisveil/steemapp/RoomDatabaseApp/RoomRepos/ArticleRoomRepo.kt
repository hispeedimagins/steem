package com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomRepos

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.os.AsyncTask
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDaos.ArticleDao
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDaos.FollowsDao
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDatabaseApp
import java.util.concurrent.Executors

/**
 * The article repo for accessing the db
 */
class ArticleRoomRepo(application: Application) {
    //fetch the db and then the article DAO
    private var articleDao = RoomDatabaseApp.getDatabase(application).articleDao()
    private var followerRepo = FollowersRepo(application)
    private var firstFiveList: LiveData<List<FeedArticleDataHolder.FeedArticleHolder>>? = null
    private var fetchedItem : LiveData<FeedArticleDataHolder.FeedArticleHolder>? = null
    private var pagedUpdatedList : LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>>? = null
    private var lastKey:LiveData<Int>? = null

    /**
     * get the first twenty items
     */
    fun getFirstTwenty(): LiveData<List<FeedArticleDataHolder.FeedArticleHolder>> {
        firstFiveList = articleDao.getFirstTwenty()
        return firstFiveList!!
    }

    /**
     * get an article
     * @param id the database id
     */
    fun getFetchedItem(id:Int): LiveData<FeedArticleDataHolder.FeedArticleHolder> {
        fetchedItem = articleDao.getArticleMy(id)
        return fetchedItem!!
    }

    /**
     * get the last items id
     */
    fun getLastDbKey():LiveData<Int>{
        lastKey = articleDao.getLastDbId()
        return lastKey!!
    }

    /**
     * get a paged list
     * @param isBlog indicates if blog posts are wanted
     */
    fun getPagedUpdatedList(isBlog:Boolean = false): LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>> {

        /*val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(10)
                .setPageSize(20).build()

        pagedUpdatedList = LivePagedListBuilder(articleDao.getPagedList(), pagedListConfig)
                //.setFetchExecutor(Executors.newFixedThreadPool(5))
                .build()*/

        pagedUpdatedList = LivePagedListBuilder(articleDao.getPagedList(isBlog),20).build()
        return pagedUpdatedList!!
    }

    /**
     * get a paged list
     * @param dbKey the id from where to start
     * @param isBlog true for blog posts else false for feed posts
     */
    fun getPagedUpdatedList(dbKey:Int,isBlog:Boolean = false): LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>> {
        pagedUpdatedList = LivePagedListBuilder(articleDao.getPagedList(dbKey,isBlog),20).build()
        return pagedUpdatedList!!
    }

    /**
     * delete all the articles
     */
    fun deleteAll(){
        deleteTaskAllAsync(articleDao).execute()
    }


    /**
     * insert a list of articles
     */
    fun insert(data:List<FeedArticleDataHolder.FeedArticleHolder>){
        insertTaskAsync(articleDao,followerRepo).execute(data)
    }

    /**
     * insert a single item
     *
     */
    fun insert(data:FeedArticleDataHolder.FeedArticleHolder){
        insertTaskSingleAsync(articleDao,followerRepo).execute(data)
    }

    private class insertTaskAsync internal constructor(private val dao: ArticleDao,private val fRepo:FollowersRepo):
            AsyncTask<List<FeedArticleDataHolder.FeedArticleHolder>, Void, Void>(){
        override fun doInBackground(vararg params: List<FeedArticleDataHolder.FeedArticleHolder>): Void? {
            for(item in params[0]){
                //check for followers
                item.followsYou = fRepo.searchFollower(item.author)
                dao.insert(item)
            }
            return null
        }
    }

    private class insertTaskSingleAsync internal constructor(private val dao: ArticleDao,private val fRepo:FollowersRepo):
            AsyncTask<FeedArticleDataHolder.FeedArticleHolder, Void, Void>(){
        override fun doInBackground(vararg params: FeedArticleDataHolder.FeedArticleHolder): Void? {
            var item = params[0]
            //check if a follower
            item.followsYou = fRepo.searchFollower(item.author)
            dao.insert(item)
            return null
        }
    }

    private class deleteTaskAllAsync internal constructor(private val dao: ArticleDao):
            AsyncTask<Void, Void, Void>(){
        override fun doInBackground(vararg params: Void): Void? {
            dao.deleteAll()
            return null
        }
    }
}