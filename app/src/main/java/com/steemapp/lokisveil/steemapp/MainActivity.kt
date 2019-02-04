package com.steemapp.lokisveil.steemapp

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.steemapp.lokisveil.steemapp.Databases.drafts
import com.steemapp.lokisveil.steemapp.Fragments.FeedFragment
import com.steemapp.lokisveil.steemapp.Fragments.MyFeedFragment
import com.steemapp.lokisveil.steemapp.HelperClasses.*
import com.steemapp.lokisveil.steemapp.Interfaces.GlobalInterface
import com.steemapp.lokisveil.steemapp.Interfaces.TagsInterface
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.AccountName
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.Operation
import com.steemapp.lokisveil.steemapp.jsonclasses.prof
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.follow_progress.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,TagsInterface,GlobalInterface {
    override fun notifyRequestMadeSuccess() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    override fun getFab(): FloatingActionButton? {
        return fab
    }

    override fun okclicked(originalval: String, tag: String, limit: String, request: String) {
        var it = Intent(this@MainActivity,MainTags::class.java)
        it.putExtra(CentralConstants.MainRequest,request)
        it.putExtra(CentralConstants.MainTag,tag)
        it.putExtra(CentralConstants.OriginalRequest,originalval)
        runOnUiThread({
            startActivity(it)
        })

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
        }
    }

    /**
     * reports the follower progress
     * @param got the number got till now
     * @param total the total number of people to get
     */
    override fun followerProgress(got:Int,total:Int){
        //calculate percentage
        var pro = (got/total) * 100
        progressNow.progress = pro
        progressDone.text = "syncing people : $got/${runs.totalSize}"
    }

    /**
     * reports the following progress
     * @param got the number got till now
     * @param total the total number of people to get
     */
    override fun followingProgress(got:Int,total:Int){
        var pro = (got/total) * 100
        progressNow.progress = pro
        progressDone.text = "syncing people : $got/${runs.totalSize}"
    }
    override fun followHasChanged(){
        if(username != null){
            progress_foll.visibility = View.VISIBLE
            progressDone.text = "syncing followers : 0/${runs.totalSize}"
            runs.refreshFollowDbNow()
        }

    }

    var username : String? = null
    var key : String? = null
    internal var tabLayout: TabLayout? = null
    internal var viewPager: ViewPager? = null
    internal var feedFragment : FeedFragment? = null
    internal var blogFragment :MyFeedFragment? = null
    internal var viewPagerAdapteradapter: ViewPagerAdapter? = null
    lateinit var runs: GeneralRequestsFeedIntoConstants
    override fun onCreate(savedInstanceState: Bundle?) {
        MiscConstants.ApplyMyTheme(this@MainActivity)

        //this is set so as someone type's using his phones physical keyboard
        //or tab keyboard, the search will open as default on the home screen
        setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()*/
            var i = Intent(applicationContext,Post::class.java)
            startActivity(i)
        }
        Mint.initAndStartSession(this.application, "dd37aa8e")

        //for updating the value of the voting power when the drawer is opened
        //and readding the images and removing them to save RAM - next build
        val toggle = object : ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            override fun onDrawerClosed(drawerView: View) {
                /*val headv = nav_view.getHeaderView(0)
                val pfp : ImageView? = headv.findViewById(R.id.pfp)
                val name:TextView? = headv.findViewById(R.id.name)
                val status:TextView? = headv.findViewById(R.id.status)
                val vote_power = headv.findViewById<TextView>(R.id.vote_power)
                val vote_value = headv.findViewById<ProgressBar>(R.id.vote_value)*/
                //pfp?.setImageBitmap(null)
                //cover?.setImageBitmap(null)
                super.onDrawerClosed(drawerView)
            }

            override fun onDrawerOpened(drawerView: View) {
                //var sh = getSharedPreferences(CentralConstants.sharedprefname,0)
                var vp = CentralConstantsOfSteem.getInstance().currentvotingpower
                vp /= 100;
                vote_power.text = "Voting power : $vp%";
                vote_value.progress = vp
                super.onDrawerOpened(drawerView)
            }
        }
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val sharedPreferences = getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences.getString(CentralConstants.username, null)
        key = sharedPreferences.getString(CentralConstants.key, null)

        //trim usernames so any spaces are removed from the previous versions as well
        if(!(sharedPreferences.getBoolean("updatefortrim",false))){
            var shae = sharedPreferences.edit()
            shae.putString(CentralConstants.username,username?.trim())
            shae.putString(CentralConstants.key,key?.trim())
            shae.putBoolean("updatefortrim",true)
            shae.apply()
        }


        if(username.isNullOrEmpty() or key.isNullOrEmpty()){

            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivityForResult(intent,12)
        }
        else{
            firstinit()

        }



    }



    fun firstinit(){
        mysetup()

        val headv = nav_view.getHeaderView(0)
        //val men = nav_view.menu
        //val pfp : ImageView? = headv.findViewById(R.id.pfp)
        val name:TextView? = headv.findViewById(R.id.name)
        //val status:TextView? = headv.findViewById(R.id.status)
        name?.text = username
        //mysetup()
        if(username != null){
            runs = GeneralRequestsFeedIntoConstants(this@MainActivity,application)
            runs.RunThemAll()

            GetProfile()
            //addAccount()

            runs.GetFollowCount(username!!,null,null,null)
            //check the intent if, someone shared links then we can open them
            //this happens if the app was not running
            extractLinks(intent)
        }

    }

    fun mysetup(){
        if (viewPager == null) viewPager = findViewById(R.id.pager)

        setupViewPager(viewPager)
        tabLayout = findViewById(R.id.tabs)
        tabLayout?.setupWithViewPager(viewPager)

        setTabViewItems()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    //if app is running this is where the shared data intent will come
    override fun onNewIntent(intent: Intent?) {
        extractLinks(intent)
        super.onNewIntent(intent)
    }

    //gets the shared text
    fun handleSendText(intent: Intent):String{
        return intent.getStringExtra(Intent.EXTRA_TEXT)
    }

    //try to extract information from the links
    //some more work has to be done
    //then start the article activity
    fun openLink(lis:List<String>){
        val myIntent = Intent(this, ArticleActivity::class.java)
        myIntent.putExtra("username", lis[2].removePrefix("@"))
        myIntent.putExtra("tag", lis[1])
        var sl = lis[3]
        if(sl.contains("#")){
            sl = sl.split("#").first()
        }
        myIntent.putExtra("permlink", sl)
        this.startActivity(myIntent)
    }


    fun extractLinks(intent:Intent?){
        if(intent != null){
            //chekc the action and type
            val action = intent.action
            val type = intent.type
            if(action == null || type == null){
                return
            }
            //if they match
            if(Intent.ACTION_SEND == action && type != null){
                if("text/plain" == type){
                    var potu = handleSendText(intent)

                    //this is normal text probably
                    //if contains words send it to the Post activity for making an article/post
                    if(potu.contains("\\s+".toRegex())){

                        var db = drafts(this@MainActivity)
                        var di = db.Insert("","",potu,"")

                        val myIntent = Intent(this@MainActivity, Post::class.java)
                        myIntent.putExtra("db",di.toInt() )

                        startActivity(myIntent)
                        return
                    }
                    //get links are regexp, form the links class
                    var mat = Links.urlwithoutexGroups().toRegex()
                    //match them to the text
                    var mats = mat.findAll(potu).toList()
                    //if no more than one match occurs
                    if(mats.any() && mats.size == 1){

                        if(mats[0].groupValues.size == 4){
                            var sp = mats[0].groupValues[3].split("/")
                            if(sp.size == 2){
                                //if this then it a link to a profile
                                //var uname = sp[1]
                                sendToOtherGuy(sp[1].removePrefix("@"))
                            } else if(sp.size > 2){
                                //if this then it is a link to an article
                                /*var tag = sp[1]
                                var uname = sp[2]
                                var permlink = sp[3]*/
                                openLink(sp)
                            }
                        }

                    }
                }
            } else if(Intent.ACTION_SEND_MULTIPLE == action && type != null){

            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_notifications_busy ->{
                var ints = Intent(this@MainActivity,NotificationsBusyD::class.java)
                startActivity(ints)
                return true
            }
            R.id.action_search ->{
                //as recommended by google you should include and icon
                //as android might not always show it by default
                this.onSearchRequested()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    //function to open the otherguy activity
    fun sendToOtherGuy(uname:String){
        val i = Intent(this, OpenOtherGuyBlog::class.java)
        i.putExtra(CentralConstants.OtherGuyNamePasser,uname)
        this.startActivity(i)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_open_blog ->{
                val alertDialogBuilder = AlertDialog.Builder(MiscConstants.ApplyMyThemeRet(this@MainActivity))
                alertDialogBuilder.setTitle("Open a blog")

                val inflater = layoutInflater
                val dialogView : View = inflater.inflate(R.layout.dialog_open_a_blog, null)

                alertDialogBuilder.setView(dialogView)
                val edittext = dialogView.findViewById<EditText>(R.id.name)
                alertDialogBuilder.setPositiveButton("ok") { diin, num ->
                    if(edittext.text != null){
                        sendToOtherGuy(edittext.text.toString())
                    }


                }

                alertDialogBuilder.setNegativeButton("No", DialogInterface.OnClickListener { diin, num ->

                })
                val alertDialog = alertDialogBuilder.create()

                alertDialog.show()
            }
            R.id.nav_open_favourites->{
                val intent = Intent(this,FavouritesActivity::class.java)
                this.startActivity(intent)
            }
            R.id.nav_followers ->{
                val intent = Intent(this,FollowDisplay::class.java)
                this.startActivity(intent)
            }
            R.id.nav_open_drafts ->{
                val intent = Intent(this,DraftActivity::class.java)
                this.startActivity(intent)
            }
            R.id.nav_open_trending ->{
                var ta = TagRequestHelper(this@MainActivity,this)
            }
            R.id.nav_open_Comments_Replies ->{
                var ins = Intent(this@MainActivity,CommentNotifications::class.java)
                startActivity(ins)
            }
            R.id.nav_settings ->{
                var ins = Intent(this@MainActivity,SettingsActivity::class.java)
                startActivity(ins)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    fun addAccount(){
        /*var myConfig : SteemJConfig = SteemJConfig.getInstance()
        val sharedPreferences = applicationContext.getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences.getString(CentralConstants.username, null)
        key = sharedPreferences.getString(CentralConstants.key, null)
        //myConfig.defaultAccount = AccountName(username)

        var privateKeys : List<ImmutablePair<PrivateKeyType, String>> =  ArrayList<ImmutablePair<PrivateKeyType, String>>()
        privateKeys += (ImmutablePair<PrivateKeyType,String>(PrivateKeyType.POSTING,key))
        myConfig.getPrivateKeyStorage().addAccount( AccountName(username), privateKeys)*/
        //myConfig.steemJWeight = 100.toShort()
        /*class someTask() : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String? {
                // ...
                //var block : BlockId = BlockId(parse.result.blockId)
                var vop : VoteOperation = VoteOperation(AccountName("xeroc"), AccountName("xeroc"), Permlink("piston"))
                val operations = ArrayList<Operation>()
                operations.add(vop)
                var signedtra = SignedTransaction(block,operations,null)
                var fors = FeedHelperFunctions.forewarder(vop, signedtra)
                //var sigas = signalrstarterasync().execute(fors)
                if(key == null){
                    key = sharedpref.getString(CentralConstants.key,null)
                }

                signedtra.signMy(SteemJConfig.getInstance().getChainId(),ImmutablePair(PrivateKeyType.POSTING,key))
                BroadcastSynchronous(vop,signedtra)
                return ""
            }
        }
        someTask().execute()*/

    }


    override fun onActivityResult(requestcode: Int, result: Int, intent: Intent?) {
        super.onActivityResult(requestcode, result, intent)
        if ((requestcode == 1 || requestcode == 12) && result == Activity.RESULT_OK && intent != null) {

            val sharedPreferences = applicationContext.getSharedPreferences(CentralConstants.sharedprefname, 0)
            username = sharedPreferences.getString(CentralConstants.username, null)
            key = sharedPreferences.getString(CentralConstants.key, null)
            firstinit()

        } else {

            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivityForResult(intent,12)

        }
    }

    fun GetProfile(){
        val headv = nav_view.getHeaderView(0)
        val pfp : ImageView? = headv.findViewById(R.id.pfp)
        val name:TextView? = headv.findViewById(R.id.name)
        val status:TextView? = headv.findViewById(R.id.status)
        val vote_power = headv.findViewById<TextView>(R.id.vote_power)
        val vote_value = headv.findViewById<ProgressBar>(R.id.vote_value)
        val url = "https://api.steemjs.com/get_accounts?names[]=[\"$username\"]"
        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener { response ->
                    val gson = Gson()

                    val collectionType = object : TypeToken<List<prof.profile>>() {
                    }.type
                    val result = gson.fromJson<List<prof.profile>>(response,collectionType)
                    if(result.isNotEmpty()){
                        val resulto = result[0]
                        if(result != null){
                            CentralConstantsOfSteem.getInstance().profile = resulto
                            name?.text = "${resulto.name} (${StaticMethodsMisc.CalculateRepScore(resulto.reputation)})"
                            //var lastvotetime  = (SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(resulto.lastVoteTime) )
                            var lastvotetime  = StaticMethodsMisc.FormatDateGmt(resulto.lastVoteTime)
                            var votingpower = resulto.votingPower
                            val ed = getSharedPreferences(CentralConstants.sharedprefname,0)
                            var ei = ed.edit()
                            ei.putInt(CentralConstants.votingpower,votingpower)
                            ei.putLong(CentralConstants.lastvotetime,lastvotetime.time)
                            ei.putString(CentralConstants.vestingshares,resulto.vestingShares)
                            ei.putString(CentralConstants.delegatedvestingshares,resulto.delegatedVestingShares)
                            ei.putString(CentralConstants.receivedvestingshares,resulto.receivedVestingShares)
                            //ei.putString(CentralConstants.accountrep,resulto.reputation)
                            ei.apply()
                            /*var sub = (Date().time - lastvotetime.time) / 1000
                            var subf = sub / CentralConstants.FiveDaysInSeconds
                            var subm = subf * 10000
                            votingpower = (votingpower + subm).toInt()*/
                            resulto.lastVoteTimeLong = lastvotetime.time
                            CentralConstantsOfSteem.getInstance().currentvotingpower = StaticMethodsMisc.CalculateVotingPower(resulto.votingPower,lastvotetime.time).toInt()
                            val cal = CentralConstantsOfSteem.getInstance().currentvotingpower / 100
                            vote_power.text = "Voting power : $cal%"
                            vote_value.progress = cal
                            /*var reps = resulto.reputation.toDouble()
                            var replog = Math.log10(reps)
                            var subni = replog - 9
                            val mulni = subni * 9
                            val addtf = mulni + 25*/
                            if(resulto.rewardSbdBalance != "0.000 SBD" ||
                               resulto.rewardSteemBalance != "0.000 STEEM" ||
                               resulto.rewardVestingSteem != "0.000 STEEM" ||
                               resulto.rewardVestingBalance != "0.000000 VESTS"){
                                openRewardAlert(resulto)
                            }
                            val resultp = gson.fromJson<prof.profiledata>(resulto.jsonMetadata,prof.profiledata::class.java)
                            if(resultp != null){
                                status?.text = resultp.profile.about
                                val options = RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.drawable.ic_person_white_24px)
                                        //.error(R.drawable.error)
                                        .priority(Priority.HIGH)
                                        .circleCrop()
                                Glide.with(applicationContext).load(CentralConstants.GetFeedImageUrl(username))
                                        .apply(options)
                                        .into(pfp as ImageView)
                                val optionss = RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.drawable.ic_all_inclusive_black_24px)
                                        //.error(R.drawable.error)
                                        .priority(Priority.HIGH)

                                Glide.with(applicationContext).load(resultp.profile.coverImage)
                                        .apply(optionss)
                                        .into(cover)
                                //my.imageView.getLayoutParams().width = this.metrics.widthPixels / 2

                            }
                        }
                    }


                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        })

        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        volleyre.addToRequestQueue(stringRequest)

    }


    //function to open an alert if there are rewards
    //if the user wishes to claim them then a request will be made
    fun openRewardAlert(profile:prof.profile){
        val alertDialogBuilder = AlertDialog.Builder(MiscConstants.ApplyMyThemeRet(this@MainActivity))
        //val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
        alertDialogBuilder.setTitle("Claim Rewards?")

        val inflater = layoutInflater
        val dialogView : View = inflater.inflate(R.layout.dialog_open_a_blog, null)

        alertDialogBuilder.setView(dialogView)
        val edittext = dialogView.findViewById<EditText>(R.id.name)
        edittext.setText("${profile.rewardSbdBalance} , ${profile.rewardSteemBalance} , ${profile.rewardVestingSteem}")
        edittext.isEnabled = false
        alertDialogBuilder.setPositiveButton("ok") { diin, num ->
            if(edittext.text != null){
                var ms = MakeOperationsMine()
                var ope = ms.claimRewards(AccountName(username),profile.rewardSbdBalance,profile.rewardSteemBalance,profile.rewardVestingBalance,profile.rewardVestingSteem)
                var opl = ArrayList<Operation>()
                opl.add(ope)
                var dy = GetDynamicAndBlock(this@MainActivity,null,0,opl,"Rewards claimed",MyOperationTypes.claim_reward_balance,null,null)
                dy.GetDynamicGlobalProperties()
                diin.dismiss()

            }


        }

        alertDialogBuilder.setNegativeButton("No", DialogInterface.OnClickListener { diin, num ->

        })
        val alertDialog = alertDialogBuilder.create()

        alertDialog.show()
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
        if (fo != null && fo is FeedFragment) {
            feedFragment = fo as FeedFragment
        }

        val fi = fragmentManager.findFragmentByTag(makeFragmentName(vid,1))
        if(fi != null && fi is MyFeedFragment){
            blogFragment = fi as MyFeedFragment
        }

        val args = Bundle()

        //boolean tokenisrefreshingHoldon = false;
        //args.putBoolean("isrefreshing",tokenisrefreshingHoldon)
        if (feedFragment == null) {
            feedFragment = FeedFragment()
            feedFragment?.setArguments(args)
        }
        if(blogFragment == null){
            blogFragment = MyFeedFragment()
            blogFragment?.setArguments(args)
        }


        if (viewPagerAdapteradapter == null) {

            viewPagerAdapteradapter = ViewPagerAdapter(supportFragmentManager)
            //Chat
            viewPagerAdapteradapter?.addFragment(feedFragment as FeedFragment, "Feed", CentralConstants.FragmentTagFeed)
            viewPagerAdapteradapter?.addFragment(blogFragment as MyFeedFragment,"My Blog",CentralConstants.FragmentTagBlog)

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


    }


}
