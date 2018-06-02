package com.steemapp.lokisveil.steemapp.HelperClasses

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.steemapp.lokisveil.steemapp.CentralConstantsOfSteem
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Databases.beneficiariesDatabase
import com.steemapp.lokisveil.steemapp.Interfaces.BeneficiaryAddInterface
import com.steemapp.lokisveil.steemapp.Interfaces.GlobalInterface
import com.steemapp.lokisveil.steemapp.Interfaces.arvdinterface
import com.steemapp.lokisveil.steemapp.MiscConstants
import com.steemapp.lokisveil.steemapp.MyViewHolders.beneficiaryAddClasspackage
import com.steemapp.lokisveil.steemapp.R
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.Operation
import java.util.*

//popup class to easily add a beneficiary
class AddABeneficiary(context:Context,activity:Activity) {

    //val context = context
    //val activity = activity

    //val adaptedcomms: arvdinterface? = adapter
    //val position = position
    val beneficiaryAddInterface : BeneficiaryAddInterface? = if(context is BeneficiaryAddInterface) context else null
    //val progressBars = progressBar
    //var votepercentused = 0

    init {
        //alert dialogue using app theme set
        var alertDialog = AlertDialog.Builder(MiscConstants.ApplyMyThemeRet(context))
        alertDialog.setTitle("Add a beneficiary")
        val inflater = activity.layoutInflater
        val dialogView : View = inflater.inflate(R.layout.beneficiaryaddview, null)
        alertDialog.setView(dialogView)
        var vh = beneficiaryAddClasspackage(dialogView)
        vh.dialog_seekbar?.progress = 5
        vh.article_percent?.text = "2.5"
        vh.dialog_seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                //vh.article?.percent = progress
                vh.article_percent?.text = (progress.toFloat()/2).toString()
            }
        })

        alertDialog.setPositiveButton("ok", DialogInterface.OnClickListener{ diin, num ->

            /*var nam = vh.article_name?.text.toString()
            if(nam.isNotEmpty() && nam.isNotBlank() && nam.length > 1 && vh.dialog_seekbar?.progress!! > 0){
                vh.article = FeedArticleDataHolder.beneficiariesDataHolder(
                        vh.article_name.toString(),
                        vh.dialog_seekbar?.progress!!,
                        0,
                        if(vh.artile_default?.isChecked()!!) 1 else 0,
                        vh.article_tag.toString(),
                        Date().time,
                        "",
                        0,
                        0,
                        dbid = 0
                )
                var db = beneficiariesDatabase(context)
                db.Insert(vh.article!!)
            }
            else{
                if(nam.isEmpty() || nam.isBlank()){
                    vh.article_name_tip?.error = "Name cannot be blank"
                    //vh.article_name_tip?.isErrorEnabled = true
                    //Toast.makeText(context,"Username cannot be empty",Toast.LENGTH_LONG).show()
                } else if(nam.length < 1){
                    vh.article_name_tip?.error = "Username has to be more than one character"
                    //Toast.makeText(context,"Username has to be more than one character",Toast.LENGTH_LONG).show()
                } else if(vh.dialog_seekbar?.progress!! <= 0){
                    //vh.article_tags_tip?.error =
                    //Toast.makeText(context,"Percentage has to be greater than 0",Toast.LENGTH_LONG).show()
                }
            }*/


        })

        alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { diin, num ->

        })
        val alertDialogs = alertDialog.create()
        alertDialogs.show()


        // we override this because if we don't the popup closes even if there was an error
        //hence this is where we check for errors, if not then close ourselves
        alertDialogs.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener {

            var nam = vh.article_name?.text.toString()
            if(nam.isNotEmpty() && nam.isNotBlank() && nam.length > 1 && vh.dialog_seekbar?.progress!! > 0){
                vh.article = FeedArticleDataHolder.beneficiariesDataHolder(
                        nam,
                        vh.dialog_seekbar?.progress!!,
                        0,
                        if(vh.artile_default?.isChecked()!!) 1 else 0,
                        vh.article_tag?.text.toString(),
                        Date().time,
                        "",
                        0,
                        0,
                         0
                )
                //inserting into db
                var db = beneficiariesDatabase(context)
                var ins = db.Insert(vh.article!!)
                //closing the alert
                alertDialogs.dismiss()
                beneficiaryAddInterface?.AddedSuccessfully(ins)
            }
            else{
                //else errors are set
                if(nam.isEmpty() || nam.isBlank()){
                    vh.article_name_tip?.error = "Name cannot be blank"
                    //vh.article_name_tip?.isErrorEnabled = true
                    //Toast.makeText(context,"Username cannot be empty",Toast.LENGTH_LONG).show()
                } else if(nam.length < 1){
                    vh.article_name_tip?.error = "Username has to be more than one character"
                    //Toast.makeText(context,"Username has to be more than one character",Toast.LENGTH_LONG).show()
                } else if(vh.dialog_seekbar?.progress!! <= 0){
                    //vh.article_tags_tip?.error =
                    Toast.makeText(context,"Percentage has to be greater than 0",Toast.LENGTH_LONG).show()
                }
            }


            /*val wantToCloseDialog = false
            //Do stuff, possibly set wantToCloseDialog to true then...
            if (wantToCloseDialog)
                dialog.dismiss()*/
            //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
        })
    }
}