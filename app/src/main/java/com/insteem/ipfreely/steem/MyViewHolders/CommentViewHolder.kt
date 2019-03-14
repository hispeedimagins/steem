package com.insteem.ipfreely.steem.MyViewHolders


import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.insteem.ipfreely.steem.DataHolders.FeedArticleDataHolder
import com.insteem.ipfreely.steem.R

/**
 * Created by boot on 2/16/2018.
 */
class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mView: View = view
    var article: FeedArticleDataHolder.CommentHolder? = null
    //val article_resteemed_by : TextView? = view.findViewById(R.id.article_resteemed_by)
    val article_pfp : ImageView? = view.findViewById(R.id.article_pfp)
    val article_name : TextView? = view.findViewById(R.id.article_name)
    val article_date : TextView? = view.findViewById(R.id.article_date)
    //val article_tag : TextView? = view.findViewById(R.id.article_tag)
    val article_likes : TextView? = view.findViewById(R.id.article_likes)
    val article_edits : TextView? = view.findViewById(R.id.article_edits)
    val article_comments : TextView? = view.findViewById(R.id.article_comments)
    val article_reblog : TextView? = view.findViewById(R.id.article_reblog)
    val article_payout : TextView? = view.findViewById(R.id.article_payout)
    val cardviewchat : CardView? = view.findViewById(R.id.cardviewchat)
    val openarticle : LinearLayout? = view.findViewById(R.id.openarticle)
    val article_like : LinearLayout? = view.findViewById(R.id.article_like)
    val article_edit : LinearLayout? = view.findViewById(R.id.article_edit)
    val article_reblog_now : LinearLayout? = view.findViewById(R.id.article_reply_now)
    //val article_summary_markdown : eu.fiskur.markdownview.MarkdownView = view.findViewById(R.id.article_summary_markdown) as eu.fiskur.markdownview.MarkdownView
    val shareTextView = view.findViewById<TextView>(R.id.textViewOptions)
    val progressbar = view.findViewById<ProgressBar>(R.id.progressbar)
    init {
        super.itemView

    }
}