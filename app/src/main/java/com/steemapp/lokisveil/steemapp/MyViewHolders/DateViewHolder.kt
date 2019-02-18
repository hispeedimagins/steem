package com.steemapp.lokisveil.steemapp.MyViewHolders


import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.steemapp.lokisveil.steemapp.R
import java.util.*

class DateViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
    var dh: Calendar? = null
    var date: String? = null
    var dateofchatstart: TextView = mView.findViewById(R.id.dateofchatstart) as TextView

}