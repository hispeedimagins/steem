package com.steemapp.lokisveil.steemapp

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.Enums.TypeOfRequest
import com.steemapp.lokisveil.steemapp.HelperClasses.JsonRpcResultConversion
import com.steemapp.lokisveil.steemapp.HelperClasses.MakeJsonRpc
import com.steemapp.lokisveil.steemapp.HelperClasses.TagRequestHelper
import com.steemapp.lokisveil.steemapp.HelperClasses.swipecommonactionsclass
import com.steemapp.lokisveil.steemapp.Interfaces.TagsInterface
import com.steemapp.lokisveil.steemapp.R
import com.steemapp.lokisveil.steemapp.jsonclasses.feed

import kotlinx.android.synthetic.main.activity_main_tags.*
import kotlinx.android.synthetic.main.content_main_tags.*
import java.util.ArrayList

class MainTags : AppCompatActivity(), TagsInterface {
    override fun okclicked(originalval: String, tag: String, limit: String, request: String) {
        adapter?.clear()
        adapter?.notifyDataSetChanged()

        maintag  = tag
        mainrequest = request
        originalrequest = originalval

        supportActionBar?.title = "${originalrequest.toUpperCase()} - ${maintag.toUpperCase()}"
        GetFeed()
    }


    private var mParam1: String? = null
    private var mParam2: String? = null

    internal var swipecommonactionsclass: swipecommonactionsclass? = null

    private var fragmentActivity: android.support.v4.app.FragmentActivity? = null

    private var adapter: AllRecyclerViewAdapter? = null
    private var activity: Context? = null
    internal var view: View? = null
    internal var swipeRefreshLayout: SwipeRefreshLayout? = null
    internal var recyclerView: RecyclerView? = null
    private var loading = false
    internal var pastVisiblesItems: Int = 0
    internal var visibleItemCount:Int = 0
    internal var totalItemCount:Int = 0
    internal var totalItemCountMatch:Int = 20
    internal var totalItemCountSubtractor:Int = 10
    internal var tokenisrefreshingholdon = false
    internal var startwasjustrun = false
    var username : String? = null
    var otherguy : String? = null
    var useOtherGuyOnly : Boolean? = false
    var key : String? = null

    var startAuthor : String? = null
    var startPermlink : String? = null
    var startTag : String? = null

