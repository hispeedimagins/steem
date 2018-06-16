package com.steemapp.lokisveil.steemapp.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.steemapp.lokisveil.steemapp.AllRecyclerViewAdapter
import com.steemapp.lokisveil.steemapp.CentralConstants
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Databases.RequestsDatabase
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.Enums.TypeOfRequest
import com.steemapp.lokisveil.steemapp.HelperClasses.JsonRpcResultConversion
import com.steemapp.lokisveil.steemapp.HelperClasses.MakeJsonRpc
import com.steemapp.lokisveil.steemapp.HelperClasses.swipecommonactionsclass
import com.steemapp.lokisveil.steemapp.R
import com.steemapp.lokisveil.steemapp.VolleyRequest
import com.steemapp.lokisveil.steemapp.jsonclasses.feed
import org.apache.commons.lang3.StringEscapeUtils.escapeJson
import org.apache.commons.lang3.StringEscapeUtils.unescapeJson
import org.json.JSONObject
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FeedFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FeedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FeedFragment : Fragment() {

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
    var dblist = ArrayList<Long>()

    

    private var mListener: OnFragmentInteractionListener? = null

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
        val sharedPreferences = view?.context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences?.getString(CentralConstants.username, null)
        key = sharedPreferences?.getString(CentralConstants.key, null)
        if(view == null){
            view = inflater!!.inflate(R.layout.fragment_feed, container, false)
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

        }





        activity = getActivity()?.applicationContext
        swipeRefreshLayout = view?.findViewById<SwipeRefreshLayout>(R.id.activity_feed_swipe_refresh_layout) as SwipeRefreshLayout

        swipeRefreshLayout?.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refreshcontent()
        })

        swipecommonactionsclass = swipecommonactionsclass(swipeRefreshLayout as SwipeRefreshLayout)
        fragmentActivity = getActivity()

        /*val sharedPreferences = context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences?.getString(CentralConstants.username, null)
        key = sharedPreferences?.getString(CentralConstants.key, null)*/

        if (!tokenisrefreshingholdon) {
            //getQuestionsNow(false);
        } else if (startwasjustrun) {
            //getQuestionsNow(true)

        }

        //check instance
        if(savedInstanceState != null){
            //if yes, get all the variables back
            val sharedPreferences = view?.context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
            username = sharedPreferences?.getString(CentralConstants.username, null)
            key = sharedPreferences?.getString(CentralConstants.key, null)
            startAuthor = savedInstanceState?.getString("startau")
            startPermlink = savedInstanceState?.getString("startperm")
            startTag = savedInstanceState?.getString("starttag")
            otherguy = savedInstanceState?.getString("otherguy")
            /*var asl = savedInstanceState?.getSerializable("feedlist")
            adapter?.add(if(asl != null) asl as List<Any> else java.util.ArrayList<Any>())*/

            //get db items and add them to the dblist variable for future reference
            var lar = savedInstanceState.getLongArray("dbitems")
            dblist.addAll(lar.toList())
            var db = RequestsDatabase(context!!)
            for(x in lar){
                var req = db.GetAllQuestions(x)
                if(req != null){
                    //Since we save JSONObject , so we don't need json, just initialize
                    var jso = JSONObject(req.json)
                    if(req.otherInfo == "more"){
                        //if they are items which come after the initial requests
                        addMoreItems(jso,GetNameToUse())
                    } else {
                        //if this is the initial request data
                        addItems(jso,GetNameToUse())
                    }
                }
            }
            //var islas = true

        } else {
            GetFeed()
        }

        return view
    }



    //save stuff to the state
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //outState.putSerializable("feedlist",adapter?.getList() as Serializable)
        outState.putLongArray("dbitems",dblist.toLongArray())
        outState.putString("startau",startAuthor)
        outState.putString("startperm",startPermlink)
        outState.putString("starttag",startTag)
        outState.putString("otherguy",otherguy)
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

                adapter = AllRecyclerViewAdapter(getActivity() as FragmentActivity, ArrayList(), recyclerView as RecyclerView, view as View, AdapterToUseFor.feed)

                recyclerView?.setAdapter(adapter)

            }

        }

        /*adapter = new MyquestionslistRecyclerViewAdapter(new ArrayList<ForReturningQuestionsLite>(), mListener);
        recyclerView.setAdapter(adapter);*/
        //getQuestionsNow(true)
        GetFeed()
    }


