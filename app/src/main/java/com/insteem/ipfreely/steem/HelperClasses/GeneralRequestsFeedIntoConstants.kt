package com.insteem.ipfreely.steem.HelperClasses

import android.app.Application
import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.insteem.ipfreely.steem.CentralConstants
import com.insteem.ipfreely.steem.CentralConstantsOfSteem
import com.insteem.ipfreely.steem.Interfaces.GetFollowListsBack
import com.insteem.ipfreely.steem.Interfaces.GlobalInterface
import com.insteem.ipfreely.steem.Interfaces.JsonRpcResultInterface
import com.insteem.ipfreely.steem.RoomDatabaseApp.RoomRepos.FollowersRepo
import com.insteem.ipfreely.steem.VolleyRequest
import com.insteem.ipfreely.steem.jsonclasses.Block
import com.insteem.ipfreely.steem.jsonclasses.prof
import org.json.JSONObject
import java.util.*

/**
 * Created by boot on 3/12/2018.
 */
class GeneralRequestsFeedIntoConstants(context: Context):JsonRpcResultInterface {
    val applicationContext = context
    val globalInterface : GlobalInterface? = if(context is GlobalInterface) context else null
    val followlistinterface : GetFollowListsBack? = if(context is GetFollowListsBack) context else null
    var useDbCode = true
    var followers = ArrayList<prof.Resultfp>()
    var following = ArrayList<prof.Resultfp>()
    var followcount: prof.FollowCount? = null
    var followRepo:FollowersRepo? = null
    var deleDone = false
    var currRequestName = ""
    var totalSize = 0
    var gotTillNow = 0

    //used to construct the repo
    constructor(context:Context,application:Application) :this(context){
        //useDbCode = stopDbCode
        followRepo = FollowersRepo(application)
    }
    constructor(context:Context,followcounts: prof.FollowCount,stopDbCode:Boolean) :this(context){
        useDbCode = stopDbCode
        followcount = followcounts
    }

    /**
     * called when all the contants have to be loaded
     */
    fun RunThemAll(){
        GetDynamicGlobalProperties()
        GetRewardFund()
        GetPriceFeed()
    }


