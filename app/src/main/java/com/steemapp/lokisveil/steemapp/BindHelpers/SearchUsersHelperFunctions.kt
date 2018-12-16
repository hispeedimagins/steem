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
import com.steemapp.lokisveil.steemapp.DataHolders.GetReputationDataHolder
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.HelperClasses.ArticlePopUpMenu
import com.steemapp.lokisveil.steemapp.Interfaces.arvdinterface
import com.steemapp.lokisveil.steemapp.MyViewHolders.followViewHolder
import com.steemapp.lokisveil.steemapp.OpenOtherGuyBlog
import com.steemapp.lokisveil.steemapp.R
import com.steemapp.lokisveil.steemapp.jsonclasses.prof

//class for displaying the users in search results
class SearchUsersHelperFunctions(context : Context, username:String?, adapter: arvdinterface, adpterType: AdapterToUseFor) {

    val con: Context = context
    var name:String? = username
    val adaptedcomms: arvdinterface = adapter
    val and = AndDown()
    private var selectedPos = -1
    val sharedpref : SharedPreferences = context.getSharedPreferences(CentralConstants.sharedprefname,0)
    var key = sharedpref.getString(CentralConstants.key,null)
    //val globallist = ArrayList<Any>()
    val adaptype = adpterType


    //additional initialization stuff
    init {
        if(name == null){
            name = sharedpref.getString(CentralConstants.username,null)
        }
    }



    //called when adapter needs to bind with the view
    public fun Bind(mholder: RecyclerView.ViewHolder, position:Int){
        mholder.itemView.isSelected = selectedPos == position
        val holder  = mholder as followViewHolder
        holder.userUseWithSearch = adaptedcomms.getObject(position) as GetReputationDataHolder

        var fol : String? = holder.userUseWithSearch?.account

        holder.article_name?.text = holder.userUseWithSearch?.displayName
        holder.article_name?.setOnClickListener(View.OnClickListener {
            val i = Intent(con, OpenOtherGuyBlog::class.java)
            i.putExtra(CentralConstants.OtherGuyNamePasser,fol)
            con.startActivity(i)
        })
        //options for glide to display the profile picture
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


        //the common article menu to inflate for additional functions
        val articlepop = ArticlePopUpMenu(con,mholder.shareTextView,"${CentralConstants.baseUrlView}@$fol",null,holder?.userUseWithSearch?.followInternal,name,fol,adaptedcomms,position,holder?.progressbar,null,null,false)


    }

}