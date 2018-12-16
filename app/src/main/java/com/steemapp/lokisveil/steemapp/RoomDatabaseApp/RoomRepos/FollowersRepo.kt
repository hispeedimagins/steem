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
    /*fun deleteAll(){
        deleteTaskAllAsync(articleDao).execute()
    }*/

    init {
        if(application != null){
            dao = RoomDatabaseApp.getDatabase(application).followsDao()
        }
    }

    constructor(context: Context, application: Application?):this(application){
        dao = RoomDatabaseApp.getDatabase(context).followsDao()
    }

    fun searchFollower(name:String):Boolean{
        return dao.searchFollower("$name-follower")
    }

    fun searchFollowing(name:String):Boolean{
        return dao.searchFollowing("$name-following")
    }

    fun deleteAll(jni:JsonRpcResultInterface){
        deleteTaskAllAsync(dao,jni).execute()
    }

    fun insert(data:List<prof.Resultfp>,isFollower:Boolean = false){
        insertTaskAsync(dao,isFollower).execute(data)
    }

    fun insert(data:prof.Resultfp,isFollower:Boolean = false){
        insertTaskSingleAsync(dao,isFollower).execute(data)
    }

    fun getCount(jni:JsonRpcResultInterface):Int{
        countTaskAsync(dao,jni).execute()
        return 0
    }

    private class insertTaskAsync internal constructor(private val dao: FollowsDao,private val isFollower:Boolean = false):
            AsyncTask<List<prof.Resultfp>, Void, Void>(){
        override fun doInBackground(vararg params: List<prof.Resultfp>): Void? {
            for(item in params[0]){
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