    /**
     * fetches the dynamic global properties to use for calculations
     */
    fun GetDynamicGlobalProperties(){
        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()

        val s = JsonObjectRequest(Request.Method.POST,url,d.globalProperties,
                Response.Listener { response ->

                    val gson = Gson()
                    var parse : Block.DynamicGlobalProperties = gson.fromJson(response.toString(), Block.DynamicGlobalProperties::class.java)
                    if(parse != null && parse.result != null){
                        //CentralConstantsOfSteem.getInstance().dynamicglobalprops = parse.result
                        val votePowerReserveRate = parse.result.votePowerReserveRate
                        val sps = SharedPrefrencesSingleton.getInstance(applicationContext)
                        sps.put(CentralConstants.dynamicBlockVotePowerReserveRate,votePowerReserveRate)
                        sps.put(CentralConstants.lastSaveTimeOfMedianandBase, Date().time)
                        sps.commit()
                        //CentralConstantsOfSteem.getInstance().resultfund = parse.result
                        val ccsi = CentralConstantsOfSteem.getInstance()
                        ccsi.dynamicVotePowerReserveRate = votePowerReserveRate
                    }
                }, Response.ErrorListener {
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }

    /**
     * make a request to fetch the reward fund for likes calculations
     */
    fun GetRewardFund(){
        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()
        val s = JsonObjectRequest(Request.Method.POST,url,d.rewardFund,
                Response.Listener { response ->
                    val parse : Block.rewardfund = Gson().fromJson(response.toString(), Block.rewardfund::class.java)
                    if(parse?.result != null){
                        val recenClaims = parse.result.recentClaims.toLong()
                        val rewardsFund = StaticMethodsMisc.convertRewardsToDouble(parse.result.rewardBalance)
                        val sps = SharedPrefrencesSingleton.getInstance(applicationContext)
                        sps.put(CentralConstants.resultFundRecentClaims,recenClaims)
                        sps.put(CentralConstants.resultFundRewardsBalance,rewardsFund)
                        sps.put(CentralConstants.lastSaveTimeOfMedianandBase, Date().time)
                        sps.commit()
                        //CentralConstantsOfSteem.getInstance().resultfund = parse.result
                        val ccsi = CentralConstantsOfSteem.getInstance()
                        ccsi.resultFundRecentClaims = recenClaims
                        ccsi.resultFundRewards = rewardsFund
                    }
                }, Response.ErrorListener {
        }

        )
        volleyre.addToRequestQueue(s)
    }


    /**
     * fetches the price feed from steem for calculations
     */
    fun GetPriceFeed(){
        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()

        val s = JsonObjectRequest(Request.Method.POST,url,d.priceFeed,
                Response.Listener { response ->

                    val gson = Gson()
                    val parse : Block.FeedHistoryPrice = gson.fromJson(response.toString(), Block.FeedHistoryPrice::class.java)
                    if(parse?.result != null){
                        val sps = SharedPrefrencesSingleton.getInstance(applicationContext)
                        val median = StaticMethodsMisc.convertMerBaseToDouble(parse.result.currentMedianHistory.base)
                        sps.put(CentralConstants.currentMedianHistoryBase,median)
                        sps.put(CentralConstants.lastSaveTimeOfMedianandBase, Date().time)
                        sps.commit()
                        val ccsi = CentralConstantsOfSteem.getInstance()
                        ccsi.currentMedianHistoryBase = median
                        //CentralConstantsOfSteem.getInstance().currentMedianHistory = parse.result.currentMedianHistory
                    }

                }, Response.ErrorListener {
        }

        )
        volleyre.addToRequestQueue(s)
    }


    /**
     * callback from the db with the total number of people in the db.
     * if the number of people has changed then a callback is made to begin changing items
     * @param count total number of people in db
     */
    override fun countGot(count: Int) {
        var foc = followcount?.result?.followerCount
        var floc = followcount?.result?.followingCount
        if(foc != null && floc != null){
            var tot = foc + floc
            totalSize = tot
            if(tot > count || tot < count){
                globalInterface?.followHasChanged()
            }
            //globalInterface?.followHasChanged()
        }
    }

    /**
     * deletes the follow db and waits for a callback to begin a sync
     */
    fun refreshFollowDbNow(){
        followRepo?.deleteAll(this)
    }

    /**
     * callback from the db. Begin the sync now
     */
    override fun deleDone() {
        GetFollowing(currRequestName,"",null)
        GetFollowers(currRequestName,"",null)
    }

    /**
     * fetches the number of followers and following a user has
     * @param name the user for whom to fetch
     * @param followcountlistener the listener to which results will be sent to or passed ahead for callbacks (nullable)
     * @param listener the lister which will listen to the result or passed ahead for callbacks (nullable)
     * @param followerlistener the listener to which results will be passed to or it is passed ahead for callbacks (nullable)
     */
    fun GetFollowCount(name:String, followcountlistener: Response.Listener<JSONObject>?  ,listener: Response.Listener<JSONObject>?,followerlistener: Response.Listener<JSONObject>?){
        currRequestName = name
        var listenerm: Response.Listener<JSONObject> =  Response.Listener { response ->
            val parse = Gson().fromJson(response.toString(), prof.FollowCount::class.java)
            if(parse?.result != null){
                this.followcount = parse
                followRepo?.getCount(this)
            }

        }
        if(followcountlistener != null){
            listenerm = followcountlistener
        }
        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()
        val s = JsonObjectRequest(Request.Method.POST,url,d.getFollowCount(name),
                listenerm, Response.ErrorListener {})
        volleyre.addToRequestQueue(s)
    }


    /**
     * fetches the followers for a user
     * @param name username of the user
     * @param start the user from where to start
     * @param listener the listener to which to deliver the result to (nullable)
     */
    fun GetFollowers(name:String,start:String,listener: Response.Listener<JSONObject>?){
        var listenerm: Response.Listener<JSONObject> =  Response.Listener { response ->
            var parse:prof.FollowNames? = null
            try {
                parse = Gson().fromJson(response.toString(), prof.FollowNames::class.java)
            } catch (ex:Exception){
                ex.printStackTrace()
            }
            if(parse?.result != null){
                followers.addAll(parse.result!! )
                if(!useDbCode){
                    followlistinterface?.GetFollowersList(parse.result as List<prof.Resultfp>)
                } else {
                    //add to db
                    followRepo?.insert(parse.result !!,true)
                    gotTillNow += parse.result!!.size
                    //callback the ui for updating the number of people

                    if(gotTillNow != 0 && followcount?.result?.followerCount != 0){
                        globalInterface?.followerProgress(gotTillNow,followcount?.result?.followerCount!!)
                    }

                }
                if(followers.size == followcount?.result?.followerCount){
                    //globalInterface?.notifyRequestMadeSuccess()

                    if(useDbCode){
                        /*var fold = FollowersDatabase(applicationContext)
                        var alldbpeople = fold.GetAllQuestions()
                        for(x in followers){
                            x.followInternal = MyOperationTypes.follow
                            if(fold.Insert(x)){
                                //search for the user and delte from the list
                                alldbpeople.remove(alldbpeople.find { t -> t.follower == x.follower })
                            } else {
                                //search for the user and delte from the list
                                alldbpeople.remove(alldbpeople.find { t -> t.follower == x.follower })
                            }
                        }
                        for(x in alldbpeople){
                            fold.deleteContact(x.dbid)
                        }*/
                        globalInterface?.followersDone()
                    } else{
                        followlistinterface?.FollowersDone()
                    }
                } else{
                    GetFollowers(name,followers.last().follower,listener)
                }

            }

        }
        if(listener != null){
            listenerm = listener
        }
        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()

        val s = JsonObjectRequest(Request.Method.POST,url,d.getFollowers(name,start),
                listenerm, Response.ErrorListener {})
        volleyre.addToRequestQueue(s)
    }


    /**
     * fetch users a user is following
     * @param name the username
     * @param start the user from whom to start from
     * @param listener the listener to which the result will be delivered to (nullable)
     */
    fun GetFollowing(name:String,start:String,listener: Response.Listener<JSONObject>?){
        var listenerm: Response.Listener<JSONObject> =  Response.Listener { response ->

            var parse:prof.FollowNames? = null
            try {
                parse = Gson().fromJson(response.toString(), prof.FollowNames::class.java)
            } catch (ex:Exception){
                ex.printStackTrace()
            }
            if(parse?.result != null){

                following.addAll (parse.result!!)
                if(!useDbCode){
                    followlistinterface?.GetFollowingList(parse.result!!)
                } else {
                    //update the db
                    followRepo?.insert(parse.result!!,false)
                    gotTillNow += parse.result!!.size
                    //callback so ui updates with the number
                    if(gotTillNow != 0 && followcount?.result?.followingCount != 0){
                        globalInterface?.followingProgress(gotTillNow,followcount?.result?.followingCount!!)
                    }

                }
                if(following.size == followcount?.result?.followingCount){
                    //globalInterface?.notifyRequestMadeSuccess()


                    if(useDbCode){
                        /*var fold = FollowingDatabase(applicationContext)
                        var alldbpeople = fold.GetAllQuestions()
                        for(x in following){
                            x.followInternal = MyOperationTypes.follow
                            if(fold.Insert(x)){
                                //search for the user and delte from the list
                                alldbpeople.remove(alldbpeople.find { t -> t.following == x.following })
                            } else {
                                //search for the user and delte from the list
                                alldbpeople.remove(alldbpeople.find { t -> t.following == x.following })
                            }
                        }
                        for(x in alldbpeople){
                            fold.deleteContact(x.dbid)
                        }*/
                        globalInterface?.followingDone()
                    }
                    else{
                        followlistinterface?.AllDone()
                    }

                } else{
                    GetFollowers(name,following.last().following,listener)
                }

            }

        }
        if(listener != null){
            listenerm = listener
        }
        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()

        val s = JsonObjectRequest(Request.Method.POST,url,d.getFollowing(name,start),
                listenerm
                , Response.ErrorListener {})
        volleyre.addToRequestQueue(s)
    }


    fun matchfollowsWithFollowers(){

    }

    fun matchfollowingWithFollowers(){

    }


}