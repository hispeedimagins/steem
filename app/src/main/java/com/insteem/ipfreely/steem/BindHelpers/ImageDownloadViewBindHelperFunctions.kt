package com.insteem.ipfreely.steem.BindHelpers

import android.content.Context
import android.content.SharedPreferences
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.insteem.ipfreely.steem.CentralConstants
import com.insteem.ipfreely.steem.DataHolders.ImageDownloadDataHolder
import com.insteem.ipfreely.steem.Interfaces.arvdinterface
import com.insteem.ipfreely.steem.MyViewHolders.ImageDownloaderItemViewHolder
import com.insteem.ipfreely.steem.R

/**
 * Image bind helper functions
 * @param context application context
 * @param username the username of the current user
 * @param adapter the adapter interface to callback
 */
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


    /**
     * set the selected item and forward the clicked item to the adapter
     * to forward to the activity
     * @param pos the viewholder in question
     */
    fun setSelectedPosition(pos:ImageDownloaderItemViewHolder){
        selectedPos = pos.adapterPosition
        adapter.objectClicked(pos.data)
    }


    /**
     * Called when ui is to be bound
     * @param mholder the holder which is to be cast as required
     * @param position the position of the item in the list
     */
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