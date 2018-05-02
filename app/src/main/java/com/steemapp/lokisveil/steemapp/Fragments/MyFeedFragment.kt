package com.steemapp.lokisveil.steemapp.Fragments


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.steemapp.lokisveil.steemapp.AllRecyclerViewAdapter
import com.steemapp.lokisveil.steemapp.CentralConstants
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.Enums.TypeOfRequest
import com.steemapp.lokisveil.steemapp.HelperClasses.JsonRpcResultConversion
import com.steemapp.lokisveil.steemapp.HelperClasses.MakeJsonRpc
import com.steemapp.lokisveil.steemapp.HelperClasses.calendarcalculations
import com.steemapp.lokisveil.steemapp.HelperClasses.swipecommonactionsclass
import com.steemapp.lokisveil.steemapp.Interfaces.GlobalInterface

import com.steemapp.lokisveil.steemapp.R
import com.steemapp.lokisveil.steemapp.VolleyRequest
import com.steemapp.lokisveil.steemapp.jsonclasses.feed
import java.text.SimpleDateFormat
import java.util.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [MyFeedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyFeedFragment : Fragment() {

    // TODO: Rename and change types of parameters
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
    internal var tokenisrefreshingholdon = false
    internal var startwasjustrun = false
    var username : String? = null
    var otherguy : String? = null
    var useOtherGuyOnly : Boolean? = false
    var key : String? = null
    var startAuthor : String? = null
    var startPermlink : String? = null
    var startTag : String? = null
    var globalInterface : GlobalInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments?.getString(ARG_PARAM1)
            mParam2 = arguments?.getString(ARG_PARAM2)
            otherguy = arguments?.getString(CentralConstants.OtherGuyNamePasser, null)
            useOtherGuyOnly = arguments?.getBoolean(CentralConstants.OtherGuyUseOtherGuyOnly,false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if(view == null){
            view = inflater!!.inflate(R.layout.fragment_my_feed, container, false)
            recyclerView = view?.findViewById(R.id.feedFragment)
            adapter = AllRecyclerViewAdapter(getActivity() as FragmentActivity, ArrayList(), recyclerView as RecyclerView, view as View, AdapterToUseFor.feed)
            //adapter?.setEmptyView(view?.findViewById(R.id.toDoEmptyView))

            recyclerView?.setItemAnimator(DefaultItemAnimator())
            recyclerView?.setAdapter(adapter)

            recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    /*if(dy > 0){

                            dab.hide();
                        }
                        else {
                            dab.show();
                        }*/

                    if (dy > 0) {
                        visibleItemCount = recyclerView!!.childCount
                        totalItemCount = adapter?.getItemCount() as Int
                        //pastVisiblesItems = recyclerView.findFirstVisibleItemPosition();
                        pastVisiblesItems = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                        var loadmore = false
                        if (totalItemCount >= 20) {
                            loadmore = true
                            totalItemCount -= 10
                        }

                        if (!loading) {
                            if (visibleItemCount + pastVisiblesItems >= totalItemCount && loadmore) {

                                loading = true
                                if(startAuthor != null){
                                    GetMoreItems()
                                }
                                //getMoreQuestionsNow()
                                Log.d("...", "Last Item Wow !")
                                //Do pagination.. i.e. fetch new data
                            }
                        }
                    }
                }
            })

        }





        activity = getActivity()?.applicationContext
        //activity = view?.context
        swipeRefreshLayout = view?.findViewById<SwipeRefreshLayout>(R.id.activity_feed_swipe_refresh_layout) as SwipeRefreshLayout

        swipeRefreshLayout?.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refreshcontent()
        })

        swipecommonactionsclass = swipecommonactionsclass(swipeRefreshLayout as SwipeRefreshLayout)
        fragmentActivity = getActivity()

        val sharedPreferences = context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences?.getString(CentralConstants.username, null)
        key = sharedPreferences?.getString(CentralConstants.key, null)

        if (!tokenisrefreshingholdon) {
            //getQuestionsNow(false);
        } else if (startwasjustrun) {
            //getQuestionsNow(true)

        }

        GetFeed()
        return view
        //return inflater!!.inflate(R.layout.fragment_my_feed, container, false)
    }


    private fun refreshcontent() {
        tokenisrefreshingholdon = false
        //swipecommonactionsclass?.makeswipestop()
        adapter?.clear()
        adapter?.notifyDataSetChanged()
        /*adapter = new MyquestionslistRecyclerViewAdapter(new ArrayList<ForReturningQuestionsLite>(), mListener);
        recyclerView.setAdapter(adapter);*/
        //getQuestionsNow(false)

        GetFeed()
    }


    fun runstartagain() {
        tokenisrefreshingholdon = false
        startwasjustrun = true
        swipecommonactionsclass?.makeswipestop()
        if (adapter != null) {
            adapter?.clear()
            adapter?.notifyDataSetChanged()
        } else {
            if (recyclerView != null) {

                adapter = AllRecyclerViewAdapter(getActivity() as FragmentActivity, ArrayList(), recyclerView as RecyclerView, view as View, AdapterToUseFor.blog)

                recyclerView?.setAdapter(adapter)

            }

        }

        /*adapter = new MyquestionslistRecyclerViewAdapter(new ArrayList<ForReturningQuestionsLite>(), mListener);
        recyclerView.setAdapter(adapter);*/
        //getQuestionsNow(true)
        GetFeed()
    }


    fun displayMessage(result: feed.Comment) {
        //swipecommonactionsclass?.makeswipestop()
        //adapter.questionListFunctions.add(message)
        val gson = Gson()

        var voted = false

        if(result.active_voted != null){

            for(x in result.active_voted){

                if(x.voter.equals(username)) voted = true
            }
        }

        var st : String? = result.body
        if(st?.length != null && st?.length > 300){
            st = st.substring(0,300)
        }
        var splitstring : List<String> = st?.split("\n") as List<String>
        var builder = StringBuilder()
        if(splitstring != null){

            for (x in splitstring){
                if(!x.contains("**") && !x.contains("[")&& !x.contains("<")){
                    builder.append(x)
                }
            }
        }

        result.jsonMetadata = gson.fromJson<feed.JsonMetadataInner>(result.jsonMetadataString, feed.JsonMetadataInner::class.java)
        var d = calendarcalculations() //2018-02-03T13:58:18
        var du = DateUtils.getRelativeDateTimeString(activity,(SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(result.created)).time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0)

        d.setDateOfTheData((SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(result.created) ))
        var fd : FeedArticleDataHolder.FeedArticleHolder = FeedArticleDataHolder.FeedArticleHolder(
                reblogBy = result.reblogBy,
                reblogOn = result.reblogOn,
                entryId = result.entryId,
                active =  result.active,
                author = result.author,
                body = builder.toString(),
                cashoutTime = result.cashoutTime,
                category = result.category,
                children = result.children,
                created = result.created,
                createdcon = d.getDateTimeString(),
                depth = result.depth,
                id = result.id,
                lastPayout = result.lastPayout,
                lastUpdate = result.lastUpdate,
                netVotes = result.netVotes,
                permlink = result.permlink,
                rootComment = result.rootComment,
                title = result.title,
                format = result.jsonMetadata?.format,
                app = result.jsonMetadata?.app,
                image = result.jsonMetadata?.image,
                links = result.jsonMetadata?.links,
                tags = result.jsonMetadata?.tags,
                users = result.jsonMetadata?.users,
                authorreputation = result.authorreputation,
                pending_payout_value = result.pending_payout_value,
                promoted = result.promoted,
                total_pending_payout_value = result.total_pending_payout_value,
                uservoted = voted,
                already_paid = result.totalPayoutValue,
                summary = null,
                datespan = du.toString()

        )
        adapter?.feedHelperFunctions?.add(fd)

    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is GlobalInterface) {
            globalInterface = context
        } else {
            //throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        globalInterface = null
    }

    fun displayMessage(result: List<feed.Comment>) {


        loading = false

        for (a in result){

            displayMessage(a)
        }
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
        swipecommonactionsclass?.makeswipestop()
        //adapter.questionListFunctions.add(questionsList)

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

        swipecommonactionsclass?.makeswipestop()
        //adapter.questionListFunctions.add(questionsList)

    }

    fun GetFeed(){
        swipecommonactionsclass?.makeswiperun()
        /*val queue = Volley.newRequestQueue(context)

        val url = "https://api.steemjs.com/get_blog?account=$username&limit=10"
        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener { response ->
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
                    val result = gson.fromJson<List<feed.FeedData>>(response,collectionType)
                    if(result != null && !result.isEmpty()){


                        //displayMessage(result)
                    }

                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)*/
        //val queue = Volley.newRequestQueue(context)
        val volleyre : VolleyRequest = VolleyRequest.getInstance(activity)
        if(username == null){
            val sharedPreferences = view?.context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
            username = sharedPreferences?.getString(CentralConstants.username, null)
            key = sharedPreferences?.getString(CentralConstants.key, null)
        }
        val url = "https://api.steemit.com/"
        val d = MakeJsonRpc.getInstance()
        val g = Gson()
        var nametouse : String = if(username != null) username as String else ""
        if(otherguy != null){
            nametouse = otherguy as String
        }
        val s = JsonObjectRequest(Request.Method.POST,url,d.getBlogJ(nametouse),
                Response.Listener { response ->
                    // Display the first 500 characters of the response string.
                    //mTextView.setText("Response is: "+ response.substring(0,500));
                    //swipecommonactionsclassT.makeswipestop()
                    /*val gson = Gson()
                    val result = gson.fromJson<JsonTenorResultTrending>(response, JsonTenorResultTrending::class.java!!)
                    for (s in result.results) {
                        tenoradapter.add(s.media.get(0))
                    }*/
                    val gson = Gson()
                    val collectionType = object : TypeToken<List<feed.FeedData>>() {

                    }.type
                    val con = JsonRpcResultConversion(response,nametouse as String, TypeOfRequest.blog,activity as Context)
                    //con.ParseJsonBlog()
                    val result = con.ParseJsonBlog()
                    //val result = gson.fromJson<List<feed.FeedData>>(response.toString(),collectionType)
                    if(result != null && !result.isEmpty()){


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

        val volleyre : VolleyRequest = VolleyRequest.getInstance(activity)

        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()
        var nametouse : String = username as String
        if(otherguy != null){
            nametouse = otherguy as String
        }
        val s = JsonObjectRequest(Request.Method.POST,url,d.getMoreItems(startAuthor,startPermlink,startTag,true),
                Response.Listener { response ->
                    loading = false
                    //val gson = Gson()
                    /*val collectionType = object : TypeToken<List<feed.Comment>>() {

                    }.type*/
                    //val con = JsonRpcResultConversion(response.toString(),username as String,TypeOfRequest.feed)
                    //con.ParseJsonBlog()
                    //val result = con.ParseJsonBlog()
                    val con = JsonRpcResultConversion(response,nametouse as String, TypeOfRequest.blog,if(activity != null) activity as Context else globalInterface?.getContextMine() as Context)
                    //con.ParseJsonBlog()
                    val result = con.ParseJsonBlogMore()
                    //val result = gson.fromJson<feed.FeedMoreItems>(response.toString(),feed.FeedMoreItems::class.java)
                    if(result != null && !result.isEmpty()){


                        displayMessageFeddArticle(result)
                    }
                    else{
                        displayMessageFeddArticle(ArrayList<FeedArticleDataHolder.FeedArticleHolder>())
                    }

                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyFeedFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): MyFeedFragment {
            val fragment = MyFeedFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
