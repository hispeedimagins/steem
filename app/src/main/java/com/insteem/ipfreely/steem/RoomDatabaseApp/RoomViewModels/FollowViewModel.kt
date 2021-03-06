package com.insteem.ipfreely.steem.RoomDatabaseApp.RoomViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.insteem.ipfreely.steem.RoomDatabaseApp.RoomRepos.FollowersRepo
import com.insteem.ipfreely.steem.jsonclasses.prof

/**
 * the follower view model class
 */
class FollowViewModel(application: Application): AndroidViewModel(application) {
    private val repo = FollowersRepo(application)
    private var pagedUpdatedList : LiveData<PagedList<prof.Resultfp>>? = null


    /**
     * gets the paged update list for followers
     * @param isFollower true if followers list else false for following list
     */
    fun getpagedList(isFollower:Boolean = true):LiveData<PagedList<prof.Resultfp>>{
        pagedUpdatedList = repo.getPagedUpdatedList(isFollower)
        return pagedUpdatedList!!
    }

    /**
     * insert a list of followers
     */
    fun insert(data:List<prof.Resultfp>,isFollower:Boolean = false){
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


    fun checkFollowing(data:List<prof.Resultfp>,isFollower:Boolean = false){
        repo.checkIfFollowing(data,isFollower)
    }

/*    fun deleteAll(){
        articleRepo.deleteAll()
    }*/
}