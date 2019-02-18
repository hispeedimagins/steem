package com.steemapp.lokisveil.steemapp.MyViewHolders

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.steemapp.lokisveil.steemapp.R
import com.steemapp.lokisveil.steemapp.jsonclasses.feed

/**
 * Created by boot on 3/13/2018.
 */
class UpvoteViewHolder constructor(view: View) : RecyclerView.ViewHolder(view)   {
    val mView: View = view
    var article: feed.avtiveVotes? = null


    val article_pfp : ImageView? = view.findViewById(R.id.article_pfp)
    val name : TextView? = view.findViewById(R.id.name)
    val dialog_vote_percent : TextView? = view.findViewById(R.id.dialog_vote_percent)
    val dialog_vote_power : TextView? = view.findViewById(R.id.dialog_vote_power)
    val value : TextView? = view.findViewById(R.id.value)
    val dialog_date : TextView? = view.findViewById(R.id.dialog_date)

    val progressbar = view.findViewById<ProgressBar>(R.id.progressbar)
    val shareTextView = view.findViewById<TextView>(R.id.textViewOptions)
    //val article_summary_markdown : eu.fiskur.markdownview.MarkdownView = view.findViewById(R.id.article_summary_markdown) as eu.fiskur.markdownview.MarkdownView

    init {
        super.itemView

    }
}