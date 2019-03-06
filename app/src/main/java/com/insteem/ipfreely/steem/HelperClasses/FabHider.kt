package com.insteem.ipfreely.steem.HelperClasses


import android.view.View
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FabHider(recyclerView: RecyclerView?, fabs: FloatingActionButton?,nestedscroll:NestedScrollView? = null) {


    //saves the old scrollvalue for scrollview
    var oldscroll = 0

    //this is used when we have scrollview
    constructor(recyclerView: RecyclerView?, fabs: FloatingActionButton?, otherfab: FloatingActionMenu?, scrollView: ScrollView?):this(recyclerView,fabs){
        if(scrollView != null){
            //the other constructer for scrollview will not work on older versions
            //to maintain compatibility we use this and store the value to evaluate ourselves
            scrollView.viewTreeObserver.addOnScrollChangedListener {
                 // For ScrollView
                //val scrollX = scrollView.scrollX // For HorizontalScrollView


                //we use view invisible as otherfab is not exactly a FAB button so
                //does not have the hide function
                if(scrollView.scrollY > oldscroll){
                    otherfab?.visibility = View.INVISIBLE
                    oldscroll = scrollView.scrollY

                } else if(oldscroll > scrollView.scrollY) {
                    otherfab?.visibility = View.VISIBLE
                    oldscroll = scrollView.scrollY

                } else if(oldscroll == scrollView.scrollY){
                    otherfab?.visibility = View.VISIBLE
                    oldscroll = scrollView.scrollY
                }


                // DO SOMETHING WITH THE SCROLL COORDINATES
            };
        }
    }
    //simple class to hide the fab buttons when scrolling. Have to add support for
    //scroll viewers and nested scroll viewers
    init {

        if(recyclerView != null){
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {

                        fabs?.hide()
                    } else {
                        fabs?.show()
                    }
                }
            })
        } else if(nestedscroll != null){

            //Is used for nested scrollview
            nestedscroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                /*if(scrollY > 0){
                    fabs?.hide()
                } else {
                    fabs?.show()
                }*/
                if (scrollY > oldScrollY) {
                    //Log.i(TAG, "Scroll DOWN")
                    fabs?.hide()
                }
                if (scrollY < oldScrollY) {
                    //Log.i(TAG, "Scroll UP")
                    fabs?.show()
                }

                if (scrollY == 0) {
                    //Log.i(TAG, "TOP SCROLL")
                }

                if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                    //Log.i(TAG, "BOTTOM SCROLL")
                }
            })
        }

    }
}