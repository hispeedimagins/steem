package com.steemapp.lokisveil.steemapp.HelperClasses

import android.R.attr.fromXDelta
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.TaskStackBuilder
import android.widget.Toast
import com.google.gson.Gson
import com.steemapp.lokisveil.steemapp.Databases.NotificationsBusyDb
import com.steemapp.lokisveil.steemapp.Enums.NotificationType
import com.steemapp.lokisveil.steemapp.jsonclasses.BusyNotificationJson
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.decodeHex
import okio.ByteString.decodeHex
import org.json.JSONArray
import org.json.JSONObject
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.support.v4.app.RemoteInput
import android.R.attr.label

import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat.GROUP_ALERT_ALL
import android.support.v4.app.NotificationCompat.GROUP_ALERT_SUMMARY
import com.steemapp.lokisveil.steemapp.*
import com.steemapp.lokisveil.steemapp.Interfaces.NotificationsInterface
import java.util.*
import kotlin.collections.ArrayList


//WebSocketListener()
class NotificationsWebSocketListener(username:String?,context:Context?) : WebSocketListener() {
    private val NORMAL_CLOSURE_STATUS = 1000
    private var context : Context? = context
    private var notificationsInterface: NotificationsInterface? = null
    private val name = username
    private val SUMMARY_ID = 0

    var db: NotificationsBusyDb? = null

    init {
        notificationsInterface = if(context is NotificationsInterface) context else null
    }



