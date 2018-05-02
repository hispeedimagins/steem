package com.steemapp.lokisveil.steemapp.HelperClasses

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.*
import com.steemapp.lokisveil.steemapp.Interfaces.arvdinterface
import com.steemapp.lokisveil.steemapp.R
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.Operation
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.VoteOperation
import org.apache.commons.lang3.Conversion
import android.widget.SeekBar.OnSeekBarChangeListener
import com.steemapp.lokisveil.steemapp.CentralConstantsOfSteem
import com.steemapp.lokisveil.steemapp.Interfaces.GlobalInterface


/**
 * Created by boot on 3/9/2018.
 */
class VoteWeightThenVote(context: Context, activity: Activity, vote:VoteOperation, adapter: arvdinterface?, position : Int, progressBar: ProgressBar?, globalInterface: GlobalInterface?) {
    val context = context
    val activity = activity
    val vote = vote
    val adaptedcomms: arvdinterface? = adapter
    val position = position
    val globalInterfaces = globalInterface
    val progressBars = progressBar
   // val toastString = toastString
//, toastString: String
    fun makeDialog(){
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Vote")
        val inflater = activity.layoutInflater
        val dialogView : View = inflater.inflate(R.layout.votedialog, null)
        alertDialogBuilder.setView(dialogView)
        val numberPicker : NumberPicker = dialogView.findViewById(R.id.dialog_number_picker)
        numberPicker.maxValue = 100
        numberPicker.minValue = 0
        numberPicker.wrapSelectorWheel = false
        val seekbar = dialogView.findViewById<SeekBar>(R.id.dialog_seekbar)
        seekbar.max = 100
        val text = dialogView.findViewById<TextView>(R.id.dialog_vote_percent)
        val sbdworth = dialogView.findViewById<TextView>(R.id.dialog_sbdworth)

       sbdworth.text = "0.000 SBD"
       text.text = "Vote percentage : 0%"

       seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

           override fun onStopTrackingTouch(seekBar: SeekBar) {
               // TODO Auto-generated method stub

           }

           override fun onStartTrackingTouch(seekBar: SeekBar) {
               // TODO Auto-generated method stub
           }

           override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
               // TODO Auto-generated method stub

               val stem = CentralConstantsOfSteem.getInstance()
               var vshare = StaticMethodsMisc.CalculateVoteRshares(StaticMethodsMisc.CalculateVotingPower(stem.profile.votingPower,stem.profile.lastVoteTime).toInt(),seekBar.progress * 100)
               var share = StaticMethodsMisc.CalculateVotingValueRshares(vshare)
               var sbshare = StaticMethodsMisc.VotingValueSteemToSd(share)
               sbdworth.text = String.format("%.3f",sbshare)  + " SBD"



               text.text = "Vote percentage : " +progress.toString() + "%"
               /*t1.setTextSize(progress)
               Toast.makeText(getApplicationContext(), progress.toString(), Toast.LENGTH_LONG).show()*/

           }
       })

        numberPicker.setOnValueChangedListener(NumberPicker.OnValueChangeListener{ picked, i, i1 ->

        })

        alertDialogBuilder.setPositiveButton("ok", DialogInterface.OnClickListener{ diin, num ->
            //vote.weight = numberPicker.value as Short
            vote.weight =  (seekbar.progress * 100).toShort()
            var list  = ArrayList<Operation>()
            list.add(vote)
            var bloc = GetDynamicAndBlock(context ,adaptedcomms,position,list,"Upvoted ${vote.permlink.link}", MyOperationTypes.vote,progressBars,globalInterfaces)
            bloc.GetDynamicGlobalProperties()
            //val runs = GeneralRequestsFeedIntoConstants(context)

        })

        alertDialogBuilder.setNegativeButton("No", DialogInterface.OnClickListener { diin, num ->

        })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}