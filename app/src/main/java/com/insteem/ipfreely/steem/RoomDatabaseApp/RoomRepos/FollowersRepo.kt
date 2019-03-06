package com.insteem.ipfreely.steem.RoomDatabaseApp.RoomRepos

import android.app.Application
import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.insteem.ipfreely.steem.Interfaces.JsonRpcResultInterface
import com.insteem.ipfreely.steem.RoomDatabaseApp.RoomDaos.FollowsDao
import com.insteem.ipfreely.steem.RoomDatabaseApp.RoomDatabaseApp
import com.insteem.ipfreely.steem.SteemBackend.Config.Enums.MyOperationTypes
import com.insteem.ipfreely.steem.jsonclasses.prof

class FollowersRepo(application: Application?) {
    lateinit var dao:FollowsDao
    private var deleteWasRun = false
    private var pagedUpdatedList : LiveData<PagedList<prof.Resultfp>>? = null


    //executed if application is not null
    init {
        if(application != null){
            dao = RoomDatabaseApp.getDatabase(application).followsDao()
        }
    }

    /**
     * executed if context is available and not application
     */
    constructor(context: Context, application: Application?):this(application){
        dao = RoomDatabaseApp.getDatabase(context).followsDao()
    }


    /**
     * get a paged list
     */
    fun getPagedUpdatedList(isFollower:Boolean = true): LiveData<PagedList<prof.Resultfp>> {
        pagedUpdatedList = LivePagedListBuilder(dao.getPagedList(isFollower),20).build()
        return pagedUpdatedList!!
    }

    /**
     * search for a follower
     */
    fun searchFollower(name:String):Boolean{
        //-follower is attached to the name as we have to search for unique names in the common
        //index
        return dao.searchFollower("$name-follower")
    }

    /**
     * search for following
     */
    fun searchFollowing(name:String):Boolean{
        //-follower is attached to the name as we have to search for unique names in the common
        //index
        return dao.searchFollowing("$name-following")
    }

    /**
     * search for following
     */
    fun searchFollowing(name:String?,jni:JsonRpcResultInterface){
        //-follower is attached to the name as we have to search for unique names in the common
        //index
        if(name == null) return
        search(dao,jni,"$name-following").execute()
    }

    /**
     * delete all with a callback when done
     * @param jni the callback interface to execute
     */
    fun deleteAll(jni:JsonRpcResultInterface){
        deleteTaskAllAsync(dao,jni).execute()
    }

    /**
     * insert a list of people in the db
     */
    fun insert(data:List<prof.Resultfp>,isFollower:Boolean = false){
        insertTaskAsync(dao,isFollower).execute(data)
    }

    /**
     * insert a single person
     */
    fun insert(data:prof.Resultfp,isFollower:Boolean = false){
        insertTaskSingleAsync(dao,isFollower).execute(data)
    }

    /**
     * get the total item count
     * @param jni the interface where the callback is made with the result
     */
    fun getCount(jni:JsonRpcResultInterface):Int{
        countTaskAsync(dao,jni).execute()
        return 0
    }


    /**
     * check if a list of people are following the user
     * @param data list of followers/following
     * @param isFollower if they are followers, default value id false
     */
    fun checkIfFollowing(data:List<prof.Resultfp>,isFollower:Boolean = false){
        CheckIfFollowing(dao,isFollower).execute(data)
    }

    /**
     * class called when we have to insert people into the db
     * @param dao the dao of the database
     * @param isFollower if is follower
     */
    private class insertTaskAsync internal constructor(private val dao: FollowsDao,private val isFollower:Boolean = false):
            AsyncTask<List<prof.Resultfp>, Void, Void>(){
        override fun doInBackground(vararg params: List<prof.Resultfp>): Void? {
            for(item in params[0]){
                //if ifFollower then we add -follower to the name else -following
                //this is so we have one place to search
                val sn = if(isFollower) "${item.follower}-follower" else "${item.following}-following"
                item.followInternal = MyOperationTypes.follow
                item.isFollower = isFollower
                item.uniqueName = sn
                dao.insert(item)
            }
            return null
        }
    }

    /**
     * class called when we have to insert a person into the db
     * @param dao the dao of the database
     * @param isFollower if is follower
     */
    private class insertTaskSingleAsync internal constructor(private val dao: FollowsDao,private val isFollower:Boolean = false,private val delete:Boolean = true):
            AsyncTask<prof.Resultfp, Void, Void>(){
        override fun doInBackground(vararg params: prof.Resultfp): Void? {
            val item = params[0]
            val sn = if(isFollower) "${item.follower}-follower" else "${item.following}-following"
            item.followInternal = MyOperationTypes.follow
            item.isFollower = isFollower
            item.uniqueName = sn
            dao.insert(item)
            return null
        }
    }

    /**
     * executed when we have to delete the database
     * @param dao the database dao
     * @param jni the callback interface
     */
    private class deleteTaskAllAsync internal constructor(private val dao: FollowsDao,private val jni:JsonRpcResultInterface):
            AsyncTask<Void, Void, Void>(){
        override fun doInBackground(vararg params: Void): Void? {
            dao.deleteAll()
            return null
        }

        override fun onPostExecute(result: Void?) {
            jni.deleDone()
            super.onPostExecute(result)
        }
    }


    /**
     * executed when we have to count the items in the db
     * @param dao the database dao
     * @param jni the callback interface
     */
    private class countTaskAsync internal constructor(private val dao: FollowsDao,private val jni:JsonRpcResultInterface):
            AsyncTask<Void, Void, Int>(){
        override fun doInBackground(vararg params: Void): Int? {
            return dao.getCount()
        }

        override fun onPostExecute(result: Int?) {
            jni.countGot(result!!)
            super.onPostExecute(result)
        }
    }

    /**
     * executed when we have to search
     * @param dao the database dao
     * @param jni the callback interface
     * @param name name of the person to searchf for
     * @param isFollower if person is a follower
     */
    private class search internal constructor(private val dao: FollowsDao,private val jni:JsonRpcResultInterface,private val name:String,private val isFollower: Boolean = false):
            AsyncTask<Void, Void, Boolean>(){
        override fun doInBackground(vararg params: Void): Boolean {
            return if(isFollower) dao.searchFollower(name) else dao.searchFollowing(name)
        }

        override fun onPostExecute(result: Boolean) {
            if(isFollower) jni.searchFollower(name,result) else jni.searchFollowing(name,result)
            super.onPostExecute(result)
        }
    }



    /**
     * executed when we have to search if a list of people are following us
     * @param dao the database dao
     * @param isFollower if person is a follower
     */
    private class CheckIfFollowing internal constructor(private val dao: FollowsDao,private val isFollower:Boolean = false):
            AsyncTask<List<prof.Resultfp>, Void, Void>(){
        override fun doInBackground(vararg params: List<prof.Resultfp>): Void? {
            for(item in params[0]){
                var startValue = item.followInternal
                val sn = if(isFollower) "${item.follower}-follower" else "${item.following}-following"
                val ret = dao.searchFollowing(sn)
                var newValue = if(ret){
                    MyOperationTypes.unfollow
                } else {
                    MyOperationTypes.follow
                }
                if(startValue != newValue){
                    var reItem = item
                    reItem.followInternal = newValue
                    dao.update(reItem)
                }
            }
            return null
        }
    }
}