package com.steemapp.lokisveil.steemapp

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Enums.TypeOfRequest
import com.steemapp.lokisveil.steemapp.HelperClasses.AllAppWidget
import com.steemapp.lokisveil.steemapp.HelperClasses.AllAppWidgetConfigureActivity
import com.steemapp.lokisveil.steemapp.HelperClasses.JsonRpcResultConversion
import com.steemapp.lokisveil.steemapp.HelperClasses.MakeJsonRpc
import com.steemapp.lokisveil.steemapp.Interfaces.JsonRpcResultInterface
import com.steemapp.lokisveil.steemapp.MiscConstants.Companion.getBitmap
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomRepos.WidgetRepo
import java.util.*

//This is the class android invokes for handling list views in widgets
//most of the code is similar to the lg one, the same code will be slowly joined and deleted from here
class WidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsService.RemoteViewsFactory {
        return StackRemoteViewsFactory(this.applicationContext, intent)
    }
}

class WidgetItem(var text: String)

internal class StackRemoteViewsFactory(private val mContext: Context, intent: Intent) : RemoteViewsService.RemoteViewsFactory,JsonRpcResultInterface {
    private val mWidgetItems = ArrayList<FeedArticleDataHolder.FeedArticleHolder>()
    //var textColorMineTheme: Int = 0
    private var username = mContext.getSharedPreferences(CentralConstants.sharedprefname,0).getString(CentralConstants.username,null)
    private val mAppWidgetId: Int = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID)
    private val isrefresh :Boolean = intent.getBooleanExtra("isrefresh",false)
    lateinit var repo: WidgetRepo
    private var prevCount = 0
    //var textColorMineTheme = 0
    init {
        /*mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID)*/
    }

    /**
     * callback tell us to fetch more data as processing is done now
     */
    override fun processingDone(count: Int) {
        //repo.getLastDbKey(this)
        repo.getList(prevCount,this)
    }

    /**
     * callback get the daata list from the db
     */
    override fun insert(data: List<FeedArticleDataHolder.FeedArticleHolder>) {
        if(data.isEmpty()) return
        //clear the list
        mWidgetItems.clear()
        //add more
        mWidgetItems.addAll(data)
        //reverse the list as the data is new now
        mWidgetItems.reverse()
        updateTheWidget()
    }

    /**
     * insert the item into the db
     */
    override fun insert(data: FeedArticleDataHolder.FeedArticleHolder) {
        repo.insert(data)
    }

    /**
     * callback tells us how many items we have in the db
     */
    override fun countGot(count: Int?) {
        var id = 0
        if(count != null) id = count
        prevCount = id
        repo.getList(id,this)
    }

    override fun onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.

        // if it is a user refresh, clear and tell it to update the ui
        if(isrefresh){
            mWidgetItems?.clear()
            AppWidgetManager.getInstance(mContext).notifyAppWidgetViewDataChanged(mAppWidgetId,R.id.stack_view)
        }

        //construct the repo and fetc hthe last db key
        repo = WidgetRepo(mContext,null)
        repo.getLastDbKey(this)
        callcreate()
    }

    fun callcreate(){
        var tag = AllAppWidgetConfigureActivity.loadTitlePref(mContext,mAppWidgetId,"tag")
        var spinner = AllAppWidgetConfigureActivity.loadTitlePref(mContext,mAppWidgetId,"spinner")

        var req = "get_discussions_by_"
        //var tagte = tags_autocom.text.toString()
        //var sptl = spinnerTrending.selectedItem.toString()

        //mainremo.removeAllViews()
        req += if(spinner == "new"){
            "created"
        }
        else{
            spinner
        }

        if(spinner == "feed"){
            GetFeed()
        } else{
            GetFeed(req,tag)
        }
    }

    override fun onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        mWidgetItems.clear()
    }

    override fun getCount(): Int {
        Log.d("getCount","msi ${mWidgetItems.size}")
        return mWidgetItems.size
    }

    //called when fetching new views
    override fun getViewAt(position: Int): RemoteViews {
        // position will always range from 0 to getCount() - 1.
        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.

        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        if(mWidgetItems.size > 0){
            //bind them here
            Bind(rv,mWidgetItems[position],position)
        }
        Log.d("widgetservicesize","size is ${mWidgetItems.size} position is $position")


//        Glide
//         .with(mContext)
//                .load("https://www.google.es/images/srpr/logo11w.png")
//                .asBitmap()
//                .into(object : SimpleTarget<Bitmap>(100, 100) {
//                     fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation) {
//                        image.setImageBitmap(resource) // Possibly runOnUiThread()
//                    }
//                })
        //val ips = url.getContent() as InputStream
        //val dr = Drawable.createFromStream(ips,null)
        //rv.setImageViewBitmap(R.id.article_image, BitmapFactory.decodeStream(url.openConnection().getInputStream()))
        //rv.set

        // You can do heaving lifting in here, synchronously. For example, if you need to
        // process an image, fetch something from the network, etc., it is ok to do it here,
        // synchronously. A loading view will show up in lieu of the actual contents in the
        // interim.
        try {
            println("Loading view $position")
            //Thread.sleep(500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        // Return the remote views object.
        return rv
    }

    override fun getLoadingView(): RemoteViews? {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun onDataSetChanged() {
        Log.d("data called","now items size is ${mWidgetItems.size}")
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.

    }


    fun GetFeed(mainrequest:String,maintag:String){
        val volleyre : VolleyRequest = VolleyRequest.getInstance(mContext)
        val url = "https://api.steemit.com/"
        val d = MakeJsonRpc.getInstance()
        var nametouse : String = if(username != null)  username as String else ""


        val s = JsonObjectRequest(Request.Method.POST,url,d.getTagQuery(mainrequest,maintag,"20"),
                Response.Listener { response ->

                    val con = JsonRpcResultConversion(response,nametouse, TypeOfRequest.feed,mContext,this,false)

                    val result = con.ParseJsonBlogMore()

                    if(result != null && !result.isEmpty()){
                        mWidgetItems.addAll(result)
                        AppWidgetManager.getInstance(mContext).notifyAppWidgetViewDataChanged(mAppWidgetId,R.id.stack_view)
                    } else{
                    }

                }, Response.ErrorListener {
        }

        )
        volleyre.addToRequestQueue(s)
    }

    fun updateTheWidget(){
        AppWidgetManager.getInstance(mContext).notifyAppWidgetViewDataChanged(mAppWidgetId,R.id.stack_view)
    }

    fun GetFeed(){
        if(username == null){
            val sharedPreferences = mContext.getSharedPreferences(CentralConstants.sharedprefname, 0)
            username = sharedPreferences?.getString(CentralConstants.username, null)
            //key = sharedPreferences?.getString(CentralConstants.key, null)
        }

        val volleyre : VolleyRequest = VolleyRequest.getInstance(mContext)
        val url = "https://api.steemit.com/"
        val d = MakeJsonRpc.getInstance()

        var nametouse = username

        val s = JsonObjectRequest(Request.Method.POST,url,d.getFeedJ(nametouse),
                Response.Listener { response ->
                    val con = JsonRpcResultConversion(response,nametouse,TypeOfRequest.feed,mContext,this,false)
                    val result = con.ParseJsonBlog()
                    if(result != null && !result.isEmpty()){
                        mWidgetItems.addAll(result)
                        AppWidgetManager.getInstance(mContext).notifyAppWidgetViewDataChanged(mAppWidgetId,R.id.stack_view)
                    } else {
                    }


                }, Response.ErrorListener {
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }


    public fun Bind(rv:RemoteViews,article:FeedArticleDataHolder.FeedArticleHolder,position:Int){


        var pay = article?.pending_payout_value
        if((article?.already_paid.orEmpty() != "0.000 SBD")){
            pay = article?.already_paid
        }


        rv.setTextViewText(R.id.article_comments,article.children.toString()) //.article_comments?.text = holder.article?.children.toString()

        if(article?.uservoted != null && article?.uservoted as Boolean == true){
            rv.setTextColor(R.id.article_likes,ContextCompat.getColor(mContext, R.color.colorAccent))
            rv.setTextViewCompoundDrawables(R.id.article_likes,R.drawable.ic_thumb_up_likeint_24px,0,0,0)
        }
        else {

            //holder.article_likes?.setTextColor(ContextCompat.getColor(con, R.color.abc_hint_foreground_material_light))
            rv.setTextColor(R.id.article_likes,ContextCompat.getColor(mContext, R.color.white))
            rv.setTextViewCompoundDrawables(R.id.article_likes,R.drawable.ic_thumb_up_white_24px,0,0,0)
        }
        rv.setTextViewText(R.id.article_likes,article.netVotes.toString())

        rv.setTextViewText(R.id.article_name,article.displayName)
        //holder.article_name?.text = holder.article?.displayName
        if(article?.followsYou!!){
            rv.setTextColor(R.id.article_name,ContextCompat.getColor(mContext, R.color.colorAccent))
            //holder.article_name?.setTextColor(ContextCompat.getColor(con, R.color.colorAccent))
        } else{
            rv.setTextColor(R.id.article_name,ContextCompat.getColor(mContext, R.color.white))
        }
        /*holder.article_name?.setOnClickListener(View.OnClickListener {
            val i = Intent(con, OpenOtherGuyBlog::class.java)
            i.putExtra(CentralConstants.OtherGuyNamePasser,holder.article?.author)
            con.startActivity(i)
        })*/
        rv.setTextViewText(R.id.article_payout,pay)
        rv.setTextViewText(R.id.article_date,article.datespan)
        val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_person_white_24px)
                //.error(R.drawable.error)
                .priority(Priority.HIGH)
                .circleCrop()
        var sh = Glide.with(mContext).load(CentralConstants.GetFeedImageUrl(article?.author)).apply(options)
                //.placeholder(R.drawable.common_full_open_on_phone)
                .submit()

        rv.setImageViewBitmap(R.id.article_pfp,getBitmap(mContext,sh,true))


        if(article?.reblogBy == null || article?.reblogBy?.isEmpty() as Boolean){
            //holder.article_resteemed_by?.visibility = View.GONE
            //hide the resteem bar if there are no resteems
            //holder.article_resteemed_by_Linear?.visibility = View.GONE
        }
        else{
            //holder.article_resteemed_by?.visibility = View.VISIBLE
            //holder.article_resteemed_by_Linear?.visibility = View.VISIBLE
            rv.setTextViewText(R.id.article_resteemed_by,article.reblogBy?.get(0))
            rv.setTextViewCompoundDrawables(R.id.article_resteemed_by,R.drawable.ic_repeat_black_24px,0,0,0)
            /*holder.article_resteemed_by?.setOnClickListener(View.OnClickListener {
                val i = Intent(con, OpenOtherGuyBlog::class.java)
                i.putExtra(CentralConstants.OtherGuyNamePasser,holder.article?.reblogBy?.get(0))
                con?.startActivity(i)
            })*/
        }

        //Add the app name for users to view
        rv.setTextViewText(R.id.article_tag,"in ${article?.category} using ${article?.app}")
       /* holder.article_tag?.setOnClickListener(View.OnClickListener {
            var it = Intent(con,MainTags::class.java)
            it.putExtra(CentralConstants.MainRequest,"get_discussions_by_trending")
            it.putExtra(CentralConstants.MainTag,holder.article?.category)
            it.putExtra(CentralConstants.OriginalRequest,"trending")
            con.startActivity(it)

        })*/
        rv.setTextViewText(R.id.article_title,article.title)


        val optionss = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_all_inclusive_black_24px)
                //.error(R.drawable.error)
                .priority(Priority.HIGH)

        //use first of null so the app does not crash if not images exist
        if(article?.image?.firstOrNull() != null){
            var sg = Glide.with(mContext).load(article?.image?.firstOrNull()).apply(optionss)
                    //.placeholder(R.drawable.common_full_open_on_phone)
                    .submit()
            rv.setImageViewBitmap(R.id.article_image,getBitmap(mContext,sg))
        }
        


        //var by : in.uncod.android.bypass =



        // fill the intent instead of making one as in lg widget
        val extras = Bundle()
        extras.putInt(AllAppWidget.EXTRA_ITEM, position)

        extras.putString("username", if(article?.rootAuthor != null) article?.rootAuthor else article?.author)
        extras.putString("tag", article?.category)
        extras.putString("permlink", if(article?.rootPermlink != null) article?.rootPermlink else article?.permlink)

        //forward the dbid so we do not have to fetch the item from the server
        extras.putInt("dbId",article.myDbKey)
        extras.putBoolean("fromWidget",true)


        //if(adaptype == AdapterToUseFor.commentNoti || adaptype == AdapterToUseFor.replyNoti) myIntent.putExtra("permlinkToFind",holder.article?.permlink)

        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.openarticle, fillInIntent)

        //mholder.openarticle
        /*mholder.openarticle?.setOnClickListener(View.OnClickListener {
            //ForReturningQuestionsLite q = item;

            val myIntent = Intent(con, ArticleActivity::class.java)
            myIntent.putExtra("username", if(holder.article?.rootAuthor != null) holder.article?.rootAuthor else holder.article?.author)
            myIntent.putExtra("tag", holder.article?.category)
            myIntent.putExtra("permlink", if(holder.article?.rootPermlink != null) holder.article?.rootPermlink else holder.article?.permlink)
            if(adaptype == AdapterToUseFor.commentNoti || adaptype == AdapterToUseFor.replyNoti) myIntent.putExtra("permlinkToFind",holder.article?.permlink)
            con.startActivity(myIntent)
            //creating a popup menu

        })*/

        /*mholder.article_like?.setOnClickListener(View.OnClickListener {
            // Toast.makeText(con,"Processing. Please wait....",Toast.LENGTH_LONG).show()
            //ForReturningQuestionsLite q = item;
            var articles = mholder.article as FeedArticleDataHolder.FeedArticleHolder
            //var s : SteemJ = SteemJ()
            //s.vote(AccountName(holder.article?.author), Permlink(holder.article?.permlink),10000)
            var vop  = VoteOperation(AccountName(name), AccountName(articles.author), Permlink(articles.permlink))
            vop.weight = 100
            var obs = Response.Listener<JSONObject> { response ->
                //var articles = mholder.article as FeedArticleDataHolder.FeedArticleHolder
                val gson = Gson()
                var ress = gson.fromJson<Block.BlockAdded>(response.toString(), Block.BlockAdded::class.java)
                if(ress != null && ress.result != null ){
                    //con.run { Toast.makeText(con,"Upvoted ${vop.permlink}",Toast.LENGTH_LONG).show() }
                    Toast.makeText(con,"Upvoted ${vop.permlink}", Toast.LENGTH_LONG).show()
                    holder.article?.uservoted.to(true)
                    holder.article_likes?.setTextColor(ContextCompat.getColor(con, R.color.colorAccent))
                    adaptedcomms.notifyitemcchanged(mholder.adapterPosition)
                    //Runnable { run {  } }
                    //Toast.makeText(con,"$name has upvoted ${vop.author.name}",Toast.LENGTH_LONG).show()
                }
            }
            //var bloc = GetDynamicAndBlock(con ,adaptedcomms,position,mholder,null,vop,obs,"Upvoted ${vop.permlink.link}",MyOperationTypes.vote)
            *//*var list  = ArrayList<Operation>()
            list.add(vop)
            var bloc = GetDynamicAndBlock(con ,adaptedcomms,position,list,"Upvoted ${vop.permlink.link}",MyOperationTypes.vote)
            bloc.GetDynamicGlobalProperties()*//*
            val weight = VoteWeightThenVote(con,adaptedcomms.getActivity(),vop,adaptedcomms,position,holder.progressbar,null)
            weight.makeDialog()
            *//*globallist.add(bloc)

            Log.d("buttonclick",globallist.toString())*//*
            //GetDynamicGlobalProperties(mholder.article as FeedArticleDataHolder.FeedArticleHolder)

        })*/



        if(article?.author == username){
            //mholder.article_reblog_now?.visibility = View.GONE
        }
        else{
           /* mholder.article_reblog_now?.visibility = View.VISIBLE
            mholder.article_reblog_now?.setOnClickListener(View.OnClickListener {
                //Toast.makeText(con,"Processing. Please wait....",Toast.LENGTH_LONG).show()
                //ForReturningQuestionsLite q = item;
                var articles = mholder.article as FeedArticleDataHolder.FeedArticleHolder
                var vop = ReblogOperation(AccountName(name), AccountName(articles.author), Permlink(articles.permlink))
                var al = ArrayList<AccountName>()
                al.add(AccountName(name))
                var cus = CustomJsonOperation(null,al,"follow",vop.toJson())

                var list  = ArrayList<Operation>()
                list.add(cus)
                //var bloc = GetDynamicAndBlock(con ,adaptedcomms,position,mholder,null,cus,obs,"Reblogged ${vop.permlink.link}",MyOperationTypes.reblog)
                var bloc = GetDynamicAndBlock(con ,adaptedcomms,position,list,"Reblogged ${vop.permlink.link}", MyOperationTypes.reblog)
                bloc.GetDynamicGlobalProperties()

            })*/
        }

        rv.setTextViewText(R.id.article_summary,article.summary)

    }

    companion object {
        //private val mCount = 10
    }
}