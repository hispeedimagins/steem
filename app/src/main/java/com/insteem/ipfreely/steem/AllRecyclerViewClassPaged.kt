package com.insteem.ipfreely.steem

import android.app.Activity

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import com.insteem.ipfreely.steem.BindHelpers.*
import com.insteem.ipfreely.steem.DataHolders.DateTypeAndStringHolder
import com.insteem.ipfreely.steem.DataHolders.FeedArticleDataHolder
import com.insteem.ipfreely.steem.DataHolders.GetReputationDataHolder
import com.insteem.ipfreely.steem.Enums.AdapterToUseFor
import com.insteem.ipfreely.steem.Interfaces.AllRecyclerViewAdapterInterface
import com.insteem.ipfreely.steem.Interfaces.GlobalInterface
import com.insteem.ipfreely.steem.Interfaces.arvdinterface
import com.insteem.ipfreely.steem.MyViewHolders.*
import com.insteem.ipfreely.steem.jsonclasses.BusyNotificationJson
import com.insteem.ipfreely.steem.jsonclasses.OperationJson
import com.insteem.ipfreely.steem.jsonclasses.feed
import com.insteem.ipfreely.steem.jsonclasses.prof
import java.util.*

/**
 * the paged adapter class
 * the same as the normal one but uses an AsyncPagedListDiffer list with a diff callback
 */
