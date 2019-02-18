package com.steemapp.lokisveil.steemapp.MyViewHolders


import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.steemapp.lokisveil.steemapp.DataHolders.ImageDownloadDataHolder
import com.steemapp.lokisveil.steemapp.R

/**
 * the Image data holder to add to the adapter
 * @param mView view which contains the ui of the image holder
 */
class ImageDownloaderItemViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
    var data:ImageDownloadDataHolder? = null
    val image: ImageView = mView.findViewById(R.id.article_image)


}