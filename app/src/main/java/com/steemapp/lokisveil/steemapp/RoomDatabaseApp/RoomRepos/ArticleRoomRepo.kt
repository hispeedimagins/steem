package com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomRepos

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.os.AsyncTask
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Interfaces.ArticleVmRepoInterface
import com.steemapp.lokisveil.steemapp.Interfaces.JsonRpcResultInterface
import com.steemapp.lokisveil.steemapp.MiscConstants
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDaos.ArticleDao
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDatabaseApp

/**
 * The article repo for accessing the db
 */
class ArticleRoomRepo(application: Application) {
    /*constructor(application:Application,sTime: Long):this(application){
        timeOfSave = sTime
    }*/
    //fetch the db and then the article DAO

    private var articleDao = RoomDatabaseApp.getDatabase(application).articleDao()
    private var followerRepo = FollowersRepo(application)
    private var firstFiveList: LiveData<List<FeedArticleDataHolder.FeedArticleHolder>>? = null
    private var fetchedItem : LiveData<FeedArticleDataHolder.FeedArticleHolder>? = null
    private var fetchedItemId : LiveData<FeedArticleDataHolder.FeedArticleHolder>? = null
    private var pagedUpdatedList : LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>>? = null
    private var pagedUpdatedListTime : LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>>? = null

    private var lastKey:LiveData<Int>? = null
    //private var timeOfSave = 0L


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
     * get an article
     * @param id the steem id
     */
    fun getFetchedItemId(id:Long): LiveData<FeedArticleDataHolder.FeedArticleHolder> {
        fetchedItem = articleDao.getArticle(id)
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
     * get a paged list
     * @param isBlog true for blog posts else false for feed posts
     */
    fun getPagedUpdatedListTime(isBlog:Boolean = false): LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>> {
        pagedUpdatedListTime = LivePagedListBuilder(articleDao.getPagedListTime(isBlog),20).build()
        return pagedUpdatedListTime!!
    }

    /**
     * delete all the articles
     */
    fun deleteAll(){
        deleteTaskAllAsync(articleDao).execute()
    }

    /**
     * delete all the articles
     */
    fun deleteAll(isBlog: Boolean,jni:JsonRpcResultInterface? = null){
        deleteTaskAllBooleanAsync(articleDao,isBlog,jni).execute()
    }


    /**
     * insert a list of articles
     */
    fun insert(data:List<FeedArticleDataHolder.FeedArticleHolder>,sTime:Long,upt:ArticleVmRepoInterface):Long{
        val sa = sTime - 100
        //upt.updateSaveTime(sa)
        insertTaskAsync(articleDao,followerRepo,sa,upt).execute(data)
        return sa
    }

    /**
     * insert a single item
     *
     */
    fun insert(data:FeedArticleDataHolder.FeedArticleHolder,sTime:Long,upt:ArticleVmRepoInterface):Long{
        val sa = sTime - 100
        //upt.updateSaveTime(sa)
        insertTaskSingleAsync(articleDao,followerRepo,sa,upt).execute(data)
        return sa
    }


    /**
     * executed when we have to insert items into the database
     * @param dao the database dao
     * @param fRepo the followers repository
     * @param timeOfSave timestamp while saving
     */
    private class insertTaskAsync internal constructor(private val dao: ArticleDao,
                                                       private val fRepo:FollowersRepo,
                                                       private var timeOfSave:Long,
                                                       private val upt: ArticleVmRepoInterface):
            AsyncTask<List<FeedArticleDataHolder.FeedArticleHolder>, Void, Void>(){
        override fun doInBackground(vararg params: List<FeedArticleDataHolder.FeedArticleHolder>): Void? {
            for(item in params[0]){
                //check for followers
                //val sa = timeOfSave - 100
                item.followsYou = fRepo.searchFollower(item.author)
                item.saveTime = timeOfSave
                //upt.updateSaveTime(sa)
                if(dao.insert(item) == -1L){
                    var old = dao.getArticleNormal(item.id)
                    val idDouble = MiscConstants.doubleTheId(item.id,item.isBlog)
                    if(old == null) old = dao.getArticleNormal(idDouble)
                    if(old != null){
                        if(item.isBlog != old.isBlog){
                            item.id = idDouble
                            dao.insert(item)
                            return null
                        }
                        item.myDbKey = old.myDbKey

                        dao.update(item)
                    }

                }
            }
            return null
        }
    }


    /**
     * executed when we have to insert an item into the database
     * @param dao the database dao
     * @param fRepo the followers repository
     * @param timeOfSave timestamp while saving
     */
    private class insertTaskSingleAsync internal constructor(private val dao: ArticleDao,
                                                             private val fRepo:FollowersRepo,
                                                             private var timeOfSave:Long,
                                                             private val upt: ArticleVmRepoInterface):
            AsyncTask<FeedArticleDataHolder.FeedArticleHolder, Void, Void>(){
        override fun doInBackground(vararg params: FeedArticleDataHolder.FeedArticleHolder): Void? {
            val item = params[0]
            //check if a follower
            //val sa = timeOfSave - 100
            item.followsYou = fRepo.searchFollower(item.author)
            item.saveTime = timeOfSave
            //upt.updateSaveTime(sa)
            if(dao.insert(item) == -1L){
                var old = dao.getArticleNormal(item.id)
                val idDouble = MiscConstants.doubleTheId(item.id,item.isBlog)
                if(old == null) old = dao.getArticleNormal(idDouble)
                if(old != null){
                    if(item.isBlog != old.isBlog){
                        item.id = idDouble
                        dao.insert(item)
                        return null
                    }
                    item.myDbKey = old.myDbKey
                    dao.update(item)
                }
            }
            return null
        }
    }

    /**
     * executed when we have to delete the database
     * @param dao the database dao
     */
    private class deleteTaskAllAsync internal constructor(private val dao: ArticleDao):
            AsyncTask<Void, Void, Void>(){
        override fun doInBackground(vararg params: Void): Void? {
            dao.deleteAll()
            return null
        }
    }

    /**
     * executed when we have to delete the database
     * @param dao the database dao
     * @param isBlog if to delete only blog items
     * @param jni the callback interface
     */
    private class deleteTaskAllBooleanAsync internal constructor(private val dao: ArticleDao,private val isBlog:Boolean,private val jni:JsonRpcResultInterface?):
            AsyncTask<Void, Void, Void>(){
        override fun doInBackground(vararg params: Void): Void? {
            dao.deleteAll(isBlog)
            return null
        }

        override fun onPostExecute(result: Void?) {
            jni?.deleDone()
            super.onPostExecute(result)
        }
    }
}