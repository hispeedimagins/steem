package com.steemapp.lokisveil.steemapp.BindHelpers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.commonsware.cwac.anddown.AndDown
import com.steemapp.lokisveil.steemapp.AllRecyclerViewAdapter
import com.steemapp.lokisveil.steemapp.CentralConstants
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.HelperClasses.ArticlePopUpMenu
import com.steemapp.lokisveil.steemapp.Interfaces.arvdinterface
import com.steemapp.lokisveil.steemapp.MyViewHolders.followViewHolder
import com.steemapp.lokisveil.steemapp.OpenOtherGuyBlog
import com.steemapp.lokisveil.steemapp.R
import com.steemapp.lokisveil.steemapp.jsonclasses.prof

/**
 * Created by boot on 3/31/2018.
 */
class FollowDisplayHelperFunctions(context : Context, username:String?, adapter: AllRecyclerViewAdapter, adpterType: AdapterToUseFor) {

    val con:Context = context
    var name:String? = username
    val adaptedcomms: arvdinterface = adapter
    val and = AndDown()
    private var selectedPos = -1
    val sharedpref : SharedPreferences = context.getSharedPreferences(CentralConstants.sharedprefname,0)
    var key = sharedpref.getString(CentralConstants.key,null)
    val globallist = ArrayList<Any>()
    val adaptype = adpterType


    init {
        if(name == null){
            name = sharedpref.getString(CentralConstants.username,null)
        }
    }



    public fun Bind(mholder: RecyclerView.ViewHolder, position:Int){
        mholder.itemView.isSelected = selectedPos == position
        val holder  = mholder as followViewHolder
        holder.article = adaptedcomms.getObject(position) as prof.Resultfp


        /*var pay = holder.article?.pending_payout_value
        if((holder.article?.already_paid.orEmpty() != "0.000 SBD")){
            pay = holder.article?.already_paid
        }


        holder.article_comments?.text = holder.article?.children.toString()

        if(holder.article?.uservoted != null && holder.article?.uservoted as Boolean == true){
            holder.article_likes?.setTextColor(ContextCompat.getColor(con, R.color.colorAccent))
        }
        else {
            holder.article_likes?.setTextColor(ContextCompat.getColor(con, R.color.abc_hint_foreground_material_light))
        }
        holder.article_likes?.text = holder.article?.netVotes.toString()*/
        var fol : String? = ""
        when(adaptype){
            AdapterToUseFor.following ->{
                fol = holder.article?.following
            }
            AdapterToUseFor.followers ->{
                fol = holder.article?.follower
            }
            else ->{

            }
        }
        holder.article_name?.text = fol
        holder.article_name?.setOnClickListener(View.OnClickListener {
            val i = Intent(con, OpenOtherGuyBlog::class.java)
            i.putExtra(CentralConstants.OtherGuyNamePasser,fol)
            con.startActivity(i)
        })
        //holder.article_payout?.text = pay
        //val d = Calendar.getInstance()
        //d.time = holder.article?.createdcon
        val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_person_white_24px)
                //.error(R.drawable.error)
                .priority(Priority.HIGH)
                .circleCrop()
        //holder.article_date?.text = holder.article?.datespan
        Glide.with(con).load(CentralConstants.GetFeedImageUrl(fol)).apply(options)
                //.placeholder(R.drawable.common_full_open_on_phone)
                .into(holder.article_pfp as ImageView)


        /*if(holder.article?.reblogBy == null || holder.article?.reblogBy?.isEmpty() as Boolean){
            holder.article_resteemed_by?.visibility = View.GONE
            *//*if(adaptype == AdapterToUseFor.blog){
                if(this.name != holder.article?.author){
                    holder.article_resteemed_by?.visibility = View.VISIBLE
                    holder.article_resteemed_by?.text = "resteemed"
                }
                else{
                    holder.article_resteemed_by?.visibility = View.GONE
                }
            }
            else{
                holder.article_resteemed_by?.visibility = View.GONE
            }*//*



        }
        else{
            holder.article_resteemed_by?.visibility = View.VISIBLE
            holder.article_resteemed_by?.text = "by "+ holder.article?.reblogBy?.get(0)
        }
        holder.article_tag?.text = "in "+ holder.article?.category
        holder.article_title?.text = holder.article?.title


        val optionss = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_all_inclusive_black_24px)
                //.error(R.drawable.error)
                .priority(Priority.HIGH)

        Glide.with(con).load(holder.article?.image?.get(0)).apply(optionss)
                //.placeholder(R.drawable.common_full_open_on_phone)
                .into(holder.article_image as ImageView)

        //var by : in.uncod.android.bypass =


        //mholder.openarticle
        mholder.openarticle?.setOnClickListener(View.OnClickListener {
            //ForReturningQuestionsLite q = item;

            val myIntent = Intent(con, ArticleActivity::class.java)
            myIntent.putExtra("username", holder.article?.author)
            myIntent.putExtra("tag", holder.article?.category)
            myIntent.putExtra("permlink", holder.article?.permlink)
            con.startActivity(myIntent)
            //creating a popup menu

        })*/

