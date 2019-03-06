package com.insteem.ipfreely.steem.HelperClasses

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import com.insteem.ipfreely.steem.MiscConstants

import com.insteem.ipfreely.steem.R
import kotlinx.android.synthetic.main.all_app_widget_configure.*

/**
 * The configuration screen for the [AllAppWidget] AppWidget.
 */

//This is the config screen for widgets
class AllAppWidgetConfigureActivity : Activity() {
    internal var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    internal var mAppWidgetText: EditText? = null
    internal var islg : Boolean = false
    internal var settingsclicked : Boolean = false
    internal var mOnClickListener: View.OnClickListener = View.OnClickListener {
        val context = this@AllAppWidgetConfigureActivity

        // When the button is clicked, store the string locally
        val widgetText = mAppWidgetText?.text.toString()
        saveTitlePref(context, mAppWidgetId,"tag", widgetText)
        saveTitlePref(context,mAppWidgetId,"spinner",trending_spinner.selectedItem.toString())

        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        if(islg){
            LgAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId)
        } else{
            AllAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId,settingsclicked)
        }


        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    public override fun onCreate(icicle: Bundle?) {
        //set theme
        MiscConstants.ApplyMyTheme(this@AllAppWidgetConfigureActivity)
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(Activity.RESULT_CANCELED)

        setContentView(R.layout.all_app_widget_configure)
        mAppWidgetText = findViewById<View>(R.id.appwidget_text) as EditText
        var adapter : ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this@AllAppWidgetConfigureActivity,
                R.array.main_tags_widget, android.R.layout.simple_spinner_item)
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        trending_spinner.adapter = adapter
        findViewById<View>(R.id.add_button).setOnClickListener(mOnClickListener)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            islg = extras.getBoolean("islg",false)
            settingsclicked = extras.getBoolean("settingsclicked",false)
        }
        changePrefName(islg)
        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        //load saved data into the fields
        var sett = loadTitlePref(this@AllAppWidgetConfigureActivity, mAppWidgetId,"tag")
        if(sett != this@AllAppWidgetConfigureActivity.getString(R.string.appwidget_text)) mAppWidgetText?.setText(sett)
        var spi = loadTitlePref(this@AllAppWidgetConfigureActivity, mAppWidgetId,"spinner")
        for(x in 0 until  adapter.count){
            var ad = adapter.getItem(x)
            if(ad == spi){
                trending_spinner.setSelection(x)
            }
        }

    }

    companion object {
        //common for lg and other widgets, the name and id are different
        private val PREFS_NAME_All = "com.steemapp.lokisveil.steemapp.HelperClasses.AllAppWidget"
        private val PREFS_NAME_LG = "com.steemapp.lokisveil.steemapp.HelperClasses.LgAppWidget"
        private var PREFS_NAME = PREFS_NAME_All
        private val PREF_PREFIX_KEY = "appwidget_"


        //call for using the lg widget name
        fun changePrefName(islg:Boolean){
            if(islg) PREFS_NAME = PREFS_NAME_LG
        }
        // Write the prefix to the SharedPreferences object for this widget
        internal fun saveTitlePref(context: Context, appWidgetId: Int,name:String, text: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putString(PREF_PREFIX_KEY + appWidgetId+"_$name", text)
            prefs.apply()
        }
        internal fun saveTitlePref(context: Context, appWidgetId: String, text: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putString(PREF_PREFIX_KEY + appWidgetId, text)
            prefs.apply()
        }

        // Read the prefix from the SharedPreferences object for this widget.
        // If there is no preference saved, get the default from a resource
        internal fun loadTitlePref(context: Context, appWidgetId: Int,name:String): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId+"_$name", null)
            return titleValue ?: context.getString(R.string.appwidget_text)
        }

        internal fun deleteTitlePref(context: Context, appWidgetId: Int,name:String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.remove(PREF_PREFIX_KEY + appWidgetId+"_$name")
            prefs.apply()
        }
    }
}