    private var mNotificationManager: NotificationManagerCompat? = null
   // {"id":2,"jsonrpc":"2.0","method":"get_notifications","params":["hispeedimagins"]}
    override fun onOpen(webSocket: WebSocket?, response: Response?) {
       var ar = JSONArray()
       ar.put(name)
       var ob = JSONObject()
       ob.put("id",2)
       ob.put("jsonrpc","2.0")
       ob.put("method","get_notifications")
       ob.put("params",ar)


       var obs = ob.toString()

        //webSocket!!.send("{\"id\":2,\"jsonrpc\":\"2.0\",\"method\":\"get_notifications\",\"params\":[\"$name\"]}")
        webSocket!!.send(obs)
        //webSocket.send("What's up ?")
        //webSocket.send(ByteString.decodeHex("deadbeef"))
        //webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !")
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        db = NotificationsBusyDb(context as Context)
        var gson = Gson()
        //initialize notification collector, used later.
        var bus = ArrayList<BusyNotificationJson.Result>()
        var res = gson.fromJson<BusyNotificationJson.notification>(text,BusyNotificationJson.notification::class.java)
        if(res != null && res.result.any()){
            for(x in res.result){
                if((db?.Insert(x) as Boolean)){
                    var resultIntent = Intent(context, NotificationsBusyD::class.java)
                    var title = ""
                    var body = ""
                    when(x.type){
                        NotificationType.reply ->{
                            resultIntent =  Intent(context, ArticleActivity::class.java)
                            resultIntent.putExtra("permlinkToFind",x.permlink)
                            resultIntent.putExtra(CentralConstants.ArticleBlockPasser,x.block)
                            resultIntent.putExtra(CentralConstants.ArticleUsernameToState,x.author)
                            resultIntent.setAction(System.currentTimeMillis().toString())
                            resultIntent.putExtra(CentralConstants.ArticleNotiType,x.type.toString())
                            x.title = "${x.author} replied to your post"
                            x.body = x.parentPermlink?.replace("-"," ") as String
                        }
                        NotificationType.mention ->{
                            resultIntent =  Intent(context, ArticleActivity::class.java)
                            resultIntent.putExtra("permlinkToFind",x.permlink)
                            resultIntent.putExtra(CentralConstants.ArticleBlockPasser,x.block)
                            resultIntent.putExtra(CentralConstants.ArticleUsernameToState,x.author)
                            resultIntent.setAction(System.currentTimeMillis().toString())
                            resultIntent.putExtra(CentralConstants.ArticleNotiType,x.type.toString())
                            x.title = "${x.author} mentioned you"
                            x.body = x.permlink?.replace("-"," ") as String
                        }
                        NotificationType.reblog ->{
                            resultIntent =  Intent(context, ArticleActivity::class.java)
                            resultIntent.putExtra("permlinkToFind",x.permlink)
                            resultIntent.putExtra(CentralConstants.ArticleBlockPasser,x.block)
                            resultIntent.putExtra(CentralConstants.ArticleUsernameToState,x.account)
                            resultIntent.setAction(System.currentTimeMillis().toString())
                            resultIntent.putExtra(CentralConstants.ArticleNotiType,x.type.toString())
                            x.title = "${x.account} reblogged your post"
                            x.body = x.permlink?.replace("-"," ") as String
                        }
                        NotificationType.follow ->{
                            resultIntent =  Intent(context, OpenOtherGuyBlog::class.java)
                            x.title = "${x.follower} followed you"
                            resultIntent.putExtra(CentralConstants.OtherGuyNamePasser,x.follower)
                            resultIntent.setAction(System.currentTimeMillis().toString())
                            resultIntent.putExtra(CentralConstants.ArticleNotiType,x.type.toString())
                            x.body = ""
                        }
                        NotificationType.transfer->{
                            x.title = "From ${x.from} ${x.amount}"
                            x.body = "${x.memo}"
                        }
                        NotificationType.vote -> {
                            resultIntent = Intent(context, ArticleActivity::class.java)
                            resultIntent.putExtra("permlinkToFind", x?.permlink)
                            resultIntent.putExtra(CentralConstants.ArticleBlockPasser, x.block)
                            resultIntent.putExtra(CentralConstants.ArticleUsernameToState, x.voter)
                            resultIntent.putExtra(CentralConstants.ArticleNotiType,x.type.toString())
                            //title = "${holder.article?.author} replied to your post"
                            x.title = "${x.voter} voted on your post"
                            x.body = x.permlink?.replace("-", " ") as String
                            //body = holder.article?.author as String
                        }
                    }
                    x.notiIntent = resultIntent
                    bus.add(x)
                    //notificationbuildermainuni(resultIntent, context as Context, "${x.type?.name}", x.timestamp as Int, title, body, false, "", x.type!!,x)
                    //notificationbuildermainuni()
                }
            }

        }
        //now we issue the notifications, if there are any
        notificationbuildermainuni(context as Context,true,bus)
        db?.close()
        db = null
        notificationsInterface?.dbLoaded()
        webSocket?.close(NORMAL_CLOSURE_STATUS,null)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        //output("Receiving bytes : " + bytes.hex())
    }

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        webSocket!!.close(NORMAL_CLOSURE_STATUS, null)
        //output("Closing : $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        //output("Error : " + t!!.message)
    }

    /*private fun notificationbuildermainuni(intent: Intent, appcon: Context, groupkey: String,
                                           notiid: Int, notificationtitle: String, notificationmaintext: String,
                                           usetoast: Boolean, toastmessage: String, stack: NotificationType, om:List<BusyNotificationJson.Result>) {
    */
    private fun notificationbuildermainuni(appcon: Context,
                                           usetoast: Boolean, om:List<BusyNotificationJson.Result>) {

        var group = "com.steemapp.lokisveil.steemapp.HelperClasses.Notifications"
        var omsiz = om.size
        var notilist : List<NotificationCompat.Builder> = ArrayList()
        val stackBuilderSum = TaskStackBuilder.create(appcon)

        //This is the summary notification and is needed for grouping them.
        //We need that. Got pretty annoying when you get a lot of notifications
        // and individual vibrates
        stackBuilderSum.addParentStack(MainActivity::class.java)
        stackBuilderSum.addNextIntent(Intent(context, NotificationsBusyD::class.java))
        val resultPendingIntentSum = stackBuilderSum.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        val summary  = NotificationCompat.Builder(appcon,"Summary")
                //.setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_steemerggsv)
                .setContentTitle("New notifications" )
                .setContentText("$omsiz steem notifications")
                .setAutoCancel(true)
                .setGroup(group)
                .setContentIntent(resultPendingIntentSum)
                .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)

                //.setGroupAlertBehavior(GROUP_ALERT_SUMMARY)

                .setWhen((om.first().timestamp!!.toLong() * 1000))
                .setGroupSummary(true)
        var s = NotificationCompat.InboxStyle()

        for(x in om){
            if (usetoast) {

                /*if (myHandler != null) {

                    myHandler.post(Runnable { Toast.makeText(getApplicationContext(), toastmessage, Toast.LENGTH_LONG).show() })
                }*/

                /*new Runnable(){
                    @Override
                    public void run(){


                    }
                };*/

            }


            //String GROUP_KEY_EMAILS = groupkey;
            //int notiId = notiid;
            // Random rand = new Random();




            //build individual notifications here
            val mBuilder  = NotificationCompat.Builder(appcon,x.type?.name!!)
                    //.setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.ic_steemerggsv)
                    .setContentTitle(x.title )
                    .setContentText(x.body)
                    .setAutoCancel(true)
                    .setGroup(group)
                    .setWhen((x.timestamp!!.toLong() * 1000))
                    //.setGroupSummary(true)


            if(omsiz > 1){
                /*mBuilder.setStyle( NotificationCompat.InboxStyle()
                        .addLine(messageSnippet1)
                        .addLine(messageSnippet2))*/
                //var s = NotificationCompat.InboxStyle()


                //this is where we add summary lines
                s.addLine(x.title)


            }

            //.setStyle(NotificationCompat.BigTextStyle().bigText(notificationmaintext))








            val stackBuilder = TaskStackBuilder.create(appcon)
            // Adds the back stack for the Intent (but not the Intent itself)

            //Set the stack
            when (x.type) {
                NotificationType.follow -> {
                    stackBuilder.addParentStack(MainActivity::class.java)
                    x.notiIntent?.putExtra("usechatpage", true)
                }
                NotificationType.reblog -> {
                    stackBuilder.addParentStack(MainActivity::class.java)
                    x.notiIntent?.putExtra("usequestion", true)
                }
                NotificationType.mention ->{
                    stackBuilder.addParentStack(MainActivity::class.java)
                    x.notiIntent?.putExtra("usechatpage", true)
                }
                NotificationType.reply ->{
                    stackBuilder.addParentStack(MainActivity::class.java)
                    x.notiIntent?.putExtra("usechatpage", true)
                }
            }

            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(x.notiIntent!!)
            val resultPendingIntent = stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
            mBuilder.setContentIntent(resultPendingIntent)


            //Only for oreo notifications channels, untested
            val notification_manager = appcon.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (SDK_INT >= Build.VERSION_CODES.O) {
                // Create the NotificationChannel, but only on API 26+ because
                // the NotificationChannel class is new and not in the support library
                //val name = getString(R.string.channel_name)
                //val description = getString(R.string.channel_description)

                //val importance =
                val channel = NotificationChannel(name, name, NotificationManager.IMPORTANCE_DEFAULT)
                channel.description = x.type?.name
                channel.enableLights(true)
                // Sets whether notification posted to this channel should vibrate.
                channel.enableVibration(true)
                // Sets the notification light color for notifications posted to this channel
                //androidChannel.setLightColor(Color.GREEN);
                // Sets whether notifications posted to this channel appear on the lockscreen or not
                channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE;
                //channel.importance = NotificationManager.IMPORTANCE_DEFAULT

                // Register the channel with the system
                /*var hs = NotificationManager()
                val notificationManager = NotificationManagerCompat.from(appcon)
                notificationManager.createNotificationChannel(channel)*/

                notification_manager.createNotificationChannel(channel)


            }
            else{

            }
            notilist += mBuilder
        }





        if (mNotificationManager == null) {
            mNotificationManager = NotificationManagerCompat.from(appcon)

        }

        // mId allows you to update the notification later on.
        //var ord = stack.ordinal

        //now we fire them all
        if(omsiz > 1){
            s.setBigContentTitle("New notifications $name")
            s.setSummaryText("${notilist.size} steem notifications")
            summary.setStyle(s)
            summary.setDefaults(Notification.DEFAULT_ALL)
            mNotificationManager?.notify(SUMMARY_ID, summary.build())
        }
        //needed so there is one notification min
        else if(omsiz == 1){
            notilist.first().setDefaults(Notification.DEFAULT_ALL)
        }
        for(x in notilist){
            mNotificationManager?.notify(Date().time.toInt(), x.build())
        }

        //mNotificationManager?.notify(Date().time.toInt(), mBuilder.build())
        //mNotificationManager?.notify(ord+notiid, groupBuilder.build())
    }
}

