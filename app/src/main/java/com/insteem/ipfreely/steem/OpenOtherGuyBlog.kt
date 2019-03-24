package com.insteem.ipfreely.steem

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.insteem.ipfreely.steem.Fragments.MyFeedFragment
import com.insteem.ipfreely.steem.HelperClasses.*
import com.insteem.ipfreely.steem.Interfaces.GlobalInterface
import com.insteem.ipfreely.steem.SteemBackend.Config.Enums.MyOperationTypes
import com.insteem.ipfreely.steem.jsonclasses.ProfileJsonSteemit
import com.insteem.ipfreely.steem.jsonclasses.prof
import kotlinx.android.synthetic.main.activity_open_other_guy_blog.*
import kotlinx.android.synthetic.main.fragment_open_other_guy_blog.view.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class OpenOtherGuyBlog : AppCompatActivity() ,GlobalInterface {
    override fun notifyRequestMadeError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getObjectMine(): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContextMine(): Context {
        return applicationContext
    }

    override fun getActivityMine(): Activity {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun notifyRequestMadeSuccess() {
        if(articlepop != null){
            if(articlepop?.followInternal  == MyOperationTypes.follow){
                fab.setImageResource(R.drawable.ic_person_remove_black_24px)
                articlepop = ArticlePopUpMenu(applicationContext,null,null,null,MyOperationTypes.unfollow,username,otherguy,null,0,null,this@OpenOtherGuyBlog)

            }
            else if(articlepop?.followInternal  == MyOperationTypes.unfollow){
                fab.setImageResource(R.drawable.ic_person_add_black_24px)
                articlepop = ArticlePopUpMenu(applicationContext,null,null,null,MyOperationTypes.follow,username,otherguy,null,0,null,this@OpenOtherGuyBlog)

            }
        }

    }

    var username: String? = null
    var otherguy : String? = null
    var key: String? = null
    internal var tabLayout: TabLayout? = null
    internal var viewPager: ViewPager? = null
    internal var viewPagerAdapteradapter: ViewPagerAdapter? = null
    internal var articleuser = ""
    internal var articletag = ""
    internal var articlepermlink = ""
    internal var blogFragment :MyFeedFragment? = null
    var articlepop : ArticlePopUpMenu? = null
    var followCount: prof.FollowCount? = null
    var followers: List<prof.Resultfp> = ArrayList()
    var following: MutableList<prof.Resultfp> = ArrayList()
    var animatedVec : AnimatedVectorDrawableCompat? = null

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
   // private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        MiscConstants.ApplyMyTheme(this@OpenOtherGuyBlog)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_other_guy_blog)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        animatedVec = AnimatedVectorDrawableCompat.create(this@OpenOtherGuyBlog,R.drawable.animated_loader)
        toolbar_layout.title =  ""
        toolbar.title = ""
        val sharedPreferences = applicationContext.getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences.getString(CentralConstants.username, null)
        //key = sharedPreferences.getString(CentralConstants.key, null)
        /*appbar.addOnOffsetChangedListener({appBarLayout, verticalOffset ->
            if(appBarLayout.totalScrollRange != 0){
                val maxScroll = appBarLayout.totalScrollRange
                val percentage = (Math.abs(verticalOffset).toDouble() / maxScroll.toDouble())
                Log.d("verticalOffset ",verticalOffset.toString() + ", maxScroll " + maxScroll.toString() +", percentage " + percentage.toString())
                handleAlphaOnTitle(percentage)
                handleToolbarTitleVisibility(percentage)
            }

        })
        startAlphaAnimation(toolbar_linear, 0, View.INVISIBLE)*/
        val runs = GeneralRequestsFeedIntoConstants(applicationContext)
        runs.RunThemAll()


        var too : ShowHideCollapsingToolbar = ShowHideCollapsingToolbar(appbar,main_linearlayout_title,toolbar_linear)
        too.startAlphaAnimation(toolbar_linear,0,View.INVISIBLE)
        if(intent != null && intent.extras != null){
            otherguy = intent.extras.getString(CentralConstants.OtherGuyNamePasser,null)
        }
        if(intent != null && intent.data != null){
            var d = intent.data.toString()
            otherguy = d.split("@")[1]
        }

        if(otherguy != null){

        }
        /*var params : CoordinatorLayout.LayoutParams = pfp_outer.layoutParams as CoordinatorLayout.LayoutParams
        var avatarImageBehavior = AvatarImageBehavior(applicationContext,null)
        params.behavior = avatarImageBehavior
        pfp_outer.requestLayout()*/
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        /*mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter*/
        GetProfile()
        getFollowCount()
        mysetup()

        //var con = FollowApiConstants.getInstance()

        if(StaticMethodsMisc.CheckFollowing(otherguy,applicationContext) ){
            fab.setImageResource(R.drawable.ic_person_remove_black_24px)
            articlepop = ArticlePopUpMenu(applicationContext,null,null,null,MyOperationTypes.unfollow,username,otherguy,null,0,null,this@OpenOtherGuyBlog)

        }
        else {
            fab.setImageResource(R.drawable.ic_person_add_black_24px)
            articlepop = ArticlePopUpMenu(applicationContext,null,null,null,MyOperationTypes.follow,username,otherguy,null,0,null,this@OpenOtherGuyBlog)

        }

        fab.setOnClickListener { view ->
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()*/
            articlepop?.postCustomJson()
        }


    }


    /*private fun handleToolbarTitleVisibility(percentage: Double) {
        Log.d("percent toolbar   ",percentage.toString())
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
        Log.d("percent should be ",percentage.toString()+" "+PERCENTAGE_TO_HIDE_TITLE_DETAILS)
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
    }*/




    fun mysetup(){
        if (viewPager == null) viewPager = container

        setupViewPager(viewPager)
        tabLayout = tabs
        tabLayout?.setupWithViewPager(viewPager)

        setTabViewItems()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_open_other_guy_blog, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_open_other_guy_blog, container, false)
            rootView.section_label.text = getString(R.string.section_format, arguments?.getInt(ARG_SECTION_NUMBER))
            return rootView
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
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

        /*val fo = fragmentManager.findFragmentByTag(makeFragmentName(vid, 0))
        if (fo != null && fo is FeedFragment) {
            feedFragment = fo as FeedFragment
        }*/

        val fi = fragmentManager.findFragmentByTag(makeFragmentName(vid,1))
        if(fi != null && fi is MyFeedFragment){
            blogFragment = fi as MyFeedFragment
        }

        /*val ft = fragmentManager.findFragmentByTag(makeFragmentName(vid, 1))
        if (ft != null && ft is questionslistFragment) {
            questionslistFragmentf = ft as questionslistFragment
        }

        val fth = fragmentManager.findFragmentByTag(makeFragmentName(vid, 2))
        if (fth != null && fth is MyPeopleFragment) {
            myPeopleFragmentf = fth as MyPeopleFragment
        }

        val ff = fragmentManager.findFragmentByTag(makeFragmentName(vid, 3))
        if (ff != null && ff is NotificationsFragment) {
            notificationsFragment = ff as NotificationsFragment
        }*/

        val args = Bundle()

        //boolean tokenisrefreshingHoldon = false;
        args.putString(CentralConstants.OtherGuyNamePasser,otherguy)
        args.putBoolean(CentralConstants.OtherGuyUseOtherGuyOnly,true)
        /*if (feedFragment == null) {
            feedFragment = FeedFragment()
            feedFragment?.setArguments(args)
        }*/
        if(blogFragment == null){
            blogFragment = MyFeedFragment()
            blogFragment?.setArguments(args)
        }


        /*if (myChatMessageFragmentf == null) {
            myChatMessageFragmentf = MyChatMessageFragment()
            myChatMessageFragmentf.setArguments(args)
        }


        if (myPeopleFragmentf == null) {
            myPeopleFragmentf = MyPeopleFragment()
            myPeopleFragmentf.setArguments(args)
        }


        if (notificationsFragment == null) {
            notificationsFragment = NotificationsFragment()
            notificationsFragment.setArguments(args)
        }*/


        if (viewPagerAdapteradapter == null) {

            viewPagerAdapteradapter = ViewPagerAdapter(supportFragmentManager)
            //Chat
            //viewPagerAdapteradapter?.addFragment(feedFragment as FeedFragment, "Feed", CentralConstants.FragmentTagFeed)
            viewPagerAdapteradapter?.addFragment(blogFragment as MyFeedFragment,"Blog",CentralConstants.FragmentTagBlog)

            //Questions
            /*viewPagerAdapteradapter.addFragment(questionslistFragmentf, "Discuss", CentralConstantsRepository.FragmentTagQuestions)

            //People
            viewPagerAdapteradapter.addFragment(myPeopleFragmentf, "", CentralConstantsRepository.FragmentTagPeople)

            viewPagerAdapteradapter.addFragment(notificationsFragment, "", CentralConstantsRepository.FragmentTagNotifications)*/

            /*adapter.addFragment(new TwoFragment(), "TWO");
        adapter.addFragment(new ThreeFragment(), "THREE");*/
            viewPager?.adapter = viewPagerAdapteradapter
        }

        /*if (frag1 == null) frag1 = viewPagerAdapteradapter.mFragmentList.get(1) as questionslistFragment
        if (frag2 == null) frag2 = viewPagerAdapteradapter.mFragmentList.get(0) as MyChatMessageFragment

        if (fragnotification == null) fragnotification = viewPagerAdapteradapter.mFragmentList.get(3) as NotificationsFragment

        frag2.SetShareText(SharedTextToMe)*/


        /* if (usechatpage) {
             viewPager.setCurrentItem(0, true)
         } else {
             viewPager.setCurrentItem(1, true)
         }*/


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




    fun GetProfile(){
        val pfp : ImageView? = findViewById(R.id.profileFullImage)
        val pfpr : ImageView? = findViewById(R.id.pfp)
        val url = CentralConstants.GetProfileUrl(otherguy)
        val stringRequest = JsonObjectRequest(Request.Method.GET, url,null,
                Response.Listener { response ->

                    if(response.has("statue") && response.getInt("status") == 200){
                        val result = Gson().fromJson<ProfileJsonSteemit>(response.toString(), ProfileJsonSteemit::class.java)
                        if(result.user != null){
                            //CentralConstantsOfSteem.getInstance().profile = resulto
                            var nam = "${result.user.name} (${StaticMethodsMisc.CalculateRepScore(result.user.reputation)})"
                            toolbar_layout.title =  ""
                            toolbar.title = ""
                            activity_username.text = nam
                            var d = calendarcalculations()
                            d.setDateOfTheData((SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(result.user.created)))
                            //activity_join_date.text = DateUtils.getRelativeDateTimeString(applicationContext,(SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(resulto.created)).time, DateUtils.DAY_IN_MILLIS, DateUtils.YEAR_IN_MILLIS,0)
                            activity_join_date.text = d.getDateString()

                            activity_posts.text = "${result.user.post_count.toString()} posts"
                            activity_username_toolbar.text = nam


                            if(result.user.json_metadata?.profile != null){
                                //check if the name is not null
                                if(!TextUtils.isEmpty(result.user.json_metadata.profile.name)){
                                    activity_username.text = result.user.json_metadata.profile.name
                                    activity_username_toolbar.text = result.user.json_metadata.profile.name
                                }
                                if(!TextUtils.isEmpty(result.user.json_metadata.profile.location)) activity_location.text = result.user.json_metadata.profile.location
                                activity_website.movementMethod = LinkMovementMethod.getInstance()
                                if(result.user.json_metadata.profile.website == null) cardviewsix.visibility = View.GONE
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                                    activity_website.text = Html.fromHtml("<a href=\"${result.user.json_metadata.profile.website}\" >${result.user.json_metadata.profile.website}</a>", Html.FROM_HTML_MODE_LEGACY)
                                }
                                else {
                                    activity_website.text = Html.fromHtml("<a href=\"${result.user.json_metadata.profile.website}\" >${result.user.json_metadata.profile.website}</a>")
                                }


                                activity_motto.text = result.user.json_metadata.profile.about
                                val options = RequestOptions()
                                        .centerCrop()
                                        .placeholder(animatedVec)
                                        //.error(R.drawable.error)
                                        .priority(Priority.HIGH)
                                        .circleCrop()
                                val optionss = RequestOptions()
                                        .centerCrop()
                                        .placeholder(animatedVec)
                                        //.error(R.drawable.error)
                                        .priority(Priority.HIGH)
                                animatedVec?.start()
                                //status?.text = resultp.profile.about
                                Glide.with(applicationContext).load(result.user.json_metadata.profile.cover_image).apply(optionss)
                                        //.placeholder(R.drawable.common_full_open_on_phone)
                                        .into(pfp as ImageView)
                                Glide.with(applicationContext).load(result.user.json_metadata.profile.profile_image).apply(options)
                                        //.placeholder(R.drawable.common_full_open_on_phone)
                                        .into(pfpr as ImageView)
                                Glide.with(applicationContext).load(result.user.json_metadata.profile.profile_image).apply(options)
                                        //.placeholder(R.drawable.common_full_open_on_phone)
                                        .into(pfp_outer)

                                //my.imageView.getLayoutParams().width = this.metrics.widthPixels / 2

                            }
                        }
                    } else{
                        //Start the intent creation
                        var inte = Intent(this@OpenOtherGuyBlog,SearchActivity::class.java)
                        //used to easy compatibility
                        inte.putExtra(SearchManager.QUERY,username)
                        //add so not extra code has to be written
                        inte.action = Intent.ACTION_SEARCH
                        startActivity(inte)
                        //close this activity
                        finish()
                    }


                }, Response.ErrorListener {
            val i = it
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        })

        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        volleyre.addToRequestQueue(stringRequest)

    }

    fun getFollowCount(){
        var followcountlistener: Response.Listener<JSONObject> =  Response.Listener { response ->

            val gson = Gson()
            var parse = gson.fromJson(response.toString(), prof.FollowCount::class.java)
            if(parse != null && parse.result != null){
                followCount = parse
                activity_followers.text = "${parse.result.followerCount.toString()} followers"
                activity_following.text = "${parse.result.followingCount.toString()} following"
                activity_following.setOnClickListener { v ->
                    startfollows()
                }
                activity_followers.setOnClickListener { v ->
                    startfollows()
                }
                /*StaticMethodsMisc.MakeFollowRequests(parse,applicationContext,followinglistener)
                StaticMethodsMisc.MakeFollowRequestsFollowers(parse,applicationContext,followerlistener)*/
            }

        }
        var gens = GeneralRequestsFeedIntoConstants(applicationContext)
        gens.GetFollowCount(otherguy as String,followcountlistener,null,null)
    }


    fun startfollows(){
        CentralConstantsOfSteem.getInstance().otherGuyFollowCount = followCount
        var i = Intent(applicationContext,FollowDisplay::class.java)
        i.putExtra(CentralConstants.OtherGuyNamePasser,otherguy)
        i.putExtra(CentralConstants.OtherGuyUseOtherGuyOnly,true)
        startActivity(i)
    }



}
