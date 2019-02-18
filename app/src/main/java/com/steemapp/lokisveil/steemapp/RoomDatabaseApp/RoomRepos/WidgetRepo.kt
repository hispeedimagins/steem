package com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomRepos

import android.app.Application
import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Interfaces.JsonRpcResultInterface
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDaos.WidgetDao
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDatabaseApp

/**
 * the widget repo class handles the major data access
 */
class WidgetRepo(application: Application?) {


    lateinit var articleDao : WidgetDao
    lateinit var followerRepo : FollowersRepo
    private var firstFiveList: LiveData<List<FeedArticleDataHolder.FeedArticleHolder>>? = null
    private var fetchedItem : LiveData<FeedArticleDataHolder.FeedArticleHolder>? = null
    private var pagedUpdatedList : LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>>? = null
    private var lastKey: LiveData<Int>? = null

    /**
     * if applicaion is not null use this
     */
    init {
        if(application != null){
            articleDao = RoomDatabaseApp.getDatabase(application).widgetDao()
            followerRepo = FollowersRepo(application)
        }
    }

    /**
     * if no access to application we use the context
     */
    constructor(context: Context,application: Application?):this(application){
        articleDao = RoomDatabaseApp.getDatabase(context).widgetDao()
        followerRepo = FollowersRepo(context,null)
    }


    /**
     * get the first twenty items
     */
    fun getFirstTwenty(): LiveData<List<FeedArticleDataHolder.FeedArticleHolder>> {
        firstFiveList = articleDao.getFirstTwenty()
        return firstFiveList!!
    }

    /**
     * get a lsit of items
     * @param dbKey the database key to fetch from
     * @param jni the interface where a callback is made
     */
    fun getList(dbKey: Int,jni: JsonRpcResultInterface){
        getListTaskAsync(articleDao,jni,dbKey).execute()
    }

    /**
     * get a list of articles
     */
    fun getFetchedItem(id:Int): LiveData<FeedArticleDataHolder.FeedArticleHolder> {
        fetchedItem = articleDao.getArticleMy(id)
        return fetchedItem!!
    }

    /**
     * get the last db item inserted
     * @param jni the interface where a callback is made with the result
     */
    fun getLastDbKey(jni: JsonRpcResultInterface) {
        countTaskAsync(articleDao,jni).execute()
    }

    /**
     * get a paged list of data
     */
    fun getPagedUpdatedList(isBlog:Boolean = false): LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>> {
        pagedUpdatedList = LivePagedListBuilder(articleDao.getPagedList(isBlog),20).build()
        return pagedUpdatedList!!
    }

    /**
     * get a paged list after a db key
     */
    fun getPagedUpdatedList(dbKey:Int,isBlog:Boolean = false): LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>> {
        pagedUpdatedList = LivePagedListBuilder(articleDao.getPagedList(dbKey,isBlog),20).build()
        return pagedUpdatedList!!
    }

    /**
     * delete all items
     */
    fun deleteAll(){
        deleteTaskAllAsync(articleDao).execute()
    }


    fun insert(data:List<FeedArticleDataHolder.FeedArticleHolder>){
        insertTaskAsync(articleDao,followerRepo).execute(data)
    }

    fun insert(data: FeedArticleDataHolder.FeedArticleHolder){

        insertTaskSingleAsync(articleDao,followerRepo).execute(FeedArticleDataHolder.feedToWidget(data))
    }

    /**
     * insert items into the db
     */
    private class insertTaskAsync internal constructor(private val dao: WidgetDao, private val fRepo:FollowersRepo):
            AsyncTask<List<FeedArticleDataHolder.FeedArticleHolder>, Void, Void>(){
        override fun doInBackground(vararg params: List<FeedArticleDataHolder.FeedArticleHolder>): Void? {
            for(item in params[0]){
                //check if the person follows the user
                item.followsYou = fRepo.searchFollower(item.author)
                val conv = FeedArticleDataHolder.feedToWidget(item)
                if(dao.insert(conv) == -1L){
                    val old = dao.getArticleNormal(item.id)
                    if(old != null){
                        conv.myDbKey = old.myDbKey
                        dao.update(conv)
                    }
                }
            }
            return null
        }
    }

    private class insertTaskSingleAsync internal constructor(private val dao: WidgetDao, private val fRepo:FollowersRepo):
            AsyncTask<FeedArticleDataHolder.WidgetArticleHolder, Void, Void>(){
        override fun doInBackground(vararg params: FeedArticleDataHolder.WidgetArticleHolder): Void? {
            val item = params[0]
            //check if the person follows the user
            item.followsYou = fRepo.searchFollower(item.author)
            if(dao.insert(item) == -1L){
                val old = dao.getArticleNormal(item.id)
                if(old != null){
                    item.myDbKey = old.myDbKey
                    dao.update(item)
                }
            }
            return null
        }
    }

    private class deleteTaskAllAsync internal constructor(private val dao: WidgetDao):
            AsyncTask<Void, Void, Void>(){
        override fun doInBackground(vararg params: Void): Void? {
            dao.deleteAll()
            return null
        }
    }

    private class countTaskAsync internal constructor(private val dao: WidgetDao, private val jni: JsonRpcResultInterface):
            AsyncTask<Void, Void, Int>(){
        override fun doInBackground(vararg params: Void): Int? {
            return dao.getLastDbIdInt()
        }

        /**
         * execute the result interface on the ui thread
         */
        override fun onPostExecute(result: Int?) {
            jni.countGot(result)
            super.onPostExecute(result)
        }
    }

    private class getListTaskAsync internal constructor(private val dao: WidgetDao, private val jni: JsonRpcResultInterface,private val dbKey:Int):
            AsyncTask<Void, Void, List<FeedArticleDataHolder.FeedArticleHolder>>(){
        override fun doInBackground(vararg params: Void): List<FeedArticleDataHolder.FeedArticleHolder> {
            return dao.getList(dbKey)
        }

        override fun onPostExecute(result: List<FeedArticleDataHolder.FeedArticleHolder>) {
            jni.insert(result)
            super.onPostExecute(result)
        }
    }
}