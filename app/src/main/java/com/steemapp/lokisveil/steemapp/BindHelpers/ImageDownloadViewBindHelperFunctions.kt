package com.steemapp.lokisveil.steemapp.BindHelpers

import android.content.Context
import android.content.SharedPreferences
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.steemapp.lokisveil.steemapp.CentralConstants
import com.steemapp.lokisveil.steemapp.DataHolders.ImageDownloadDataHolder
import com.steemapp.lokisveil.steemapp.Interfaces.arvdinterface
import com.steemapp.lokisveil.steemapp.MyViewHolders.ImageDownloaderItemViewHolder
import com.steemapp.lokisveil.steemapp.R

class ImageDownloadViewBindHelperFunctions (val context : Context, var username:String?,
                                            val adapter: arvdinterface)  {
    //var name:String? = username
    //val adaptedcomms: arvdinterface = adapter
    //internal var scale: Float = scale
    //internal var metrics: DisplayMetrics = metrics
    var selectedPos = -1
    var animatedVec : AnimatedVectorDrawableCompat? = null
    //var floatingDateHolder = dateHolder
    init {
        if(username == null){
            val sharedpref : SharedPreferences = context.getSharedPreferences(CentralConstants.sharedprefname,0)
            username = sharedpref.getString(CentralConstants.username,null)
        }
        animatedVec = AnimatedVectorDrawableCompat.create(context,R.drawable.animated_loader)
    }


    fun setSelectedPosition(pos:ImageDownloaderItemViewHolder){
        selectedPos = pos.adapterPosition
        adapter.objectClicked(pos.data)
    }


    fun Bind(mholder: RecyclerView.ViewHolder, position:Int){
        mholder.itemView.setSelected(selectedPos == position)
        val holder : ImageDownloaderItemViewHolder = mholder as ImageDownloaderItemViewHolder
        holder.data = adapter.getObject(position) as ImageDownloadDataHolder
        val optionss = RequestOptions()
                .centerCrop()
                .placeholder(animatedVec)
                //.error(R.drawable.error)
                .priority(Priority.HIGH)
        animatedVec?.start()
        Glide.with(context).load(holder.data?.url).apply(optionss)
                .into(holder.image)

    }

}