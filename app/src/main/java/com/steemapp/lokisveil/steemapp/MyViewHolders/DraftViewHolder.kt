package com.steemapp.lokisveil.steemapp.MyViewHolders


import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.steemapp.lokisveil.steemapp.R
import com.steemapp.lokisveil.steemapp.jsonclasses.OperationJson

class DraftViewHolder constructor(view: View) : RecyclerView.ViewHolder(view)  {
    val mView: View = view
    var article: OperationJson.draftholder? = null
    val article_date : TextView? = view.findViewById(R.id.article_date)
    val article_tags : TextView? = view.findViewById(R.id.article_tags)
    val article_title : TextView? = view.findViewById(R.id.article_title)
    val article_image : ImageView? = view.findViewById(R.id.article_image)
    val article_summary : TextView? = view.findViewById(R.id.article_summary)
    val openarticle : LinearLayout? = view.findViewById(R.id.openarticle)
    val shareTextView = view.findViewById<TextView>(R.id.textViewOptions)
    init {
        super.itemView

    }


}