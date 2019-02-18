package com.steemapp.lokisveil.steemapp.HelperClasses

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import android.widget.Toast
import com.steemapp.lokisveil.steemapp.ArticleActivity
import com.steemapp.lokisveil.steemapp.R
import com.steemapp.lokisveil.steemapp.WidgetService
import java.util.*


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [AllAppWidgetConfigureActivity]
 */

//App widget provider needed for enabling widgets. Handles widget updates and saving of data.
class AllAppWidget : AppWidgetProvider() {


    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            AllAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId,"tag")
            AllAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId,"spinner")
        }
        super.onDeleted(context, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        super.onEnabled(context)

    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context)
    }

    override fun onReceive(context: Context, intent: Intent) {

        //This is where pending intents and intents are recieved after interaction with the
        //widget. You have to get the widget id for updating, which is saving in the
        //intent. Check the action type set and do what needs to be done.
        val mgr = AppWidgetManager.getInstance(context)
        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID)
        val viewIndex = intent.getIntExtra(EXTRA_ITEM, 0)
        if (intent.action == TOAST_ACTION) {
            if(intent.getBooleanExtra("refresh",false)){
                Toast.makeText(context, "Refreshing", Toast.LENGTH_SHORT).show()
                updateAppWidget(context,mgr,appWidgetId,true)
            } else{
                Toast.makeText(context, "opening ${intent.getStringExtra("permlink")}", Toast.LENGTH_SHORT).show()

                val myIntent = Intent(context, ArticleActivity::class.java)
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                myIntent.putExtra("username", intent.getStringExtra("username"))
                myIntent.putExtra("tag", intent.getStringExtra("tag"))
                myIntent.putExtra("permlink", intent.getStringExtra("permlink"))
                //pass the dbid along
                myIntent.putExtra("dbId",intent.getIntExtra("dbId",-1))
                myIntent.putExtra("fromWidget",true)
                context.startActivity(myIntent)
            }


        } else if(intent.action == LgAppWidget.sb_settings){
            var myIntent = Intent(context,AllAppWidgetConfigureActivity::class.java)
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            myIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId)
            myIntent.putExtra("islg",false)
            myIntent.putExtra("settingsclicked",true)
            context.startActivity(myIntent)
        }

        super.onReceive(context, intent)
    }

    companion object {
        val TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION"
        val EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM"

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int,isrefresh:Boolean=false) {

            /* val widgetText = AllAppWidgetConfigureActivity.loadTitlePref(context, appWidgetId)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.all_app_widget)
            views.setTextViewText(R.id.appwidget_text, widgetText)*/

            // Instruct the widget manager to update the widget

            // Here we setup the intent which points to the StackViewService which will
            // provide the views for this collection.
            val intent = Intent(context, WidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            //set if this is a refresh by us and pass it on
            intent.putExtra("isrefresh",isrefresh)
            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            val rv = RemoteViews(context.packageName, R.layout.all_app_widget)
            rv.setRemoteAdapter(R.id.stack_view, intent)
            // The empty view is displayed when the collection has no items. It should be a sibling
            // of the collection view.
            rv.setEmptyView(R.id.stack_view, R.id.empty_view)


            //fetch the data which is saved with the appwidgetid
            var tag = AllAppWidgetConfigureActivity.loadTitlePref(context,appWidgetId,"tag")
            var spinner = AllAppWidgetConfigureActivity.loadTitlePref(context,appWidgetId,"spinner")
            rv.setTextViewText(R.id.widget_heading,spinner.trim())
            if(tag.isNotEmpty() && tag.isNotBlank() && spinner != "feed"){
                rv.setTextViewText(R.id.widget_heading,"${spinner.trim()} - ${tag.trim()}")
            }
            var cal = calendarcalculations()
            cal.setDateOfTheData(Date().time,true)
            rv.setTextViewText(R.id.widget_refreshed,cal.getTimeString() )
            // Here we setup the a pending intent template. Individuals items of a collection
            // cannot setup their own pending intents, instead, the collection as a whole can
            // setup a pending intent template, and the individual items can set a fillInIntent
            // to create unique before on an item to item basis.
            val toastIntent = Intent(context, AllAppWidget::class.java)
            toastIntent.action = this.TOAST_ACTION
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            val toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)

            /*val extras = Bundle()
            extras.putBoolean("refresh",true)*/
            /*val fillInIntent = Intent()
            fillInIntent.putExtras(extras)*/
            var ointent = Intent(context, AllAppWidget::class.java)
            ointent.action = this.TOAST_ACTION
            ointent.putExtra("refresh",true)
            ointent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            ointent.data = Uri.parse(ointent.toUri(Intent.URI_INTENT_SCHEME))
            rv.setOnClickPendingIntent(R.id.widget_refreshed,PendingIntent.getBroadcast(context, 0, ointent,
                    PendingIntent.FLAG_UPDATE_CURRENT))

            var ointentd = Intent(context, AllAppWidget::class.java)
            ointentd.action = LgAppWidget.sb_settings
            ointentd.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            ointentd.putExtra("sbsettingsclicked",true)
            ointentd.data = Uri.parse(ointentd.toUri(Intent.URI_INTENT_SCHEME))
            rv.setOnClickPendingIntent(R.id.widget_settings,PendingIntent.getBroadcast(context, 0, ointentd,
                    PendingIntent.FLAG_UPDATE_CURRENT))
            //rv.setOnClickFillInIntent(R.id.widget_refreshed, fillInIntent)

            if(isrefresh){
                //appWidgetManager.updateAppWidget(appWidgetId, rv)
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.stack_view)
            }

            appWidgetManager.updateAppWidget(appWidgetId, rv)


            //appWidgetManager.updateAppWidget(appWidgetId, rv)
        }
    }
}

