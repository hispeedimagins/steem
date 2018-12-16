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
class FollowDisplayHelperFunctions(context : Context, username:String?, adapter: arvdinterface, adpterType: AdapterToUseFor) {

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


        val articlepop = ArticlePopUpMenu(con,mholder.shareTextView,"${CentralConstants.baseUrlView}@$fol",null,holder?.article?.followInternal,name,fol,adaptedcomms,position,holder?.progressbar,null,null,false)


    }

}