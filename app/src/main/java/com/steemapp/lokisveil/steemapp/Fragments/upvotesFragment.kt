package com.steemapp.lokisveil.steemapp.Fragments

import android.content.Context
import android.os.Bundle
import android.text.format.DateUtils
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
import com.steemapp.lokisveil.steemapp.AllRecyclerViewAdapter
import com.steemapp.lokisveil.steemapp.CentralConstants
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.FollowApiConstants
import com.steemapp.lokisveil.steemapp.HelperClasses.StaticMethodsMisc
import com.steemapp.lokisveil.steemapp.HelperClasses.dummy.DummyContent.DummyItem
import com.steemapp.lokisveil.steemapp.R
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.jsonclasses.feed
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
class upvotesFragment : Fragment() {
    // TODO: Customize parameters
    private var mColumnCount = 1
    private var mListener: OnListFragmentInteractionListener? = null


    private var fragmentActivity: FragmentActivity? = null

    private var adapter: AllRecyclerViewAdapter? = null
    private var activity: Context? = null
    internal var view: View? = null
    internal var swipeRefreshLayout: SwipeRefreshLayout? = null
    internal var recyclerView: RecyclerView? = null
    private var loading = false
/*    internal var pastVisiblesItems: Int = 0
    internal var visibleItemCount:Int = 0
    internal var totalItemCount:Int = 0*/
    internal var tokenisrefreshingholdon = false
    internal var startwasjustrun = false
    var username : String? = null
    var otherguy : String? = null
    var useOtherGuyOnly : Boolean? = false
    var key : String? = null

    var startAuthor : String? = null
    var startPermlink : String? = null
    var startTag : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            mColumnCount = arguments?.getInt(ARG_COLUMN_COUNT) as Int
            otherguy = arguments?.getString(CentralConstants.OtherGuyNamePasser, null)
            useOtherGuyOnly = arguments?.getBoolean(CentralConstants.OtherGuyUseOtherGuyOnly,false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_upvotes_list, container, false)

        recyclerView = view?.findViewById(R.id.list)
        adapter = AllRecyclerViewAdapter(getActivity() as FragmentActivity, ArrayList(), recyclerView as RecyclerView, view as View, AdapterToUseFor.upvotes)
        //adapter?.setEmptyView(view?.findViewById(R.id.toDoEmptyView))

        recyclerView?.setItemAnimator(DefaultItemAnimator())
        recyclerView?.setAdapter(adapter)
        // Set the adapter
        if (recyclerView is RecyclerView) {
            val context = view.getContext()
            if (mColumnCount <= 1) {
                recyclerView?.layoutManager = LinearLayoutManager(context)
            } else {
                recyclerView?.layoutManager = GridLayoutManager(context, mColumnCount)
            }
            //view.adapter = MyupvotesRecyclerViewAdapter(DummyContent.ITEMS, mListener)
        }

        activity = getActivity()?.applicationContext
        val sharedPreferences = context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences?.getString(CentralConstants.username, null)
        key = sharedPreferences?.getString(CentralConstants.key, null)
        return view
    }


    private fun refreshcontent() {
        tokenisrefreshingholdon = false
        //swipecommonactionsclass?.makeswipestop()
        adapter?.clear()
        adapter?.notifyDataSetChanged()
        /*adapter = new MyquestionslistRecyclerViewAdapter(new ArrayList<ForReturningQuestionsLite>(), mListener);
        recyclerView.setAdapter(adapter);*/
        //getQuestionsNow(false)

        //GetFeed()
    }


    fun runstartagain() {
        tokenisrefreshingholdon = false
        startwasjustrun = true
        //swipecommonactionsclass?.makeswipestop()
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
        //GetFeed()
    }

    override fun onStop() {
        super.onStop()
        //webview = null
        super.onDestroy()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            mListener = context
        } else {
            //throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
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
        fun onListFragmentInteraction(item: DummyItem)
    }

    companion object {

        // TODO: Customize parameter argument names
        private val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        fun newInstance(columnCount: Int): upvotesFragment {
            val fragment = upvotesFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }



    fun display(activevotes : List<feed.avtiveVotes>){
        var con = FollowApiConstants.getInstance()

        for(x in activevotes){
            x.namewithrep = "${x.voter} (${StaticMethodsMisc.CalculateRepScore(x.reputation)})"
            x.calculatedpercent = "Vote percent :"+  ((x.percent as String).toInt() / 100).toString()
            x.calculatedvotepercent = "Vote power :"+ ((x.weight as String).toInt() / 100).toString()
            var du = DateUtils.getRelativeDateTimeString(context,StaticMethodsMisc.FormatDateGmt(x.time).time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0)
            x.dateString = du.toString()

            x.calculatedrshares = StaticMethodsMisc.FormatVotingValueToSBD(StaticMethodsMisc.VotingValueSteemToSd(StaticMethodsMisc.CalculateVotingValueRshares(x.rshares)))
            var fol = MyOperationTypes.unfollow
            fol = if(StaticMethodsMisc.CheckFollowing(x.voter,context)){
                MyOperationTypes.unfollow
            }
            else{
                MyOperationTypes.follow
            }
            if(!con.following.isEmpty() && con.following.any { p -> p.following == x.voter }){
                x.followInternal = MyOperationTypes.unfollow
            }
            else {
                x.followInternal = MyOperationTypes.follow
            }
            x.percent = ""
            x.weight = ""
            x.time = ""
            x.rshares = ""
            x.reputation = ""
            adapter?.add(x)
        }
    }
}
