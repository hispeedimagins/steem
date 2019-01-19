package com.steemapp.lokisveil.steemapp.BindHelpers

//import com.commonsware.cwac.anddown.AndDown
/*import eu.bittrade.libs.steemj.SteemJ
import eu.bittrade.libs.steemj.base.models.AccountName
import eu.bittrade.libs.steemj.base.models.Permlink*/
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.commonsware.cwac.anddown.AndDown
import com.steemapp.lokisveil.steemapp.*
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.HelperClasses.ArticlePopUpMenu
import com.steemapp.lokisveil.steemapp.HelperClasses.GetDynamicAndBlock
import com.steemapp.lokisveil.steemapp.HelperClasses.VoteWeightThenVote
import com.steemapp.lokisveil.steemapp.HelperClasses.calendarcalculations
import com.steemapp.lokisveil.steemapp.Interfaces.arvdinterface
import com.steemapp.lokisveil.steemapp.MyViewHolders.ArticleViewHolder
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.AccountName
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.Permlink
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.CustomJsonOperation
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.Operation
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.ReblogOperation
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.VoteOperation


/**
 * Created by boot on 2/4/2018.
 */
class FeedHelperFunctions(context : Context,username:String?,adapter:arvdinterface ,adpterType:AdapterToUseFor,dateHolder: FloatingDateHolder? = null) {
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

        val attrs  = intArrayOf(R.attr.textColorMine)
        val ta = context.obtainStyledAttributes(attrs)
        val textColorMineThemeint = ta.getResourceId(0, android.R.color.black)
        ta.recycle()
        textColorMineTheme = ContextCompat.getColor(con, textColorMineThemeint)
    }

    fun add(article : FeedArticleDataHolder.FeedArticleHolder){
        if(article.date != null) floatingDateHolder?.checktimeandaddQuestions(article.date!!)
        adaptedcomms.add(article)
        //adapter.notifyDataSetChanged();
        adaptedcomms.notifyitemcinserted(adaptedcomms.getSize())
    }

    fun add(articles:List<FeedArticleDataHolder.FeedArticleHolder>){
        for (x in articles){
            add(x)
        }
    }


    fun Bind(mholder:RecyclerView.ViewHolder,position:Int){
        mholder.itemView.setSelected(selectedPos == position)

        val holder : ArticleViewHolder = mholder as ArticleViewHolder
        val ho = adaptedcomms.getObject(position)
        if(ho == null){
            return
        }
        holder.article =ho  as FeedArticleDataHolder.FeedArticleHolder


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

            holder.article_likes?.setTextColor(textColorMineTheme)
            holder.article_likes?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_black_24px,0,0,0)
        }
        holder.article_likes?.text = holder.article?.netVotes.toString()
        holder.article_name?.text = holder.article?.displayName
        if(holder.article?.followsYou!!){
            holder.article_name?.setTextColor(ContextCompat.getColor(con, R.color.colorAccent))
        } else{
            holder.article_name?.setTextColor(textColorMineTheme)
        }
        holder.article_name?.setOnClickListener {
            val i = Intent(con, OpenOtherGuyBlog::class.java)
            i.putExtra(CentralConstants.OtherGuyNamePasser,holder.article?.author)
            con.startActivity(i)
        }
        holder.article_payout?.text = pay
        val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_person_white_24px)
                //.error(R.drawable.error)
                .priority(Priority.HIGH)
                .circleCrop()

        //get the current relative date time
        holder.article?.datespan = MiscConstants.dateToRelDate(holder.article?.date!!,con)
        holder.article_date?.text = holder.article?.datespan
        Glide.with(con).load(CentralConstants.GetFeedImageUrl(holder.article?.author))
                .apply(options)
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
            holder.article_resteemed_by?.setOnClickListener {
                val i = Intent(con, OpenOtherGuyBlog::class.java)
                i.putExtra(CentralConstants.OtherGuyNamePasser,holder.article?.reblogBy?.get(0))
                con?.startActivity(i)
            }
        }

        //Add the app name for users to view
        holder.article_tag?.text = "in ${holder.article?.category} using ${holder.article?.app}"
        holder.article_tag?.setOnClickListener {
            var it = Intent(con,MainTags::class.java)
            it.putExtra(CentralConstants.MainRequest,"get_discussions_by_trending")
            it.putExtra(CentralConstants.MainTag,holder.article?.category)
            it.putExtra(CentralConstants.OriginalRequest,"trending")
            con.startActivity(it)

        }
        holder.article_title?.text = holder.article?.title


        if(adaptype == AdapterToUseFor.commentNoti || adaptype == AdapterToUseFor.replyNoti){
            holder.article_image?.visibility = View.GONE
        }
        else{
            holder.article_image?.visibility = View.GONE
            val im = holder.article?.image?.firstOrNull()
            if(im != null){
                holder.article_image?.visibility = View.VISIBLE
                val optionss = RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.ic_all_inclusive_black_24px)
                        //.error(R.drawable.error)
                        .priority(Priority.HIGH)

                //use first of null so the app does not crash if no images exist
                Glide.with(con).load(im).apply(optionss)
                        .into(holder.article_image!!)
            }


        }

        mholder.openarticle?.setOnClickListener{

            val myIntent = Intent(con, ArticleActivity::class.java)
            myIntent.putExtra("username", if(holder.article?.rootAuthor != null) holder.article?.rootAuthor else holder.article?.author)
            myIntent.putExtra("tag", holder.article?.category)
            myIntent.putExtra("permlink", if(holder.article?.rootPermlink != null) holder.article?.rootPermlink else holder.article?.permlink)
            myIntent.putExtra("dbId",holder?.article?.myDbKey)
            if(adaptype == AdapterToUseFor.commentNoti || adaptype == AdapterToUseFor.replyNoti) myIntent.putExtra("permlinkToFind",holder.article?.permlink)

            con.startActivity(myIntent)

        }

        val articlepop = ArticlePopUpMenu(con,mholder.shareTextView,"${CentralConstants.baseUrlView}@${holder?.article?.author}","${CentralConstants.baseUrlView}${holder?.article?.category}/@${holder?.article?.author}/${holder?.article?.permlink}",null,name,holder.article?.author,adaptedcomms,position,holder?.progressbar,null,holder?.article?.activeVotes,true)


        mholder.article_like?.setOnClickListener{
            var articles = mholder.article as FeedArticleDataHolder.FeedArticleHolder
            var vop  = VoteOperation(AccountName(name), AccountName(articles.author), Permlink(articles.permlink))
            vop.weight = 100
            val weight = VoteWeightThenVote(con,adaptedcomms.getActivity(),vop,adaptedcomms,position,holder.progressbar,null)
            weight.makeDialog()
        }


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
            mholder.article_reblog_now?.setOnClickListener{
                var articles = mholder.article as FeedArticleDataHolder.FeedArticleHolder
                var vop = ReblogOperation(AccountName(name),AccountName(articles.author),Permlink(articles.permlink))
                var al = ArrayList<AccountName>()
                al.add(AccountName(name))
                var cus = CustomJsonOperation(null,al,"follow",vop.toJson())
                var list  = ArrayList<Operation>()
                list.add(cus)
                var bloc = GetDynamicAndBlock(con ,adaptedcomms,position,list,"Reblogged ${vop.permlink.link}",MyOperationTypes.reblog)
                bloc.GetDynamicGlobalProperties()
            }
        }

        holder.article_summary?.text = holder.article?.summary
    }

}