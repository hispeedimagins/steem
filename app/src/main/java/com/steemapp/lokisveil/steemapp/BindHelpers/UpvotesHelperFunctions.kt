package com.steemapp.lokisveil.steemapp.BindHelpers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.steemapp.lokisveil.steemapp.CentralConstants
import com.steemapp.lokisveil.steemapp.HelperClasses.ArticlePopUpMenu
import com.steemapp.lokisveil.steemapp.Interfaces.arvdinterface
import com.steemapp.lokisveil.steemapp.MyViewHolders.UpvoteViewHolder
import com.steemapp.lokisveil.steemapp.OpenOtherGuyBlog
import com.steemapp.lokisveil.steemapp.R
import com.steemapp.lokisveil.steemapp.jsonclasses.feed

/**
 * Created by boot on 3/13/2018.
 */
class UpvotesHelperFunctions(context : Context, username:String?, adapter: arvdinterface,dateHolder: FloatingDateHolder? = null){
    val con:Context = context
    var name:String? = username
    val adaptedcomms: arvdinterface = adapter
    var floatingDateHolder:FloatingDateHolder? = dateHolder
    //val and = AndDown()
    private var selectedPos = -1
    val sharedpref : SharedPreferences = context.getSharedPreferences(CentralConstants.sharedprefname,0)
    var key = sharedpref.getString(CentralConstants.key,null)

    init {
        if(name == null){
            name = sharedpref.getString(CentralConstants.username,null)
        }
    }


    fun add(vote:feed.avtiveVotes){
        if(vote.date != null){
            floatingDateHolder?.checktimeandaddQuestions(vote.date!!)
        }

        adaptedcomms.add(vote)
        adaptedcomms.notifyitemcinserted(adaptedcomms.getSize())
    }

    fun add(list:List<feed.avtiveVotes>){
        for(x in list){
            add(x)
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