package com.steemapp.lokisveil.steemapp


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_comment_notifications.*
import kotlinx.android.synthetic.main.content_comment_notifications.*
import java.util.*

class CommentNotifications : AppCompatActivity() {
    var username : String? = null
    var key : String? = null

    internal var commentNotificationsSteemitFragment : CommentNotificationsSteemitFragment? = null
    internal var repliesNotificationsFragment : RepliesNotificationsFragment? = null
    internal var viewPagerAdapteradapter: ViewPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        MiscConstants.ApplyMyThemeArticle(this@CommentNotifications)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_notifications)
        setSupportActionBar(toolbar)
        val sharedPreferences = applicationContext.getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences.getString(CentralConstants.username, null)
        key = sharedPreferences.getString(CentralConstants.key, null)
        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }*/
        mysetup()
    }

    fun mysetup(){
        //if (pager == null) viewPager = findViewById(R.id.pager)

        setupViewPager(pager)
        //tabs = findViewById(R.id.tabs)
        tabs.setupWithViewPager(pager)

        setTabViewItems()
    }


    private fun setTabViewItems() {
        //tabLayout.getTabAt(0).setIcon(R.drawable.ic_chat_white);
        //tabLayout.getTabAt(1).setIcon(R.drawable.ic_question_answer_white);
        //tabLayout?.getTabAt(2)!!.setIcon(R.drawable.ic_person_pin_white_48dp)
        // tabLayout?.getTabAt(3)!!.setIcon(R.drawable.ic_notifications_none_white_24dp)
    }

    private fun makeFragmentName(viewPagerId: Int?, index: Int): String {
        return "android:switcher:$viewPagerId:$index"
    }

    private fun setupViewPager(viewPager: ViewPager?) {

        val vid = viewPager?.id

        val fragmentManager = supportFragmentManager

        val fo = fragmentManager.findFragmentByTag(makeFragmentName(vid, 0))
        if (fo != null && fo is CommentNotificationsSteemitFragment) {
            commentNotificationsSteemitFragment = fo
        }

        val fi = fragmentManager.findFragmentByTag(makeFragmentName(vid,1))
        if(fi != null && fi is RepliesNotificationsFragment){
            repliesNotificationsFragment = fi
        }

        val args = Bundle()
        if (commentNotificationsSteemitFragment == null) {
            commentNotificationsSteemitFragment = CommentNotificationsSteemitFragment()
            commentNotificationsSteemitFragment?.setArguments(args)
        }
        if(repliesNotificationsFragment == null){
            repliesNotificationsFragment = RepliesNotificationsFragment()
            repliesNotificationsFragment?.setArguments(args)
        }


        if (viewPagerAdapteradapter == null) {

            viewPagerAdapteradapter = ViewPagerAdapter(supportFragmentManager)
            //Chat
            viewPagerAdapteradapter?.addFragment(commentNotificationsSteemitFragment as CommentNotificationsSteemitFragment, "Your comments", CentralConstants.FragmentTagCommentsNoti)
            viewPagerAdapteradapter?.addFragment(repliesNotificationsFragment as RepliesNotificationsFragment,"Replies",CentralConstants.FragmentTagReplies)
            viewPager?.adapter = viewPagerAdapteradapter
        }


    }


    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList = ArrayList<String>()
        private var managers: FragmentManager? = null

        private val mFragmentTags = HashMap<Int, String>()

        init {
            managers = manager
        }

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String, tag: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }

        /*@Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object obj = super.instantiateItem(container, position);
            if (obj instanceof Fragment) {
                Fragment f = (Fragment) obj;
                String tag = f.getTag();
                mFragmentTags.put(position, tag);
            }
            return obj;
        }*/


    }

}
