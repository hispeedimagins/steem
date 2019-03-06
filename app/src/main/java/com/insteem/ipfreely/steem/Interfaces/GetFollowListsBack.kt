package com.insteem.ipfreely.steem.Interfaces

import com.insteem.ipfreely.steem.jsonclasses.prof

/**
 * Created by boot on 4/3/2018.
 */
interface GetFollowListsBack {
    fun GetFollowersList(followerlist:List<prof.Resultfp>)
    fun GetFollowingList(followinglist:List<prof.Resultfp>)
    fun AllDone()
    fun FollowersDone()

}