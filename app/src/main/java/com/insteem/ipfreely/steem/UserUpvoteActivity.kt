package com.insteem.ipfreely.steem

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.insteem.ipfreely.steem.Enums.AdapterToUseFor
import com.insteem.ipfreely.steem.HelperClasses.StaticMethodsMisc
import com.insteem.ipfreely.steem.RoomDatabaseApp.RoomViewModels.ArticleRoomVM
import com.insteem.ipfreely.steem.SteemBackend.Config.Enums.MyOperationTypes
import com.insteem.ipfreely.steem.jsonclasses.feed
import kotlinx.android.synthetic.main.activity_user_upvote.*
import kotlinx.android.synthetic.main.content_user_upvote.*
import org.json.JSONArray
import org.json.JSONObject


class UserUpvoteActivity : AppCompatActivity() {
    private var adapter: AllRecyclerViewAdapter? = null
    private var activity: Context? = null
    internal var recyclerView: RecyclerView? = null
    var username : String? = null
    var otherguy : String? = null
    var useOtherGuyOnly : Boolean? = false
    var key : String? = null
    var dbKey = 0
    lateinit var articleVm: ArticleRoomVM
    //private var res: FeedArticleDataHolder.FeedArticleHolder? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        MiscConstants.ApplyMyThemeArticle(this@UserUpvoteActivity)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_upvote)

        recyclerView = list

        adapter = AllRecyclerViewAdapter(this, ArrayList(), recyclerView as RecyclerView, dateanim, AdapterToUseFor.upvotes)
        //adapter?.setEmptyView(view?.findViewById(R.id.toDoEmptyView))

        recyclerView?.itemAnimator = DefaultItemAnimator()
        recyclerView?.adapter = adapter
        val sharedPreferences = getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences?.getString(CentralConstants.username, null)
        key = sharedPreferences?.getString(CentralConstants.key, null)

        if(intent != null){
            dbKey = intent.getIntExtra("dbId",0)
        }
        if(dbKey != 0){
            articleVm = ViewModelProviders.of(this@UserUpvoteActivity).get(ArticleRoomVM::class.java)
            articleVm.getActiveVotes(dbKey).observe(this, Observer {
                if(it != null){
                    display(it)
                }
            })
        } else {
            display(CentralConstantsOfSteem.getInstance().jsonArray)
        }

    }


    //sort the likes by the value of vote, reverse it and return
    //we reverse as the data is not sorted by descending
    private fun sortList(data:List<feed.avtiveVotes>):List<feed.avtiveVotes>{
        return data.sortedWith(compareBy({it.votevalforsorting})).reversed()
    }




    fun display(jsonArray : JSONArray?){
        if(jsonArray == null) return
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
                var dat = if(jo.has("time")) StaticMethodsMisc.FormatDateGmt(jo.getString("time")) else null
                var du = if(dat != null) MiscConstants.dateToRelDate(dat,this) else ""
                var votvalr = StaticMethodsMisc.VotingValueSteemToSd(StaticMethodsMisc.CalculateVotingValueRshares(jo.getString("rshares")))
                var av = feed.avtiveVotes(
                        voter = jo.getString("voter"),
                        calculatedpercent = "Vote percent :"+  (jo.getString("percent").toInt() / 100).toString(),
                        calculatedrep = StaticMethodsMisc.CalculateRepScore(jo.getString("reputation")),
                        calculatedrshares = StaticMethodsMisc.FormatVotingValueToSBD(votvalr),
                        votevalforsorting = votvalr,
                        calculatedtime = "",
                        calculatedvotepercent = if(jo.has("weight"))"Vote power :"+ (jo.getString("weight").toInt() / 100).toString() else "",
                        dateString = du.toString(),
                        date = dat,
                        namewithrep = "${jo.getString("voter")} (${(jo.getString("reputation"))})",
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
