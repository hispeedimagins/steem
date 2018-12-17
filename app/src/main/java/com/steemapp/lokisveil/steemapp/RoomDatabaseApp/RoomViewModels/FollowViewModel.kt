package com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomViewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomRepos.FollowersRepo
import com.steemapp.lokisveil.steemapp.jsonclasses.prof

/**
 * the follower view model class
 */
class FollowViewModel(application: Application): AndroidViewModel(application) {
    private val repo = FollowersRepo(application)


    /**
     * insert a list of followers
     */
    fun insert(data:List<prof.Resultfp>){
        repo.insert(data)
    }

    /**
     * insert a single follower
     */
    fun insert(data:prof.Resultfp){
        repo.insert(data)
    }

    /**
     * serarch for a follower
     */
    fun searchFollower(name:String):Boolean{
        return repo.searchFollower(name)
    }

    /**
     * search following
     */
    fun searchFollowing(name:String):Boolean{
        return repo.searchFollowing(name)
    }

/*    fun deleteAll(){
        articleRepo.deleteAll()
    }*/
}