package com.insteem.ipfreely.steem.BindHelpers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.commonsware.cwac.anddown.AndDown
import com.google.gson.Gson
import com.insteem.ipfreely.steem.AllRecyclerViewAdapter
import com.insteem.ipfreely.steem.CentralConstants
import com.insteem.ipfreely.steem.DataHolders.FeedArticleDataHolder
import com.insteem.ipfreely.steem.Enums.AdapterToUseFor
import com.insteem.ipfreely.steem.HelperClasses.GetDynamicAndBlock
import com.insteem.ipfreely.steem.HelperClasses.StaticMethodsMisc
import com.insteem.ipfreely.steem.HelperClasses.VoteWeightThenVote
import com.insteem.ipfreely.steem.MyViewHolders.ArticleViewHolder
import com.insteem.ipfreely.steem.OpenOtherGuyBlog
import com.insteem.ipfreely.steem.R
import com.insteem.ipfreely.steem.SteemBackend.Config.Enums.MyOperationTypes
import com.insteem.ipfreely.steem.SteemBackend.Config.Models.AccountName
import com.insteem.ipfreely.steem.SteemBackend.Config.Models.Permlink
import com.insteem.ipfreely.steem.SteemBackend.Config.Operations.CustomJsonOperation
import com.insteem.ipfreely.steem.SteemBackend.Config.Operations.Operation
import com.insteem.ipfreely.steem.SteemBackend.Config.Operations.ReblogOperation
import com.insteem.ipfreely.steem.SteemBackend.Config.Operations.VoteOperation
import com.insteem.ipfreely.steem.jsonclasses.Block
import org.json.JSONObject

/**
 * Binder functions for articles
 * @param con application context
 * @param name username of the current user
 * @param adaptedcomms the callback to the adapter
 * @param adaptype the adapter enum in use
 * @param scale the scale of the ui
 * @param metrics the display metrics in use
 */
