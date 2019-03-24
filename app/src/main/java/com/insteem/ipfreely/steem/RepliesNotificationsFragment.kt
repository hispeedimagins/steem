package com.insteem.ipfreely.steem

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.insteem.ipfreely.steem.DataHolders.FeedArticleDataHolder
import com.insteem.ipfreely.steem.Databases.RequestsDatabase
import com.insteem.ipfreely.steem.Enums.AdapterToUseFor
import com.insteem.ipfreely.steem.Enums.TypeOfRequest
import com.insteem.ipfreely.steem.HelperClasses.JsonRpcResultConversion
import com.insteem.ipfreely.steem.HelperClasses.MakeJsonRpc
import com.insteem.ipfreely.steem.HelperClasses.swipecommonactionsclass
import com.insteem.ipfreely.steem.dummy.DummyContent.DummyItem
import org.json.JSONObject
import java.util.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [RepliesNotificationsFragment.OnListFragmentInteractionListener] interface.
 */
class RepliesNotificationsFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null
    internal var swipecommonactionsclass: swipecommonactionsclass? = null

    private var fragmentActivity: FragmentActivity? = null

    private var adapter: AllRecyclerViewAdapter? = null
    private var activity: Context? = null
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
    var dblist = java.util.ArrayList<Long>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_repliesnotifications_list, container, false)
        val sharedPreferences = view?.context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences?.getString(CentralConstants.username, null)
        key = sharedPreferences?.getString(CentralConstants.key, null)
        // Set the adapter
        var recy = view?.findViewById<RecyclerView>(R.id.list)
        if (recy is RecyclerView) {
            with(recy) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

            }
        }

        adapter = AllRecyclerViewAdapter(getActivity() as FragmentActivity, ArrayList(), recy as RecyclerView, view as View, AdapterToUseFor.commentNoti)
        //adapter?.setEmptyView(view?.findViewById(R.id.toDoEmptyView))

        recy.setItemAnimator(DefaultItemAnimator())
        recy.setAdapter(adapter)

        recy.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    visibleItemCount = recyclerView.childCount
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







        activity = getActivity()?.applicationContext
        var swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout_reps) as SwipeRefreshLayout

        swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            refreshcontent()
        })

        swipecommonactionsclass = swipecommonactionsclass(swipeRefreshLayout)
        fragmentActivity = getActivity()

        if (!tokenisrefreshingholdon) {
            //getQuestionsNow(false);
        } else if (startwasjustrun) {
            //getQuestionsNow(true)

        }
        //check for instance
        if(savedInstanceState != null){
            //initialize variables from the state
            val sharedPreferences = view.context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
            username = sharedPreferences?.getString(CentralConstants.username, null)
            key = sharedPreferences?.getString(CentralConstants.key, null)
            startAuthor = savedInstanceState.getString("startau")
            startPermlink = savedInstanceState.getString("startperm")
            startTag = savedInstanceState.getString("starttag")
            otherguy = savedInstanceState.getString("otherguy")
            /*var asl = savedInstanceState?.getSerializable("feedlist")
            adapter?.add(if(asl != null) asl as List<Any> else java.util.ArrayList<Any>())*/
            var lar = savedInstanceState.getLongArray("dbitems")
            dblist.addAll(lar.toList())
            //var gson = Gson()
            //restore data from the db
            var db = RequestsDatabase(context!!)
            for(x in lar){
                var req = db.GetAllQuestions(x)
                if(req != null){
                    var jso = JSONObject(req.json)
                    if(req.otherInfo == "more"){
                        addMoreItems(jso,GetNameToUse())
                    } else {
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


    //save to state
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
        swipecommonactionsclass?.makeswipestop()
        adapter?.clear()
        adapter?.notifyDataSetChanged()
        GetFeed()
    }


    fun clear(){
        adapter?.clear()
    }





    fun displayMessageFeddArticle(result: List<FeedArticleDataHolder.FeedArticleHolder>) {
        loading = false
        adapter?.commentNotiHelperFunctions?.add(result)
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
    }

    fun GetFeed(){
        swipecommonactionsclass?.makeswiperun()

        if(username == null){
            val sharedPreferences = view?.context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
            username = sharedPreferences?.getString(CentralConstants.username, null)
            key = sharedPreferences?.getString(CentralConstants.key, null)
        }

        val volleyre : VolleyRequest = VolleyRequest.getInstance(context)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = CentralConstants.baseUrl(context)
        val d = MakeJsonRpc.getInstance()
        var nametouse = GetNameToUse()
        val s = JsonObjectRequest(Request.Method.POST,url,d.getFeedJ(nametouse,"recent-replies"),
                Response.Listener { response ->
                    if(context != null){
                        val req = RequestsDatabase(context!!)
                        var ad = req.Insert(com.insteem.ipfreely.steem.DataHolders.Request(json = response.toString() ,dateLong = Date().time, typeOfRequest = TypeOfRequest.replies.name,otherInfo = "repliesfirst"))
                        if(ad > 0){
                            dblist.add(ad)
                        }
                    }
                    addItems(response,nametouse)

                }, Response.ErrorListener {

        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }

    //get name to use
    fun GetNameToUse():String{
        var nametouse : String =  if(username != null) username as String else ""
        if(otherguy != null){
            nametouse = otherguy as String
        }
        return nametouse
    }

    fun addItems(response:JSONObject,nametouse:String){
        val con = JsonRpcResultConversion(response,nametouse, TypeOfRequest.replies,context as Context)
        //con.ParseJsonBlog()
        val result = con.ParseJsonBlog()
        //val result = gson.fromJson<List<feed.FeedData>>(response.toString(),collectionType)
        displayMessageFeddArticle(result)
    }

    fun GetMoreItems(){
        swipecommonactionsclass?.makeswiperun()
        val volleyre : VolleyRequest = VolleyRequest.getInstance(context)
        val url = CentralConstants.baseUrl(context)
        val d = MakeJsonRpc.getInstance()
        var nametouse = GetNameToUse()
        val s = JsonObjectRequest(Request.Method.POST,url,d.getMoreItems(startAuthor,startPermlink,startTag,"get_replies_by_last_update"),
                Response.Listener { response ->
                    loading = false

                    //save to db, id goes to state
                    if(context != null){
                        val req = RequestsDatabase(context!!)
                        var ad = req.Insert(com.insteem.ipfreely.steem.DataHolders.Request(json = response.toString(),dateLong = Date().time, typeOfRequest = TypeOfRequest.replies.name,otherInfo = "more"))
                        if(ad > 0){
                            dblist.add(ad)
                        }
                    }
                    addMoreItems(response,nametouse)

                }, Response.ErrorListener {
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }

    fun addMoreItems(response:JSONObject,nametouse:String){
        val con = JsonRpcResultConversion(response,nametouse, TypeOfRequest.replies,context as Context)
        //con.ParseJsonBlog()
        val result = con.ParseJsonBlogMore()
        //val result = gson.fromJson<feed.FeedMoreItems>(response.toString(),feed.FeedMoreItems::class.java)
        displayMessageFeddArticle(result)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            //throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: DummyItem?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                RepliesNotificationsFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
