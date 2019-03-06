package com.insteem.ipfreely.steem.MyViewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.insteem.ipfreely.steem.R

//A viewholder for the header text in search results
//can be used anywhere
class HeaderViewHolder constructor(view: View) : RecyclerView.ViewHolder(view)  {
    val mView: View = view
    var article: String? = null

    val headertext : TextView? = view.findViewById(R.id.headertext)

    init {
        super.itemView

    }


}