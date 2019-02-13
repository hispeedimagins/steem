package com.steemapp.lokisveil.steemapp.MyViewHolders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.steemapp.lokisveil.steemapp.DataHolders.ImageDownloadDataHolder
import com.steemapp.lokisveil.steemapp.R

class ImageDownloaderItemViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
    var data:ImageDownloadDataHolder? = null
    val image: ImageView = mView.findViewById(R.id.article_image)


}