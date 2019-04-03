package com.insteem.ipfreely.steem.HelperClasses


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.insteem.ipfreely.steem.R

class FcmHelpers {
    companion object {

        val topicsSubscribed = "topicsSubscribed"
        val updatechannelId = "app_update"
        val steemitAnnouncementChannelId = "steem_announcements"
        val steemNotifications_local = "steem_notifications_local"
        /**
         * subscribe to all topics for notifications
         */
        infix fun subscribeToAll(context: Context){
            if(!SharedPrefrencesSingleton.getInstance(context).getBoolean(topicsSubscribed)){
                subscriptToTopic("appupdate")
                subscriptToTopic("steemitannouncement",context)
                setupChannels(context)
            }
        }

        /**
         * function for subscribing to a topic
         * @param topicName name of topic to subscribe to
         */
        fun subscriptToTopic(topicName:String,context: Context? = null){
            FirebaseMessaging.getInstance().subscribeToTopic(topicName)
                    .addOnCompleteListener { task ->
                        var msg = "init"
                        if (!task.isSuccessful) {
                            msg = "done"
                            context?.let {
                                SharedPrefrencesSingleton.getInstance(it).put(topicsSubscribed,true,true)
                            }
                        }
                        Log.d("FcmHelpers", msg)
                        //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    }
        }


        /**
         * sets up notification channels for fcm and other notifications
         */
        fun setupChannels(context: Context){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create channel to show notifications.
                val notificationsName = context.getString(R.string.fcm_steem_notifications_local)
                val channelName = context.getString(R.string.fcm_app_update_title)
                val steemitAnnouncementChannelName = context.getString(R.string.fcm_steem_announcement_title)
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager?.createNotificationChannel(
                        NotificationChannel(updatechannelId,
                                channelName, NotificationManager.IMPORTANCE_HIGH)
                )
                notificationManager?.createNotificationChannel(
                        NotificationChannel(steemitAnnouncementChannelId,
                                steemitAnnouncementChannelName, NotificationManager.IMPORTANCE_HIGH)
                )
                notificationManager?.createNotificationChannel(
                        NotificationChannel(steemNotifications_local,
                                notificationsName, NotificationManager.IMPORTANCE_HIGH)
                )
            }
        }
    }
}