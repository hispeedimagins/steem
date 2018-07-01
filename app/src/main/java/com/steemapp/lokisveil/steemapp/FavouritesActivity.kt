package com.steemapp.lokisveil.steemapp

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import com.steemapp.lokisveil.steemapp.Databases.FavouritesDatabase
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.HelperClasses.swipecommonactionsclass

import kotlinx.android.synthetic.main.activity_favourites.*
import kotlinx.android.synthetic.main.content_favourites.*

import java.util.ArrayList

class FavouritesActivity : AppCompatActivity() {
    private var adapter: AllRecyclerViewAdapter? = null
    private var activity: Context? = null
    internal var recyclerView: RecyclerView? = null
    var username : String? = null
    var otherguy : String? = null
    var useOtherGuyOnly : Boolean? = false
    var key : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        MiscConstants.ApplyMyTheme(this@FavouritesActivity)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)
        setSupportActionBar(toolbar)

        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }*/
        recyclerView = list
        var sw  = swipecommonactionsclass(activity_feed_swipe_refresh_layout_favs)
        adapter = AllRecyclerViewAdapter(this, ArrayList(), recyclerView as RecyclerView, dateanim, AdapterToUseFor.feed)
        //var sw = swipecommonactionsclass(activity_feed_swipe_refresh_layout_favs)
        val db = FavouritesDatabase(applicationContext)
        activity_feed_swipe_refresh_layout_favs.setOnRefreshListener {
            adapter?.feedHelperFunctions?.add(db.GetAllQuestions())
        }
        //adapter?.setEmptyView(view?.findViewById(R.id.toDoEmptyView))

        recyclerView?.itemAnimator = DefaultItemAnimator()
        recyclerView?.adapter = adapter
        val sharedPreferences = getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences?.getString(CentralConstants.username, null)
        key = sharedPreferences?.getString(CentralConstants.key, null)
        //display(CentralConstantsOfSteem.getInstance().jsonArray)


        adapter?.feedHelperFunctions?.add(db.GetAllQuestions())
    }



}
