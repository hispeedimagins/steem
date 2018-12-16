package com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomViewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomRepos.FollowersRepo
import com.steemapp.lokisveil.steemapp.jsonclasses.prof

class FollowViewModel(application: Application): AndroidViewModel(application) {
    private val repo = FollowersRepo(application)



    fun insert(data:List<prof.Resultfp>){
        repo.insert(data)
    }

    fun insert(data:prof.Resultfp){
        repo.insert(data)
    }

    fun searchFollower(name:String):Boolean{
        return repo.searchFollower(name)
    }

    fun searchFollowing(name:String):Boolean{
        return repo.searchFollowing(name)
    }

/*    fun deleteAll(){
        articleRepo.deleteAll()
    }*/
}