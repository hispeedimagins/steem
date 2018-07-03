package com.steemapp.lokisveil.steemapp.MyViewHolders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.R

/**
 * Created by boot on 2/4/2018.
 */
class ArticleViewHolder constructor(view: View) : RecyclerView.ViewHolder(view)  {
    val mView: View = view
    var article: FeedArticleDataHolder.FeedArticleHolder? = null
    val article_resteemed_by : TextView? = view.findViewById(R.id.article_resteemed_by)
    //added resteemed by linear holder
    val article_resteemed_by_Linear : LinearLayout? = view.findViewById(R.id.article_resteemed_by_Linear)
    val article_pfp : ImageView? = view.findViewById(R.id.article_pfp)
    val article_name : TextView? = view.findViewById(R.id.article_name)
    val article_date : TextView? = view.findViewById(R.id.article_date)
    val article_tag : TextView? = view.findViewById(R.id.article_tag)
    val article_title : TextView? = view.findViewById(R.id.article_title)
    val article_image : ImageView? = view.findViewById(R.id.article_image)
    val article_summary : TextView? = view.findViewById(R.id.article_summary)
    val article_likes : TextView? = view.findViewById(R.id.article_likes)
    val article_comments : TextView? = view.findViewById(R.id.article_comments)
    val article_reblog : TextView? = view.findViewById(R.id.article_reblog)
    val article_payout : TextView? = view.findViewById(R.id.article_payout)

    val openarticle : LinearLayout? = view.findViewById(R.id.openarticle)

    val article_like : LinearLayout? = view.findViewById(R.id.article_like)
    val article_reblog_now : LinearLayout? = view.findViewById(R.id.article_reblog_now)
    val shareTextView = view.findViewById<TextView>(R.id.textViewOptions)
    val progressbar = view.findViewById<ProgressBar>(R.id.progressbar)
    val article_edits : TextView? = view.findViewById(R.id.article_edits)
    val article_edit : LinearLayout? = view.findViewById(R.id.article_edit)
    val markdownView = view.findViewById<br.tiagohm.markdownview.MarkdownView>(R.id.markdown_view)
    //val article_summary_markdown : eu.fiskur.markdownview.MarkdownView = view.findViewById(R.id.article_summary_markdown) as eu.fiskur.markdownview.MarkdownView

    init {
        super.itemView

    }


}