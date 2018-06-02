package com.steemapp.lokisveil.steemapp.MyViewHolders

import android.support.design.widget.TextInputLayout
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.AppCompatSeekBar
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.R

//View holder for adding a beneficiary, makes life easier
class beneficiaryAddClasspackage(view: View) : RecyclerView.ViewHolder(view) {
    val mView: View = view
    var article: FeedArticleDataHolder.beneficiariesDataHolder? = null
    val article_pfp : ImageView? = view.findViewById(R.id.article_pfp)
    val article_name : EditText? = view.findViewById(R.id.article_name)
    val article_date : TextView? = view.findViewById(R.id.article_date)
    val article_percent : TextView? = view.findViewById(R.id.article_percent)
    val article_tag : EditText? = view.findViewById(R.id.article_tags)
    val dialog_seekbar : AppCompatSeekBar? = view.findViewById(R.id.dialog_seekbar)
    val artile_default : AppCompatCheckBox? = view.findViewById(R.id.artile_default)
    val artile_use_now : AppCompatCheckBox? = view.findViewById(R.id.article_use_now)
    val cardviewchat : CardView? = view.findViewById(R.id.cardviewchat)
    val openarticle : LinearLayout? = view.findViewById(R.id.openarticle)
    val article_like : LinearLayout? = view.findViewById(R.id.article_like)
    val article_use : LinearLayout? = view.findViewById(R.id.article_use)
    val progressbar = view.findViewById<ProgressBar>(R.id.progressbar)
    val article_tags_tip : TextInputLayout? = view.findViewById(R.id.article_tags_tip)
    val article_name_tip : TextInputLayout? = view.findViewById(R.id.article_name_tip)
    init {
        super.itemView

    }
}