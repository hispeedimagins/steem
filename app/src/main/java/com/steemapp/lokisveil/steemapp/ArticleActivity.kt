package com.steemapp.lokisveil.steemapp

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Enums.NotificationType
import com.steemapp.lokisveil.steemapp.Enums.TypeOfRequest
import com.steemapp.lokisveil.steemapp.Fragments.ArticleFragment
import com.steemapp.lokisveil.steemapp.Fragments.CommentsFragment
import com.steemapp.lokisveil.steemapp.Fragments.upvotesFragment
import com.steemapp.lokisveil.steemapp.HelperClasses.GetDynamicAndBlock
import com.steemapp.lokisveil.steemapp.HelperClasses.JsonRpcResultConversion
import com.steemapp.lokisveil.steemapp.HelperClasses.MakeJsonRpc
import com.steemapp.lokisveil.steemapp.HelperClasses.StaticMethodsMisc
import com.steemapp.lokisveil.steemapp.Interfaces.ArticleActivityInterface
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.BlockId
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.SignedTransaction
import com.steemapp.lokisveil.steemapp.jsonclasses.Block
import com.steemapp.lokisveil.steemapp.jsonclasses.feed
import com.steemapp.lokisveil.steemapp.jsonclasses.prof
import java.text.SimpleDateFormat
import java.util.*

class ArticleActivity : AppCompatActivity(),ArticleActivityInterface {
    override fun linkClicked(tag: String, name: String, link: String) {
        val myIntent = Intent(this@ArticleActivity, ArticleActivity::class.java)
        myIntent.putExtra("username", name)
        myIntent.putExtra("tag", tag)
        myIntent.putExtra("permlink", link)
        //if(adaptype == AdapterToUseFor.commentNoti || adaptype == AdapterToUseFor.replyNoti) myIntent.putExtra("permlinkToFind",holder.article?.permlink)
        this@ArticleActivity.startActivity(myIntent)
    }

    override fun getContextMine(): Context {
        return this@ArticleActivity
    }

    override fun ReloadData() {
        GetFeed(articletag as String,articleuser as String,articlepermlink as String)
    }
    /*override fun GetDisplayMetrics(): DisplayMetrics {
        var metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
    }*/

    override fun UserClicked(name: String) {
        val i = Intent(this@ArticleActivity, OpenOtherGuyBlog::class.java)
        i.putExtra(CentralConstants.OtherGuyNamePasser, name)
        this@ArticleActivity.startActivity(i)
    }

    var username: String? = null
    var key: String? = null
    internal var tabLayout: TabLayout? = null
    internal var viewPager: ViewPager? = null
    internal var articleFragment : ArticleFragment? = null
    internal var commentsFragment :CommentsFragment? = null
    //internal var upvoteFragment: upvotesFragment? = null
    internal var viewPagerAdapteradapter: ViewPagerAdapter? = null
    internal var articleuser:String? = ""
    internal var articletag:String? = ""
    internal var articlepermlink:String? = ""
    internal var permlinkToFind:String? = ""
    internal var blockNumberToFind:Int = 0
    internal var usernameToState:String? = ""
    var notifitype: NotificationType? = null
    internal var result : List<feed.Comment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        MiscConstants.ApplyMyThemeArticle(this@ArticleActivity)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        setSupportActionBar(toolbar)


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

            articleFragment?.fabclicked(supportFragmentManager)

        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val sharedPreferences = applicationContext.getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences.getString(CentralConstants.username, null)
        key = sharedPreferences.getString(CentralConstants.key, null)




        var i = intent
        if(i != null && i.extras != null){
            var extras = i.extras
            articleuser = extras.getString("username","")
            articletag = extras.getString("tag","")
            articlepermlink = extras.getString("permlink","")
            permlinkToFind = extras.getString("permlinkToFind","")
            blockNumberToFind = extras.getInt(CentralConstants.ArticleBlockPasser,0)
            usernameToState = extras.getString(CentralConstants.ArticleUsernameToState,"")
            var notistr = extras.getString(CentralConstants.ArticleNotiType,null)
            if(notistr != null){
                notifitype = NotificationType.valueOf(notistr)
            }
            supportActionBar?.title = articlepermlink?.replace("-"," ")
            //toolbar.title = articlepermlink
        }
        else if( i != null && i.action != null && i.data != null){
            val action = i.action
            val data = i.data
            val datada = data.encodedQuery
            val datas = data.encodedPath
        }

