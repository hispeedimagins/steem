package com.steemapp.lokisveil.steemapp.HelperClasses

import android.support.v7.widget.RecyclerView
import android.support.design.widget.FloatingActionButton



class FabHider(recyclerView: RecyclerView, fabs: FloatingActionButton) {

    //simple class to hide the fab buttons when scrolling. Have to add support for
    //scroll viewers and nested scroll viewers
    init {


        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {

                    fabs.hide()
                } else {
                    fabs.show()
                }
            }
        })
    }
}