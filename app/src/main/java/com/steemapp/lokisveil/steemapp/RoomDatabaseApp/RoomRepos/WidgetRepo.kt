package com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomRepos

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.content.Context
import android.os.AsyncTask
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Interfaces.JsonRpcResultInterface
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDaos.ArticleDao
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDaos.FollowsDao
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDaos.WidgetDao
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDatabaseApp

class WidgetRepo(application: Application?) {


    lateinit var articleDao : WidgetDao
    lateinit var followerRepo : FollowersRepo
    private var firstFiveList: LiveData<List<FeedArticleDataHolder.FeedArticleHolder>>? = null
    private var fetchedItem : LiveData<FeedArticleDataHolder.FeedArticleHolder>? = null
    private var pagedUpdatedList : LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>>? = null
    private var lastKey: LiveData<Int>? = null


    init {
        if(application != null){
            articleDao = RoomDatabaseApp.getDatabase(application).widgetDao()
            followerRepo = FollowersRepo(application)
        }
    }

    constructor(context: Context,application: Application?):this(application){
        articleDao = RoomDatabaseApp.getDatabase(context).widgetDao()
        followerRepo = FollowersRepo(context,null)
    }


    fun getFirstTwenty(): LiveData<List<FeedArticleDataHolder.FeedArticleHolder>> {
        firstFiveList = articleDao.getFirstTwenty()
        return firstFiveList!!
    }

    fun getList(dbKey: Int,jni: JsonRpcResultInterface){
        getListTaskAsync(articleDao,jni,dbKey).execute()
    }

    fun getFetchedItem(id:Int): LiveData<FeedArticleDataHolder.FeedArticleHolder> {
        fetchedItem = articleDao.getArticleMy(id)
        return fetchedItem!!
    }

    fun getLastDbKey(jni: JsonRpcResultInterface) {
        countTaskAsync(articleDao,jni).execute()
    }

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

    fun getPagedUpdatedList(dbKey:Int,isBlog:Boolean = false): LiveData<PagedList<FeedArticleDataHolder.FeedArticleHolder>> {
        pagedUpdatedList = LivePagedListBuilder(articleDao.getPagedList(dbKey,isBlog),20).build()
        return pagedUpdatedList!!
    }

    fun deleteAll(){
        deleteTaskAllAsync(articleDao).execute()
    }


    fun insert(data:List<FeedArticleDataHolder.FeedArticleHolder>){
        insertTaskAsync(articleDao,followerRepo).execute(data)
    }

    fun insert(data: FeedArticleDataHolder.FeedArticleHolder){

        insertTaskSingleAsync(articleDao,followerRepo).execute(FeedArticleDataHolder.feedToWidget(data))
    }

    private class insertTaskAsync internal constructor(private val dao: WidgetDao, private val fRepo:FollowersRepo):
            AsyncTask<List<FeedArticleDataHolder.FeedArticleHolder>, Void, Void>(){
        override fun doInBackground(vararg params: List<FeedArticleDataHolder.FeedArticleHolder>): Void? {
            for(item in params[0]){
                item.followsYou = fRepo.searchFollower(item.author)
                dao.insert(FeedArticleDataHolder.feedToWidget(item))
            }
            return null
        }
    }

    private class insertTaskSingleAsync internal constructor(private val dao: WidgetDao, private val fRepo:FollowersRepo):
            AsyncTask<FeedArticleDataHolder.WidgetArticleHolder, Void, Void>(){
        override fun doInBackground(vararg params: FeedArticleDataHolder.WidgetArticleHolder): Void? {
            var item = params[0]
            item.followsYou = fRepo.searchFollower(item.author)
            var id = dao.insert(item)
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