        mysetup()
        if(notifitype != null){
            when(notifitype){
                NotificationType.vote -> {
                    //GetBlock(blockNumberToFind)
                    GetContent(username!!,permlinkToFind!!)
                }
                NotificationType.transfer -> {

                }
                NotificationType.follow -> {

                }
                NotificationType.reblog ->{
                    //GetBlock(blockNumberToFind)
                    GetContent(username!!,permlinkToFind!!)
                }
                NotificationType.mention ->{
                    GetContent(usernameToState!!,permlinkToFind!!)
                }
                NotificationType.reply ->{
                    GetContent(usernameToState!!,permlinkToFind!!)
                }
            }
        }
        else{
            GetFeed(articletag as String,articleuser as String,articlepermlink as String)
        }
        /*if(blockNumberToFind == 0){
            GetFeed(articletag as String,articleuser as String,articlepermlink as String)
        }
        else{
            GetBlock(blockNumberToFind)
        }*/
    }


    /*override fun onStop() {
        super.onStop()
        finish()
    }*/

    /*fun firstinit() {
        val headv = nav_view.getHeaderView(0)

        mysetup()
        // GetProfile()
    }*/


    fun mysetup() {
        if (viewPager == null) viewPager = findViewById(R.id.pager)

        setupViewPager(viewPager)
        tabLayout = findViewById(R.id.tabs)
        tabLayout?.setupWithViewPager(viewPager)




        setTabViewItems()
        if(permlinkToFind != null && permlinkToFind != ""){
            viewPager?.setCurrentItem(1,true)
        }
        //commentsFragment?.swipecommonactionsclass?.makeswiperun()

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
        if (fo != null && fo is ArticleFragment) {
            articleFragment = fo as ArticleFragment
        }

        val fi = fragmentManager.findFragmentByTag(makeFragmentName(vid, 1))
        if (fi != null && fi is CommentsFragment) {
            commentsFragment = fi as CommentsFragment
        }

        /*val fs = fragmentManager.findFragmentByTag(makeFragmentName(vid, 1))
        if (fi != null && fi is upvotesFragment) {
            upvoteFragment = fi as upvotesFragment
        }*/

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
        //boolean tokenisrefreshingHoldon = false;args.putBoolean("isrefreshing",tokenisrefreshingHoldon);
        if (articleFragment == null) {
            articleFragment = ArticleFragment()
            articleFragment?.setArguments(args)
        }
        if (commentsFragment == null) {
            commentsFragment = CommentsFragment()
            commentsFragment?.setArguments(args)
        }
       /* if (upvoteFragment == null) {
            upvoteFragment = upvotesFragment()
            upvoteFragment?.setArguments(args)
        }*/


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
            viewPagerAdapteradapter?.addFragment(articleFragment as ArticleFragment, "Article", CentralConstants.FragmentTagFeed)
            viewPagerAdapteradapter?.addFragment(commentsFragment as CommentsFragment,"Comments",CentralConstants.FragmentTagComments)
            //viewPagerAdapteradapter?.addFragment(upvoteFragment as upvotesFragment,"Upvotes",CentralConstants.FragmentTagUpvotes)
            //viewPagerAdapteradapter?.addFragment(blogFragment as MyFeedFragment, "My Blog", CentralConstants.FragmentTagBlog)

            //Questions
            /*viewPagerAdapteradapter.addFragment(questionslistFragmentf, "Discuss", CentralConstantsRepository.FragmentTagQuestions)

            //People
            viewPagerAdapteradapter.addFragment(myPeopleFragmentf, "", CentralConstantsRepository.FragmentTagPeople)

            viewPagerAdapteradapter.addFragment(notificationsFragment, "", CentralConstantsRepository.FragmentTagNotifications)*/

            /*adapter.addFragment(new TwoFragment(), "TWO");
        adapter.addFragment(new ThreeFragment(), "THREE");*/
            viewPager?.offscreenPageLimit = 4
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


/*

    fun GetFeed(articletag : String,articleusername : String,articlepermlink : String){
        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)

        val url = "https://api.steemit.com/$articletag/@$articleusername/$articlepermlink"
        val d = MakeJsonRpc.getInstance()


        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener { response ->

                    val gson = Gson()


                    val result = gson.fromJson<feed.Post>(response, feed.Post::class.java)
                    if(result != null){

                    }


                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        })
        //queue.add(s)
        volleyre.addToRequestQueue(stringRequest)
    }*/

    /**
     * Function to use get_content api to fetch the post for getting root author,permlin.tag attributes
     * @param username username of the person who wrote the post/comment
     * @param permlink permlink of the post/comment
     * This method will redirect to the GetFeed method
     *
     */
    fun GetContent(username:String,permlink:String){
//val queue = Volley.newRequestQueue(context)
        //commentsFragment?.swipecommonactionsclass?.makeswiperun()
        //swipecommonactionsclass?.makeswiperun()

        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()

        val s = JsonObjectRequest(Request.Method.POST,url,d.getContent(username,permlink),
                Response.Listener { response ->
                    if(response.getJSONObject("result") != null){
                        var resul = response.getJSONObject("result")
                        var rooau = resul.getString("root_author")
                        var rootper = resul.getString("root_permlink")
                        var roottag = resul.getString("category")

                        articletag = roottag
                        articleuser = rooau
                        articlepermlink = rootper
                        supportActionBar?.title = articlepermlink?.replace("-"," ")
                        GetFeed(articletag as String,articleuser as String,articlepermlink as String)
                    }


                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }


    fun GetBlock(blocknumber : Int){
        //val queue = Volley.newRequestQueue(context)
        //commentsFragment?.swipecommonactionsclass?.makeswiperun()
        //swipecommonactionsclass?.makeswiperun()

        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()

        val s = JsonObjectRequest(Request.Method.POST,url,d.getBlock(blocknumber),
                Response.Listener { response ->

                    val gson = Gson()
                    var parse : Block.GetBlockResult = gson.fromJson(response.toString(), Block.GetBlockResult::class.java)
                    if(parse != null && parse.result != null){
                        for(t in parse.result.transactions){
                            for(y in t.operations){
                                for(x in y){
                                    if(x is String){

                                    }
                                    else{
                                        var co = x as LinkedTreeMap<*, *>
                                        if(notifitype != null){
                                            when(notifitype){
                                                NotificationType.vote -> {
                                                    var auth : String = co["author"].toString()
                                                    if(usernameToState == "findvoter") usernameToState = auth
                                                }
                                                NotificationType.reblog ->{
                                                    //if(co["id"])
                                                }
                                            }
                                        }
                                        if(co.contains("permlink")){
                                            if(co.get("permlink") == permlinkToFind){
                                                /*var auth : String = co["author"].toString()
                                                if(usernameToState == "findvoter") usernameToState = auth*/
                                                var jsonMetadata = gson.fromJson<feed.JsonMetadataInner>(co["json_metadata"].toString(),feed.JsonMetadataInner::class.java)
                                                if(jsonMetadata != null){
                                                     GetMiddleState("${jsonMetadata.tags?.get(0)}/@$usernameToState/$permlinkToFind","$usernameToState/$permlinkToFind")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }



    fun GetMiddleState(page:String,matcher:String){
        //val queue = Volley.newRequestQueue(context)

        //swipecommonactionsclass?.makeswiperun()

        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()

        val s = JsonObjectRequest(Request.Method.POST,url,d.getState(page),
                Response.Listener { response ->

                    //val gson = Gson()

                    var result = if(response?.has("result")!!) response?.getJSONObject("result") else null
                    if(result != null){
                        var content = result.getJSONObject("content")
                        if(content.has(matcher)){
                            var mat = content.getJSONObject(matcher)
                            var root_perm = mat.getString("root_permlink")
                            var root_auth = mat.getString("root_author")
                            var cat = mat.getString("category")
                            articletag = cat
                            articleuser = root_auth
                            articlepermlink = root_perm
                            supportActionBar?.title = articlepermlink?.replace("-"," ")
                            GetFeed(articletag as String,articleuser as String,articlepermlink as String)
                        }
                    }




                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }




    fun GetFeed(articletag : String,articleusername : String,articlepermlink : String){
        //val queue = Volley.newRequestQueue(context)

        //swipecommonactionsclass?.makeswiperun()

        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = "https://api.steemit.com/"
        val d = MakeJsonRpc.getInstance()
        val g = Gson()

        val s = JsonObjectRequest(Request.Method.POST,url,d.getArticle(articletag,articleusername,articlepermlink),
                Response.Listener { response ->
                    // Display the first 500 characters of the response string.
                    //mTextView.setText("Response is: "+ response.substring(0,500));
                    //swipecommonactionsclassT.makeswipestop()
                    /*val gson = Gson()
                    val result = gson.fromJson<JsonTenorResultTrending>(response, JsonTenorResultTrending::class.java!!)
                    for (s in result.results) {
                        tenoradapter.add(s.media.get(0))
                    }*/
                    val gson = Gson()
                    val collectionType = object : TypeToken<List<feed.FeedData>>() {

                    }.type
                    val con = JsonRpcResultConversion(response,username as String, TypeOfRequest.feed,applicationContext)
                    //con.ParseJsonBlog()
                    val result = con.ParseReplies(articleusername+"/"+articlepermlink)
                    //val result = gson.fromJson<List<feed.FeedData>>(response.toString(),collectionType)
                    if(result != null && !result.isEmpty()!!){
                        commentsFragment?.clear()
                        commentsFragment?.setPermlinkToFind(permlinkToFind as String)
                        //this.result = result
                        /*if(result[0].active_voted != null){
                            for(r in result[0].active_voted as List<feed.avtiveVotes>){
                                var na = r.voter
                                var pa = StaticMethodsMisc.CalculateVotingValueRshares(r.rshares)
                                var sbds = StaticMethodsMisc.VotingValueSteemToSd(pa)
                                var per = r.percent
                                var re = StaticMethodsMisc.CalculateRepScore(r.reputation)
                                var pers = r.reputation
                            }
                        }*/


                        articleFragment?.displayMessage(result[0] as FeedArticleDataHolder.FeedArticleHolder)
                        /*if(result[0].active_voted != null){
                            upvoteFragment?.display(result[0].active_voted as List<feed.avtiveVotes>)
                        }*/

                        if(result.size > 2){
                            //var si = result.size - 1
                            commentsFragment?.displayMessage(result.subList(1,result.size) as List<FeedArticleDataHolder.CommentHolder>)
                        }
                        else if(result.size == 2){
                            commentsFragment?.displayMessage(result[1] as FeedArticleDataHolder.CommentHolder)
                            commentsFragment?.swipecommonactionsclass?.makeswipestop()
                        }
                        else if(result.size == 1){
                            commentsFragment?.swipecommonactionsclass?.makeswipestop()
                        }


                    }

                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }

}
