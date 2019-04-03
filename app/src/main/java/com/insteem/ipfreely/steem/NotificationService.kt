package com.insteem.ipfreely.steem


import android.content.Context

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.insteem.ipfreely.steem.Databases.NotificationsBusyDb
import com.insteem.ipfreely.steem.HelperClasses.NotificationsWebSocketListener
import com.insteem.ipfreely.steem.Interfaces.NotificationInterface
import okhttp3.OkHttpClient
//import okhttp3.OkHttpClient

import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject


class NotificationService : JobService() {
    var db:NotificationsBusyDb? = null
    var appcon:Context? = null
    private var notificationInterface : NotificationInterface? = null
    private var username : String? = null
    private var context : Context? = null
    private var client: OkHttpClient? = null

    override fun onStopJob(job: JobParameters): Boolean {
        client = null
        return true
    }

    override fun onStartJob(job: JobParameters): Boolean {
        var se = this.getSharedPreferences(CentralConstants.sharedprefname,0)
        username = se.getString(CentralConstants.username,null)
        context = this
        appcon = this.applicationContext
        client = OkHttpClient()
        start()
        return true
    }


    fun getNotifications(){

    }

    private fun start() {
        val request = Request.Builder().url("wss://api.busy.org/").build()
        val listener = NotificationsWebSocketListener(username,context)
        val ws = client?.newWebSocket(request, listener)



        var ar = JSONArray()
        ar.put(username)
        var ob = JSONObject()
        ob.put("id",2)
        ob.put("jsonrpc","2.0")
        ob.put("method","get_notifications")
        ob.put("params",ar)

        var obs = ob.toString()
        ws?.send(obs)

        //client?.dispatcher()?.executorService()?.shutdown()
    }

    //private val mBinder = LocalBinder()
    //private var client: OkHttpClient? = null


    /*override fun onCreate() {
        super.onCreate()

        //client = OkHttpClient()
    }*/


    /*override fun onUnbind(intent: Intent): Boolean {
        //client = null
        notificationInterface = null
        Log.i("signalronUnbind", "unbind called")
        super.onUnbind(intent)
        return false
    }

    *//*override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }*//*


    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        //startSignalR();

        Log.i("signalrbinddone", "binddone called")
        return mBinder
    }
*/
    /*inner class LocalBinder : Binder() {
        // Return this instance of SignalRService so clients can call public methods
        val service: NotificationService
            get() = this@NotificationService
    }*/
}
