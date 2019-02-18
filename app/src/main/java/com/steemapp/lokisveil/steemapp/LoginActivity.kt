package com.steemapp.lokisveil.steemapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.steemapp.lokisveil.steemapp.HelperClasses.GeneralRequestsFeedIntoConstants
import com.steemapp.lokisveil.steemapp.HelperClasses.RegisterJob
import com.steemapp.lokisveil.steemapp.Interfaces.GlobalInterface
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.follow_progress.*


class LoginActivity : AppCompatActivity() , GlobalInterface{
    private var followersisdone = false
    private var followingisdone = false
    lateinit var runs: GeneralRequestsFeedIntoConstants


    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Set up the login form.
        //populateAutoComplete()
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        email_sign_in_button.setOnClickListener { attemptLogin() }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {

        // Reset errors.
        email.error = null
        password.error = null

        // Store values at the time of the login attempt.
        // use trim to remove any unwanted spaces
        val emailStr = email.text.toString().trim()
        val passwordStr = password.text.toString().trim()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)

            val sharedPreferences = applicationContext.getSharedPreferences(CentralConstants.sharedprefname, 0)
            val edit = sharedPreferences.edit()
            edit.putString(CentralConstants.username,emailStr)
            edit.putString(CentralConstants.key,passwordStr)
            edit.apply()
            //init the general feed class
            runs = GeneralRequestsFeedIntoConstants(this@LoginActivity,application)

            //start the follow count function
            runs.GetFollowCount(emailStr,null,null,null)
            email_login_form.removeAllViews()
            //register firebase job
            var reg = RegisterJob.reg(this)

        }
    }

    private fun allDone(){
        if(followersisdone && followingisdone){

            finishActivity()
        }
    }

    fun finishActivity(){
        val ou = Intent()
        ou.putExtra("TokenMine", "tokenfromuploading")
        setResult(Activity.RESULT_OK, ou)

        runOnUiThread {
            finish()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private fun showProgress(show: Boolean) {
        // simply show
        // and hide the relevant UI components.
        login_progress.visibility = if (show) View.VISIBLE else View.GONE
        login_form.visibility = if (show) View.GONE else View.VISIBLE
    }



    override fun followersDone(){
        allFollowDone()
    }
    override fun followingDone(){
        allFollowDone()
    }

    /**
     * callback tell us we can hide the progress showing ui now
     */
    fun allFollowDone(){
        if(runs.gotTillNow == runs.totalSize){
            progress_foll.visibility = View.GONE
            finishActivity()
        }
    }


    override fun followerProgress(got:Int,total:Int){
        //calculate percentage
        var pro = (got/total) * 100
        progressNow.progress = pro
        progressDone.text = "syncing people : $got/${runs.totalSize}"
    }


    override fun followingProgress(got:Int,total:Int){
        var pro = (got/total) * 100
        progressNow.progress = pro
        progressDone.text = "syncing people : $got/${runs.totalSize}"
    }

    override fun followHasChanged(){
        login_progress.visibility = View.GONE
        progress_foll.visibility = View.VISIBLE
        progressDone.text = "syncing followers : 0/${runs.totalSize}"
        runs.refreshFollowDbNow()

    }

    override fun notifyRequestMadeSuccess() {
        /*if(!followingisdone){
            followingisdone = true
        }
        else if(!followersisdone){
            followersisdone = true
        }
        else{
            allDone()
        }*/
    }

    override fun notifyRequestMadeError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

}
