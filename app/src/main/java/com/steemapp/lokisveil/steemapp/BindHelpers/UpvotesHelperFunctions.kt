package com.steemapp.lokisveil.steemapp.BindHelpers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Response
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.commonsware.cwac.anddown.AndDown
import com.google.gson.Gson
import com.steemapp.lokisveil.steemapp.*
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Enums.FollowInternal
import com.steemapp.lokisveil.steemapp.HelperClasses.ArticlePopUpMenu
import com.steemapp.lokisveil.steemapp.HelperClasses.GetDynamicAndBlock
import com.steemapp.lokisveil.steemapp.HelperClasses.StaticMethodsMisc
import com.steemapp.lokisveil.steemapp.HelperClasses.VoteWeightThenVote
import com.steemapp.lokisveil.steemapp.Interfaces.arvdinterface
import com.steemapp.lokisveil.steemapp.MyViewHolders.ArticleViewHolder
import com.steemapp.lokisveil.steemapp.MyViewHolders.UpvoteViewHolder
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

/**
 * Created by boot on 3/13/2018.
 */
class UpvotesHelperFunctions(context : Context, username:String?, adapter: AllRecyclerViewAdapter){
    val con:Context = context
    var name:String? = username
    val adaptedcomms: arvdinterface = adapter
    //val and = AndDown()
    private var selectedPos = -1
    val sharedpref : SharedPreferences = context.getSharedPreferences(CentralConstants.sharedprefname,0)
    var key = sharedpref.getString(CentralConstants.key,null)

    init {
        if(name == null){
            name = sharedpref.getString(CentralConstants.username,null)
        }
    }
   // val globallist = ArrayList<Any>()

    public fun Bind(mholder: RecyclerView.ViewHolder, position:Int){
        mholder.itemView.setSelected(selectedPos == position)
        val holder : UpvoteViewHolder = mholder as UpvoteViewHolder
        holder.article = adaptedcomms.getObject(position) as feed.avtiveVotes








       // holder.name?.text = "${holder.article?.voter} (${StaticMethodsMisc.CalculateRepScore(holder.article?.reputation)})"
        holder.name?.text = holder.article?.namewithrep
        holder.name?.setOnClickListener(View.OnClickListener {
            val i = Intent(con, OpenOtherGuyBlog::class.java)
            i.putExtra(CentralConstants.OtherGuyNamePasser,holder.article?.voter)
            con.startActivity(i)
        })

        //holder.dialog_vote_percent?.text ="Vote percent :"+  ((holder.article?.percent as String).toInt() / 100).toString()
        holder.dialog_vote_percent?.text =holder.article?.calculatedpercent
        //val d = Calendar.getInstance()
        //d.time = holder.article?.createdcon
        holder.dialog_date?.text = holder.article?.dateString
        val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_person_white_24px)
                //.error(R.drawable.error)
                .priority(Priority.HIGH)
                .circleCrop()
        Glide.with(con).load(CentralConstants.GetFeedImageUrl(holder.article?.voter)).apply(options)
                //.placeholder(R.drawable.common_full_open_on_phone)
                .into(holder.article_pfp as ImageView)

        holder.dialog_vote_power?.text =  holder.article?.calculatedvotepercent
        //holder.dialog_vote_power?.text =  "Vote power :"+ ((holder.article?.weight as String).toInt() / 100).toString()
        //holder.value?.text = StaticMethodsMisc.FormatVotingValueToSBD(StaticMethodsMisc.VotingValueSteemToSd(StaticMethodsMisc.CalculateVotingValueRshares(holder.article?.rshares)))
        holder.value?.text = holder.article?.calculatedrshares


        val articlepop = ArticlePopUpMenu(con,mholder.shareTextView,"${CentralConstants.baseUrlView}@${holder?.article?.voter}",null,holder.article?.followInternal,name,holder.article?.voter,adaptedcomms,position,holder.progressbar,null)




    }
}