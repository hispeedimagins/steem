package com.insteem.ipfreely.steem.MyViewHolders


import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.insteem.ipfreely.steem.DataHolders.GetReputationDataHolder
import com.insteem.ipfreely.steem.R
import com.insteem.ipfreely.steem.jsonclasses.prof

/**
 * Created by boot on 3/31/2018.
 */
class followViewHolder constructor(view: View) : RecyclerView.ViewHolder(view)  {
    val mView: View = view
    var article: prof.Resultfp? = null
    //added a class type to reuse this holder for search results
    var userUseWithSearch : GetReputationDataHolder? = null
    val article_pfp : ImageView? = view.findViewById(R.id.article_pfp)
    val article_name : TextView? = view.findViewById(R.id.article_name)
    val shareTextView = view.findViewById<TextView>(R.id.textViewOptions)
    val progressbar = view.findViewById<ProgressBar>(R.id.progressbar)

    init {
        super.itemView

    }


}