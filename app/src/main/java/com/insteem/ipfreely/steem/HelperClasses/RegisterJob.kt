package com.insteem.ipfreely.steem.HelperClasses

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.firebase.jobdispatcher.*
import com.insteem.ipfreely.steem.NotificationService


//class for registering and unregistering firebase jobs for android
// power efficient
class RegisterJob {


    companion object {
        val tag = "my-unique-tag"
        fun unreg(context: Context){
            val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))
            var un = dispatcher.cancel(tag)
            Log.d("firebasedispatcher",un.toString())
        }

        fun reg(context: Context){
            val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))
            val job = createJob(dispatcher)

            val dis = dispatcher.schedule(job)
            Log.d("firebasedispatcher",dis.toString())
            if (dis != FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS) {
                Log.d("TAG", "Error while creating JOB ")
            }
        }


        fun createJob(dispatcher: FirebaseJobDispatcher): Job {

            val myExtrasBundle = Bundle()
            myExtrasBundle.putString("some_key", "some_value")

//dispatcher.mustSchedule(myJob);


            return dispatcher.newJobBuilder()
                    // the JobService that will be called
                    .setService(NotificationService::class.java)
                    // uniquely identifies the job
                    .setTag(tag)
                    // one-off job
                    .setRecurring(true)
                    // don't persist past a device reboot
                    .setLifetime(Lifetime.FOREVER)
                    // start between 0 and 60 seconds from now
                    .setTrigger(Trigger.executionWindow(600 * 2, 600 * 3))
                    //.setTrigger(Trigger.executionWindow(0, 60))

                    // don't overwrite an existing job with the same tag
                    .setReplaceCurrent(false)
                    //.setReplaceCurrent(true)
                    // retry with exponential backoff
                    .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                    // constraints that need to be satisfied for the job to run
                    .setConstraints(
                            Constraint.ON_ANY_NETWORK

                            // only run on an unmetered network
                            //Constraint.ON_UNMETERED_NETWORK,
                            // only run when the device is charging
                            //Constraint.DEVICE_CHARGING

                    )
                    .setExtras(myExtrasBundle)
                    .build()
        }
    }


}