/*    fun displayMessage(result: feed.Comment) {
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

        result.jsonMetadata = gson.fromJson<feed.JsonMetadataInner>(result.jsonMetadataString,feed.JsonMetadataInner::class.java)
        var du = DateUtils.getRelativeDateTimeString(context,(SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(result.created)).time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0)

        var d = calendarcalculations() //2018-02-03T13:58:18
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

    }*/


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
        //val queue = Volley.newRequestQueue(context)

        swipecommonactionsclass?.makeswiperun()

        if(username == null){
            val sharedPreferences = view?.context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
            username = sharedPreferences?.getString(CentralConstants.username, null)
            key = sharedPreferences?.getString(CentralConstants.key, null)
        }

        val volleyre : VolleyRequest = VolleyRequest.getInstance(context)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = "https://api.steemit.com/"
        val d = MakeJsonRpc.getInstance()

        var nametouse = GetNameToUse()

        val s = JsonObjectRequest(Request.Method.POST,url,d.getFeedJ(nametouse),
                Response.Listener { response ->

                    //save request to db, id goes to state
                    if(context != null){
                        val req = RequestsDatabase(context!!)
                        var ad = req.Insert(com.steemapp.lokisveil.steemapp.DataHolders.Request(json = response.toString() ,dateLong = Date().time, typeOfRequest = TypeOfRequest.feed.name,otherInfo = "feedfirst"))
                        if(ad > 0){
                            dblist.add(ad)
                        }
                    }

                    addItems(response,nametouse)

                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

                )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }

    //which name to use, user or the otherguy?
    fun GetNameToUse():String{
        var nametouse : String =  if(username != null) username as String else ""
        if(otherguy != null){
            nametouse = otherguy as String
        }
        return nametouse
    }

    fun GetMoreItems(){
        //val queue = Volley.newRequestQueue(context)

        swipecommonactionsclass?.makeswiperun()

        val volleyre : VolleyRequest = VolleyRequest.getInstance(context)

        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()
        /*var nametouse : String =  if(username != null) username as String else ""
        if(otherguy != null){
            nametouse = otherguy as String
        }*/
        var nametouse = GetNameToUse()
        val s = JsonObjectRequest(Request.Method.POST,url,d.getMoreItems(startAuthor,startPermlink,startTag,false),
                Response.Listener { response ->
                    loading = false

                    /*val gson = Gson()
                    val collectionType = object : TypeToken<List<feed.Comment>>() {

                    }.type*/

                    //add items to the db, id goes to the state
                    if(context != null){
                        val req = RequestsDatabase(context!!)
                        var ad = req.Insert(com.steemapp.lokisveil.steemapp.DataHolders.Request(json = response.toString(),dateLong = Date().time, typeOfRequest = TypeOfRequest.blog.name,otherInfo = "more"))
                        if(ad > 0){
                            dblist.add(ad)
                        }
                    }

                    addMoreItems(response,nametouse)


                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }

    fun addMoreItems(response:JSONObject,nametouse:String){
        val con = JsonRpcResultConversion(response,nametouse as String, TypeOfRequest.blog,context as Context)
        //con.ParseJsonBlog()
        val result = con.ParseJsonBlogMore()
        //val result = gson.fromJson<feed.FeedMoreItems>(response.toString(),feed.FeedMoreItems::class.java)
        if(result != null && !result.isEmpty()){


            displayMessageFeddArticle(result)
        }
        else{
            displayMessageFeddArticle(ArrayList<FeedArticleDataHolder.FeedArticleHolder>())
        }
    }

    fun addItems(response:JSONObject,nametouse:String){
        val con = JsonRpcResultConversion(response,nametouse as String,TypeOfRequest.feed,context as Context)
        //con.ParseJsonBlog()
        val result = con.ParseJsonBlog()
        //val result = gson.fromJson<List<feed.FeedData>>(response.toString(),collectionType)
        if(result != null && !result.isEmpty()){

            /*adapter?.feedHelperFunctions.add(result)*/
            //displayMessage(result)
            displayMessageFeddArticle(result)
        }
    }
    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            //throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
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
         * @return A new instance of fragment FeedFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FeedFragment {
            val fragment = FeedFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
