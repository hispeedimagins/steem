package com.steemapp.lokisveil.steemapp.HelperClasses

import android.app.Application
import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.steemapp.lokisveil.steemapp.CentralConstants
import com.steemapp.lokisveil.steemapp.CentralConstantsOfSteem
import com.steemapp.lokisveil.steemapp.Databases.FollowersDatabase
import com.steemapp.lokisveil.steemapp.Databases.FollowingDatabase
import com.steemapp.lokisveil.steemapp.FollowApiConstants
import com.steemapp.lokisveil.steemapp.Interfaces.GetFollowListsBack
import com.steemapp.lokisveil.steemapp.Interfaces.GlobalInterface
import com.steemapp.lokisveil.steemapp.Interfaces.JsonRpcResultInterface
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomRepos.FollowersRepo
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.VolleyRequest
import com.steemapp.lokisveil.steemapp.jsonclasses.Block
import com.steemapp.lokisveil.steemapp.jsonclasses.prof
import org.json.JSONObject
import java.util.function.Predicate

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

    /*init {
        globalInterface  = if(context is GlobalInterface) context else null
        followlistinterface  = if(context is GetFollowListsBack) context else null
    }*/
    fun RunThemAll(){
        GetDynamicGlobalProperties()
        GetRewardFund()
        GetPriceFeed()
    }







    fun GetDynamicGlobalProperties(){
        //val queue = Volley.newRequestQueue(context)

        //swipecommonactionsclass?.makeswiperun()
        //Toast.makeText(applicationContext,"Processing. Please wait....", Toast.LENGTH_LONG).show()
        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()

        val s = JsonObjectRequest(Request.Method.POST,url,d.globalProperties,
                Response.Listener { response ->

                    val gson = Gson()
                    var parse : Block.DynamicGlobalProperties = gson.fromJson(response.toString(), Block.DynamicGlobalProperties::class.java)
                    if(parse != null && parse.result != null){
                        CentralConstantsOfSteem.getInstance().dynamicglobalprops = parse.result
                    }

                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }

    fun GetRewardFund(){
        //val queue = Volley.newRequestQueue(context)
        //swipecommonactionsclass?.makeswiperun()
        //Toast.makeText(applicationContext,"Processing. Please wait....", Toast.LENGTH_LONG).show()
        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()

        val s = JsonObjectRequest(Request.Method.POST,url,d.rewardFund,
                Response.Listener { response ->

                    val gson = Gson()
                    var parse : Block.rewardfund = gson.fromJson(response.toString(), Block.rewardfund::class.java)
                    if(parse != null && parse.result != null){
                        CentralConstantsOfSteem.getInstance().resultfund = parse.result
                    }

                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }

    fun GetPriceFeed(){
        //val queue = Volley.newRequestQueue(context)

        //swipecommonactionsclass?.makeswiperun()
        //Toast.makeText(applicationContext,"Processing. Please wait....", Toast.LENGTH_LONG).show()
        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()

        val s = JsonObjectRequest(Request.Method.POST,url,d.priceFeed,
                Response.Listener { response ->

                    val gson = Gson()
                    var parse : Block.FeedHistoryPrice = gson.fromJson(response.toString(), Block.FeedHistoryPrice::class.java)
                    if(parse != null && parse.result != null){
                        CentralConstantsOfSteem.getInstance().currentMedianHistory = parse.result.currentMedianHistory
                    }

                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
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

    fun GetFollowCount(name:String, followcountlistener: Response.Listener<JSONObject>?  ,listener: Response.Listener<JSONObject>?,followerlistener: Response.Listener<JSONObject>?){
        currRequestName = name
        var listenerm: Response.Listener<JSONObject> =  Response.Listener { response ->

            val gson = Gson()
            var parse = gson.fromJson(response.toString(), prof.FollowCount::class.java)
            if(parse != null && parse.result != null){
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


    fun GetFollowers(name:String,start:String,listener: Response.Listener<JSONObject>?){
        //val queue = Volley.newRequestQueue(context)
        var listenerm: Response.Listener<JSONObject> =  Response.Listener { response ->

            val gson = Gson()
            var parse = gson.fromJson(response.toString(), prof.FollowNames::class.java)
            if(parse != null && parse.result != null){

                followers.addAll(parse.result!! )
                if(!useDbCode){
                    followlistinterface?.GetFollowersList(parse.result as List<prof.Resultfp>)
                } else {
                    //add to db
                    followRepo?.insert(parse.result !!,true)
                    gotTillNow += parse.result!!.size
                    //callback the ui for updating the number of people
                    globalInterface?.followerProgress(gotTillNow,followcount?.result?.followerCount!!)
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


    fun GetFollowing(name:String,start:String,listener: Response.Listener<JSONObject>?){
        //val queue = Volley.newRequestQueue(context)
        var listenerm: Response.Listener<JSONObject> =  Response.Listener { response ->

            val gson = Gson()
            var parse = gson.fromJson(response.toString(), prof.FollowNames::class.java)
            if(parse != null && parse.result != null){

                following.addAll (parse.result !!)
                if(!useDbCode){
                    followlistinterface?.GetFollowingList(parse.result !!)
                } else {
                    //update the db
                    followRepo?.insert(parse.result !!,false)
                    gotTillNow += parse.result!!.size
                    //callback so ui updates with the number
                    globalInterface?.followingProgress(gotTillNow,followcount?.result?.followingCount!!)
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


}