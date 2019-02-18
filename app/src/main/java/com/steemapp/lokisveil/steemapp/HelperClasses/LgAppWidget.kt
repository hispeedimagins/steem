package com.steemapp.lokisveil.steemapp.HelperClasses

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.steemapp.lokisveil.steemapp.ArticleActivity
import com.steemapp.lokisveil.steemapp.CentralConstants
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Enums.TypeOfRequest
import com.steemapp.lokisveil.steemapp.MiscConstants.Companion.getBitmap
import com.steemapp.lokisveil.steemapp.R
import com.steemapp.lokisveil.steemapp.VolleyRequest
import java.util.*
import kotlin.collections.ArrayList


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [AllAppWidgetConfigureActivity]
 */

//The widget provider for lg smart bulletin
//Almost the same as the other one but has some differences.
//The width of the sb is infinite, hence you have to add items not use a list as in
//android providers.
class LgAppWidget : AppWidgetProvider() {

    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            AllAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId,"tag")
            AllAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId,"spinner")
        }
        super.onDeleted(context, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        super.onEnabled(context)

    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context)
    }

    override fun onReceive(context: Context, intent: Intent) {

        //same as other providers, for clicks on intents
        val mgr = AppWidgetManager.getInstance(context)
        var appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID)

        if(appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID){
            var ids = mgr.getAppWidgetIds(ComponentName(context,this@LgAppWidget::class.java))
            Log.d("id not found, using",ids.toString())
            if(ids != null){
                appWidgetId = ids[0]
                Log.d("widgetid updated ",appWidgetId.toString())
            }
        }

        Log.d("appwidgetrec","appwidgetid got is $appWidgetId")
        //val viewIndex = intent.getIntExtra(EXTRA_ITEM, 0)
        if(intent.action == LgAppWidget.sb_refresh){
            if(intent.getBooleanExtra("refresh",false)){
                Toast.makeText(context, "Refreshing", Toast.LENGTH_SHORT).show()
                updateAppWidget(context,mgr,appWidgetId)
            }
        } else if(intent.action == LgAppWidget.article_clicked){
            Toast.makeText(context, "opening ${intent.getStringExtra("permlink")}", Toast.LENGTH_SHORT).show()

            val myIntent = Intent(context, ArticleActivity::class.java)
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            myIntent.putExtra("username", intent.getStringExtra("username"))
            myIntent.putExtra("tag", intent.getStringExtra("tag"))
            myIntent.putExtra("permlink", intent.getStringExtra("permlink"))
            context.startActivity(myIntent)
        } else if(intent.action == LgAppWidget.sb_settings){
            var myIntent = Intent(context,AllAppWidgetConfigureActivity::class.java)
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            myIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId)
            myIntent.putExtra("islg",true)
            context.startActivity(myIntent)
        }


        super.onReceive(context, intent)
    }

    companion object {
        val TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION"
        val EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM"
        val article_clicked = "sb.article.clicked"
        val sb_refresh = "sb.refresh"
        val sb_settings = "sb.settings"

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            /* val widgetText = AllAppWidgetConfigureActivity.loadTitlePref(context, appWidgetId)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.all_app_widget)
            views.setTextViewText(R.id.appwidget_text, widgetText)*/

            // Instruct the widget manager to update the widget

            // Here we setup the intent which points to the StackViewService which will
            // provide the views for this collection.
            /*val intent = Intent(context, WidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)*/

            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            //intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            val rv = RemoteViews(context.packageName, R.layout.lg_app_widget)
            //rv.setRemoteAdapter(R.id.stack_view, intent)

            // The empty view is displayed when the collection has no items. It should be a sibling
            // of the collection view.
            //rv.setEmptyView(R.id.stack_view, R.id.empty_view)




            //Tell the widget that it is lg, hence use lg's pref name
            AllAppWidgetConfigureActivity.changePrefName(true)
            //get the data now
            var tag = AllAppWidgetConfigureActivity.loadTitlePref(context,appWidgetId,"tag")
            var spinner = AllAppWidgetConfigureActivity.loadTitlePref(context,appWidgetId,"spinner")
            var ex = context.getString(R.string.appwidget_text)
            Log.d("spinner shit ","tag : $tag , spinner : $spinner , ex : $ex , appwidgetid : $appWidgetId")
            if(tag == ex) tag = ""
            if(spinner == ex) spinner = "new"
            val sharedPreferences = context.getSharedPreferences(CentralConstants.sharedprefname, 0)
            val username = sharedPreferences.getString(CentralConstants.username, "")
            callcreate(tag,spinner,context,username,rv,appWidgetManager,appWidgetId)
            rv.setTextViewText(R.id.widget_heading,spinner.toUpperCase())
            if(tag.isNotEmpty() && tag.isNotBlank() && spinner != "feed"){
                rv.setTextViewText(R.id.widget_heading,"$spinner - $tag")
            }
            var cal = calendarcalculations()
            cal.setDateOfTheData(Date().time,true)
            rv.setTextViewText(R.id.widget_refreshed,cal.getTimeString() )


            // Here we setup the a pending intent template. Individuals items of a collection
            // cannot setup their own pending intents, instead, the collection as a whole can
            // setup a pending intent template, and the individual items can set a fillInIntent
            // to create unique before on an item to item basis.
            /*val toastIntent = Intent(context, LgAppWidget::class.java)
            toastIntent.action = this.TOAST_ACTION
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            //intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            val toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)*/
            Log.d("startlg","appwidgetiszero $appWidgetId")
            /*val extras = Bundle()
            extras.putBoolean("refresh",true)*/
            /*val fillInIntent = Intent()
            fillInIntent.putExtras(extras)
            rv.setOnClickFillInIntent(R.id.widget_refreshed, fillInIntent)*/

            var ointent = LgAppWidget.getPendingSelfIntent(context)
            //ointent.putExtras(extras)
            ointent.putExtra("refresh",true)
            rv.setOnClickPendingIntent(R.id.widget_refreshed,getPendingSelfIntent(context,LgAppWidget.sb_refresh,ointent,appWidgetId))



            rv.setOnClickPendingIntent(R.id.widget_settings,getPendingSelfIntent(context,LgAppWidget.sb_settings,appWidgetId = appWidgetId))


            appWidgetManager.updateAppWidget(appWidgetId, rv)


            //appWidgetManager.updateAppWidget(appWidgetId, rv)
        }






        //starts the process of gettings results
        fun callcreate(tag:String,spinner:String,mContext:Context,username:String,rv:RemoteViews,appWidgetManager: AppWidgetManager,appWidgetId: Int){


            var req = "get_discussions_by_"
            //var tagte = tags_autocom.text.toString()
            //var sptl = spinnerTrending.selectedItem.toString()

            //mainremo.removeAllViews()

            // add the apinner data to req if it is not new
            req += if(spinner == "new"){
                "created"
            }
            else{
                spinner
            }
            //check if we are getting the feed or by tag
            if(spinner == "feed"){
                GetFeed(mContext,username,rv,appWidgetManager,appWidgetId)
            } else{
                GetFeed(req,tag,mContext,username,rv,appWidgetManager,appWidgetId)
            }
        }


        //Fetch the tag
        fun GetFeed(mainrequest:String,maintag:String,mContext:Context,username:String,rv:RemoteViews,appWidgetManager: AppWidgetManager,appWidgetId: Int){

            val volleyre : VolleyRequest = VolleyRequest.getInstance(mContext)
            //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
            val url = "https://api.steemit.com/"
            val d = MakeJsonRpc.getInstance()
            //val g = Gson()
            var nametouse : String = if(username != null)  username as String else ""


            val s = JsonObjectRequest(Request.Method.POST,url,d.getTagQuery(mainrequest,maintag,"20"),
                    Response.Listener { response ->

                        val con = JsonRpcResultConversion(response,nametouse as String, TypeOfRequest.feed,mContext)
                        //con.ParseJsonBlog()
                        //val result = con.ParseJsonBlog()

                        //parse the data using as a tag
                        val result = con.ParseJsonBlogMore()
                        //val result = gson.fromJson<List<feed.FeedData>>(response.toString(),collectionType)
                        if(result != null && !result.isEmpty()){

                            //beging adding the items
                            AddItems(result,rv,mContext,appWidgetManager,appWidgetId)
                            //AppWidgetManager.getInstance(mContext).notifyAppWidgetViewDataChanged(mAppWidgetId,R.id.stack_view)
                        } else{
                            //callcreate()
                        }

                    }, Response.ErrorListener {
            }

            )
            //queue.add(s)
            volleyre.addToRequestQueue(s)
        }


        //fetch a users feed
        fun GetFeed(mContext:Context,username:String,rv:RemoteViews,appWidgetManager: AppWidgetManager,appWidgetId: Int){

            val volleyre : VolleyRequest = VolleyRequest.getInstance(mContext)
            //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
            val url = "https://api.steemit.com/"
            val d = MakeJsonRpc.getInstance()

            var nametouse = username

            val s = JsonObjectRequest(Request.Method.POST,url,d.getFeedJ(nametouse),
                    Response.Listener { response ->

                        val con = JsonRpcResultConversion(response,nametouse, TypeOfRequest.feed,mContext)
                        //con.ParseJsonBlog()

                        //parse as a blog
                        val result = con.ParseJsonBlog()
                        //val result = gson.fromJson<List<feed.FeedData>>(response.toString(),collectionType)
                        if(result != null && !result.isEmpty()){
                            //begin adding items
                            AddItems(result,rv,mContext,appWidgetManager,appWidgetId)
                            //AppWidgetManager.getInstance(mContext).notifyAppWidgetViewDataChanged(mAppWidgetId,R.id.stack_view)
                        } else {
                            //callcreate()
                        }
                        //addItems(response,nametouse)


                    }, Response.ErrorListener {

            }

            )
            //queue.add(s)
            volleyre.addToRequestQueue(s)
        }

        fun AddItems(list:List<FeedArticleDataHolder.FeedArticleHolder>,rv:RemoteViews,mContext: Context,appWidgetManager: AppWidgetManager,appWidgetId: Int){
            /*for(x in list){
                class someTask() : AsyncTask<Void, Void, String>() {
                    override fun doInBackground(vararg params: Void?): String? {
                        var r = Bind(x,0,mContext)
                        //rv.addView(R.id.stack_view,x)
                        Log.d("reached add","seems to be addng ${rv.`package`}")
                        rv.addView(R.id.stack_view, r )
                        appWidgetManager.updateAppWidget(appWidgetId,rv)
                        return ""
                    }
                }
                someTask().execute()
            }*/

            //make an async class
            class someTask() : AsyncTask<Void, Void, ArrayList<RemoteViews>>() {

                override fun onPreExecute() {
                    //remove old views
                    rv.removeAllViews(R.id.stack_view)
                    appWidgetManager.updateAppWidget(appWidgetId, rv)
                    Log.d("removed","removed all views")
                    super.onPreExecute()
                }

                override fun doInBackground(vararg params: Void?): ArrayList<RemoteViews>? {
                    var rvlist = ArrayList<RemoteViews>()
                    //beging binding
                    for(x in list){
                        try{
                            var r = Bind(x,appWidgetId,mContext)
                            Log.d("reached add","seems to be addng ${rv.`package`}")
                            //accumulate in the list
                            rvlist.add(r)
                            //rv.addView(R.id.stack_view,r)
                            //appWidgetManager.updateAppWidget(appWidgetId,rv)
                        } catch (ex:Exception){
                            Log.e("lgwidgetback",ex.message)
                        }

                        //rv.addView(R.id.stack_view, r )
                    }

                    return rvlist
                }

                override fun onPostExecute(result: ArrayList<RemoteViews>?) {
                    Log.d("on post ",result.toString());
                    //render on the ui thread
                    try{
                        if(result != null){
                            for(x in result){
                                rv.addView(R.id.stack_view,x)
                                Log.d("reached post add","seems to be addng ${rv.layoutId}")
                            }
                        }
                        //after adding them all,
                        //now call the update widget function of android
                        //telling it to update the ui
                        appWidgetManager.updateAppWidget(appWidgetId,rv)
                        super.onPostExecute(result)
                    } catch(ex:Exception){
                        Log.e("error",ex.message)
                    }


                }
            }
            someTask().execute()

        }

        fun forchangingcolor(context:Context){
            val intent = Intent("com.lge.launcher2.smartbulletin.configuration.color")
            intent.putExtra("component_name", "com.steemapp.lokisveil.steemapp.HelperClasses/"+
                    "com.steemapp.lokisveil.steemapp.HelperClasses.LgAppWidget")
            intent.putExtra("background_color", Color.RED)
            intent.putExtra("title_color", Color.WHITE)
            context.sendBroadcast(intent)
        }





        //common function for getting an intent
        fun getPendingSelfIntent(context:Context):Intent{
            return Intent(context, LgAppWidget::class.java)
        }

        fun getPendingSelfIntent(context:Context, action:String,ointent:Intent? = null,appWidgetId: Int):PendingIntent {
            var intent = if(ointent == null) getPendingSelfIntent(context) else ointent
            intent.action = action
            //add the app widget id for use in intents
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            //since extras are ignored while comparing intents, parse it to the data
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            return PendingIntent.getBroadcast(context, 0, intent, 0)
        }

       /*fun addCard(context:Context) {
            var list = CardList(context);
            var current = list.addCard("Card" + list.cardCount);
            var remoteViews = RemoteViews(context.getPackageName(),R.layout.card_layout);
            init(context, remoteViews);
            update(context, remoteViews);
        }*/


        public fun Bind(article: FeedArticleDataHolder.FeedArticleHolder, appWidgetId:Int,mContext: Context):RemoteViews{

            // a lot of this code is going to be used
            //for creating other actions in widgets
            //hence the commeting


            val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
            var pay = article?.pending_payout_value
            if((article?.already_paid.orEmpty() != "0.000 SBD")){
                pay = article?.already_paid
            }


            //setting data for remote views here for each article
            rv.setTextViewText(R.id.article_comments,article.children.toString()) //.article_comments?.text = holder.article?.children.toString()

            if(article?.uservoted != null && article?.uservoted as Boolean == true){
                rv.setTextColor(R.id.article_likes,ContextCompat.getColor(mContext, R.color.colorAccent))
                rv.setTextViewCompoundDrawables(R.id.article_likes,R.drawable.ic_thumb_up_likeint_24px,0,0,0)
            }
            else {

                //holder.article_likes?.setTextColor(ContextCompat.getColor(con, R.color.abc_hint_foreground_material_light))
                //rv.setTextColor(R.id.article_likes,textColorMineTheme)
                //rv.setTextViewCompoundDrawables(R.id.article_likes,R.drawable.ic_thumb_up_black_24px,0,0,0)
            }
            rv.setTextViewText(R.id.article_likes,article.netVotes.toString())

            rv.setTextViewText(R.id.article_name,article.displayName)
            //holder.article_name?.text = holder.article?.displayName
            if(article?.followsYou!!){
                rv.setTextColor(R.id.article_name,ContextCompat.getColor(mContext, R.color.colorAccent))
                //holder.article_name?.setTextColor(ContextCompat.getColor(con, R.color.colorAccent))
            } else{
                //rv.setTextColor(R.id.article_name,textColorMineTheme)
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

            //Get the image via glide to use its cache for faster loads
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
                //var url = URL(article?.image?.firstOrNull())
                //val ips = url.getContent() as InputStream
                //val dr = Drawable.createFromStream(ips,null)
                //rv.setImageViewBitmap(R.id.article_image, BitmapFactory.decodeStream(url.openConnection().getInputStream()))

                var sg = Glide.with(mContext).load(article?.image?.firstOrNull()).apply(optionss)
                        //.placeholder(R.drawable.common_full_open_on_phone)
                        .submit()
                var set = getBitmap(mContext,sg)
                if(set != null) rv.setImageViewBitmap(R.id.article_image,set)

            }



            //var by : in.uncod.android.bypass =


            /*val extras = Bundle()
            //extras.putInt(AllAppWidget.EXTRA_ITEM, position)

            extras.putString("username", if(article?.rootAuthor != null) article?.rootAuthor else article?.author)
            extras.putString("tag", article?.category)
            extras.putString("permlink", if(article?.rootPermlink != null) article?.rootPermlink else article?.permlink)*/
            //if(adaptype == AdapterToUseFor.commentNoti || adaptype == AdapterToUseFor.replyNoti) myIntent.putExtra("permlinkToFind",holder.article?.permlink)


            //open the article
            var openintent = getPendingSelfIntent(mContext)
            //openintent.putExtras(extras)
            openintent.putExtra("username", if(article?.rootAuthor != null) article?.rootAuthor else article?.author)
            openintent.putExtra("tag", article?.category)
            openintent.putExtra("permlink", if(article?.rootPermlink != null) article?.rootPermlink else article?.permlink)
            rv.setOnClickPendingIntent(R.id.openarticle, getPendingSelfIntent(mContext,this.article_clicked,openintent,appWidgetId))

            /*val fillInIntent = Intent()
            fillInIntent.putExtras(extras)
            rv.setOnClickFillInIntent(R.id.openarticle, fillInIntent)*/


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



            /*if(article?.author == username){
                //mholder.article_reblog_now?.visibility = View.GONE
            }
            else{
                *//* mholder.article_reblog_now?.visibility = View.VISIBLE
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

                 })*//*
            }*/

            rv.setTextViewText(R.id.article_summary,article.body)
            return rv
        }



    }
}

