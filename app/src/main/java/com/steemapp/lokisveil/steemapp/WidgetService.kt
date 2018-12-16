package com.steemapp.lokisveil.steemapp

import android.appwidget.AppWidgetManager
import android.widget.RemoteViewsService
import android.text.method.TextKeyListener.clear
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.RemoteViews
import java.io.InputStream
import java.net.URL
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.Transition
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.R.attr.bitmap
import android.app.Application
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Canvas
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.request.FutureTarget
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Databases.RequestsDatabase
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.Enums.TypeOfRequest
import com.steemapp.lokisveil.steemapp.HelperClasses.*
import com.steemapp.lokisveil.steemapp.Interfaces.JsonRpcResultInterface
import com.steemapp.lokisveil.steemapp.MiscConstants.Companion.getBitmap
import com.steemapp.lokisveil.steemapp.MyViewHolders.ArticleViewHolder
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomRepos.WidgetRepo
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomViewModels.WidgetVM
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.AccountName
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.Permlink
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.CustomJsonOperation
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.Operation
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.ReblogOperation
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.VoteOperation
import com.steemapp.lokisveil.steemapp.jsonclasses.Block
import com.steemapp.lokisveil.steemapp.jsonclasses.feed
import org.json.JSONObject
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

    override fun processingDone(count: Int) {
        //repo.getLastDbKey(this)
        repo.getList(prevCount,this)
    }

    override fun insert(data: List<FeedArticleDataHolder.FeedArticleHolder>) {
        if(data.isEmpty()) return
        mWidgetItems.clear()
        mWidgetItems.addAll(data)
        mWidgetItems.reverse()
        updateTheWidget()
    }

    override fun insert(data: FeedArticleDataHolder.FeedArticleHolder) {
        repo.insert(data)
    }

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
        /*for (i in 0 until mCount) {
            mWidgetItems.add(WidgetItem(i.toString() + "!"))
        }*/

        // if it is a user refresh, clear and tell it to update the ui
        if(isrefresh){
            mWidgetItems?.clear()
            AppWidgetManager.getInstance(mContext).notifyAppWidgetViewDataChanged(mAppWidgetId,R.id.stack_view)
        }
        /*var attrs  = intArrayOf(R.attr.textColorMine)
        var ta = mContext.obtainStyledAttributes(attrs)
        var textColorMineThemeint = ta.getResourceId(0, android.R.color.black)
        ta.recycle()
        textColorMineTheme = ContextCompat.getColor(mContext, textColorMineThemeint)*/
        // We sleep for 3 seconds here to show how the empty view appears in the interim.
        // The empty view is set in the StackWidgetProvider and should be a sibling of the
        // collection view.
        /*try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }*/
        /*var attrs  = intArrayOf(R.attr.textColorMine)
        var ta = mContext.obtainStyledAttributes(attrs)
        var textColorMineThemeint = ta.getResourceId(0, android.R.color.black)
        ta.recycle()
        textColorMineTheme = ContextCompat.getColor(mContext, textColorMineThemeint)*/
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
        // Next, we set a fill-intent which will be used to fill-in the pending intent template
        // which is set on the collection view in StackWidgetProvider.
        /*val extras = Bundle()
        extras.putInt(AllAppWidget.EXTRA_ITEM, position)

        extras.putExtra("username", if(holder.article?.rootAuthor != null) holder.article?.rootAuthor else holder.article?.author)
        extras.putExtra("tag", holder.article?.category)
        extras.putExtra("permlink", if(holder.article?.rootPermlink != null) holder.article?.rootPermlink else holder.article?.permlink)
        //if(adaptype == AdapterToUseFor.commentNoti || adaptype == AdapterToUseFor.replyNoti) myIntent.putExtra("permlinkToFind",holder.article?.permlink)

        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent)*/
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

        /*var tag = AllAppWidgetConfigureActivity.loadTitlePref(mContext,mAppWidgetId,"tag")
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
        }*/

        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.

    }


    fun GetFeed(mainrequest:String,maintag:String){
        //val queue = Volley.newRequestQueue(context)

        /*swipecommonactionsclass?.makeswiperun()

        if(username == null){
            val sharedPreferences = view?.context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
            username = sharedPreferences?.getString(CentralConstants.username, null)
            key = sharedPreferences?.getString(CentralConstants.key, null)
        }*/

        val volleyre : VolleyRequest = VolleyRequest.getInstance(mContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = "https://api.steemit.com/"
        val d = MakeJsonRpc.getInstance()
        //val g = Gson()
        var nametouse : String = if(username != null)  username as String else ""


        val s = JsonObjectRequest(Request.Method.POST,url,d.getTagQuery(mainrequest,maintag,"20"),
                Response.Listener { response ->

                    //val gson = Gson()
                    /*val collectionType = object : TypeToken<List<feed.FeedData>>() {

                    }.type*/
                    val con = JsonRpcResultConversion(response,nametouse, TypeOfRequest.feed,mContext,this,false)
                    //con.ParseJsonBlog()
                    //val result = con.ParseJsonBlog()
                    val result = con.ParseJsonBlogMore()
                    //val result = gson.fromJson<List<feed.FeedData>>(response.toString(),collectionType)
                    if(result != null && !result.isEmpty()){

                        /*adapter?.feedHelperFunctions.add(result)*/
                        //displayMessage(result)
                        //displayMessageFeddArticle(result)
                        mWidgetItems.addAll(result)
                        /*for(x in result){
                            mWidgetItems.add(x)
                        }*/
                        AppWidgetManager.getInstance(mContext).notifyAppWidgetViewDataChanged(mAppWidgetId,R.id.stack_view)
                    } else{
                        //callcreate()
                    }

                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }

    fun updateTheWidget(){
        AppWidgetManager.getInstance(mContext).notifyAppWidgetViewDataChanged(mAppWidgetId,R.id.stack_view)
    }

    fun GetFeed(){
        //val queue = Volley.newRequestQueue(context)



        if(username == null){
            val sharedPreferences = mContext.getSharedPreferences(CentralConstants.sharedprefname, 0)
            username = sharedPreferences?.getString(CentralConstants.username, null)
            //key = sharedPreferences?.getString(CentralConstants.key, null)
        }

        val volleyre : VolleyRequest = VolleyRequest.getInstance(mContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = "https://api.steemit.com/"
        val d = MakeJsonRpc.getInstance()

        var nametouse = username

        val s = JsonObjectRequest(Request.Method.POST,url,d.getFeedJ(nametouse),
                Response.Listener { response ->

                    //save request to db, id goes to state
                    /*if(mContext != null){
                        val req = RequestsDatabase(mContext!!)
                        //req.DeleteOld()
                        var ad = req.Insert(com.steemapp.lokisveil.steemapp.DataHolders.Request(json = response.toString() ,dateLong = Date().time, typeOfRequest = TypeOfRequest.feed.name,otherInfo = "feedfirst"))
                        if(ad > 0){
                            dblist.add(ad)
                        }

                    }*/

                    val con = JsonRpcResultConversion(response,nametouse,TypeOfRequest.feed,mContext,this,false)
                    //con.ParseJsonBlog()
                    val result = con.ParseJsonBlog()
                    //val result = gson.fromJson<List<feed.FeedData>>(response.toString(),collectionType)
                    if(result != null && !result.isEmpty()){

                        /*adapter?.feedHelperFunctions.add(result)*/
                        //displayMessage(result)

                        mWidgetItems.addAll(result)
                        /*for(x in result){
                            mWidgetItems.add(x)
                        }*/
                        AppWidgetManager.getInstance(mContext).notifyAppWidgetViewDataChanged(mAppWidgetId,R.id.stack_view)
                    } else {
                        //callcreate()
                    }
                    //addItems(response,nametouse)


                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }

    /*public fun getBitmap(sh:FutureTarget<Drawable>):Bitmap?{
        try{
            var g = sh.get()
            var bt = Bitmap.createBitmap(g.getIntrinsicWidth(), g.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            val canvas = Canvas(bt)
            g.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
            g.draw(canvas)
            return bt
        } catch (ex:Exception){
            Log.d("getBitmapWidget",ex.message)
        }
        return null

    }*/


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