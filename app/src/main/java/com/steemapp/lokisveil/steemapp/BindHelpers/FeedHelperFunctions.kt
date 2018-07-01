package com.steemapp.lokisveil.steemapp.BindHelpers

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.media.Image
import android.opengl.Visibility
import android.os.AsyncTask
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.commonsware.cwac.anddown.AndDown
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.steemapp.lokisveil.steemapp.*
import com.steemapp.lokisveil.steemapp.Crypto.ECKey
import com.steemapp.lokisveil.steemapp.Crypto.MyCrypto
//import com.commonsware.cwac.anddown.AndDown
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.Enums.FollowInternal
import com.steemapp.lokisveil.steemapp.HelperClasses.*
import com.steemapp.lokisveil.steemapp.Interfaces.arvdinterface
import com.steemapp.lokisveil.steemapp.MyViewHolders.ArticleViewHolder
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.FollowType
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.PrivateKeyType
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.AccountName
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.BlockId
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.Permlink
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.SignedTransaction
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.*
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.SteemJConfig
import com.steemapp.lokisveil.steemapp.jsonclasses.Block
import org.apache.commons.lang3.tuple.ImmutablePair
/*import eu.bittrade.libs.steemj.SteemJ
import eu.bittrade.libs.steemj.base.models.AccountName
import eu.bittrade.libs.steemj.base.models.Permlink*/
import org.joou.UInteger
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutionException
import kotlin.collections.ArrayList


/**
 * Created by boot on 2/4/2018.
 */
class FeedHelperFunctions(context : Context,username:String?,adapter:AllRecyclerViewAdapter ,adpterType:AdapterToUseFor,dateHolder: FloatingDateHolder? = null) {
    val con:Context = context
    var name:String? = username
    var textColorMineTheme: Int = 0
    val adaptedcomms:arvdinterface = adapter
    val and = AndDown()
    var selectedPos = -1
    val sharedpref : SharedPreferences = context.getSharedPreferences(CentralConstants.sharedprefname,0)
    var calcs: calendarcalculations = calendarcalculations()
    var key = sharedpref.getString(CentralConstants.key,null)
    //val globallist = ArrayList<Any>()
    val adaptype = adpterType
    var floatingDateHolder:FloatingDateHolder? = null

    init {
        floatingDateHolder = dateHolder
        if(name == null){
            name = sharedpref.getString(CentralConstants.username,null)

        }
        /*var typedValue =  TypedValue()
        var theme = context.theme
        theme.resolveAttribute(R.attr.textColorMine, typedValue, true)
        textColorMineTheme = typedValue.data*/

        var attrs  = intArrayOf(R.attr.textColorMine)
        var ta = context.obtainStyledAttributes(attrs)
        var textColorMineThemeint = ta.getResourceId(0, android.R.color.black)
        ta.recycle()
        textColorMineTheme = ContextCompat.getColor(con, textColorMineThemeint)
        /*imgthumblike = context.getResources().getDrawable(R.drawable.ic_thumb_up_likeint_24px)
        imgthumbtheme = context.getResources().getDrawable(R.drawable.ic_thumb_up_black_24px)*/
    }

    public fun add(article : FeedArticleDataHolder.FeedArticleHolder){
        if(article.date != null) floatingDateHolder?.checktimeandaddQuestions(article.date!!)
        adaptedcomms.add(article)
        //adapter.notifyDataSetChanged();
        adaptedcomms.notifyitemcinserted(adaptedcomms.getSize())
    }

    public fun add(articles:List<FeedArticleDataHolder.FeedArticleHolder>){
        for (x in articles){
            add(x)
        }
    }

    /*fun viewclicked(vhf : RecyclerView.ViewHolder){
        adaptedcomms.notifyitemcchanged(selectedPos)
        selectedPos = vhf.layoutPosition
        adaptedcomms.notifyitemcchanged(selectedPos)
        var intent : Intent = Intent()

        VhUniv = vhf as OpenAQuestionDataHolder
        adaptedcomms.SetData(VhUniv.mItem)
        SetVisibilityOfButtons(VhUniv)
    }*/

