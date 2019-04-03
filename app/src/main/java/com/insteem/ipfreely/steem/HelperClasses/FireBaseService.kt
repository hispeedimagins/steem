package com.insteem.ipfreely.steem.HelperClasses

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.insteem.ipfreely.steem.CentralConstants
import com.insteem.ipfreely.steem.MainActivity
import com.insteem.ipfreely.steem.R
import java.util.*

class FireBaseService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage?.from}")
        // Check if message contains a data payload.
        remoteMessage?.data?.isNotEmpty()?.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            if (/* Check if data needs to be processed by long running job */ false) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob()
            } else {
                // Handle message within 10 seconds
                handleNow()
            }
            //sendNotification(remoteMessage.data.toString(),remoteMessage.data.toString())
        }

        // Check if message contains a notification payload.
        remoteMessage?.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            val chId = if(remoteMessage.data != null && remoteMessage.data[CentralConstants.fcmDataIsAppUpdate] == "true")
                FcmHelpers.updatechannelId
            else
                FcmHelpers.steemitAnnouncementChannelId
            sendNotification(this,it.title,it.body,remoteMessage.data,chId)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String?) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private fun scheduleJob() {
        // [START dispatch_job]
        /* val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
         val myJob = dispatcher.newJobBuilder()
             .setService(MyJobService::class.java)
             .setTag("my-job-tag")
             .build()
         dispatcher.schedule(myJob)*/
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
    }



    companion object {

        private const val TAG = "MyFirebaseMsgService"


        /**
         * Create and show a simple notification containing the received FCM message.
         * @param context applicationContext
         * @param title notification title
         * @param messageBody FCM message body received.
         * @param data data map containing data of fcm
         */
        private fun sendNotification(context: Context, messageBody: String?,
                                     title:String?, data:Map<String,String>,
                                     channelId:String) {
            val intent = Intent(context, MainActivity::class.java)
            val isAppUpdate = data[CentralConstants.fcmDataIsAppUpdate]?.toBoolean()
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(CentralConstants.fcmDataIsAppUpdate,isAppUpdate)
            intent.putExtra(CentralConstants.fcmDataAppUpdateUrl,data[CentralConstants.fcmDataAppUpdateUrl])
            intent.putExtra(CentralConstants.fcmDataPermlink,data[CentralConstants.fcmDataPermlink])
            intent.putExtra(CentralConstants.fcmDataTag,data[CentralConstants.fcmDataTag])
            intent.putExtra(CentralConstants.fcmDataUsername,data[CentralConstants.fcmDataUsername])

            val pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_steemerggsv)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(Date().time.toInt() /* ID of notification */, notificationBuilder.build())
        }
    }
}