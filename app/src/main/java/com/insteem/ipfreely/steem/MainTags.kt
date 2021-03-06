package com.insteem.ipfreely.steem

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.insteem.ipfreely.steem.DataHolders.FeedArticleDataHolder
import com.insteem.ipfreely.steem.Enums.AdapterToUseFor
import com.insteem.ipfreely.steem.Enums.TypeOfRequest
import com.insteem.ipfreely.steem.HelperClasses.JsonRpcResultConversion
import com.insteem.ipfreely.steem.HelperClasses.MakeJsonRpc
import com.insteem.ipfreely.steem.HelperClasses.TagRequestHelper
import com.insteem.ipfreely.steem.HelperClasses.swipecommonactionsclass
import com.insteem.ipfreely.steem.Interfaces.JsonRpcResultInterface
import com.insteem.ipfreely.steem.Interfaces.TagsInterface
import kotlinx.android.synthetic.main.activity_main_tags.*
import kotlinx.android.synthetic.main.content_main_tags.*
import java.util.*

/**
 * Class used to display the tag search results
 */
class MainTags : AppCompatActivity(), TagsInterface,JsonRpcResultInterface {


    override fun errorWhileParsing(error: String) {
        Toast.makeText(this,error,Toast.LENGTH_LONG).show()
        swipecommonactionsclass?.makeswipestop()
    }


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

    private var fragmentActivity: FragmentActivity? = null

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
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

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

    /**
     * Called when the content has to be refreshed
     */
    private fun refreshcontent() {
        tokenisrefreshingholdon = false
        swipecommonactionsclass?.makeswipestop()
        adapter?.clear()
        adapter?.notifyDataSetChanged()
        GetFeed()
    }


    /**
     * called when we have to display a list of the tag results
     */
    fun displayMessageFeddArticle(result: List<FeedArticleDataHolder.FeedArticleHolder>) {
        loading = false
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

    /**
     * inital call, loads all the tag results
     */
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
        val url = CentralConstants.baseUrl(this)
        val d = MakeJsonRpc.getInstance()
        //val g = Gson()
        var nametouse : String =  username as String
        if(otherguy != null){
            nametouse = otherguy as String
        }
        val s = JsonObjectRequest(Request.Method.POST,url,d.getTagQuery(mainrequest,maintag,"20"),
                Response.Listener { response ->
                    val con = JsonRpcResultConversion(response,nametouse, TypeOfRequest.feed,applicationContext)
                    val result = con.ParseJsonBlogMore(false,this)
                    if(!result.isEmpty()){
                        displayMessageFeddArticle(result)
                    }

                }, Response.ErrorListener {
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }

    /**
     * fetches more tag items
     */
    fun GetMoreItems(){
        //val queue = Volley.newRequestQueue(context)

        swipecommonactionsclass?.makeswiperun()

        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)

        val url = CentralConstants.baseUrl(applicationContext)
        val d = MakeJsonRpc.getInstance()
        var nametouse : String = username as String
        if(otherguy != null){
            nametouse = otherguy as String
        }
        val s = JsonObjectRequest(Request.Method.POST,url,d.getMoreItems(startAuthor,startPermlink,maintag,mainrequest),
                Response.Listener { response ->
                    loading = false
                    val con = JsonRpcResultConversion(response,nametouse as String, TypeOfRequest.blog,applicationContext)
                    val result = con.ParseJsonBlogMore(false,this)
                    if(!result.isEmpty()){


                        displayMessageFeddArticle(result)
                    }
                    else{
                        displayMessageFeddArticle(ArrayList<FeedArticleDataHolder.FeedArticleHolder>())
                    }

                }, Response.ErrorListener {
        }

        )
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
}




