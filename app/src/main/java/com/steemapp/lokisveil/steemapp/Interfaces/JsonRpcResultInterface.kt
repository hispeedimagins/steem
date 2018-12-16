package com.steemapp.lokisveil.steemapp.Interfaces

import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.jsonclasses.prof

interface JsonRpcResultInterface {
    fun insert(data: FeedArticleDataHolder.FeedArticleHolder){

    }
    fun insert(data: FeedArticleDataHolder.beneficiariesDataHolder){

    }
    fun insert(data: FeedArticleDataHolder.CommentHolder){

    }
    fun insert(data: List<FeedArticleDataHolder.FeedArticleHolder>){

    }
    fun insert(data: prof.Resultfp){

    }
    fun processingDone(count:Int){

    }

    fun searchFollower(name:String){

    }
    fun searchFollowing(name:String){

    }
    fun countGot(count:Int){

    }
    fun countGot(count:Int?){

    }
    fun deleDone(){

    }
}