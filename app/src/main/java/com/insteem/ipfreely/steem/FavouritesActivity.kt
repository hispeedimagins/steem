package com.insteem.ipfreely.steem

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.insteem.ipfreely.steem.Databases.FavouritesDatabase
import com.insteem.ipfreely.steem.Enums.AdapterToUseFor
import com.insteem.ipfreely.steem.HelperClasses.swipecommonactionsclass
import kotlinx.android.synthetic.main.activity_favourites.*
import kotlinx.android.synthetic.main.content_favourites.*
import java.util.*

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
