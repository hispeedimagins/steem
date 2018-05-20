package com.steemapp.lokisveil.steemapp.HelperClasses

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.Spinner
import com.steemapp.lokisveil.steemapp.CentralConstantsOfSteem
import com.steemapp.lokisveil.steemapp.Interfaces.TagsInterface
import com.steemapp.lokisveil.steemapp.MiscConstants
import com.steemapp.lokisveil.steemapp.R
import kotlinx.android.synthetic.main.dialog_trending_select.view.*

class TagRequestHelper(context: Context,activity:Activity) {

    val tagsInterface : TagsInterface? = if(context is TagsInterface) context else null
    var alertDialog : android.support.v7.app.AlertDialog? = null

    init {
        //val alertDialogBuilder = android.support.v7.app.AlertDialog.Builder(context)
        val alertDialogBuilder = android.support.v7.app.AlertDialog.Builder(MiscConstants.ApplyMyThemeRet(context))
        alertDialogBuilder.setTitle("Go to trending")
        val inflater = activity.layoutInflater

        val dialogView : View = inflater.inflate(R.layout.dialog_trending_select, null)
        alertDialogBuilder.setView(dialogView)
        var spinnerTrending : Spinner = dialogView.findViewById(R.id.trending_spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        var adapter : ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(context,
                R.array.main_tags, android.R.layout.simple_spinner_item)
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTrending.adapter = adapter
// Apply the adapter to the spinner

        //var spinnerTags : Spinner = dialogView.findViewById(R.id.tags_spinner)
        val tags_autocom : android.support.v7.widget.AppCompatAutoCompleteTextView = dialogView.findViewById(R.id.tags_autocom)
        val adapterAuto = ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, CentralConstantsOfSteem.getInstance().trendingTags)
        //val textView = findViewById(R.id.countries_list) as AutoCompleteTextView
        tags_autocom.setAdapter(adapterAuto)
        val mainremo : LinearLayout = dialogView.findViewById(R.id.mainremo)
        var sdd = EditorInfo()
        sdd.actionId = 6
        sdd.actionLabel = "Ok"
        sdd.imeOptions = EditorInfo.IME_ACTION_DONE
        //tags_autocom.onCreateInputConnection(null)



        alertDialogBuilder.setPositiveButton("ok", DialogInterface.OnClickListener{ diin, num ->
            var req = "get_discussions_by_"
            var tagte = tags_autocom.text.toString()
            var sptl = spinnerTrending.selectedItem.toString()

            //mainremo.removeAllViews()
            req += if(sptl == "new"){
                "created"
            }
            else{
                sptl
            }

            alertDialog?.dismiss()

            tagsInterface?.okclicked(sptl,tagte,"20",req)

        })

        alertDialogBuilder.setNegativeButton("No", DialogInterface.OnClickListener { diin, num ->
            alertDialog?.dismiss()
        })
        alertDialog = alertDialogBuilder.create()

        alertDialog?.show()

    }

}