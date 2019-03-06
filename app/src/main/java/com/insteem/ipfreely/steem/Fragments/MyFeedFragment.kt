package com.insteem.ipfreely.steem.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.insteem.ipfreely.steem.*
import com.insteem.ipfreely.steem.DataHolders.FeedArticleDataHolder
import com.insteem.ipfreely.steem.Enums.AdapterToUseFor
import com.insteem.ipfreely.steem.Enums.TypeOfRequest
import com.insteem.ipfreely.steem.HelperClasses.FabHider
import com.insteem.ipfreely.steem.HelperClasses.JsonRpcResultConversion
import com.insteem.ipfreely.steem.HelperClasses.MakeJsonRpc
import com.insteem.ipfreely.steem.HelperClasses.swipecommonactionsclass
import com.insteem.ipfreely.steem.Interfaces.GlobalInterface
import com.insteem.ipfreely.steem.Interfaces.JsonRpcResultInterface
import com.insteem.ipfreely.steem.RoomDatabaseApp.RoomViewModels.ArticleRoomVM
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 * Use the [MyFeedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyFeedFragment : Fragment() , JsonRpcResultInterface {

    /**
     * add to the database, callback from the processing class
     */
    override fun insert(data: FeedArticleDataHolder.FeedArticleHolder) {
        var nametouse : String = username as String
        if(otherguy != null){
            nametouse = otherguy as String
        }
        //save for pagination
        startAuthor = data.author
        startPermlink = data.permlink
        startTag = nametouse
        vm?.insert(data)
    }

    override fun deleDone() {
        refreshcontent()
    }

    override fun processingDone(count: Int) {
        swipecommonactionsclass?.makeswipestop()
    }
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    internal var swipecommonactionsclass: swipecommonactionsclass? = null

    private var fragmentActivity: FragmentActivity? = null

    private var adapter: AllRecyclerViewClassPaged? = null
    private var adapterNormal: AllRecyclerViewAdapter? = null
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
    var vm : ArticleRoomVM? = null
    var lastSaveTime = 0L
    var lastSaveOneCheck = true
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
            view = inflater.inflate(R.layout.fragment_my_feed, container, false)
            recyclerView = view?.findViewById(R.id.feedFragment)
            //adapter = AllRecyclerViewClassPaged(getActivity() as FragmentActivity, recyclerView as RecyclerView, view as View, AdapterToUseFor.feed)
            //adapter?.setEmptyView(view?.findViewById(R.id.toDoEmptyView))

            //check if this is for the users blog or someone else's
            if(GetNameToUseOtherGuy()){
                adapter = AllRecyclerViewClassPaged(getActivity() as FragmentActivity, recyclerView as RecyclerView, view as View, AdapterToUseFor.feed)
                vm = ViewModelProviders.of(this).get(ArticleRoomVM::class.java)
                vm?.getPagedUpdatedListTime(true)?.observe(this, Observer { pagedList ->
                    if(pagedList != null && pagedList.size > 0){
                        //submit the list to the adapter
                        adapter?.submitList(pagedList as PagedList<Any>)

                        if(lastSaveOneCheck){
                            val fir = pagedList.firstOrNull()
                            if(lastSaveOneCheck && lastSaveTime != 0L && fir != null){
                                if(lastSaveTime < fir.saveTime){
                                    recyclerView?.scrollToPosition(0)
                                }
                                lastSaveOneCheck = false
                            }

                            if(lastSaveTime == 0L){
                                lastSaveTime = pagedList.first().saveTime
                            }
                        }

                        //swipecommonactionsclass?.makeswipestop()
                    } else if(pagedList != null && pagedList.size == 0){
                        adapter?.submitList(pagedList as PagedList<Any>)
                        //swipecommonactionsclass?.makeswipestop()
                    }

                })
                recyclerView?.setItemAnimator(DefaultItemAnimator())
                recyclerView?.setAdapter(adapter)
            } else {
                //use the normal adapter as we do not need to save the other guys
                //articles in the db as of now
                adapterNormal = AllRecyclerViewAdapter(getActivity() as FragmentActivity, ArrayList() ,recyclerView as RecyclerView, view as View, AdapterToUseFor.feed)
                recyclerView?.setItemAnimator(DefaultItemAnimator())
                recyclerView?.setAdapter(adapterNormal)
            }



            //init fabhider to hide the FAB on scroll
            FabHider(recyclerView,globalInterface?.getFab())
            recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        visibleItemCount = recyclerView!!.childCount
                        totalItemCount = if(adapter != null) adapter?.getItemCount()!! else adapterNormal?.itemCount!!
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
        swipeRefreshLayout = view?.findViewById<SwipeRefreshLayout>(R.id.activity_feed_swipe_refresh_layout) as SwipeRefreshLayout

        swipeRefreshLayout?.setOnRefreshListener {
            vm?.deleteAll(true,this)
            //refreshcontent()
        }

        swipecommonactionsclass = swipecommonactionsclass(swipeRefreshLayout as SwipeRefreshLayout)
        fragmentActivity = getActivity()

        val sharedPreferences = context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences?.getString(CentralConstants.username, null)
        key = sharedPreferences?.getString(CentralConstants.key, null)

        /*if (!tokenisrefreshingholdon) {
            //getQuestionsNow(false);
        } else if (startwasjustrun) {
            //getQuestionsNow(true)
        }*/

        if(savedInstanceState == null) GetFeed()
        return view
    }

    private fun refreshcontent() {
        tokenisrefreshingholdon = false
        //adapter?.clear()
        //adapter?.notifyDataSetChanged()
        adapterNormal?.clear()
        adapterNormal?.notifyDataSetChanged()

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

                adapter = AllRecyclerViewClassPaged(getActivity() as FragmentActivity, recyclerView as RecyclerView, view as View, AdapterToUseFor.blog)

                recyclerView?.setAdapter(adapter)

            }

        }
        GetFeed()
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


    fun displayMessageFeddArticle(result: List<FeedArticleDataHolder.FeedArticleHolder>) {


        loading = false
        adapterNormal?.feedHelperFunctions?.add(result)
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

        val volleyre : VolleyRequest = VolleyRequest.getInstance(activity)
        if(username == null){
            val sharedPreferences = view?.context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
            username = sharedPreferences?.getString(CentralConstants.username, null)
            key = sharedPreferences?.getString(CentralConstants.key, null)
        }
        val url = "https://api.steemit.com/"
        val d = MakeJsonRpc.getInstance()
        val g = Gson()
        var nametouse = GetNameToUse()
        val s = JsonObjectRequest(Request.Method.POST,url,d.getBlogJ(nametouse),
                Response.Listener { response ->
                    addItems(response,nametouse)
                }, Response.ErrorListener {})
        volleyre.addToRequestQueue(s)

    }


    //which name to use? user or otherguy?
    fun GetNameToUse():String{
        var nametouse : String =  if(username != null) username as String else ""
        if(otherguy != null){
            nametouse = otherguy as String
        }
        return nametouse
    }

    fun GetNameToUseOtherGuy():Boolean{
        return otherguy == null
    }


    fun GetMoreItems(){
        swipecommonactionsclass?.makeswiperun()

        val volleyre : VolleyRequest = VolleyRequest.getInstance(activity)

        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()
        var nametouse = GetNameToUse()
        val s = JsonObjectRequest(Request.Method.POST,url,d.getMoreItems(startAuthor,startPermlink,startTag,true),
                Response.Listener { response ->
                    loading = false
                    addMoreItems(response,nametouse)
                }, Response.ErrorListener {})
        volleyre.addToRequestQueue(s)
    }

    /**
     * process the result and add it to the adapter/db
     * @param response the jsoobject response to process
     * @param nametouse the name for whom we are processing
     */
    fun addMoreItems(response:JSONObject,nametouse:String){
        //if context is null try to load the activity context
        val con = JsonRpcResultConversion(response,nametouse,
                TypeOfRequest.blog,
                if(context != null) context as Context else this.activity?.applicationContext!!,
                if(GetNameToUseOtherGuy())this else null,true)
        val result = con.ParseJsonBlogMore()
        if(result != null && !result.isEmpty()){
            displayMessageFeddArticle(result)
        }
        else{
            displayMessageFeddArticle(ArrayList())
        }
    }

    /**
     * process the result and add it to the adapter/db
     * @param response the jsoobject response to process
     * @param nametouse the name for whom we are processing
     */
    fun addItems(response:JSONObject,nametouse:String){
        //if context is null try to load the activity context
        val con = JsonRpcResultConversion(response,
                nametouse,
                TypeOfRequest.blog,
                if(context != null) context as Context else this.activity?.applicationContext!!,
                if(GetNameToUseOtherGuy())this else null,true)
        val result = con.ParseJsonBlog()
        if(result != null && !result.isEmpty()){
            displayMessageFeddArticle(result)
        }
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
