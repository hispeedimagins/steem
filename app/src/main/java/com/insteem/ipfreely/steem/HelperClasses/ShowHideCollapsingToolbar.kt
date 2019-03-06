package com.insteem.ipfreely.steem.HelperClasses



import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.LinearLayout
import com.google.android.material.appbar.AppBarLayout

/**
 * Created by boot on 3/20/2018.
 */
class ShowHideCollapsingToolbar(appbar: AppBarLayout, main_linearlayout_title:LinearLayout, toolbar_linear:LinearLayout) {
    private val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f
    //private val PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f
    private val PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.9f
    private val PERCENTAGE_TO_HIDE_TITLE_DETAILS_two = 0.82f
    private val ALPHA_ANIMATIONS_DURATION : Long = 200
    private var mIsTheTitleVisible = false
    private var mIsTheTitleContainerVisible = true
    private var main_linearlayout_title = main_linearlayout_title
    private var toolbar_linear = toolbar_linear

    init {
        val ob = object:AppBarLayout.OnOffsetChangedListener{
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if(appBarLayout!= null && appBarLayout.totalScrollRange != 0){
                    val maxScroll = appBarLayout.totalScrollRange
                    val percentage = (Math.abs(verticalOffset).toDouble() / maxScroll.toDouble())
                    // Log.d("verticalOffset ",verticalOffset.toString() + ", maxScroll " + maxScroll.toString() +", percentage " + percentage.toString())
                    handleAlphaOnTitle(percentage)
                    handleToolbarTitleVisibility(percentage)
                }
            }
        }
        appbar.addOnOffsetChangedListener(ob)
    }


    private fun handleToolbarTitleVisibility(percentage: Double) {
        //Log.d("percent toolbar   ",percentage.toString())
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(toolbar_linear, ALPHA_ANIMATIONS_DURATION, View.VISIBLE)
                mIsTheTitleVisible = true
            }

        }
        else if (percentage in PERCENTAGE_TO_HIDE_TITLE_DETAILS_two..PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(toolbar_linear, ALPHA_ANIMATIONS_DURATION, View.VISIBLE)
                mIsTheTitleVisible = true
            }
        }
        else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(toolbar_linear, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE)
                mIsTheTitleVisible = false
            }
        }
    }

    private fun handleAlphaOnTitle(percentage: Double) {
       // Log.d("percent should be ",percentage.toString()+" "+PERCENTAGE_TO_HIDE_TITLE_DETAILS)
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(main_linearlayout_title, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE)
                mIsTheTitleContainerVisible = false
            }

        }

        else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(main_linearlayout_title, ALPHA_ANIMATIONS_DURATION, View.VISIBLE)
                mIsTheTitleContainerVisible = true
            }
        }
    }

    fun startAlphaAnimation(v: View, duration: Long, visibility: Int) {
        val alphaAnimation = if (visibility == View.VISIBLE)
            AlphaAnimation(0f, 1f)
        else
            AlphaAnimation(1f, 0f)

        alphaAnimation.setDuration(duration)
        alphaAnimation.setFillAfter(true)
        v.startAnimation(alphaAnimation)
    }
}