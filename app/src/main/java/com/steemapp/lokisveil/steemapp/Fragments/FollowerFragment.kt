package com.steemapp.lokisveil.steemapp.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Response
import com.google.gson.Gson
import com.steemapp.lokisveil.steemapp.AllRecyclerViewAdapter
import com.steemapp.lokisveil.steemapp.CentralConstants
import com.steemapp.lokisveil.steemapp.CentralConstantsOfSteem
import com.steemapp.lokisveil.steemapp.Databases.FollowersDatabase
import com.steemapp.lokisveil.steemapp.Databases.FollowingDatabase
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.HelperClasses.GeneralRequestsFeedIntoConstants
import com.steemapp.lokisveil.steemapp.HelperClasses.JsonRpcResultConversion
import com.steemapp.lokisveil.steemapp.HelperClasses.StaticMethodsMisc
import com.steemapp.lokisveil.steemapp.HelperClasses.swipecommonactionsclass
import com.steemapp.lokisveil.steemapp.Interfaces.GetFollowListsBack

import com.steemapp.lokisveil.steemapp.R
import com.steemapp.lokisveil.steemapp.jsonclasses.prof
import org.json.JSONObject
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FollowerFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FollowerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FollowerFragment : Fragment() {
    fun AllDone() {
        swipecommonactionsclass?.makeswipestop()
    }

    fun GetFollowersList(followerList:List<prof.Resultfp>) {
        displayfollowing(followerList)
    }

    fun GetFollowingList(followinglist:List<prof.Resultfp>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    internal var swipecommonactionsclass: swipecommonactionsclass? = null

    private var fragmentActivity: android.support.v4.app.FragmentActivity? = null

    private var adapter: AllRecyclerViewAdapter? = null
    private var activity: Context? = null
    internal var view: View? = null
    internal var swipeRefreshLayout: SwipeRefreshLayout? = null
    internal var recyclerView: RecyclerView? = null
    private var loading = false

    internal var tokenisrefreshingholdon = false
    internal var startwasjustrun = false
    var username : String? = null
    var otherguy : String? = null
    var useOtherGuyOnly : Boolean? = false
    //var useFollower : Boolean?  = false
    var key : String? = null
    var followers: List<prof.Resultfp> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
            otherguy = arguments?.getString(CentralConstants.OtherGuyNamePasser, null)
            useOtherGuyOnly = arguments?.getBoolean(CentralConstants.OtherGuyUseOtherGuyOnly,false)
            //useFollower = arguments?.getBoolean(CentralConstants.FollowingFragmentUseFollower,false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        recyclerView = view?.findViewById(R.id.list)
        swipeRefreshLayout = view?.findViewById(R.id.fragment_follow_swipe_refresh_layout)
        swipecommonactionsclass = swipecommonactionsclass(swipeRefreshLayout as SwipeRefreshLayout)
        // Set the adapter
        if (recyclerView is RecyclerView) {
            val context = view.getContext()
            /*if (mColumnCount <= 1) {
                recyclerView?.layoutManager = LinearLayoutManager(context)
            } else {
                recyclerView?.layoutManager = GridLayoutManager(context, mColumnCount)
            }*/
            //recyclerView = view

            adapter = AllRecyclerViewAdapter(getActivity() as FragmentActivity, ArrayList(), recyclerView as RecyclerView, view as View, AdapterToUseFor.followers)
            //adapter?.setEmptyView(view?.findViewById(R.id.toDoEmptyView))

            recyclerView?.setItemAnimator(DefaultItemAnimator())
            recyclerView?.setAdapter(adapter)
        }
        activity = getActivity()?.applicationContext
        val sharedPreferences = context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences?.getString(CentralConstants.username, null)
        key = sharedPreferences?.getString(CentralConstants.key, null)

        if(useOtherGuyOnly as Boolean){
            swipecommonactionsclass?.makeswiperun()
            /*var followerlistener: Response.Listener<JSONObject> =  Response.Listener { response ->

                val gson = Gson()
                var parse = gson.fromJson(response.toString(), prof.FollowNames::class.java)
                if(parse != null && parse.result != null){

                    followers += (parse.result as List<prof.Resultfp>)
                    displayfollowing(parse.result as List<prof.Resultfp>)
                    if(followers.size == CentralConstantsOfSteem.getInstance().otherGuyFollowCount.result.followerCount){
                        swipecommonactionsclass?.makeswipestop()
                    }

                }

            }*/

            //StaticMethodsMisc.MakeFollowRequestsFollowers(CentralConstantsOfSteem.getInstance().otherGuyFollowCount,context,followerlistener)
            var gens = GeneralRequestsFeedIntoConstants(context as Context,CentralConstantsOfSteem.getInstance().otherGuyFollowCount,false)
            gens.GetFollowers(otherguy as String,"",null)
        }
        else{
            var parse = JsonRpcResultConversion(null,username as String,null,context as Context)
            var fol = FollowersDatabase(context as Context)
            var fols = fol.GetAllQuestions()
            displayfollowing(parse.processfollowlist(fols,true))
            swipecommonactionsclass?.makeswipestop()
        }
        return view
    }

    fun displayfollowing(list : List<prof.Resultfp>){
        for(x in list){
            adapter?.add(x)
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            //throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
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
         * @return A new instance of fragment FollowerFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): FollowerFragment {
            val fragment = FollowerFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