    public fun Bind(mholder:RecyclerView.ViewHolder,position:Int){
        mholder.itemView.setSelected(selectedPos == position)

        val holder : ArticleViewHolder = mholder as ArticleViewHolder
        holder.article = adaptedcomms.getObject(position) as FeedArticleDataHolder.FeedArticleHolder


        var pay = holder.article?.pending_payout_value
        if((holder.article?.already_paid.orEmpty() != "0.000 SBD")){
            pay = holder.article?.already_paid
        }


        holder.article_comments?.text = holder.article?.children.toString()

        if(holder.article?.uservoted != null && holder.article?.uservoted as Boolean == true){
            holder.article_likes?.setTextColor(ContextCompat.getColor(con, R.color.colorAccent))
            holder.article_likes?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_likeint_24px,0,0,0)
        }
        else {

            //holder.article_likes?.setTextColor(ContextCompat.getColor(con, R.color.abc_hint_foreground_material_light))

            holder.article_likes?.setTextColor(textColorMineTheme)
            holder.article_likes?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_black_24px,0,0,0)
        }
        holder.article_likes?.text = holder.article?.netVotes.toString()
       // holder.article_name?.text = "${holder.article?.author} (${StaticMethodsMisc.CalculateRepScore(holder.article?.authorreputation)})"
        holder.article_name?.text = holder.article?.displayName
        holder.article_name?.setOnClickListener(View.OnClickListener {
            val i = Intent(con, OpenOtherGuyBlog::class.java)
            i.putExtra(CentralConstants.OtherGuyNamePasser,holder.article?.author)
            con.startActivity(i)
        })
        holder.article_payout?.text = pay
        //val d = Calendar.getInstance()
        //d.time = holder.article?.createdcon
        val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_person_white_24px)
                //.error(R.drawable.error)
                .priority(Priority.HIGH)
                .circleCrop()
        holder.article_date?.text = holder.article?.datespan
        Glide.with(con).load(CentralConstants.GetFeedImageUrl(holder.article?.author)).apply(options)
                //.placeholder(R.drawable.common_full_open_on_phone)
                .into(holder.article_pfp as ImageView)


        if(holder.article?.reblogBy == null || holder.article?.reblogBy?.isEmpty() as Boolean){
            holder.article_resteemed_by?.visibility = View.GONE
            //hide the resteem bar if there are no resteems
            holder.article_resteemed_by_Linear?.visibility = View.GONE
        }
        else{
            holder.article_resteemed_by?.visibility = View.VISIBLE
            holder.article_resteemed_by_Linear?.visibility = View.VISIBLE
            holder.article_resteemed_by?.text = "by "+ holder.article?.reblogBy?.get(0)
            holder.article_resteemed_by?.setOnClickListener(View.OnClickListener {
                val i = Intent(con, OpenOtherGuyBlog::class.java)
                i.putExtra(CentralConstants.OtherGuyNamePasser,holder.article?.reblogBy?.get(0))
                con?.startActivity(i)
            })
        }

        //Add the app name for users to view
        holder.article_tag?.text = "in ${holder.article?.category} using ${holder.article?.app}"
        holder.article_tag?.setOnClickListener(View.OnClickListener {
            var it = Intent(con,MainTags::class.java)
            it.putExtra(CentralConstants.MainRequest,"get_discussions_by_trending")
            it.putExtra(CentralConstants.MainTag,holder.article?.category)
            it.putExtra(CentralConstants.OriginalRequest,"trending")
            con.startActivity(it)

        })
        holder.article_title?.text = holder.article?.title


        if(adaptype == AdapterToUseFor.commentNoti || adaptype == AdapterToUseFor.replyNoti){
            //mholder.article_reblog_now?.visibility = View.GONE
            holder.article_image?.visibility = View.GONE
        }
        else{
            holder.article_image?.visibility = View.VISIBLE
            val optionss = RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_all_inclusive_black_24px)
                    //.error(R.drawable.error)
                    .priority(Priority.HIGH)

            Glide.with(con).load(holder.article?.image?.firstOrNull()).apply(optionss)
                    //.placeholder(R.drawable.common_full_open_on_phone)
                    .into(holder.article_image as ImageView)

        }


        //var by : in.uncod.android.bypass =


        //mholder.openarticle
        mholder.openarticle?.setOnClickListener(View.OnClickListener {
            //ForReturningQuestionsLite q = item;

            val myIntent = Intent(con, ArticleActivity::class.java)
            myIntent.putExtra("username", if(holder.article?.rootAuthor != null) holder.article?.rootAuthor else holder.article?.author)
            myIntent.putExtra("tag", holder.article?.category)
            myIntent.putExtra("permlink", if(holder.article?.rootPermlink != null) holder.article?.rootPermlink else holder.article?.permlink)
            if(adaptype == AdapterToUseFor.commentNoti || adaptype == AdapterToUseFor.replyNoti) myIntent.putExtra("permlinkToFind",holder.article?.permlink)
            con.startActivity(myIntent)
            //creating a popup menu

        })

        val articlepop = ArticlePopUpMenu(con,mholder.shareTextView,"${CentralConstants.baseUrlView}@${holder?.article?.author}","${CentralConstants.baseUrlView}${holder?.article?.category}/@${holder?.article?.author}/${holder?.article?.permlink}",null,name,holder.article?.author,adaptedcomms,position,holder?.progressbar,null,holder?.article?.activeVotes,true)

       /* mholder.shareTextView.setOnClickListener(View.OnClickListener{
            *//*val sendIntent: Intent =  Intent()
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "${CentralConstants.baseUrl}${holder?.article?.category}/@${holder?.article?.author}/${holder?.article?.permlink}")
            sendIntent.setType("text/plain");
            con.startActivity(Intent.createChooser(sendIntent,"Share Link"))*//*



            val popup = PopupMenu(con,mholder.shareTextView)
            //inflating menu from xml resource
            popup.inflate(R.menu.article_dialog_menu)
            //adding click listener
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    when (item.getItemId()) {
                        R.id.article_dialog_share_author_profile -> {
                        }
                        R.id.article_dialog_share_article -> {
                        }
                        *//*R.id.menu3 -> {
                        }*//*
                    }//handle menu1 click
                    //handle menu2 click
                    //handle menu3 click
                    return false
                }
            })
            //displaying the popup
            popup.show()


        })*/

        mholder.article_like?.setOnClickListener(View.OnClickListener {
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
                var ress = gson.fromJson<Block.BlockAdded>(response.toString(),Block.BlockAdded::class.java)
                if(ress != null && ress.result != null ){
                    //con.run { Toast.makeText(con,"Upvoted ${vop.permlink}",Toast.LENGTH_LONG).show() }
                    Toast.makeText(con,"Upvoted ${vop.permlink}",Toast.LENGTH_LONG).show()
                    holder.article?.uservoted.to(true)
                    holder.article_likes?.setTextColor(ContextCompat.getColor(con, R.color.colorAccent))
                    adaptedcomms.notifyitemcchanged(mholder.adapterPosition)
                    //Runnable { run {  } }
                    //Toast.makeText(con,"$name has upvoted ${vop.author.name}",Toast.LENGTH_LONG).show()
                }
            }
            //var bloc = GetDynamicAndBlock(con ,adaptedcomms,position,mholder,null,vop,obs,"Upvoted ${vop.permlink.link}",MyOperationTypes.vote)
            /*var list  = ArrayList<Operation>()
            list.add(vop)
            var bloc = GetDynamicAndBlock(con ,adaptedcomms,position,list,"Upvoted ${vop.permlink.link}",MyOperationTypes.vote)
            bloc.GetDynamicGlobalProperties()*/
            val weight = VoteWeightThenVote(con,adaptedcomms.getActivity(),vop,adaptedcomms,position,holder.progressbar,null)
            weight.makeDialog()
            /*globallist.add(bloc)

            Log.d("buttonclick",globallist.toString())*/
            //GetDynamicGlobalProperties(mholder.article as FeedArticleDataHolder.FeedArticleHolder)

        })


        if(adaptype == AdapterToUseFor.commentNoti || adaptype == AdapterToUseFor.replyNoti){
            mholder.article_reblog_now?.visibility = View.GONE
            holder.article_image?.visibility = View.GONE
        }
        else{
            mholder.article_reblog_now?.visibility = View.VISIBLE
            holder.article_image?.visibility = View.VISIBLE
        }
        if(holder.article?.author == name){
            mholder.article_reblog_now?.visibility = View.GONE
        }
        else{
            mholder.article_reblog_now?.visibility = View.VISIBLE
            mholder.article_reblog_now?.setOnClickListener(View.OnClickListener {
                //Toast.makeText(con,"Processing. Please wait....",Toast.LENGTH_LONG).show()
                //ForReturningQuestionsLite q = item;
                var articles = mholder.article as FeedArticleDataHolder.FeedArticleHolder
                var vop = ReblogOperation(AccountName(name),AccountName(articles.author),Permlink(articles.permlink))
                var al = ArrayList<AccountName>()
                al.add(AccountName(name))
                var cus = CustomJsonOperation(null,al,"follow",vop.toJson())


                /*var l : List<FollowType> = ArrayList()
                l += FollowType.BLOG
                var fop = FollowOperation(AccountName(name), AccountName(articles.author), l)
                var fus = CustomJsonOperation(null,al,"follow",fop.toJson())

                var obs = Response.Listener<JSONObject> { response ->
                    //var articles = mholder.article as FeedArticleDataHolder.FeedArticleHolder
                    val gson = Gson()
                    var ress = gson.fromJson<Block.BlockAdded>(response.toString(),Block.BlockAdded::class.java)
                    if(ress != null && ress.result != null ){
                        *//*Runnable { run { Toast.makeText(con,"$name has upvoted ${vop.author.name}",Toast.LENGTH_LONG).show() } }*//*
                    Toast.makeText(con,"Reblogged ${vop.permlink.link}",Toast.LENGTH_LONG).show()

                }
            }*/
                var list  = ArrayList<Operation>()
                list.add(cus)
                //var bloc = GetDynamicAndBlock(con ,adaptedcomms,position,mholder,null,cus,obs,"Reblogged ${vop.permlink.link}",MyOperationTypes.reblog)
                var bloc = GetDynamicAndBlock(con ,adaptedcomms,position,list,"Reblogged ${vop.permlink.link}",MyOperationTypes.reblog)
                bloc.GetDynamicGlobalProperties()
                /*globallist.add(bloc)
                Log.d("buttonclick",globallist.toString())*/
                /*val myIntent = Intent(con, ArticleActivity::class.java)
                myIntent.putExtra("username", holder.article?.author)
                myIntent.putExtra("tag", holder.article?.category)
                myIntent.putExtra("permlink", holder.article?.permlink)
                con.startActivity(myIntent)*/
            })
        }

        //and = AndDown()
        /*var st : String? = holder.article?.body
        if(st?.length != null && st?.length > 300){
            st = st.substring(0,300)
        }
        var splitstring : List<String> = st?.split("\n") as List<String>
        var builder = StringBuilder()
        if(splitstring != null){

            for (x in splitstring){
                if(!x.contains("**") && !x.contains("[")&& !x.contains("<")){
                    builder.append(x)
                }
            }
        }*/
        //var sp : String = st.split(" ",false,300)
        holder.article_summary?.text = holder.article?.body

        /*val s : String = and.markdownToHtml(st, AndDown.HOEDOWN_EXT_QUOTE, 0)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            // tvDocument.setText(Html.fromHtml(bodyData,Html.FROM_HTML_MODE_LEGACY))
            holder.article_summary?.text = Html.fromHtml(s,Html.FROM_HTML_MODE_LEGACY)
            //webview?.loadDataWithBaseURL("",s,null,"UTF-8",null)
        } else {
            //tvDocument.setText(Html.fromHtml(bodyData))
            holder.article_summary?.text = Html.fromHtml(s)
            //webview?.loadDataWithBaseURL("",s,null,"UTF-8",null)
        }*/
        //holder.article_summary?.text = and.markdownToHtml(holder.article?.body, AndDown.HOEDOWN_EXT_QUOTE, 0)
        //holder.article_summary?.text = holder.article?.body
        //var h =  markdownhelper(con)
       /* h.setMarkDownText(holder.article?.body?.substring(0,400))*/
        //holder.article_summary_markdown.showMarkdown(holder.article?.body?.substring(0,200))
        //holder.article_summary_markdown.showMarkdown("# Hello Markdown\n\n[fiskurgit](https://github.com/fiskurgit)")
    }


    /*fun makeDialog(){
        val alertDialogBuilder = AlertDialog.Builder(con)
        alertDialogBuilder.setTitle("Vote")
        val inflater = adaptedcomms.getActivity().layoutInflater
        val dialogView : View = inflater.inflate(R.layout.votedialog, null)
        alertDialogBuilder.setView(dialogView)
        val numberPicker : NumberPicker = dialogView.findViewById(R.id.dialog_number_picker)
        numberPicker.maxValue = 100
        numberPicker.minValue = 0
        numberPicker.wrapSelectorWheel = false

        numberPicker.setOnValueChangedListener(NumberPicker.OnValueChangeListener{picked,i,i1 ->

        })

        alertDialogBuilder.setPositiveButton("ok",DialogInterface.OnClickListener{ diin,num ->
            numberPicker.value
        })

        alertDialogBuilder.setNegativeButton("No", DialogInterface.OnClickListener { diin,num ->

        })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    fun GetDynamicGlobalProperties(acticle : FeedArticleDataHolder.FeedArticleHolder){
        //val queue = Volley.newRequestQueue(context)

        //swipecommonactionsclass?.makeswiperun()

        val volleyre : VolleyRequest = VolleyRequest.getInstance(con)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()

        val s = JsonObjectRequest(Request.Method.POST,url,d.globalProperties,
                Response.Listener { response ->

                    val gson = Gson()
                    var parse : Block.DynamicGlobalProperties = gson.fromJson(response.toString(), Block.DynamicGlobalProperties::class.java)
                    if(parse != null && parse.result != null){
                        GetBlock(parse.result.lastIrreversibleBlockNum,acticle)
                    }

                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }
    
    fun VoteListener(){
        Response.Listener<JSONObject> { response ->
            
        }
    }

    fun GetBlock(blocknumber : Int,acticle : FeedArticleDataHolder.FeedArticleHolder){
        //val queue = Volley.newRequestQueue(context)

        //swipecommonactionsclass?.makeswiperun()

        val volleyre : VolleyRequest = VolleyRequest.getInstance(con)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()

        val s = JsonObjectRequest(Request.Method.POST,url,d.getBlock(blocknumber),
                Response.Listener { response ->

                    val gson = Gson()
                    var parse : Block.GetBlockResult = gson.fromJson(response.toString(), Block.GetBlockResult::class.java)
                    if(parse != null){


                        *//*var block : BlockId = BlockId(parse.result.blockId)
                        var vop : VoteOperation = VoteOperation(AccountName(name), AccountName(acticle.author), Permlink(acticle.permlink))
                        val operations = ArrayList<Operation>()
                        operations.add(vop)
                        var signedtra = SignedTransaction(block,operations,null)
                        var fors = forewarder(vop, signedtra)
                        var sigas = signalrstarterasync().execute(fors)*//*

                        class someTask() : AsyncTask<Void, Void, String>() {
                            override fun doInBackground(vararg params: Void?): String? {
                                // ...
                                var block : BlockId = BlockId(parse.result.blockId)
                                var vop : VoteOperation = VoteOperation(AccountName(name), AccountName(acticle.author), Permlink(acticle.permlink))
                                vop.weight = 10000
                                val operations = ArrayList<Operation>()
                                operations.add(vop)
                                var signedtra = SignedTransaction(block,operations,null)
                                var fors = forewarder(vop, signedtra)
                                //var sigas = signalrstarterasync().execute(fors)
                                if(key == null){
                                    key = sharedpref.getString(CentralConstants.key,null)
                                }

                                signedtra.signMy(SteemJConfig.getInstance().getChainId(),ImmutablePair(PrivateKeyType.POSTING,key))
                                BroadcastSynchronous(vop,signedtra)
                                return ""
                            }
                        }
                        someTask().execute()
                        //signedtra.sign()
                        //var s = signedtra.signatures
                        //BroadcastSynchronous(vop,signedtra)

                        *//*var hashval = block.hashValue
                        var hashnum = block.numberFromHash
                        var myc = MyCrypto()
                        var secrefnum = myc.GetRefBlockNumber(blocknumber)
                        var refbnum = myc.GetRefBlockNumber(hashnum)*//*
                        var blockval : Int = 334
                    }

                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }


    fun BroadcastSynchronous(vop : VoteOperation,signedtra : SignedTransaction){
        //val queue = Volley.newRequestQueue(context)

        //swipecommonactionsclass?.makeswiperun()

        val volleyre : VolleyRequest = VolleyRequest.getInstance(con)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()
        var re1j : JSONObject? = null
        try{
            re1j  = d.networkBroadcaseSynchronous(signedtra.expirationDate.dateTime,"vote",vop.author.name.toString(),vop.permlink.link.toString(),vop.voter.name.toString(),"10000",signedtra.refBlockNum.toString(),signedtra.refBlockPrefix.toString(),signedtra.signatures[0])

        }
        catch (exception : java.lang.Exception){
            var ex = exception
            var exm = ex.message
        }

        val s = JsonObjectRequest(Request.Method.POST,url,re1j,

                Response.Listener { response ->

                    val gson = Gson()
                    var ress = gson.fromJson<Block.BlockAdded>(response.toString(),Block.BlockAdded::class.java)
                    if(ress != null && ress.result != null ){
                        Toast.makeText(con,"$name has upvoted ${vop.author.name}",Toast.LENGTH_LONG).show()
                    }

                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }


    private inner class signalrstarterasync : AsyncTask<forewarder, forewarder, forewarder>() {


        var mostlyconnected = false
        var ie: InterruptedException? = null
        var ex: ExecutionException? = null


        override fun onPreExecute() {
            super.onPreExecute()
            if(key == null){
                key = sharedpref.getString(CentralConstants.key,null)
            }
            Log.i("startingsignmaasy", "mainasync pre")
            //useIfOne++;
        }

        override fun doInBackground(vararg params: forewarder): forewarder? {


            try {

               params[0].signedtra.sign(ImmutablePair(PrivateKeyType.POSTING,key))
            } catch (e: InterruptedException) {


                mostlyconnected = false
                ie = e

            } catch (e: ExecutionException) {
                mostlyconnected = false
                ex = e
            } catch (ex: Exception) {


                mostlyconnected = false

            }


            //do stuff
            return params[0]
        }

        override fun onPostExecute(result: forewarder) {
            super.onPostExecute(result)
            Log.i("startingsignmaasy", "mainasync post mostly connected is " + mostlyconnected.toString())
            if(!result.signedtra.signatures.isEmpty()){
                BroadcastSynchronous(result.vop,result.signedtra)
            }


            if (mostlyconnected) {

                //Toast.makeText(appContext,"SignalRStarted",Toast.LENGTH_LONG).show();
            } else {

                if (ex != null) {
                    Log.i("startingsignmaasy", "mainasync post error may be " + ex!!.message)
                } else {
                    Log.i("startingsignmaasy", "no error i guess")
                }



                //Toast.makeText(appContext,"Please check your connection and try again.",Toast.LENGTH_SHORT).show();
                *//*myHubCon.disconnect();
                myHubCon.stop();
                myHubCon = null;*//*


            }

        }
    }



    private class forewarder(vop : VoteOperation,signedtra : SignedTransaction){
        var signedtra : SignedTransaction = signedtra
        var vop = vop

    }*/

}