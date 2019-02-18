package com.steemapp.lokisveil.steemapp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import com.steemapp.lokisveil.steemapp.Databases.drafts
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.HelperClasses.swipecommonactionsclass
import kotlinx.android.synthetic.main.activity_draft.*
import kotlinx.android.synthetic.main.content_draft.*
import java.util.*

class DraftActivity : AppCompatActivity() {
    private var adapter: AllRecyclerViewAdapter? = null
    private var activity: Context? = null
    //internal var recyclerView: RecyclerView? = null
    var username : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        MiscConstants.ApplyMyTheme(this@DraftActivity)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draft)
        setSupportActionBar(toolbar)

        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }*/
        //recyclerView = list
        var sw  = swipecommonactionsclass(activity_feed_swipe_refresh_layout)
        adapter = AllRecyclerViewAdapter(this, ArrayList(), list_draft, null, AdapterToUseFor.draft)
        //var sw = swipecommonactionsclass(activity_feed_swipe_refresh_layout_favs)
        val db = drafts(applicationContext)
        activity_feed_swipe_refresh_layout.setOnRefreshListener {
            adapter?.draftHelperFunctionss?.add(db.GetAllQuestions())
            //stop the loading bar once finished
            sw.makeswipestopDef()
        }
        //adapter?.setEmptyView(view?.findViewById(R.id.toDoEmptyView))

        list_draft.itemAnimator = DefaultItemAnimator()
        list_draft.adapter = adapter
        val sharedPreferences = getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences?.getString(CentralConstants.username, null)
        //key = sharedPreferences?.getString(CentralConstants.key, null)
        //display(CentralConstantsOfSteem.getInstance().jsonArray)


        adapter?.draftHelperFunctionss?.add(db.GetAllQuestions())
        //adapter?.notifyDataSetChanged()
    }

}