        val articlepop = ArticlePopUpMenu(con,mholder.shareTextView,"${CentralConstants.baseUrlView}@$fol",null,holder?.article?.followInternal,name,fol,adaptedcomms,position,holder?.progressbar,null,null,false)

        //val articlepop = ArticlePopUpMenu(con,mholder.shareTextView,"${CentralConstants.baseUrlView}@${holder?.article?.author}","${CentralConstants.baseUrl}${holder?.article?.category}/@${holder?.article?.author}/${holder?.article?.permlink}",holder?.article?.useFollow,name,holder?.article?.author,adaptedcomms,position,holder?.progressbar,null,holder?.article?.activeVotes)

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

        /*mholder.article_like?.setOnClickListener(View.OnClickListener {
            // Toast.makeText(con,"Processing. Please wait....",Toast.LENGTH_LONG).show()
            //ForReturningQuestionsLite q = item;
            var articles = mholder.article as FeedArticleDataHolder.FeedArticleHolder
            //var s : SteemJ = SteemJ()
            //s.vote(AccountName(holder.article?.author), Permlink(holder.article?.permlink),10000)
            var vop : VoteOperation = VoteOperation(AccountName(name), AccountName(articles.author), Permlink(articles.permlink))
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

        })

        mholder.article_reblog_now?.setOnClickListener(View.OnClickListener {
            //Toast.makeText(con,"Processing. Please wait....",Toast.LENGTH_LONG).show()
            //ForReturningQuestionsLite q = item;
            var articles = mholder.article as FeedArticleDataHolder.FeedArticleHolder
            var vop = ReblogOperation(AccountName(name), AccountName(articles.author), Permlink(articles.permlink))
            var al = ArrayList<AccountName>()
            al.add(AccountName(name))
            var cus = CustomJsonOperation(null,al,"follow",vop.toJson())


            *//*var l : List<FollowType> = ArrayList()
            l += FollowType.BLOG
            var fop = FollowOperation(AccountName(name), AccountName(articles.author), l)
            var fus = CustomJsonOperation(null,al,"follow",fop.toJson())

            var obs = Response.Listener<JSONObject> { response ->
                //var articles = mholder.article as FeedArticleDataHolder.FeedArticleHolder
                val gson = Gson()
                var ress = gson.fromJson<Block.BlockAdded>(response.toString(),Block.BlockAdded::class.java)
                if(ress != null && ress.result != null ){
                    *//**//*Runnable { run { Toast.makeText(con,"$name has upvoted ${vop.author.name}",Toast.LENGTH_LONG).show() } }*//**//*
                    Toast.makeText(con,"Reblogged ${vop.permlink.link}",Toast.LENGTH_LONG).show()

                }
            }*//*
            var list  = ArrayList<Operation>()
            list.add(cus)
            //var bloc = GetDynamicAndBlock(con ,adaptedcomms,position,mholder,null,cus,obs,"Reblogged ${vop.permlink.link}",MyOperationTypes.reblog)
            var bloc = GetDynamicAndBlock(con ,adaptedcomms,position,list,"Reblogged ${vop.permlink.link}", MyOperationTypes.reblog)
            bloc.GetDynamicGlobalProperties()
            *//*globallist.add(bloc)
            Log.d("buttonclick",globallist.toString())*//*
            *//*val myIntent = Intent(con, ArticleActivity::class.java)
            myIntent.putExtra("username", holder.article?.author)
            myIntent.putExtra("tag", holder.article?.category)
            myIntent.putExtra("permlink", holder.article?.permlink)
            con.startActivity(myIntent)*//*
        })*/
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
        //holder.article_summary?.text = holder.article?.body

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

}