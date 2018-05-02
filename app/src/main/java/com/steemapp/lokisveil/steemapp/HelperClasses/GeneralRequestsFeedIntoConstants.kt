package com.steemapp.lokisveil.steemapp.HelperClasses

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
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.VolleyRequest
import com.steemapp.lokisveil.steemapp.jsonclasses.Block
import com.steemapp.lokisveil.steemapp.jsonclasses.prof
import org.json.JSONObject

/**
 * Created by boot on 3/12/2018.
 */
class GeneralRequestsFeedIntoConstants(context: Context) {
    val applicationContext = context
    val globalInterface : GlobalInterface? = if(context is GlobalInterface) context else null
    val followlistinterface : GetFollowListsBack? = if(context is GetFollowListsBack) context else null
    var useDbCode = true
    var followers: List<prof.Resultfp> = java.util.ArrayList()
    var following: List<prof.Resultfp> = java.util.ArrayList()
    var followcount: prof.FollowCount? = null
    constructor(context:Context,stopDbCode:Boolean) :this(context){
        useDbCode = stopDbCode
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




    fun GetFollowCount(name:String, followcountlistener: Response.Listener<JSONObject>?  ,listener: Response.Listener<JSONObject>?,followerlistener: Response.Listener<JSONObject>?){
        //val queue = Volley.newRequestQueue(context)
        var listenerm: Response.Listener<JSONObject> =  Response.Listener { response ->

            val gson = Gson()
            var parse = gson.fromJson(response.toString(), prof.FollowCount::class.java)
            if(parse != null && parse.result != null){
                /*FollowApiConstants.getInstance().followCount = parse*/
                this.followcount = parse
                /*StaticMethodsMisc.MakeFollowRequests(parse,applicationContext,listener)
                StaticMethodsMisc.MakeFollowRequestsFollowers(parse,applicationContext,followerlistener)*/
                GetFollowers(name,"",followerlistener)
                GetFollowing(name,"",listener)
            }

        }
        if(followcountlistener != null){
            listenerm = followcountlistener
        }

        //swipecommonactionsclass?.makeswiperun()
        //Toast.makeText(applicationContext,"Processing. Please wait....", Toast.LENGTH_LONG).show()
        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()

        val s = JsonObjectRequest(Request.Method.POST,url,d.getFollowCount(name),
                listenerm
                /*Response.Listener { response ->

                    val gson = Gson()
                    var parse = gson.fromJson(response.toString(), prof.FollowCount::class.java)
                    if(parse != null && parse.result != null){

                    }

                }*/, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }


    fun GetFollowers(name:String,start:String,listener: Response.Listener<JSONObject>?){
        //val queue = Volley.newRequestQueue(context)
        var listenerm: Response.Listener<JSONObject> =  Response.Listener { response ->

            val gson = Gson()
            var parse = gson.fromJson(response.toString(), prof.FollowNames::class.java)
            if(parse != null && parse.result != null){

                followers += (parse.result as List<prof.Resultfp>)
                if(!useDbCode){
                    followlistinterface?.GetFollowersList(parse.result as List<prof.Resultfp>)
                }
                if(followers.size == followcount?.result?.followerCount){
                    //globalInterface?.notifyRequestMadeSuccess()

                    if(useDbCode){
                        var fold = FollowersDatabase(applicationContext)
                        var alldbpeople = fold.GetAllQuestions()
                        for(x in followers){
                            x.followInternal = MyOperationTypes.follow
                            if(fold.Insert(x)){
                                alldbpeople.remove(x)
                            } else {
                                alldbpeople.remove(x)
                            }
                        }
                        for(x in alldbpeople){
                            fold.deleteContact(x.follower)
                        }
                        globalInterface?.followersDone()
                    } else{
                        followlistinterface?.FollowersDone()
                    }

                    //FollowApiConstants.getInstance().followers = ArrayList()
                } else{
                    GetFollowers(name,followers.last().follower,listener)
                }

            }

        }
        if(listener != null){
            listenerm = listener
        }

        //swipecommonactionsclass?.makeswiperun()
        //Toast.makeText(applicationContext,"Processing. Please wait....", Toast.LENGTH_LONG).show()
        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()

        val s = JsonObjectRequest(Request.Method.POST,url,d.getFollowers(name,start),
                listenerm
                /*Response.Listener { response ->

                    val gson = Gson()
                    var parse = gson.fromJson(response.toString(), prof.FollowCount::class.java)
                    if(parse != null && parse.result != null){

                    }

                }*/, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }


    fun GetFollowing(name:String,start:String,listener: Response.Listener<JSONObject>?){
        //val queue = Volley.newRequestQueue(context)
        var listenerm: Response.Listener<JSONObject> =  Response.Listener { response ->

            val gson = Gson()
            var parse = gson.fromJson(response.toString(), prof.FollowNames::class.java)
            if(parse != null && parse.result != null){

                following += (parse.result as List<prof.Resultfp>)
                if(!useDbCode){
                    followlistinterface?.GetFollowingList(parse.result as List<prof.Resultfp>)
                }
                if(following.size == followcount?.result?.followingCount){
                    //globalInterface?.notifyRequestMadeSuccess()

                    if(useDbCode){
                        var fold = FollowingDatabase(applicationContext)
                        var alldbpeople = fold.GetAllQuestions()
                        for(x in following){
                            x.followInternal = MyOperationTypes.follow
                            if(fold.Insert(x)){
                                alldbpeople.remove(x)
                            } else {
                                alldbpeople.remove(x)
                            }
                        }
                        for(x in alldbpeople){
                            fold.deleteContact(x.following)
                        }
                        globalInterface?.followingDone()
                    }
                    else{
                        followlistinterface?.AllDone()
                    }


                    //FollowApiConstants.getInstance().following = ArrayList()
                } else{
                    GetFollowers(name,following.last().following,listener)
                }

            }

        }
        if(listener != null){
            listenerm = listener
        }

        //swipecommonactionsclass?.makeswiperun()
        //Toast.makeText(applicationContext,"Processing. Please wait....", Toast.LENGTH_LONG).show()
        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()

        val s = JsonObjectRequest(Request.Method.POST,url,d.getFollowing(name,start),
                listenerm
                /*Response.Listener { response ->

                    val gson = Gson()
                    var parse = gson.fromJson(response.toString(), prof.FollowCount::class.java)
                    if(parse != null && parse.result != null){

                    }

                }*/, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }




}