class ArticleHelperFunctions(val con : Context,
                             var name:String?,
                             val adaptedcomms: AllRecyclerViewAdapter,
                             val adaptype: AdapterToUseFor ,
                             val scale: Float,
                             val metrics: DisplayMetrics) {

    val and = AndDown()
    private var selectedPos = -1
    val sharedpref : SharedPreferences = con.getSharedPreferences(CentralConstants.sharedprefname,0)

    var key = sharedpref.getString(CentralConstants.key,null)
    val globallist = ArrayList<Any>()

    init {
        if(name == null){
            name = sharedpref.getString(CentralConstants.username,null)
        }
    }


    /**
     * convert digital pixels to pixels
     * @param db the value of dp in float
     * @return pixels in Int
     */
    fun GetPx(dp : Float) : Int{

        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp + 5,
                metrics
        ).toInt()
    }

    /**
     * Maked the layout parameters
     * @param mar the margin to use
     */
    fun GetLayourParamsMargin(mar : Int) : LinearLayout.LayoutParams{
        var param : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        param.leftMargin = mar
        //param.setMargins(mar, 0, 5, 0)

        return param
    }

    fun add(article : FeedArticleDataHolder.FeedArticleHolder){
        adaptedcomms.add(article)
        //adapter.notifyDataSetChanged();
        adaptedcomms.notifyitemcinserted(adaptedcomms.getSize())
    }

    fun add(articles:List<FeedArticleDataHolder.FeedArticleHolder>){
        for (x in articles){
            add(x)
        }
    }

    /*fun viewclicked(vhf : RecyclerView.ViewHolder){
        adaptedcomms.notifyitemcchanged(selectedPos)
        selectedPos = vhf.layoutPosition
        adaptedcomms.notifyitemcchanged(selectedPos)
        var intent : Intent = Intent()

        *//*VhUniv = vhf as OpenAQuestionDataHolder
        adaptedcomms.SetData(VhUniv.mItem)
        SetVisibilityOfButtons(VhUniv)*//*
    }*/


    /**
     * Bind the ui to the data
     * @param mholder the article holder
     * @param position the position of the item in the list
     */
    fun Bind(mholder: RecyclerView.ViewHolder, position:Int){
        mholder.itemView.setSelected(selectedPos == position)
        val holder : ArticleViewHolder = mholder as ArticleViewHolder
        holder.article = adaptedcomms.getObject(position) as FeedArticleDataHolder.FeedArticleHolder


        //val holder : ArticleViewHolder = mholder as ArticleViewHolder
        //holder.article = adaptedcomms.getObject(position) as FeedArticleDataHolder.FeedArticleHolder
        var pay = holder.article?.pending_payout_value
        if((holder.article?.already_paid.orEmpty() != "0.000 SBD")){
            pay = holder.article?.already_paid
        }


        holder.article_comments?.text = holder.article?.children.toString()

        if(holder.article?.uservoted != null && holder.article?.uservoted as Boolean == true){
            holder.article_likes?.setTextColor(ContextCompat.getColor(con, R.color.colorAccent))
        }
        holder.article_likes?.text = holder.article?.netVotes.toString()
        //holder.article_name?.text = holder.article?.author
        holder.article_name?.text = holder.article?.displayName //"${holder.article?.author} (${StaticMethodsMisc.CalculateRepScore(holder.article?.authorreputation)})"
        holder.article_payout?.text = pay
        //val d = Calendar.getInstance()
        //d.time = holder.article?.createdcon
        holder.article_date?.text = holder.article?.datespan
        val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.common_full_open_on_phone)
                //.error(R.drawable.error)
                .priority(Priority.HIGH)
                .circleCrop()
        Glide.with(con).load(CentralConstants.GetFeedImageUrl(holder.article?.author)).apply(options)
                // .placeholder(R.drawable.common_full_open_on_phone)
                .into(holder.article_pfp as ImageView)


        if(holder.article?.reblogBy == null || holder.article?.reblogBy?.isEmpty() as Boolean){
            holder.article_resteemed_by?.visibility = View.GONE
        }
        else{
            holder.article_resteemed_by?.visibility = View.VISIBLE
            holder.article_resteemed_by?.text = "by "+ holder.article?.reblogBy?.get(0)
        }
        holder.article_tag?.text = "in "+ holder.article?.category
        holder.article_title?.text = holder.article?.title
        val and = AndDown()
        //val s : String = and.markdownToHtml(holder.article?.body, AndDown.HOEDOWN_EXT_QUOTE, 0)
        var bod = StaticMethodsMisc.CorrectMarkDown(holder.article?.body,holder.article?.image)
        //bod = StaticMethodsMisc.CorrectMarkDownUsers(bod,holder.article?.users)
        /*if(holder.article?.image != null){
            for(img in holder.article?.image as List<String>){
                val check = "($img)"
                if(bod != null && !(bod?.contains(check))){
                    val reps = "![name]$check"
                    bod = bod?.replace(img,reps)
                }
            }
        }*/

        //var s : String = and.markdownToHtml(holder.article?.body, AndDown.HOEDOWN_EXT_AUTOLINK, 0)
        var s : String = ""
        if(holder.article?.format == "html"){
            s = bod
        }
        else{
            s  = and.markdownToHtml(bod, AndDown.HOEDOWN_EXT_AUTOLINK, AndDown.HOEDOWN_HTML_SKIP_HTML)
        }
        s = StaticMethodsMisc.CorrectAfterMainImages(s)
        /*s += "<style>*{max-width:100%}</style>"
        s += "<script type=\"text/javascript\">\n" +
                "    function UserClicked(user) {\n" +
                "        Android.UserClicked(user);\n" +
                "    }\n" +
                "</script>"*/
        /*if(holder.article?.image != null){
            for(img in holder.article?.image as List<String>){
                val check = "src=\"$img\""
                if(!s.contains(check)){
                    val reps = "<img $check />"
                    s.replace(img,reps)
                }
            }
        }*/

        holder.article_name?.setOnClickListener(View.OnClickListener {
            val i = Intent(con, OpenOtherGuyBlog::class.java)
            i.putExtra(CentralConstants.OtherGuyNamePasser,holder.article?.author)
            con?.startActivity(i)
        })

        holder.article_like?.setOnClickListener(View.OnClickListener {
            // Toast.makeText(con,"Processing. Please wait....",Toast.LENGTH_LONG).show()
            //ForReturningQuestionsLite q = item;
            var articles = holder.article as FeedArticleDataHolder.FeedArticleHolder
            //var s : SteemJ = SteemJ()
            //s.vote(AccountName(holder.article?.author), Permlink(holder.article?.permlink),10000)
            var vop : VoteOperation = VoteOperation(AccountName(name), AccountName(articles.author), Permlink(articles.permlink))
            vop.weight = 100

            val weight = VoteWeightThenVote(con,adaptedcomms.getActivity(),vop,null, 0,holder.progressbar,null)
            weight.makeDialog()
            /*globallist.add(bloc)

            Log.d("buttonclick",globallist.toString())*/
            //GetDynamicGlobalProperties(mholder.article as FeedArticleDataHolder.FeedArticleHolder)

        })

        holder.article_reblog_now?.setOnClickListener(View.OnClickListener {
            //Toast.makeText(con,"Processing. Please wait....",Toast.LENGTH_LONG).show()
            //ForReturningQuestionsLite q = item;
            var articles = holder.article as FeedArticleDataHolder.FeedArticleHolder
            var vop = ReblogOperation(AccountName(name), AccountName(articles.author), Permlink(articles.permlink))
            var al = java.util.ArrayList<AccountName>()
            al.add(AccountName(name))
            var cus = CustomJsonOperation(null,al,"follow",vop.toJson())
            var obs = Response.Listener<JSONObject> { response ->
                //var articles = mholder.article as FeedArticleDataHolder.FeedArticleHolder
                val gson = Gson()
                var ress = gson.fromJson<Block.BlockAdded>(response.toString(), Block.BlockAdded::class.java)
                if(ress != null && ress.result != null ){
                    /*Runnable { run { Toast.makeText(con,"$name has upvoted ${vop.author.name}",Toast.LENGTH_LONG).show() } }*/
                    Toast.makeText(con,"Reblogged ${vop.permlink.link}", Toast.LENGTH_LONG).show()

                }
            }
            var list  = java.util.ArrayList<Operation>()
            list.add(cus)
            //var bloc = GetDynamicAndBlock(con ,adaptedcomms,position,mholder,null,cus,obs,"Reblogged ${vop.permlink.link}",MyOperationTypes.reblog)
            var bloc = GetDynamicAndBlock(con ,null, 0,list,"Reblogged ${vop.permlink.link}",MyOperationTypes.reblog)
            bloc.GetDynamicGlobalProperties()
            /*globallist.add(bloc)
            Log.d("buttonclick",globallist.toString())*/
            /*val myIntent = Intent(con, ArticleActivity::class.java)
            myIntent.putExtra("username", holder.article?.author)
            myIntent.putExtra("tag", holder.article?.category)
            myIntent.putExtra("permlink", holder.article?.permlink)
            con.startActivity(myIntent)*/
        })

        setWebView(s,holder)

    }


    /**
     * set the webview with the data
     * @param s the body in string
     * @param holder the holder which has the article and the ui
     */
    fun setWebView(s:String,holder : ArticleViewHolder){
        holder?.openarticle?.removeAllViews()
        //univBody = s
        var objects = StaticMethodsMisc.ConvertTextToList(s,holder?.article?.image)
        if(objects != null){
            val lparams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

            for(x in objects){
                if(x is String && con != null){
                    var tx = TextView(con)
                    tx.setTextColor(ContextCompat.getColor(con,R.color.black))
                    lparams.setMargins(GetPx(5f),0,GetPx(5f),0)
                    tx.layoutParams = lparams
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        // tvDocument.setText(Html.fromHtml(bodyData,Html.FROM_HTML_MODE_LEGACY))
                        tx.text = Html.fromHtml(x, Html.FROM_HTML_MODE_LEGACY)

                        //webview?.loadDataWithBaseURL("",s,null,"UTF-8",null)
                    } else {
                        //tvDocument.setText(Html.fromHtml(bodyData))
                        tx.text = Html.fromHtml(s)

                        //webview?.loadDataWithBaseURL("",s,null,"UTF-8",null)
                    }
                    tx.movementMethod = LinkMovementMethod()
                    holder?.openarticle?.addView(tx)
                }
                else if(x is Int && con != null){

                    var iamge = ImageView(con)
                    iamge.layoutParams = lparams
                    iamge.adjustViewBounds = true
                    val options = RequestOptions()

                            .placeholder(R.drawable.ic_all_inclusive_black_24px)
                            //.error(R.drawable.error)
                            .priority(Priority.HIGH)
                    holder?.openarticle?.addView(iamge)
                    var url = holder?.article?.image!![x]
                    Glide.with(con).load(url).apply(options)
                            // .placeholder(R.drawable.common_full_open_on_phone)
                            .into(iamge)

                }
            }
        }

    }
}