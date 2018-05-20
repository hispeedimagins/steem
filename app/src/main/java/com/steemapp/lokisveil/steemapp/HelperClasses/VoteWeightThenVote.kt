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
import com.steemapp.lokisveil.steemapp.MiscConstants
import java.util.*


/**
 * Created by boot on 3/9/2018.
 */
class VoteWeightThenVote(context: Context, activity: Activity, vote:VoteOperation, adapter: arvdinterface?, position : Int, progressBar: ProgressBar?, globalInterface: GlobalInterface?): GlobalInterface {
    override fun notifyRequestMadeSuccess() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //reassign variables for calculating power in callback so you get the live value on each vote now
        val stem = CentralConstantsOfSteem.getInstance()
        stem.profile.lastVoteTimeLong = Date().time
        var per = Math.abs(votepercentused)
        var subper = (per * 2) / 100
        stem.profile.votingPower -= subper

    }

    override fun notifyRequestMadeError() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getObjectMine(): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContextMine(): Context {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActivityMine(): Activity {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val context = context
    val activity = activity
    val vote = vote
    val adaptedcomms: arvdinterface? = adapter
    val position = position
    val globalInterfaces = globalInterface
    val progressBars = progressBar
    var votepercentused = 0
   // val toastString = toastString
//, toastString: String
    fun makeDialog(){
        //val alertDialogBuilder = AlertDialog.Builder(context)
        val alertDialogBuilder = AlertDialog.Builder(MiscConstants.ApplyMyThemeRet(context))
        alertDialogBuilder.setTitle("Vote")
        val inflater = activity.layoutInflater
        val dialogView : View = inflater.inflate(R.layout.votedialog, null)
        alertDialogBuilder.setView(dialogView)
        val numberPicker : NumberPicker = dialogView.findViewById(R.id.dialog_number_picker)
        numberPicker.maxValue = 100
        numberPicker.minValue = 0
        numberPicker.wrapSelectorWheel = false
        val seekbar = dialogView.findViewById<android.support.v7.widget.AppCompatSeekBar>(R.id.dialog_seekbar)
       //cannot use as it requires min api 26 we are at 19
       //seekbar.min = -100
        seekbar.max = 200
       seekbar.progress = 100
       //seekbar.progress = 0
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

               var seekbp = getvoteseek(seekBar.progress)

               val stem = CentralConstantsOfSteem.getInstance()
               var com : Long = 0
               if(stem.profile.lastVoteTimeLong == com){
                   stem.profile.lastVoteTimeLong = StaticMethodsMisc.ConvertSteemDateToDate(stem.profile.lastVoteTime).time
               }
               var vshare = StaticMethodsMisc.CalculateVoteRshares(StaticMethodsMisc.CalculateVotingPower(stem.profile.votingPower,stem.profile.lastVoteTimeLong).toInt(),seekbp * 100)
               var share = StaticMethodsMisc.CalculateVotingValueRshares(vshare)
               var sbshare = StaticMethodsMisc.VotingValueSteemToSd(share)
               sbdworth.text = String.format("%.3f",sbshare)  + " SBD"



               text.text = "Vote percentage : " +seekbp.toString() + "%"
               /*t1.setTextSize(progress)
               Toast.makeText(getApplicationContext(), progress.toString(), Toast.LENGTH_LONG).show()*/

           }
       })

        numberPicker.setOnValueChangedListener(NumberPicker.OnValueChangeListener{ picked, i, i1 ->

        })

        alertDialogBuilder.setPositiveButton("ok", DialogInterface.OnClickListener{ diin, num ->
            //vote.weight = numberPicker.value as Short
            votepercentused = (getvoteseek(seekbar.progress))
            vote.weight =  (getvoteseek(seekbar.progress) * 100).toShort()
            var list  = ArrayList<Operation>()
            list.add(vote)
            var bloc = GetDynamicAndBlock(context ,adaptedcomms,position,list,"Upvoted ${vote.permlink.link}", MyOperationTypes.vote,progressBars,globalInterfaces,this)
            bloc.GetDynamicGlobalProperties()
            //val runs = GeneralRequestsFeedIntoConstants(context)

        })

        alertDialogBuilder.setNegativeButton("No", DialogInterface.OnClickListener { diin, num ->

        })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun getvoteseek(progress:Int):Int{
        var seekbp = progress
        if(seekbp == 100){
            seekbp = 0
        }
        else if(seekbp < 100){

            seekbp += -100
        }
        else if(seekbp > 100){
            seekbp -= 100
        }

        return seekbp
    }
}