    var maintag : String = ""
    var mainrequest = ""
    var originalrequest = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        //apply app theme
        MiscConstants.ApplyMyTheme(this@MainTags)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_tags)
        setSupportActionBar(toolbar)



        val sharedPreferences = getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences?.getString(CentralConstants.username, null)
        key = sharedPreferences?.getString(CentralConstants.key, null)

        adapter = AllRecyclerViewAdapter(this@MainTags, ArrayList(), list, dateanim, AdapterToUseFor.feed)
        //adapter?.setEmptyView(view?.findViewById(R.id.toDoEmptyView))

        list.setItemAnimator(DefaultItemAnimator())
        list.setAdapter(adapter)


        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                /*if(dy > 0){

                        dab.hide();
                    }
                    else {
                        dab.show();
                    }*/

                if (dy > 0) {
                    visibleItemCount = list.childCount
                    totalItemCount = adapter?.getItemCount() as Int
                    //pastVisiblesItems = recyclerView.findFirstVisibleItemPosition();
                    pastVisiblesItems = (list.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    var loadmore = false
                    if (totalItemCount >= totalItemCountMatch) {
                        loadmore = true
                        totalItemCount -= totalItemCountSubtractor
                    }

                    if (!loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount && loadmore) {

                            loading = true
                            //getMoreQuestionsNow()
                            if(startAuthor != null){
                                GetMoreItems()
                            }
                            Log.d("...", "Last Item Wow !")
                            //Do pagination.. i.e. fetch new data
                        }
                    }
                }
            }
        })



        var i = intent
        if(i != null && i.extras != null){
            var extras = i.extras
            maintag = extras.getString(CentralConstants.MainTag)
            mainrequest = extras.getString(CentralConstants.MainRequest)
            originalrequest = extras.getString(CentralConstants.OriginalRequest)
            supportActionBar?.title = "${originalrequest.toUpperCase()} - ${maintag.toUpperCase()}"
            //supportActionBar?.title = articlepermlink
            //toolbar.title = articlepermlink
        }


        activity_feed_swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refreshcontent()
        })

        swipecommonactionsclass = swipecommonactionsclass(activity_feed_swipe_refresh_layout)


        GetFeed()

    }

    private fun refreshcontent() {
        tokenisrefreshingholdon = false
        swipecommonactionsclass?.makeswipestop()
        adapter?.clear()
        adapter?.notifyDataSetChanged()
        /*adapter = new MyquestionslistRecyclerViewAdapter(new ArrayList<ForReturningQuestionsLite>(), mListener);
        recyclerView.setAdapter(adapter);*/
        //getQuestionsNow(false)

        GetFeed()
    }


    fun displayMessageFeddArticle(result: List<FeedArticleDataHolder.FeedArticleHolder>) {


        loading = false

        /*for (a in result){

            displayMessage(a)
        }*/
        adapter?.feedHelperFunctions?.add(result)
        if(result.isNotEmpty()){
            var lc = result[result.size - 1]
            if(lc != null){
                var nametouse : String = username as String
                if(otherguy != null){
                    nametouse = otherguy as String
                }
                startAuthor = lc.author
                startPermlink = lc.permlink
                startTag = nametouse
            }
        }

        totalItemCountMatch = result.size
        totalItemCountSubtractor = result.size / 2

        swipecommonactionsclass?.makeswipestop()
        //adapter.questionListFunctions.add(questionsList)

    }

    fun GetFeed(){
        //val queue = Volley.newRequestQueue(context)

        swipecommonactionsclass?.makeswiperun()

        if(username == null){
            val sharedPreferences = view?.context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
            username = sharedPreferences?.getString(CentralConstants.username, null)
            key = sharedPreferences?.getString(CentralConstants.key, null)
        }

        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = "https://api.steemit.com/"
        val d = MakeJsonRpc.getInstance()
        //val g = Gson()
        var nametouse : String =  username as String
        if(otherguy != null){
            nametouse = otherguy as String
        }
        //val j = d.feed //g.fromJson(d.feed, JSONObject::class.java)
        //val jj = d.getFeed(true)
        /*val gs = GsonRequest(url,null,null,j,jj,

                Response.Listener<String> { response ->
                    // Display the first 500 characters of the response string.
                    //mTextView.setText("Response is: "+ response.substring(0,500));
                    //swipecommonactionsclassT.makeswipestop()
                    *//*val gson = Gson()
                    val result = gson.fromJson<JsonTenorResultTrending>(response, JsonTenorResultTrending::class.java!!)
                    for (s in result.results) {
                        tenoradapter.add(s.media.get(0))
                    }*//*
                    val gson = Gson()
                    val collectionType = object : TypeToken<List<feed.FeedData>>() {

                    }.type
                    val con = JsonRpcResultConversion(response.toString(),username as String,TypeOfRequest.feed)
                    con.ParseJsonBlog()
                    val result = gson.fromJson<List<feed.FeedData>>(response.toString(),collectionType)
                    if(result != null && !result.isEmpty()){


                        displayMessage(result)
                    }

                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

                )*/
        val s = JsonObjectRequest(Request.Method.POST,url,d.getTagQuery(mainrequest,maintag,"20"),
                Response.Listener { response ->
                    // Display the first 500 characters of the response string.
                    //mTextView.setText("Response is: "+ response.substring(0,500));
                    //swipecommonactionsclassT.makeswipestop()
                    /*val gson = Gson()
                    val result = gson.fromJson<JsonTenorResultTrending>(response, JsonTenorResultTrending::class.java!!)
                    for (s in result.results) {
                        tenoradapter.add(s.media.get(0))
                    }*/
                    //var res : Int = response
                    val gson = Gson()
                    val collectionType = object : TypeToken<List<feed.FeedData>>() {

                    }.type
                    val con = JsonRpcResultConversion(response,nametouse as String, TypeOfRequest.feed,applicationContext)
                    //con.ParseJsonBlog()
                    //val result = con.ParseJsonBlog()
                    val result = con.ParseJsonBlogMore()
                    //val result = gson.fromJson<List<feed.FeedData>>(response.toString(),collectionType)
                    if(result != null && !result.isEmpty()){

                        /*adapter?.feedHelperFunctions.add(result)*/
                        //displayMessage(result)
                        displayMessageFeddArticle(result)
                    }

                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }

    fun GetMoreItems(){
        //val queue = Volley.newRequestQueue(context)

        swipecommonactionsclass?.makeswiperun()

        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)

        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()
        var nametouse : String = username as String
        if(otherguy != null){
            nametouse = otherguy as String
        }
        val s = JsonObjectRequest(Request.Method.POST,url,d.getMoreItems(startAuthor,startPermlink,maintag,mainrequest),
                Response.Listener { response ->
                    loading = false
                    /*val gson = Gson()
                    val collectionType = object : TypeToken<List<feed.Comment>>() {

                    }.type*/
                    //val con = JsonRpcResultConversion(response.toString(),username as String,TypeOfRequest.feed)
                    //con.ParseJsonBlog()
                    //val result = con.ParseJsonBlog()
                    val con = JsonRpcResultConversion(response,nametouse as String, TypeOfRequest.blog,applicationContext)
                    //con.ParseJsonBlog()
                    val result = con.ParseJsonBlogMore()
                    //val result = gson.fromJson<feed.FeedMoreItems>(response.toString(),feed.FeedMoreItems::class.java)
                    if(result != null && !result.isEmpty()){


                        displayMessageFeddArticle(result)
                    }
                    else{
                        displayMessageFeddArticle(ArrayList<FeedArticleDataHolder.FeedArticleHolder>())
                    }
                    /*val result = gson.fromJson<feed.FeedMoreItems>(response.toString(),feed.FeedMoreItems::class.java)
                    if(result != null && !result.comment.isEmpty()){


                        displayMessage(result.comment)
                    }*/

                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }







    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_tags, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.research -> {
                var s = TagRequestHelper(this@MainTags,this)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }*/
    }




