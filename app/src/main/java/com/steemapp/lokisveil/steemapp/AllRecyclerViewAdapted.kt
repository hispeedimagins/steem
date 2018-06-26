package com.steemapp.lokisveil.steemapp

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.AsyncTask
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import com.google.gson.Gson
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import com.steemapp.lokisveil.steemapp.BindHelpers.*
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.DataHolders.GetReputationDataHolder
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.Interfaces.AllRecyclerViewAdapterInterface
import com.steemapp.lokisveil.steemapp.Interfaces.GlobalInterface
import com.steemapp.lokisveil.steemapp.Interfaces.arvdinterface
import com.steemapp.lokisveil.steemapp.MyViewHolders.*
import com.steemapp.lokisveil.steemapp.jsonclasses.BusyNotificationJson
import com.steemapp.lokisveil.steemapp.jsonclasses.OperationJson
import com.steemapp.lokisveil.steemapp.jsonclasses.feed
import com.steemapp.lokisveil.steemapp.jsonclasses.prof
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by boot on 2/4/2018.
 */

public class AllRecyclerViewAdapter(activity: Activity, items: MutableList<Any>, thisRecyclerView: RecyclerView, view: View?, initiate: AdapterToUseFor,globalInterface: GlobalInterface? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() , arvdinterface , FastScrollRecyclerView.SectionedAdapter {
    override fun getSectionName(position: Int): String {
        var ob = mValues?.get(position)
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
        /*if(followDisplayHelperFunctions != null){
            return (ob as prof.Resultfp).follower
        }
        else if(f)*/


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

    private var globalInterface = globalInterface
    private var mValues: MutableList<Any>? = null
    internal var activity = activity
    //private final questionslistFragment.OnListFragmentInteractionListener mListener;
    //internal var calcs: calendarcalculations
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
    /*private val isdateview = 65
    private val ischatview = 72
    private val isadview = 74
    private val isQuestionView = 122
    private val isPeopleView = 125
    private val isNoMessageView = 161
    private val isOpenAQuestionView = 187
    private val isNotificationView = 9877
    private val isTenorView = 1001*/
    internal var datetopaste: Calendar? = null
    private val pastethedatespecifically: Boolean = false
    private val messagecounter: Long = 0
    //private val fontSingleton: FontSingleton
    //private var floatingDateHolder: FloatingDateHolder? = null
    private val ownInstance: AllRecyclerViewAdapter? = null
    //var chatMessageFunctions: ChatMessageFunctions


    //private Activity activity;
    //private MyQuestionsMainListDb db;
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
    var searchUsersHelperFunctions:SearchUsersHelperFunctions? = null
    //var otherguy = otherguy
    /*var peopleFunctionsList: PeopleFunctionsList
    var openAQuestionHelperFunctions: OpenAQuestionHelperFunctions

    var notificationListFunctions: NotificationListFunctions

    var tenorHelperFunctions: TenorHelperFunctions*/

    private val adRequest: com.google.android.gms.ads.AdRequest? = null
    internal var adapterInterface: AllRecyclerViewAdapterInterface? = null
    var apptype = initiate

    init{
        mValues = items
        //mListener = listener;
        if (activity is AllRecyclerViewAdapterInterface) {
            this.adapterInterface = activity as AllRecyclerViewAdapterInterface
        }

        //this.calcs = calendarcalculations()

        this.recyclerView = thisRecyclerView
        this.prevdate = Calendar.getInstance()


        this.datetopaste = Calendar.getInstance()
        this.gson = Gson()
        this.context = activity
        //this.fontSingleton = FontSingleton.getInstance(activity)
        metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        this.s = context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
        this.appUserName = s?.getString("username", null)

        scale = context?.resources?.displayMetrics?.density
       // this.adRequest = AdRequest.Builder().build()


        this.registerAdapterDataObserver(observer)
        observer.onChanged()

        //sometimes username is null, check it before going forward
        appUserName = if(appUserName != null) appUserName as String else context?.getSharedPreferences(CentralConstants.sharedprefname, 0)!!.getString("username", null)

        when (initiate) {
            AdapterToUseFor.feed -> {
                //this.floatingDateHolder = FloatingDateHolder(context, view, recyclerView, this.fontSingleton, this@AllRecyclerViewAdapter)
                //this.chatMessageFunctions = ChatMessageFunctions(metrics, context, floatingDateHolder, this@AllRecyclerViewAdapter)
                this.feedHelperFunctions = FeedHelperFunctions(context as Context,if(appUserName != null) appUserName as String else context?.getSharedPreferences(CentralConstants.sharedprefname, 0)!!.getString("username", null),this,AdapterToUseFor.feed)
            }
            AdapterToUseFor.blog -> {
               // this.floatingDateHolder = FloatingDateHolder(context, view, recyclerView, this.fontSingleton, this@AllRecyclerViewAdapter)
              //  this.questionListFunctions = QuestionListFunctions(context, this@AllRecyclerViewAdapter, floatingDateHolder)
               // this.peopleFunctionsList = PeopleFunctionsList(mValues, this@AllRecyclerViewAdapter)
                this.blogHelperFunctions = FeedHelperFunctions(context as Context,if(appUserName != null) appUserName as String else context?.getSharedPreferences(CentralConstants.sharedprefname, 0)!!.getString("username", null),this,AdapterToUseFor.blog)
            }

            AdapterToUseFor.notifications -> {
                this.notificationsBusyHelperFunctions = NotificationsBusyHelperFundtions(context as Context,appUserName as String,this)
                //this.peopleFunctionsList = PeopleFunctionsList(mValues, this@AllRecyclerViewAdapter)
            }
            AdapterToUseFor.wallet -> {
              //  this.floatingDateHolder = FloatingDateHolder(context, view, recyclerView, this.fontSingleton, this@AllRecyclerViewAdapter)
              //  this.questionListFunctions = QuestionListFunctions(context, this@AllRecyclerViewAdapter, floatingDateHolder)
            }
            AdapterToUseFor.article ->{

            }
            AdapterToUseFor.comments ->{
                this.commentHelperFunctions = CommentsHelperFunctions(context as Context,if(appUserName != null) appUserName as String else context?.getSharedPreferences(CentralConstants.sharedprefname, 0)!!.getString("username", null),this,scale as Float,metrics as DisplayMetrics)
            }
            AdapterToUseFor.upvotes ->{
                this.upvotesHelperFunctions = UpvotesHelperFunctions(context as Context,if(appUserName != null) appUserName as String else context?.getSharedPreferences(CentralConstants.sharedprefname, 0)!!.getString("username", null),this)
            }
            AdapterToUseFor.followers ->{
                this.followDisplayHelperFunctions = FollowDisplayHelperFunctions(context as Context,if(appUserName != null) appUserName as String else context?.getSharedPreferences(CentralConstants.sharedprefname, 0)!!.getString("username", null),this,AdapterToUseFor.followers)
            }
            AdapterToUseFor.following ->{
                this.followDisplayHelperFunctions = FollowDisplayHelperFunctions(context as Context,if(appUserName != null) appUserName as String else context?.getSharedPreferences(CentralConstants.sharedprefname, 0)!!.getString("username", null),this,AdapterToUseFor.following)
            }
            AdapterToUseFor.draft ->{
                this.draftHelperFunctionss = draftHelperFunctions(context as Context,if(appUserName != null) appUserName as String else context?.getSharedPreferences(CentralConstants.sharedprefname, 0)!!.getString("username", null),this,AdapterToUseFor.draft)
            }
            AdapterToUseFor.replyNoti ->{
                this.repliesHelperFunctions = FeedHelperFunctions(context as Context,if(appUserName != null) appUserName as String else context?.getSharedPreferences(CentralConstants.sharedprefname, 0)!!.getString("username", null),this,apptype)
            }
            AdapterToUseFor.commentNoti ->{
                this.commentNotiHelperFunctions = FeedHelperFunctions(context as Context,if(appUserName != null) appUserName as String else context?.getSharedPreferences(CentralConstants.sharedprefname, 0)!!.getString("username", null),this,apptype)
            }
            AdapterToUseFor.beneficiaries -> {
                this.beneficiaryHelperFunctionsOb = beneficiaryHelperFunctions(context as Context,if(appUserName != null) appUserName as String else context?.getSharedPreferences(CentralConstants.sharedprefname, 0)!!.getString("username", null),this)
            }
            AdapterToUseFor.search ->{
                //initialize all the classes needed for searches
                this.feedHelperFunctions = FeedHelperFunctions(context as Context,appUserName,this,AdapterToUseFor.feed)
                this.searchUsersHelperFunctions = SearchUsersHelperFunctions(context as Context,appUserName as String ,this,AdapterToUseFor.followers)
                this.headerHelperFunctions = HeaderHelperFunctions(context as Context,appUserName as String,this,apptype)
            }

                //this.openAQuestionHelperFunctions = OpenAQuestionHelperFunctions(metrics, calcs, this.appUserName, this.fontSingleton, this@AllRecyclerViewAdapter)
            /*AdapterToUseFor.feed -> {
                this.floatingDateHolder = FloatingDateHolder(context, view, recyclerView, this.fontSingleton, this@AllRecyclerViewAdapter)
                this.notificationListFunctions = NotificationListFunctions(context, this@AllRecyclerViewAdapter, this.floatingDateHolder)
            }
            AdapterToUseFor.feed -> {
                //thisRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,1));
                tenorHelperFunctions = TenorHelperFunctions(appUserName, this@AllRecyclerViewAdapter)
                val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
                //thisRecyclerView.setLayoutManager(new GridLayoutManager(context,2));
                thisRecyclerView.layoutManager = layoutManager
            }*/
            else -> {
            }
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        /*View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_layout, parent, false);
        return new ViewHolder(view);*/
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
        /*else if (viewType == ischatview) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.chat_message_layout, parent, false)
            vh = ChatViewHolder(v)
        }
        if (viewType == isQuestionView) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.questionviewkeeper, parent, false)
            vh = QuestionsListViewHolder(v)
        } else if (viewType == isdateview) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.dateresourceforchat, parent, false)
            vh = DateViewHolder(v)
        } else if (viewType == isPeopleView) {

            v = LayoutInflater.from(parent.context).inflate(R.layout.peopleviewkeeper, parent, false)
            vh = PeopleViewHolder(v)
        } else if (viewType == isNoMessageView) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.messageifnothingtoshow, parent, false)
            vh = NothingToShow(v)
        } else if (viewType == isOpenAQuestionView) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.openaquestiondisplaynewlayout, parent, false)
            vh = OpenAQuestionDataHolder(v)

            val vhf = vh
            v!!.setOnClickListener {
                //here you set your current position from holder of clicked view

                openAQuestionHelperFunctions.OpenQuestionClickPasser(vhf)
            }

        } else if (viewType == isNotificationView) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.fragment_notification_manager_view, parent, false)
            vh = NotificationViewHolder(v)
        } else if (viewType == isTenorView) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.imageandgifview, parent, false)
            vh = TenorGifDataHolder(v)
            val vhf = vh
            v!!.setOnClickListener {
                tenorHelperFunctions.OpenQuestionClickPasser(vhf)
                //SetVisibilityOfButtons(VhUniv);
            }
        }*/
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



    }


    fun clear() {
        mValues?.clear()
        notifyDataSetChanged()
    }

    fun add(num: Long) {
        mValues?.add(num)
        notifyDataSetChanged()
    }

    fun getList():MutableList<Any>?{
        return mValues
    }


    /*fun add(holder: NothingToShowDataHolder) {
        mValues?.add(holder)
        notifyDataSetChanged()
    }*/


    override fun getItemViewType(position: Int): Int {
        //return mValues.get(position).get;

        /*if (position % 20 == 0)
            return isadview;*/

        val ins = mValues?.get(position)
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

        return isFeedView
        /*else if (ins is Long) {
            return isadview
        } else if (ins is DateTypeAndStringHolder) {
            return isdateview
        } else if (ins is ForReturningQuestionsLite) {
            return isQuestionView
        } else if (ins is PeopleDataHolder) {
            return isPeopleView
        } else if (ins is NothingToShowDataHolder) {
            return isNoMessageView
        } else if (ins is OpenAQuestionClassForShowingData) {
            return isOpenAQuestionView
        } else if (ins is QuestionNotifications) {
            return isNotificationView
        } else if (ins is JsonTenorTrendingMedium) {
            return isTenorView
        }

        return if (position % 10 == 0) isadview else ischatview*/
    }


    fun setEmptyView(v: View) {
        emptyView = v
    }


    fun showEmptyView(): Boolean {


        if (emptyView != null && recyclerView != null) {
            if (getItemCount() == 0) {
                if (emptyView!!.visibility == GONE) {
                    emptyView!!.visibility = VISIBLE
                    recyclerView?.visibility = GONE
                }

            } else {

                if (emptyView!!.visibility == VISIBLE) {
                    emptyView!!.visibility = GONE
                    recyclerView?.visibility = VISIBLE
                }


            }
            return true
        }


        return false

    }


    override fun getItemCount(): Int {
        return mValues?.size as Int
    }

    fun add(all: List<Any>) {
        for(x in all){
            add(x)
        }



    }


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
        mValues?.add(`object`)
        var si = mValues?.size as Int
        notifyitemcinserted(si)
        //return ""



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
        mValues?.removeAt(position)
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

    override fun getObject(position: Int): Any {
        return mValues?.get(position) as Any
    }

    override fun getSize(): Int {
        return mValues?.size as Int
    }

    override  fun getActivity():Activity{

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


}

