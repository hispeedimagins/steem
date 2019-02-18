package com.steemapp.lokisveil.steemapp.Fragments


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.steemapp.lokisveil.steemapp.*
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.HelperClasses.GeneralRequestsFeedIntoConstants
import com.steemapp.lokisveil.steemapp.HelperClasses.JsonRpcResultConversion
import com.steemapp.lokisveil.steemapp.HelperClasses.swipecommonactionsclass
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomViewModels.FollowViewModel
import com.steemapp.lokisveil.steemapp.jsonclasses.prof
import java.util.*

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

    private var fragmentActivity: FragmentActivity? = null

    private var adapter: AllRecyclerViewAdapter? = null
    private var adapterPaged: AllRecyclerViewClassPaged? = null
    private var activity: Context? = null
    internal var view: View? = null
    internal var swipeRefreshLayout: SwipeRefreshLayout? = null
    internal var recyclerView: RecyclerView? = null
    private var loading = false

    internal var tokenisrefreshingholdon = false
    internal var startwasjustrun = false
    var username : String? = null
    var otherguy : String? = null
    var useOtherGuyOnly : Boolean = false
    var vm:FollowViewModel? = null
    //var useFollower : Boolean?  = false
    var key : String? = null
    var followers: List<prof.Resultfp> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
            otherguy = arguments?.getString(CentralConstants.OtherGuyNamePasser, null)
            useOtherGuyOnly = arguments?.getBoolean(CentralConstants.OtherGuyUseOtherGuyOnly,false)!!
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
            if(useOtherGuyOnly){
                adapter = AllRecyclerViewAdapter(getActivity() as FragmentActivity, ArrayList(), recyclerView as RecyclerView, view as View, AdapterToUseFor.followers)
                //adapter?.setEmptyView(view?.findViewById(R.id.toDoEmptyView))
                recyclerView?.setItemAnimator(DefaultItemAnimator())
                recyclerView?.setAdapter(adapter)
            } else {
                //if it is for us we use the db
                adapterPaged = AllRecyclerViewClassPaged(getActivity() as FragmentActivity,recyclerView!!,view!!,AdapterToUseFor.followers)
                recyclerView?.setItemAnimator(DefaultItemAnimator())
                recyclerView?.setAdapter(adapterPaged)
            }

        }
        activity = getActivity()?.applicationContext
        val sharedPreferences = context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences?.getString(CentralConstants.username, null)
        key = sharedPreferences?.getString(CentralConstants.key, null)

        if(useOtherGuyOnly){
            swipecommonactionsclass?.makeswiperun()
            var gens = GeneralRequestsFeedIntoConstants(context as Context,CentralConstantsOfSteem.getInstance().otherGuyFollowCount,false)
            gens.GetFollowers(otherguy as String,"",null)
        }
        else{
            //setup the viewmodel
            vm = ViewModelProviders.of(getActivity() as FragmentActivity).get(FollowViewModel::class.java)

            var parse = JsonRpcResultConversion(null,username as String,null,context as Context)
            /*var fol = FollowersDatabase(context as Context)
            var fols = fol.GetAllQuestions()*/


            //fetch the paged list
            vm?.getpagedList()?.observe(this, Observer {
                if(it != null){
                    adapterPaged?.submitList(it as PagedList<Any>)
                }
            })
            //displayfollowing(parse.processfollowlist(fols,true))
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
