package com.insteem.ipfreely.steem.MyViewHolders


import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.insteem.ipfreely.steem.DataHolders.ImageDownloadDataHolder
import com.insteem.ipfreely.steem.R

/**
 * the Image data holder to add to the adapter
 * @param mView view which contains the ui of the image holder
 */
class ImageDownloaderItemViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
    var data:ImageDownloadDataHolder? = null
    val image: ImageView = mView.findViewById(R.id.article_image)


}