package com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomRepos

import android.app.Application
import android.content.Context
import android.os.AsyncTask
import com.steemapp.lokisveil.steemapp.Interfaces.JsonRpcResultInterface
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDaos.FollowsDao
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDatabaseApp
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.jsonclasses.prof

class FollowersRepo(application: Application?) {
    lateinit var dao:FollowsDao
    private var deleteWasRun = false

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
}