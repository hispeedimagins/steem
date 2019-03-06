package com.insteem.ipfreely.steem.Fragments

import android.content.Context
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
import com.insteem.ipfreely.steem.*
import com.insteem.ipfreely.steem.Enums.AdapterToUseFor
import com.insteem.ipfreely.steem.HelperClasses.GeneralRequestsFeedIntoConstants
import com.insteem.ipfreely.steem.HelperClasses.JsonRpcResultConversion
import com.insteem.ipfreely.steem.HelperClasses.swipecommonactionsclass
import com.insteem.ipfreely.steem.RoomDatabaseApp.RoomViewModels.FollowViewModel
import com.insteem.ipfreely.steem.jsonclasses.prof
import java.util.*

/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class FollowingFragment : Fragment() {
    fun AllDone() {
        swipecommonactionsclass?.makeswipestop()
    }

    fun GetFollowersList(followerList:List<prof.Resultfp>) {

    }

    fun GetFollowingList(followinglist:List<prof.Resultfp>) {
        displayfollowing(followinglist)
    }
    // TODO: Customize parameters
    private var mColumnCount = 1
    private var mListener: OnListFragmentInteractionListener? = null

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
    //var useFollower : Boolean?  = false
    var key : String? = null
    var following: MutableList<prof.Resultfp> = ArrayList()
    var vm:FollowViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            mColumnCount = arguments!!.getInt(ARG_COLUMN_COUNT)
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
                adapter = AllRecyclerViewAdapter(getActivity() as FragmentActivity, ArrayList(), recyclerView as RecyclerView, view as View, AdapterToUseFor.following)
                //adapter?.setEmptyView(view?.findViewById(R.id.toDoEmptyView))
                recyclerView?.setItemAnimator(DefaultItemAnimator())
                recyclerView?.setAdapter(adapter)
            } else {
                //if it is for us we use the db
                adapterPaged = AllRecyclerViewClassPaged(getActivity() as FragmentActivity,recyclerView!!,view!!,AdapterToUseFor.following)
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
            gens.GetFollowing(otherguy as String,"",null)
        }
        else{
            /*var parse = JsonRpcResultConversion(null,username as String,null,context as Context)
            var fol = FollowingDatabase(context as Context)
            displayfollowing(parse.processfollowlist(fol.GetAllQuestions(),false))
            swipecommonactionsclass?.makeswipestop()*/

            //setup the viewmodel
            vm = ViewModelProviders.of(getActivity() as FragmentActivity).get(FollowViewModel::class.java)
            var parse = JsonRpcResultConversion(null,username as String,null,context as Context)
            /*var fol = FollowersDatabase(context as Context)
            var fols = fol.GetAllQuestions()*/

            //fetch the paged list
            vm?.getpagedList(false)?.observe(this, Observer {
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



    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            mListener = context
        } else {
            //throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
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
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        //fun onListFragmentInteraction(item: DummyItem)
    }

    companion object {

        // TODO: Customize parameter argument names
        private val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        fun newInstance(columnCount: Int): FollowingFragment {
            val fragment = FollowingFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}
