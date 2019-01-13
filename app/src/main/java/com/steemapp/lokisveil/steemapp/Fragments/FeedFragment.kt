package com.steemapp.lokisveil.steemapp.Fragments

import android.app.Application
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.arch.paging.PagedListAdapter
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
import com.steemapp.lokisveil.steemapp.*
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Databases.RequestsDatabase
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.Enums.TypeOfRequest
import com.steemapp.lokisveil.steemapp.HelperClasses.FabHider
import com.steemapp.lokisveil.steemapp.HelperClasses.JsonRpcResultConversion
import com.steemapp.lokisveil.steemapp.HelperClasses.MakeJsonRpc
import com.steemapp.lokisveil.steemapp.HelperClasses.swipecommonactionsclass
import com.steemapp.lokisveil.steemapp.Interfaces.GlobalInterface
import com.steemapp.lokisveil.steemapp.Interfaces.JsonRpcResultInterface
import com.steemapp.lokisveil.steemapp.MyViewHolders.ArticleViewHolder
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomRepos.FollowersRepo
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomViewModels.ArticleRoomVM
import com.steemapp.lokisveil.steemapp.jsonclasses.feed
import org.apache.commons.lang3.StringEscapeUtils.escapeJson
import org.apache.commons.lang3.StringEscapeUtils.unescapeJson
import org.json.JSONObject
import java.io.Serializable
import java.lang.reflect.TypeVariable
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
class FeedFragment : Fragment(),JsonRpcResultInterface  {

    /**
     * callback from the jni, this will insert it into the database
     */
    override fun insert(data: FeedArticleDataHolder.FeedArticleHolder) {
        var nametouse : String = username as String
        if(otherguy != null){
            nametouse = otherguy as String
        }
        startAuthor = data.author
        startPermlink = data.permlink
        startTag = nametouse
        vm?.insert(data)
    }


    //callback when deleting is done
    override fun deleDone() {
        refreshcontent()
    }

    //callback that all the items were added to the database
    override fun processingDone(count: Int) {
        swipecommonactionsclass?.makeswipestop()
    }

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    internal var swipecommonactionsclass: swipecommonactionsclass? = null

    private var fragmentActivity: android.support.v4.app.FragmentActivity? = null

    private var adapter: AllRecyclerViewClassPaged? = null
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
    var vm :ArticleRoomVM? = null
    private var mListener: GlobalInterface? = null

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

            //setup the new paged adapter
            adapter = AllRecyclerViewClassPaged(getActivity() as FragmentActivity, recyclerView as RecyclerView, view as View, AdapterToUseFor.feed)
            //setup the vm
            vm = ViewModelProviders.of(this).get(ArticleRoomVM::class.java)
            //observe the last db result only once

            //fetch the data from the paged list item sorted by saved time
            vm?.getPagedUpdatedListTime()?.observe(this,android.arch.lifecycle.Observer { pagedList ->
                if(pagedList != null && pagedList.size > 0){
                    //submit the list to the adapter
                    adapter?.submitList(pagedList as PagedList<Any>)
                    //swipecommonactionsclass?.makeswipestop()
                } else if(pagedList != null && pagedList.size == 0){
                    adapter?.submitList(pagedList as PagedList<Any>)
                    //swipecommonactionsclass?.makeswipestop()
                }

            })


            recyclerView?.setItemAnimator(DefaultItemAnimator())
            recyclerView?.setAdapter(adapter)
            //init fabhider to hide FAB on scroll
            FabHider(recyclerView,mListener?.getFab())




            recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
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

        swipeRefreshLayout?.setOnRefreshListener {
            vm?.deleteAll(false,this)

        }

        swipecommonactionsclass = swipecommonactionsclass(swipeRefreshLayout as SwipeRefreshLayout)
        fragmentActivity = getActivity()
        if (!tokenisrefreshingholdon) {
            //getQuestionsNow(false);
        } else if (startwasjustrun) {
            //getQuestionsNow(true)

        }

        if(savedInstanceState == null) {

            GetFeed()
        }
        return view
    }


    private fun refreshcontent() {
        tokenisrefreshingholdon = false

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

                adapter = AllRecyclerViewClassPaged(getActivity() as FragmentActivity, recyclerView as RecyclerView, view as View, AdapterToUseFor.feed)

                recyclerView?.setAdapter(adapter)

            }

        }
        GetFeed()
    }



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
                    addItems(response,nametouse)
                }, Response.ErrorListener {})
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
        swipecommonactionsclass?.makeswiperun()

        val volleyre : VolleyRequest = VolleyRequest.getInstance(context)

        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()
        var nametouse = GetNameToUse()
        val s = JsonObjectRequest(Request.Method.POST,url,d.getMoreItems(startAuthor,startPermlink,startTag,false),
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
        if (context == null) return
        //we pass jni into the constructor to not get a list back but insert into the db
        val con = JsonRpcResultConversion(response,nametouse, TypeOfRequest.blog,context!!,this,false)
        val result = con.ParseJsonBlogMore()
        if(result != null && !result.isEmpty()){
            displayMessageFeddArticle(result)
        }
        else{
            displayMessageFeddArticle(ArrayList<FeedArticleDataHolder.FeedArticleHolder>())
        }
    }

    /**
     * process the result and add it to the adapter/db
     * @param response the jsoobject response to process
     * @param nametouse the name for whom we are processing
     */
    fun addItems(response:JSONObject,nametouse:String){
        if (context == null) return
        //we pass jni into the constructor to not get a list back but insert into the db
        val con = JsonRpcResultConversion(response,nametouse,TypeOfRequest.feed,context!!,this,false)
        val result = con.ParseJsonBlog()
        if(result != null && !result.isEmpty()){
            displayMessageFeddArticle(result)
        }

        val req = RequestsDatabase(context!!)
        //delete old items
        req.DeleteOld()
        //close db connections so no leakages
        req.close()
    }
    // TODO: Rename method, update argument and hook method into UI event
    /*fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            //mListener!!.onFragmentInteraction(uri)
        }
    }*/

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is GlobalInterface) {
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
}
