package com.steemapp.lokisveil.steemapp

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.View
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.Fragments.upvotesFragment
import com.steemapp.lokisveil.steemapp.HelperClasses.StaticMethodsMisc
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.jsonclasses.BusyNotificationJson
import com.steemapp.lokisveil.steemapp.jsonclasses.feed

import kotlinx.android.synthetic.main.activity_user_upvote.*
import kotlinx.android.synthetic.main.content_user_upvote.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class UserUpvoteActivity : AppCompatActivity() {
    private var adapter: AllRecyclerViewAdapter? = null
    private var activity: Context? = null
    internal var recyclerView: RecyclerView? = null
    var username : String? = null
    var otherguy : String? = null
    var useOtherGuyOnly : Boolean? = false
    var key : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        MiscConstants.ApplyMyThemeArticle(this@UserUpvoteActivity)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_upvote)
        /*setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }*/


        recyclerView = list

        adapter = AllRecyclerViewAdapter(this, ArrayList(), recyclerView as RecyclerView, dateanim, AdapterToUseFor.upvotes)
        //adapter?.setEmptyView(view?.findViewById(R.id.toDoEmptyView))

        recyclerView?.itemAnimator = DefaultItemAnimator()
        recyclerView?.adapter = adapter
        val sharedPreferences = getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences?.getString(CentralConstants.username, null)
        key = sharedPreferences?.getString(CentralConstants.key, null)
        display(CentralConstantsOfSteem.getInstance().jsonArray)
    }


    //sort the likes by the value of vote, reverse it and return
    //we reverse as the data is not sorted by descending
    private fun sortList(data:List<feed.avtiveVotes>):List<feed.avtiveVotes>{
        return data.sortedWith(compareBy({it.votevalforsorting})).reversed()
    }




    fun display(jsonArray : JSONArray){
        var con = FollowApiConstants.getInstance()
        var al = ArrayList<feed.avtiveVotes>()
        for(x in 0 until jsonArray.length()){

            // try catch because some json which is not correctly made can crash the app
            var jo : JSONObject? = null
            try {
                jo = jsonArray.getJSONObject(x)
            } catch (ex:Exception){

            }
            //check for null
            if(jo != null){
                var dat : Date = StaticMethodsMisc.FormatDateGmt(jo.getString("time"))
                var du = DateUtils.getRelativeDateTimeString(applicationContext,dat.time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0)
                var votvalr = StaticMethodsMisc.VotingValueSteemToSd(StaticMethodsMisc.CalculateVotingValueRshares(jo.getString("rshares")))
                var av = feed.avtiveVotes(
                        voter = jo.getString("voter"),
                        calculatedpercent = "Vote percent :"+  (jo.getString("percent").toInt() / 100).toString(),
                        calculatedrep = StaticMethodsMisc.CalculateRepScore(jo.getString("reputation")),
                        calculatedrshares = StaticMethodsMisc.FormatVotingValueToSBD(votvalr),
                        votevalforsorting = votvalr,
                        calculatedtime = "",
                        calculatedvotepercent = "Vote power :"+ (jo.getString("weight").toInt() / 100).toString(),
                        dateString = du.toString(),
                        date = dat,
                        namewithrep = "${jo.getString("voter")} (${StaticMethodsMisc.CalculateRepScore(jo.getString("reputation"))})",
                        percent = "",
                        reputation = "",
                        rshares = "",
                        time = "",
                        weight = "",
                        followInternal = if(!con.following.isEmpty() && con.following.any { p -> p.following == jo.getString("voter") }) MyOperationTypes.unfollow else MyOperationTypes.follow
                )
                al.add(av)
            }
            /*jo = jsonArray.getJSONObject(x)*/


        }
        adapter?.upvotesHelperFunctions?.add(sortList(al))
        //adapter?.add(sortList(al))
        /*for(x in activevotes){
            x.namewithrep = "${x.voter} (${StaticMethodsMisc.CalculateRepScore(x.reputation)})"
            x.calculatedpercent = "Vote percent :"+  ((x.percent as String).toInt() / 100).toString()
            x.calculatedvotepercent = "Vote power :"+ ((x.weight as String).toInt() / 100).toString()
            var du = DateUtils.getRelativeDateTimeString(applicationContext,(SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(x.time)).time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0)
            x.dateString = du.toString()

            x.calculatedrshares = StaticMethodsMisc.FormatVotingValueToSBD(StaticMethodsMisc.VotingValueSteemToSd(StaticMethodsMisc.CalculateVotingValueRshares(x.rshares)))
            if(!con.following.isEmpty() && con.following.any { p -> p.following == x.voter }){
                x.followInternal = MyOperationTypes.unfollow
            }
            else {
                x.followInternal = MyOperationTypes.follow
            }
            x.percent = ""
            x.weight = ""
            x.time = ""
            x.rshares = ""
            x.reputation = ""
            adapter?.add(x)
        }*/
    }

}
