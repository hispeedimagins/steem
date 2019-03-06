package com.insteem.ipfreely.steem.BindHelpers

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.insteem.ipfreely.steem.Enums.AdapterToUseFor
import com.insteem.ipfreely.steem.Interfaces.arvdinterface
import com.insteem.ipfreely.steem.MyViewHolders.HeaderViewHolder

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