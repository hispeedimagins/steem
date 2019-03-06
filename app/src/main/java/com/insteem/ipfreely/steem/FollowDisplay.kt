package com.insteem.ipfreely.steem

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.insteem.ipfreely.steem.Fragments.FollowerFragment
import com.insteem.ipfreely.steem.Fragments.FollowingFragment
import com.insteem.ipfreely.steem.Interfaces.GetFollowListsBack
import com.insteem.ipfreely.steem.jsonclasses.prof
import kotlinx.android.synthetic.main.activity_follow_display.*
import kotlinx.android.synthetic.main.content_follow_display.*
import java.util.*

class FollowDisplay : AppCompatActivity() , GetFollowListsBack {
    override fun FollowersDone() {
        followerFragment?.AllDone()
    }

    override fun AllDone() {
        //swipecommonactionsclass?.makeswipestop()
        followingFragment?.AllDone()
        //followingFragment?.AllDone()
    }

    override fun GetFollowersList(followerList:List<prof.Resultfp>) {
        //displayfollowing(followerList)
        followerFragment?.GetFollowersList(followerList)
    }

    override fun GetFollowingList(followinglist:List<prof.Resultfp>) {
        followingFragment?.GetFollowingList(followinglist)
    }

    internal var followingFragment : FollowingFragment? = null
    internal var followerFragment :FollowerFragment? = null
    internal var viewPagerAdapteradapter: ViewPagerAdapter? = null
    var username: String? = null
    var otherguy : String? = null
    var useOtherGuy:Boolean = false
    var key: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        MiscConstants.ApplyMyTheme(this@FollowDisplay)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow_display)
        setSupportActionBar(toolbar)


        if(intent != null && intent.extras != null){
            otherguy = intent.extras.getString(CentralConstants.OtherGuyNamePasser,null)
            useOtherGuy = intent.extras.getBoolean(CentralConstants.OtherGuyUseOtherGuyOnly,false)
        }

        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }*/


        mysetup()
    }





    fun mysetup(){
        //if (viewPager == null) viewPager = findViewById(R.id.pager)

        setupViewPager(pager)
        //tabLayout = findViewById(R.id.tabs)
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
        if (fo != null && fo is FollowingFragment) {
            followingFragment = fo
        }

        val fi = fragmentManager.findFragmentByTag(makeFragmentName(vid,1))
        if(fi != null && fi is FollowerFragment){
            followerFragment = fi
        }

        val args = Bundle()
        args.putString(CentralConstants.OtherGuyNamePasser,otherguy)
        args.putBoolean(CentralConstants.OtherGuyUseOtherGuyOnly,useOtherGuy)

        //boolean tokenisrefreshingHoldon = false;
        //args.putBoolean("isrefreshing",tokenisrefreshingHoldon)
        if (followingFragment == null) {
            followingFragment = FollowingFragment()
            followingFragment?.setArguments(args)
        }

        val argsfo = Bundle()
        argsfo.putBoolean(CentralConstants.FollowingFragmentUseFollower,true)
        argsfo.putString(CentralConstants.OtherGuyNamePasser,otherguy)
        argsfo.putBoolean(CentralConstants.OtherGuyUseOtherGuyOnly,useOtherGuy)
        if(followerFragment == null){
            followerFragment = FollowerFragment()
            followerFragment?.setArguments(argsfo)
        }


        if (viewPagerAdapteradapter == null) {

            viewPagerAdapteradapter = ViewPagerAdapter(supportFragmentManager)
            //Chat
            viewPagerAdapteradapter?.addFragment(followingFragment as FollowingFragment, "Following", CentralConstants.FragmentTagFollowing)
            viewPagerAdapteradapter?.addFragment(followerFragment as FollowerFragment,"Followers", CentralConstants.FragmentTagFollowers)
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

            //managers.beginTransaction().remove(fragment);
            // managers.beginTransaction().add(fragment,tag);
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
