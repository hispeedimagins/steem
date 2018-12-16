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
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.HelperClasses.ArticlePopUpMenu
import com.steemapp.lokisveil.steemapp.HelperClasses.GetDynamicAndBlock
import com.steemapp.lokisveil.steemapp.HelperClasses.VoteWeightThenVote
import com.steemapp.lokisveil.steemapp.Interfaces.arvdinterface
import com.steemapp.lokisveil.steemapp.MyViewHolders.ArticleViewHolder
import com.steemapp.lokisveil.steemapp.MyViewHolders.HeaderViewHolder
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.AccountName
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.Permlink
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.CustomJsonOperation
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.Operation
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.ReblogOperation
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.VoteOperation
import com.steemapp.lokisveil.steemapp.jsonclasses.Block
import org.json.JSONObject

//this class handles the headers for search results in adapter view
class HeaderHelperFunctions(context : Context, username:String?, adapter: arvdinterface, adpterType: AdapterToUseFor) {
    val con: Context = context
    var name:String? = username
    val adaptedcomms: arvdinterface = adapter

    val adaptype = adpterType



    fun add(article : String){
        adaptedcomms.add(article)
        //adapter.notifyDataSetChanged();
        adaptedcomms.notifyitemcinserted(adaptedcomms.getSize())
    }

    fun add(articles:List<String>){
        for (x in articles){
            add(x)
        }
    }



    //called by the adapter when it needs to bind
    fun Bind(mholder: RecyclerView.ViewHolder, position:Int){
        val holder : HeaderViewHolder = mholder as HeaderViewHolder
        holder.article = adaptedcomms.getObject(position) as String
        holder.headertext?.text = holder.article

    }


}