class AllRecyclerViewClassPaged(activity: Activity, thisRecyclerView: RecyclerView, view: View?, initiate: AdapterToUseFor, globalInterface: GlobalInterface? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() , arvdinterface, FastScrollRecyclerView.SectionedAdapter {
    override fun getSectionName(position: Int): String {
        val ob = mDiffer.getItem(position)
        if(ob != null){
            when(apptype){
                AdapterToUseFor.followers->{
                    return (ob as prof.Resultfp).follower
                }
                AdapterToUseFor.following->{
                    return (ob as prof.Resultfp).following
                }
                else->{
                    return ""
                }
            }
        }
        return ""
    }


    companion object {
        /**
         * this determines if an object has to be updated
         */
        val  article_DIFF_CALLBACK = object :
                DiffUtil.ItemCallback<Any>() {
            // details may have changed if reloaded from the database,
            // but ID is fixed.
            override fun areItemsTheSame(old: Any,
                                         new: Any): Boolean{
                if(old is FeedArticleDataHolder.FeedArticleHolder && new is FeedArticleDataHolder.FeedArticleHolder){
                    return old.id == new.id
                } else if(old is prof.Resultfp && new is prof.Resultfp){
                    //if it is a profile object we check the dbid
                    return  old.dbid == new.dbid
                }
                return false
            }


            override fun areContentsTheSame(old: Any,
                                            new: Any): Boolean{
                if(old is FeedArticleDataHolder.FeedArticleHolder && new is FeedArticleDataHolder.FeedArticleHolder){
                    return old == new/*old.permlink == new.permlink &&
                            old.netVotes == new.netVotes &&
                            old.children == new.children &&
                            old.datespan == new.datespan &&
                            old.authorreputation == new.authorreputation &&
                            old.pending_payout_value == new.pending_payout_value &&
                            old.already_paid == new.already_paid &&
                            old.uservoted == new.uservoted &&
                            old.title == new.title &&
                            old.summary == new.summary*/

                } else if(old is prof.Resultfp && new is prof.Resultfp){
                    //if it is a profile object we check the unique name
                    return  old.uniqueName == new.uniqueName
                }
                return false
            }


        }
    }


    private val viewTurnedOff = false
    private var emptyView: View? = null

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            showEmptyView()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            showEmptyView()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            showEmptyView()
        }
    }

    private val mDiffer = AsyncPagedListDiffer(this, article_DIFF_CALLBACK)
    private var globalInterface = globalInterface
    //private var mValues: MutableList<Any>? = null
    internal var activity = activity
    //private final questionslistFragment.OnListFragmentInteractionListener mListener;
    //    //internal var calcs: calendarcalculations
    internal var metrics: DisplayMetrics? = null
    internal var context: Activity? = null
    internal var gson: Gson? = null
    internal var s: SharedPreferences? = null
    internal var previousmessageby: String? = null
    internal var appUserName: String? = null
    internal var scale: Float? = null
    internal var prevdate: Calendar? = null
    internal var recyclerView: RecyclerView? = null
    private val isFeedView = 100
    private val isBlogView = 200
    private val isCommentView = 300
    private val isUpvoteView = 400
    private val isFollowView = 500
    private val isDraftView = 600
    private val isNotificationBusyView = 700
    private val isBeneficaryView = 800
    private val isHeader = 900
    private val isSearchUser = 1000
    private val isDate = 1100
    internal var datetopaste: Calendar? = null
    private val pastethedatespecifically: Boolean = false
    private val messagecounter: Long = 0
    //private val fontSingleton: FontSingleton
    //private var floatingDateHolder: FloatingDateHolder? = null
    private val ownInstance: AllRecyclerViewAdapter? = null
    var notificationsBusyHelperFunctions : NotificationsBusyHelperFundtions? = null
    var feedHelperFunctions: FeedHelperFunctions? = null
    var blogHelperFunctions: FeedHelperFunctions? = null
    var repliesHelperFunctions: FeedHelperFunctions? = null
    var commentNotiHelperFunctions: FeedHelperFunctions? = null
    var commentHelperFunctions: CommentsHelperFunctions? = null
    var upvotesHelperFunctions : UpvotesHelperFunctions? = null
    var followDisplayHelperFunctions : FollowDisplayHelperFunctions? = null
    var draftHelperFunctionss : draftHelperFunctions? = null
    var beneficiaryHelperFunctionsOb : beneficiaryHelperFunctions? = null
    var headerHelperFunctions : HeaderHelperFunctions? = null
    var searchUsersHelperFunctions: SearchUsersHelperFunctions? = null
    var floatingDateHolder : FloatingDateHolder? = null
    //private val adRequest: com.google.android.gms.ads.AdRequest? = null
    internal var adapterInterface: AllRecyclerViewAdapterInterface? = null
    var apptype = initiate

    init{
        //mValues = items
        if (activity is AllRecyclerViewAdapterInterface) {
            this.adapterInterface = activity
        }



        this.recyclerView = thisRecyclerView
        this.prevdate = Calendar.getInstance()


        this.datetopaste = Calendar.getInstance()
        this.gson = Gson()
        this.context = activity
        metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        this.s = context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
        this.appUserName = s?.getString("username", null)
        scale = context?.resources?.displayMetrics?.density
        this.registerAdapterDataObserver(observer)
        observer.onChanged()
        appUserName = if(appUserName != null) appUserName as String else context?.getSharedPreferences(CentralConstants.sharedprefname, 0)!!.getString("username", null)
        when (initiate) {
            AdapterToUseFor.feed -> {
                this.floatingDateHolder = FloatingDateHolder(context!!,view!!,recyclerView!!,this)
                this.feedHelperFunctions = FeedHelperFunctions(context as Context,appUserName!!,this, AdapterToUseFor.feed,this.floatingDateHolder)
            }
            AdapterToUseFor.blog -> {
                //this.floatingDateHolder = FloatingDateHolder(context!!, view!!, recyclerView!!, this)
                this.blogHelperFunctions = FeedHelperFunctions(context as Context,appUserName!!,this, AdapterToUseFor.blog)
            }

            AdapterToUseFor.notifications -> {
                this.floatingDateHolder = FloatingDateHolder(context!!,view!!,recyclerView!!,this)
                this.notificationsBusyHelperFunctions = NotificationsBusyHelperFundtions(context as Context,appUserName!!,this,this.floatingDateHolder)
            }
            AdapterToUseFor.wallet -> {
            }
            AdapterToUseFor.article ->{

            }
            AdapterToUseFor.comments ->{
                this.floatingDateHolder = FloatingDateHolder(context!!,view!!,recyclerView!!,this)
                this.commentHelperFunctions = CommentsHelperFunctions(context as Context,appUserName!!,this,scale as Float,metrics as DisplayMetrics,this.floatingDateHolder)
            }
            AdapterToUseFor.upvotes ->{
                this.floatingDateHolder = FloatingDateHolder(context!!,view!!,recyclerView!!,this)
                this.upvotesHelperFunctions = UpvotesHelperFunctions(context as Context,appUserName!!,this,this.floatingDateHolder)
            }
            AdapterToUseFor.followers ->{
                this.followDisplayHelperFunctions = FollowDisplayHelperFunctions(context as Context,if(appUserName != null) appUserName as String else context?.getSharedPreferences(CentralConstants.sharedprefname, 0)!!.getString("username", null),this, AdapterToUseFor.followers)
            }
            AdapterToUseFor.following ->{
                this.followDisplayHelperFunctions = FollowDisplayHelperFunctions(context as Context,if(appUserName != null) appUserName as String else context?.getSharedPreferences(CentralConstants.sharedprefname, 0)!!.getString("username", null),this, AdapterToUseFor.following)
            }
            AdapterToUseFor.draft ->{
                this.draftHelperFunctionss = draftHelperFunctions(context as Context,if(appUserName != null) appUserName as String else context?.getSharedPreferences(CentralConstants.sharedprefname, 0)!!.getString("username", null),this, AdapterToUseFor.draft)
            }
            AdapterToUseFor.replyNoti ->{
                this.floatingDateHolder = FloatingDateHolder(context!!,view!!,recyclerView!!,this)
                this.repliesHelperFunctions = FeedHelperFunctions(context as Context,appUserName!!,this,apptype,this.floatingDateHolder)
            }
            AdapterToUseFor.commentNoti ->{
                this.floatingDateHolder = FloatingDateHolder(context!!,view!!,recyclerView!!,this)
                this.commentNotiHelperFunctions = FeedHelperFunctions(context as Context,appUserName!!,this,apptype,this.floatingDateHolder)
            }
            AdapterToUseFor.beneficiaries -> {
                this.beneficiaryHelperFunctionsOb = beneficiaryHelperFunctions(context as Context,if(appUserName != null) appUserName as String else context?.getSharedPreferences(CentralConstants.sharedprefname, 0)!!.getString("username", null),this)
            }
            AdapterToUseFor.search ->{
                //initialize all the classes needed for searches
                this.floatingDateHolder = FloatingDateHolder(context!!,view!!,recyclerView!!,this)
                this.feedHelperFunctions = FeedHelperFunctions(context as Context,appUserName,this, AdapterToUseFor.feed,this.floatingDateHolder)
                this.searchUsersHelperFunctions = SearchUsersHelperFunctions(context as Context,appUserName as String ,this, AdapterToUseFor.followers)
                this.headerHelperFunctions = HeaderHelperFunctions(context as Context,appUserName as String,this,apptype)
            }
            else -> {
            }
        }


    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var v: View? = null
        var vh: RecyclerView.ViewHolder? = null
        if (viewType == isFeedView) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.article_preview, parent, false)
            vh = ArticleViewHolder(v)
        }
        else if (viewType == isBlogView) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.article_preview, parent, false)
            vh = ArticleViewHolder(v)

        }
        else if (viewType == isCommentView) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.commentview, parent, false)
            vh = CommentViewHolder(v)

        }
        else if(viewType == isUpvoteView){
            v = LayoutInflater.from(parent.context).inflate(R.layout.likes_layout, parent, false)
            vh = UpvoteViewHolder(v)
        }
        else if(viewType == isFollowView){
            v = LayoutInflater.from(parent.context).inflate(R.layout.follow_view_resource, parent, false)
            vh = followViewHolder(v)
        }
        else if(viewType == isDraftView){
            v = LayoutInflater.from(parent.context).inflate(R.layout.draft_single_view, parent, false)
            vh = DraftViewHolder(v)
        }
        else if(viewType == isNotificationBusyView){
            v = LayoutInflater.from(parent.context).inflate(R.layout.notification_busy, parent, false)
            vh = NotificationBusyViewHolder(v)
        }
        else if(viewType == isBeneficaryView){
            v = LayoutInflater.from(parent.context).inflate(R.layout.beneficiaryview, parent, false)
            vh = beneficiaryViewHolder(v)
        }
        else if(viewType == isHeader){
            //initialize the layout here for search headers
            v = LayoutInflater.from(parent.context).inflate(R.layout.searchheader, parent, false)
            vh = HeaderViewHolder(v)
        }
        else if(viewType == isSearchUser){
            //initialize the layout here for search users
            v = LayoutInflater.from(parent.context).inflate(R.layout.follow_view_resource, parent, false)
            vh = followViewHolder(v)
        }
        else if(viewType == isDate){
            v = LayoutInflater.from(parent.context).inflate(R.layout.dateresourceforchat, parent, false)
            vh = DateViewHolder(v)
        }
        /*
        } else if (viewType == isOpenAQuestionView) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.openaquestiondisplaynewlayout, parent, false)
            vh = OpenAQuestionDataHolder(v)

            val vhf = vh
            v!!.setOnClickListener {
                //here you set your current position from holder of clicked view

                openAQuestionHelperFunctions.OpenQuestionClickPasser(vhf)
            }
        */
        return vh as RecyclerView.ViewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val ht = getItemViewType(position)

        if(ht == isFeedView){
            if(apptype == AdapterToUseFor.replyNoti){
                repliesHelperFunctions?.Bind(holder,position)
            } else if(apptype == AdapterToUseFor.commentNoti){
                commentNotiHelperFunctions?.Bind(holder,position)
            }
            feedHelperFunctions?.Bind(holder,position)
        }
        else if(ht == isBlogView){
            blogHelperFunctions?.Bind(holder,position)
        }
        else if(ht == isCommentView){
            commentHelperFunctions?.Bind(holder,position)
        }
        else if(ht == isUpvoteView){
            upvotesHelperFunctions?.Bind(holder,position)
        }
        else if(ht == isFollowView){
            followDisplayHelperFunctions?.Bind(holder,position)
        }
        else if(ht == isDraftView){
            draftHelperFunctionss?.Bind(holder,position)
        }
        else if(ht == isNotificationBusyView){
            notificationsBusyHelperFunctions?.Bind(holder,position)
        }
        else if(ht == isBeneficaryView){
            beneficiaryHelperFunctionsOb?.Bind(holder,position)
        }
        else if(ht == isHeader){
            //if object in the list is a header use the header class bind function
            headerHelperFunctions?.Bind(holder,position)
        }
        else if(ht == isSearchUser){
            //if the object is a search user, use the search bind function
            searchUsersHelperFunctions?.Bind(holder,position)
        }
        else if( ht == isDate){
            floatingDateHolder?.BindDateToDateTitle(holder,position)
        }



    }

    //will cause a crash do not use
    fun clear() {

        mDiffer?.currentList?.clear()
        notifyDataSetChanged()
    }
    //will cause a crash do not use
    fun add(num: Long) {
        mDiffer?.currentList?.add(num)
        notifyDataSetChanged()
    }

    fun getList():AsyncPagedListDiffer<Any>?{
        return mDiffer
    }


    /*fun add(holder: NothingToShowDataHolder) {
        mValues?.add(holder)
        notifyDataSetChanged()
    }*/


    override fun getItemViewType(position: Int): Int {

        val ins = mDiffer?.getItem(position)
        if (ins is FeedArticleDataHolder.FeedArticleHolder) {
            return isFeedView
        }
        else if(ins is FeedArticleDataHolder.CommentHolder){
            return isCommentView
        }
        else if(ins is feed.avtiveVotes){
            return isUpvoteView
        }
        else if(ins is prof.Resultfp){
            return isFollowView
        }
        else if(ins is OperationJson.draftholder){
            return isDraftView
        }
        else if(ins is BusyNotificationJson.Result){
            return isNotificationBusyView
        }
        else if(ins is FeedArticleDataHolder.beneficiariesDataHolder){
            return isBeneficaryView
        }
        else if(ins is String){
            //if the object is a string then return isHeader
            return isHeader
        }
        else if(ins is GetReputationDataHolder){
            //if object is of GetReputationDataHolder return isSearchUser
            return isSearchUser
        }
        else if(ins is DateTypeAndStringHolder){
            return isDate
        }

        return isFeedView
    }


    fun setEmptyView(v: View) {
        emptyView = v
    }


    fun showEmptyView(): Boolean {


        if (emptyView != null && recyclerView != null) {
            if (getItemCount() == 0) {
                if (emptyView!!.visibility == View.GONE) {
                    emptyView!!.visibility = View.VISIBLE
                    recyclerView?.visibility = View.GONE
                }

            } else {

                if (emptyView!!.visibility == View.VISIBLE) {
                    emptyView!!.visibility = View.GONE
                    recyclerView?.visibility = View.VISIBLE
                }


            }
            return true
        }


        return false

    }


    override fun getItemCount(): Int {
        return mDiffer?.itemCount
    }
    //will cause a crash do not use
    fun add(all: List<Any>) {
        for(x in all){
            add(x)
        }
    }

    //will cause a crash do not use
    override fun add(`object`: Any) {


        /*class someTask() : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String? {
                mValues?.add(`object`)
                var si = mValues?.size as Int
                notifyitemcinserted(si)
                return ""
            }
        }
        someTask().execute()*/
        mDiffer?.currentList?.add(`object`)
        var si = mDiffer?.itemCount
        notifyitemcinserted(si)
        //return ""



    }

    /**
     * we submit the paged list here
     */
    fun submitList(list: PagedList<Any>){
        mDiffer.submitList(list)
    }

    /**
     * invalidates any data fed to the list
     * then update the ui
     */
    fun invalidate(){
        mDiffer.currentList?.dataSource?.invalidate()
        notifyDataSetChanged()
    }

    override fun notifydatachanged() {


        try {
            notifyDataSetChanged()
        }
        catch (ex : IllegalStateException){

        }
        catch (exs: Exception){

        }
    }

    override fun notifyitemcinserted(position: Int) {
        try {
            notifyItemInserted(position)
        }
        catch (ex : IllegalStateException){
            var s = ex
        }
        catch (exs: Exception){
            var s = exs
        }

    }

    override  fun notifyitemcchanged(position: Int) {


        try {
            notifyItemChanged(position)
        }
        catch (ex : IllegalStateException){

        }
        catch (exs: Exception){

        }
    }

    //implement remove at
    override fun removeAt(position: Int) {
        //first remove the item from the list
        mDiffer?.currentList?.removeAt(position)
        //then notify the system that an item was removed
        notifyItemRemoved(position)
    }

    override fun notifyitemRemoved(position: Int) {
        //notify the system that an item was removed
        notifyitemRemoved(position)
    }

    override fun notifyitemcchanged(position: Int, payload: Any) {
        try {
            notifyItemChanged(position,payload)

        }
        catch (ex : IllegalStateException){

        }
        catch (exs: Exception){

        }

    }

    override fun getObject(position: Int): Any? {
        return mDiffer.getItem(position)
    }

    override fun getSize(): Int {
        return mDiffer?.itemCount
    }

    override  fun getActivity(): Activity {

        return activity
    }

    override fun SetMenuItemVisibility(MenuItemId: Int, visibility: Boolean) {
        adapterInterface?.SetMenuItemVisibility(MenuItemId, visibility)
    }

    override fun SetData(data: FeedArticleDataHolder.FeedArticleHolder) {
        adapterInterface?.SetData(data)
    }

    override fun GetGlobalInterface() : GlobalInterface? {
        return this.globalInterface
    }
    /*override fun SetData(data: JsonTenorTrendingMedium) {

        adapterInterface.SetData(data)
    }*/

    override fun getResources(): Resources {
        return context?.resources as Resources
    }

    override fun getContext(): Context {
        return context as Context
    }

    override fun AddItemDivider() {
        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        recyclerView!!.addItemDecoration(itemDecoration)

    }

    override fun objectClicked(data: Any?) {
        globalInterface?.objectClicked(data)
    }
}