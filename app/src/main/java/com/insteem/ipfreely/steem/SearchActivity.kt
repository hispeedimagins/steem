package com.insteem.ipfreely.steem

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.insteem.ipfreely.steem.DataHolders.FeedArticleDataHolder
import com.insteem.ipfreely.steem.DataHolders.GetReputationDataHolder
import com.insteem.ipfreely.steem.Databases.FollowersDatabase
import com.insteem.ipfreely.steem.Databases.RequestsDatabase
import com.insteem.ipfreely.steem.Enums.AdapterToUseFor
import com.insteem.ipfreely.steem.Enums.TypeOfRequest
import com.insteem.ipfreely.steem.HelperClasses.*
import com.insteem.ipfreely.steem.jsonclasses.AskSteemSearch
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.content_search.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


//this is the search activity
class SearchActivity : AppCompatActivity() {

    private var adapter: AllRecyclerViewAdapter? = null
    private var activity: Context? = null
    internal var recyclerView: RecyclerView? = null
    var username : String? = null
    var otherguy : String? = null
    var useOtherGuyOnly : Boolean? = false
    var key : String? = null
    var sw : swipecommonactionsclass? = null
    var followersDatabase : FollowersDatabase? = null
    var dblist = ArrayList<Long>()
    var usernum = 0
    var pgnum = 0
    var query:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        MiscConstants.ApplyMyTheme(this@SearchActivity)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            //if the FAB is hit, the user wants to search again, so start a search withing
            //the same activity
            //we do not need multiple search pages
            this.onSearchRequested()
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()*/
        }

        followersDatabase = FollowersDatabase(this@SearchActivity)
        recyclerView = list
        sw  = swipecommonactionsclass(swiperefreshsearch)
        adapter = AllRecyclerViewAdapter(this, ArrayList(), recyclerView as RecyclerView, dateanim, AdapterToUseFor.search)
        recyclerView?.itemAnimator = DefaultItemAnimator()
        recyclerView?.adapter = adapter
        FabHider(list,fab)
        val sharedPreferences = getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences?.getString(CentralConstants.username, null)
        key = sharedPreferences?.getString(CentralConstants.key, null)
        swiperefreshsearch.setOnRefreshListener {
            handleSearch(query)
        }
        //save instance data
        if(savedInstanceState != null){
            query = savedInstanceState.getString("query")
            usernum = savedInstanceState.getInt("usernum")
            pgnum = savedInstanceState.getInt("pgnum")

            var lar = savedInstanceState.getLongArray("dbitems")
            dblist.addAll(lar.toList())
            var db = RequestsDatabase(this@SearchActivity)
            for(x in lar){
                var req = db.GetAllQuestions(x)
                if(req != null){
                    //Since we save JSONObject , so we don't need json, just initialize

                    if(req.otherInfo == "users"){
                        //if they are items which come after the initial requests
                        var jso = JSONObject(req.json)
                        addUsers(jso)
                    } else {
                        //if this is the initial request data
                        //addItems(jso,GetNameToUse())
                        addPosts(req.json)
                    }
                }
            }
            //close db to prevent leakages
            db.close()

        } else {
            handleIntent(intent)
        }



    }

    override fun onNewIntent(intent: Intent?) {
        setIntent(intent)
        if(intent != null) handleIntent(intent)
        super.onNewIntent(intent)
    }

    //this is where we extract the search query
    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {

            val querys = intent.getStringExtra(SearchManager.QUERY)
            this.query = querys
            handleSearch(querys)

        }
    }

    //clear the adapter, run the progress bar and send the requests
    fun handleSearch(query:String){
        adapter?.clear()
        adapter?.notifyDataSetChanged()
        sw?.makeswiperun()
        GetUsers(query)
        GetPosts(query,1)
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putLongArray("dbitems",dblist.toLongArray())
        outState?.putString("query",query)
        outState?.putInt("usernum",usernum)
        outState?.putInt("pgnum",pgnum)
        super.onSaveInstanceState(outState)
    }



    //for displaying users
    fun display(users:List<GetReputationDataHolder>){

        adapter?.add("Users ${users.size}")
        for(x in users){
            adapter?.add(x)
        }
    }

    //for displaying posts
    fun display(posts:List<AskSteemSearch.Result>,fake:Boolean = false){
        sw?.makeswipestopDef()
        //adapter?.clear()
        //adapter?.notifyDataSetChanged()
        adapter?.add("Posts ${posts.size}")
        for(x in posts){
            adapter?.feedHelperFunctions?.add(getprocessedfeed(x))
        }
    }



    //generates the post data to my own class for making it easier
    fun getprocessedfeed(post:AskSteemSearch.Result) : FeedArticleDataHolder.FeedArticleHolder {
        //swipecommonactionsclass?.makeswipestop()
        //adapter.questionListFunctions.add(message)

        //val gson = Gson()

        var voted = false
        /*var repl : JSONArray? = null
        if(commstr.has("replies")){
            repl = commstr.getJSONArray("replies")
        }*/
        /*val collectionTypev = object : TypeToken<List<feed.avtiveVotes>>() {

        }.type*/
        var author = post.author
        //var vot = gson.fromJson<List<feed.avtiveVotes>>(commstr.getAsJsonArray("active_votes"),collectionTypev)
        /*if(commstr.getJSONArray("active_votes").toString().contains("\"voter\":\"$username\"")) voted = true
        if(checkforbots){
            val pattern = Pattern.compile(
                    nobots,
                    Pattern.CASE_INSENSITIVE)
            if(pattern.matcher(commstr.getJSONArray("active_votes").toString()).find() || author == "haejin"){
                return null
            }
        }*/

        /*var comv = commstr.getAsJsonArray("active_votes")
        if(comv != null){
            *//*if(commstr.get("active_votes").asString.contains(username)) voted = true*//*
            for(x in comv){
                if(x.asJsonObject.asString.contains(username)) voted = true
                //if(x.voter.equals(username)) voted = true
            }
        }*/
        /*var rpb = commstr.getJSONArray("reblogged_by")
        val collectionType = object : TypeToken<List<String>>() {

        }.type
        var ls = gson.fromJson<List<String>>(rpb.toString(),collectionType)*/

        //var st : String = commstr.getString("body")
        var st : String = post.summary
        //var builder = StringBuilder()


        //var autho = "$author (${StaticMethodsMisc.CalculateRepScore(commstr.getString("author_reputation"))})"
        var autho = author
        if(followersDatabase != null){
            if(author != username){
                autho += if(followersDatabase?.simpleSearch(author) as Boolean){
                    " follows you"
                } else{
                    ""
                }
            }
        }


        var jsonMetadata = post.meta//gson.fromJson<feed.JsonMetadataInner>(post.meta,feed.JsonMetadataInner::class.java)
        //var du = DateUtils.getRelativeDateTimeString(contex,(SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(commstr.getString("created"))).time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0)

        var d = calendarcalculations() //2018-02-03T13:58:18
        var dd = (SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(post.created) )
        d.setDateOfTheData(dd)
        var du = DateUtils.getRelativeDateTimeString(this@SearchActivity,d.getGmtToNormal()!!.timeInMillis, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0)

        var fd : FeedArticleDataHolder.FeedArticleHolder = FeedArticleDataHolder.FeedArticleHolder(
                displayName = autho,
                reblogBy = ArrayList(),
                reblogOn =  "" ,
                entryId =  0 ,
                active =  "",
                author = author,
                //body = if(bodyasis) st else builder.toString(),
                body = st,
                cashoutTime = "",
                //stability
                category = if(jsonMetadata?.tags != null) jsonMetadata?.tags?.first()!! else "",
                children = post.children,
                created = post.created,
                date = dd,
                createdcon = d.getDateTimeString(),
                depth = 0,
                id = 0,
                lastPayout = "",
                lastUpdate = "",
                netVotes = if(post.net_votes != null)post.net_votes else 0,
                permlink = post.permlink,
                rootComment =  0,
                title = post.title,
                format = "",
                app = "",
                image = jsonMetadata?.image,
                links = jsonMetadata?.links,
                tags = jsonMetadata?.tags,
                users = jsonMetadata?.users,
                authorreputation = "",
                pending_payout_value = post.payout?.toString(),
                promoted = "false",
                total_pending_payout_value = post.payout.toString(),
                uservoted = voted,
                already_paid = post.payout.toString(),
                summary = null,
                datespan = du.toString(),
                replies = null,
                activeVotes = null,
                rootAuthor = null,
                rootPermlink =null

        )
        //adapter?.feedHelperFunctions?.add(fd)
        return fd
    }


    //adding users, with page num support,
    //will add if needed by users
    fun addUsers(response:JSONObject,num:Int = 0){
        if(num != 0){
            usernum += num
        }
        val con = JsonRpcResultConversion(response,username as String, TypeOfRequest.SearchAccountReps,applicationContext)
        //con.ParseJsonBlog()
        val result = con.ParseRepAndSearch()
        display(result)
    }


    fun GetUsers(query:String,num:Int = 20){
        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        val url = CentralConstants.baseUrl(this)
        val d = MakeJsonRpc.getInstance()

        val s = JsonObjectRequest(Request.Method.POST,url,d.GetAccountRepsAndSearch(query,num),
                Response.Listener { response ->
                    /*val gson = Gson()
                    val collectionType = object : TypeToken<List<GetReputationDataHolder>>() {

                    }.type
                    var re = gson.fromJson<List<GetReputationDataHolder>>(response.getJSONArray("result").toString(),collectionType)
*/

                    val req = RequestsDatabase(this@SearchActivity)
                    //req.DeleteOld()
                    //close db to prevent leakages
                    req.close()
                    var ad = req.Insert(com.insteem.ipfreely.steem.DataHolders.Request(json = response.toString() ,dateLong = Date().time, typeOfRequest = TypeOfRequest.SearchUsers.name,otherInfo = "users"))
                    if(ad > 0){

                        dblist.add(ad)
                    }

                    /*val con = JsonRpcResultConversion(response,username as String, TypeOfRequest.SearchAccountReps,applicationContext)
                    //con.ParseJsonBlog()
                    val result = con.ParseRepAndSearch()
                    display(result)*/
                    addUsers(response)

                        /* if(result[0] != null){
                             val req = RequestsDatabase(this@ArticleActivity)
                             var ad = req.Insert(com.steemapp.lokisveil.steemapp.DataHolders.Request(json = Gson().toJson(result[0]) ,dateLong = Date().time, typeOfRequest = TypeOfRequest.blog.name,otherInfo = "article"))
                             if(ad > 0){
                                 dblist.add(ad)
                             }
                         }*/


                        //stop the comments spinner def.
                    //commentsFragment?.swipecommonactionsclass?.makeswipestopDef()



                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }

    //for adding posts, with page num
    //will implement if users need it
    fun addPosts(response:String,num:Int = 0){
        try{
            val g = Gson()
            var res = g.fromJson<AskSteemSearch.search>(response,AskSteemSearch.search::class.java)
            if(res != null && !res.error){
                if(num != 0){
                    pgnum += num
                }
                display(res.results)
            }
        } catch(ex:Exception){
            Log.d("err",ex.message)
        }

    }

    fun GetPosts(query:String,page:Int = 1){
        //val queue = Volley.newRequestQueue(context)

        //swipecommonactionsclass?.makeswiperun()

        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"


        //query using ask steem
        val url = "https://api.asksteem.com/search?q=$query&include=payout,meta&types=post&pg=$page"
        //val d = MakeJsonRpc.getInstance()
        //val g = Gson()


        val s = StringRequest(Request.Method.GET,url,Response.Listener { response ->
            val req = RequestsDatabase(this@SearchActivity)
            //req.DeleteOld()
            var ad = req.Insert(com.insteem.ipfreely.steem.DataHolders.Request(json = response ,dateLong = Date().time, typeOfRequest = TypeOfRequest.SearchPosts.name,otherInfo = "posts"))
            //close db to prevent leakages
            req.close()
            if(ad > 0){
                dblist.add(ad)
            }
            /*var res = g.fromJson<AskSteemSearch.search>(response,AskSteemSearch.search::class.java)
            if(res != null && !res.error){
                pgnum += page
                display(res.results)
            }*/
            addPosts(response,page)
        },
                Response.ErrorListener {

                }
                )

        volleyre.addToRequestQueue(s)
    }

}
