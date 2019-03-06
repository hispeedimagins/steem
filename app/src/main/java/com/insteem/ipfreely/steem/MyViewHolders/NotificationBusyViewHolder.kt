package com.insteem.ipfreely.steem.MyViewHolders

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.insteem.ipfreely.steem.R
import com.insteem.ipfreely.steem.jsonclasses.BusyNotificationJson

class NotificationBusyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mView: View = view
    var article: BusyNotificationJson.Result? = null
    val article_pfp : ImageView? = view.findViewById(R.id.article_pfp)
    val article_name : TextView? = view.findViewById(R.id.article_name)
    val article_date : TextView? = view.findViewById(R.id.article_date)
    val article_title : TextView? = view.findViewById(R.id.article_title)
    val article_summary : TextView? = view.findViewById(R.id.article_summary) as TextView
    val cardviewchat : CardView? = view.findViewById(R.id.cardviewchat)
    val openarticle : LinearLayout? = view.findViewById(R.id.openarticle)
    val progressbar = view.findViewById<ProgressBar>(R.id.progressbar)
    init {
        super.itemView

    }
}