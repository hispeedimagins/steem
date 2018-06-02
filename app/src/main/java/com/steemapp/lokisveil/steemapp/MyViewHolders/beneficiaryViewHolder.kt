package com.steemapp.lokisveil.steemapp.MyViewHolders

import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.AppCompatSeekBar
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.R

//for the beneficiary display in a recyclerview
class beneficiaryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mView: View = view
    var article: FeedArticleDataHolder.beneficiariesDataHolder? = null
    //val article_resteemed_by : TextView? = view.findViewById(R.id.article_resteemed_by)
    val article_pfp : ImageView? = view.findViewById(R.id.article_pfp)
    val article_name : TextView? = view.findViewById(R.id.article_name)
    val article_date : TextView? = view.findViewById(R.id.article_date)
    val article_percent : TextView? = view.findViewById(R.id.article_percent)
    val article_tag : EditText? = view.findViewById(R.id.article_tags)
    val dialog_seekbar : AppCompatSeekBar? = view.findViewById(R.id.dialog_seekbar)
    //val article_title : TextView? = view.findViewById(R.id.article_title)
    //val article_image : ImageView? = view.findViewById(R.id.article_image) as ImageView
    //val article_summary : TextView? = view.findViewById(R.id.article_summary) as TextView
    //val article_webview : WebView = view.findViewById(R.id.article_webview)
    val artile_default : AppCompatCheckBox? = view.findViewById(R.id.artile_default)
    val artile_use_now : AppCompatCheckBox? = view.findViewById(R.id.article_use_now)
    /*val article_edits : TextView? = view.findViewById(R.id.article_edits)
    val article_comments : TextView? = view.findViewById(R.id.article_comments)
    val article_reblog : TextView? = view.findViewById(R.id.article_reblog)
    val article_payout : TextView? = view.findViewById(R.id.article_payout)*/
    val cardviewchat : CardView? = view.findViewById(R.id.cardviewchat)
    val openarticle : LinearLayout? = view.findViewById(R.id.openarticle)
    val article_like : LinearLayout? = view.findViewById(R.id.article_like)
    val article_use : LinearLayout? = view.findViewById(R.id.article_use)
    /*val article_edit : LinearLayout? = view.findViewById(R.id.article_edit)
    val article_reblog_now : LinearLayout? = view.findViewById(R.id.article_reply_now)*/
    //val article_summary_markdown : eu.fiskur.markdownview.MarkdownView = view.findViewById(R.id.article_summary_markdown) as eu.fiskur.markdownview.MarkdownView
    //val shareTextView = view.findViewById<TextView>(R.id.textViewOptions)
    val progressbar = view.findViewById<ProgressBar>(R.id.progressbar)
    init {
        super.itemView

    }
}