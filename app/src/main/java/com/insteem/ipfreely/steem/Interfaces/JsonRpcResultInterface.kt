package com.insteem.ipfreely.steem.Interfaces

import com.insteem.ipfreely.steem.DataHolders.FeedArticleDataHolder
import com.insteem.ipfreely.steem.jsonclasses.prof

interface JsonRpcResultInterface {
    fun insert(data: FeedArticleDataHolder.FeedArticleHolder){

    }
    fun insert(data: FeedArticleDataHolder.beneficiariesDataHolder){

    }
    fun insert(data: FeedArticleDataHolder.CommentHolder){

    }
    fun insert(data: List<FeedArticleDataHolder.FeedArticleHolder>){

    }
    fun processedArticles(data: List<FeedArticleDataHolder.FeedArticleHolder>){

    }
    fun insert(data: prof.Resultfp){

    }
    fun processingDone(count:Int){

    }

    /**
     * search for followers
     * @param name the name to search for
     * @param isFollower if person is follower
     */
    fun searchFollower(name:String,isFollower:Boolean){

    }

    /**
     * search for following
     * @param name the name to search for
     * @param isFollower if person is following
     */
    fun searchFollowing(name:String,isFollowing:Boolean){

    }
    fun countGot(count:Int){

    }
    fun countGot(count:Int?){

    }
    fun deleDone(){

    }
}