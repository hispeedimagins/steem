package com.insteem.ipfreely.steem

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.viewpager.widget.ViewPager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.insteem.ipfreely.steem.DataHolders.FeedArticleDataHolder
import com.insteem.ipfreely.steem.Enums.NotificationType
import com.insteem.ipfreely.steem.Enums.TypeOfRequest
import com.insteem.ipfreely.steem.Fragments.ArticleFragment
import com.insteem.ipfreely.steem.Fragments.CommentsFragment
import com.insteem.ipfreely.steem.HelperClasses.GeneralRequestsFeedIntoConstants
import com.insteem.ipfreely.steem.HelperClasses.ImagePickersWithHelpers
import com.insteem.ipfreely.steem.HelperClasses.ImagePickersWithHelpers.Companion.PICK_IMAGE_REQUEST
import com.insteem.ipfreely.steem.HelperClasses.ImagePickersWithHelpers.Companion.uploadImage
import com.insteem.ipfreely.steem.HelperClasses.JsonRpcResultConversion
import com.insteem.ipfreely.steem.HelperClasses.MakeJsonRpc
import com.insteem.ipfreely.steem.Interfaces.ArticleActivityInterface
import com.insteem.ipfreely.steem.RoomDatabaseApp.RoomViewModels.ArticleRoomVM
import com.insteem.ipfreely.steem.RoomDatabaseApp.RoomViewModels.WidgetVM
import com.insteem.ipfreely.steem.jsonclasses.Block
import com.insteem.ipfreely.steem.jsonclasses.feed
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_article.*


class   ArticleActivity : AppCompatActivity(),ArticleActivityInterface , ImagePickersWithHelpers.onTakePick{
    override fun startActivityForResults(takePictureIntent: Intent, requesT_IMAGE_CAPTURE: Int) {
        startActivityForResult(takePictureIntent, requesT_IMAGE_CAPTURE)
    }

    override fun setURI(mCurrentPhotoUri: Uri?) {
        imageUri = mCurrentPhotoUri
    }

    override fun getFab(): FloatingActionButton? {
        return fab
    }

    override fun progress(visibility: Int) {
        progressCom?.visibility = visibility
    }

    override fun imagePickerClicked(editText: EditText?,progressBar: ProgressBar?) {
        editTextCom = editText
        progressCom = progressBar
        imageBrowse()
    }

    override fun imageProcessed(url: String) {

    }

    override fun imageProcessDoneClearVariables(url: String?) {
        this.editTextCom = null
        this.progressCom = null
    }

    //implements tag clicks
    override fun TagClicked(tag: String) {
        var it = Intent(this@ArticleActivity,MainTags::class.java)
        it.putExtra(CentralConstants.MainRequest,"get_discussions_by_trending")
        it.putExtra(CentralConstants.MainTag,tag)
        it.putExtra(CentralConstants.OriginalRequest,"trending")
        this@ArticleActivity.startActivity(it)
    }

    //Return the body of the article
    override fun getBody():String{
        return articleFragment?.holder?.article?.body!!
    }
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

    override fun getDbData(): FeedArticleDataHolder.FeedArticleHolder? {
        val temp = res
        res = null
        return temp
    }

    override fun loadDataNow() {
        fetchArticle()
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
    internal var articleTitle:String? = ""
    internal var articleCovImg:String? = ""
    internal var articleDate:String? = ""
    internal var articlepermlink:String? = ""
    internal var permlinkToFind:String? = ""
    internal var blockNumberToFind:Int = 0
    internal var usernameToState:String? = ""
    var notifitype: NotificationType? = null
    internal var result : List<feed.Comment>? = null
    var dblist = ArrayList<Long>()
    var dbId = -1
    var fromWidget = false
    lateinit var articleVm:ArticleRoomVM
    lateinit var widgetVm:WidgetVM
    private var res: FeedArticleDataHolder.FeedArticleHolder? = null
    var ani:AnimatedVectorDrawableCompat? = null
    var imageUri:Uri? = null
    var editTextCom:EditText? = null
    var progressCom:ProgressBar? = null
    var displayMet: DisplayMetrics = DisplayMetrics()
    var autoLoadOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        MiscConstants.ApplyMyThemeArticle(this@ArticleActivity)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        setSupportActionBar(toolbar)


        fab.setOnClickListener { view ->
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()*/

            articleFragment?.fabclicked(supportFragmentManager)

        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        windowManager.defaultDisplay.getMetrics(displayMet)
        if(savedInstanceState != null){
            //initiate all variables form save state
            articleuser = savedInstanceState.getString("articleuser")
            articlepermlink = savedInstanceState.getString("articlepermlink")
            articletag = savedInstanceState.getString("articletag")
            articleTitle = savedInstanceState.getString(CentralConstants.passerArticleTitle,"")
            articleCovImg = savedInstanceState.getString(CentralConstants.passerArticleDefImg,"")
            articleDate = savedInstanceState.getString(CentralConstants.passerArticleaDate,"")
            permlinkToFind = savedInstanceState.getString("permlinkToFind")
            blockNumberToFind = savedInstanceState.getInt("blockNumberToFind")
            usernameToState = savedInstanceState.getString("usernameToState")
            var not = savedInstanceState.getString("notifitype",null)
            if(not != null){
                notifitype = NotificationType.valueOf(not)
            }

            var lar = savedInstanceState.getLongArray("dbitems")
            dblist.addAll(lar.toList())


        }

    }

    override fun onStart() {
        super.onStart()

        val sharedPreferences = applicationContext.getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences.getString(CentralConstants.username, null)
        key = sharedPreferences.getString(CentralConstants.key, null)


        //Run functions to retrieve the general constants for use while
        //voting on a comment/article
        val runs = GeneralRequestsFeedIntoConstants(applicationContext)
        runs.RunThemAll()

        ani = AnimatedVectorDrawableCompat.create(this,R.drawable.animated_loader)
        val i = intent

        if(i != null && i.extras != null){
            val extras = i.extras
            articleuser = extras.getString("username","")
            articletag = extras.getString("tag","")
            articleTitle = extras.getString(CentralConstants.passerArticleTitle,"")
            articleCovImg = extras.getString(CentralConstants.passerArticleDefImg,"")
            articleDate = extras.getString(CentralConstants.passerArticleaDate,"")
            articlepermlink = extras.getString("permlink","")
            permlinkToFind = extras.getString("permlinkToFind","")
            blockNumberToFind = extras.getInt(CentralConstants.ArticleBlockPasser,0)
            usernameToState = extras.getString(CentralConstants.ArticleUsernameToState,"")
            var notistr = extras.getString(CentralConstants.ArticleNotiType,null)
            if(notistr != null){
                notifitype = NotificationType.valueOf(notistr)
            }
            dbId = extras.getInt("dbId",-1)

            //check if it is from a widget or an article
            fromWidget = extras.getBoolean("fromWidget",false)
            val title = if(TextUtils.isEmpty(articleTitle)) articlepermlink?.replace("-"," ") else articleTitle
            supportActionBar?.title = title
            toolbar_layout.title = title
            activity_article_title.text = title
            activity_username.text = articleuser
            activity_tag.text = "in $articletag"
            activity_date.text = articleDate
            /*setCollImage(articleuser!!)
            setMainImage(articleCovImg)*/
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
        setCollImage(articleuser!!)
        setMainImage(articleCovImg)
    }


    /**
     * called when the article fragment is ready, then we fetch the article either from the database or from the network
     */
    fun fetchArticle(){
        if(dbId != -1){
            if(fromWidget){
                //if from a widget we load from the widget vm
                widgetVm = ViewModelProviders.of(this@ArticleActivity).get(WidgetVM::class.java)
                widgetVm.getFetchedItem(dbId).observe(this@ArticleActivity, Observer {
                    if(it != null) {
                        res = it
                        articleFragment?.displayMessage(it)
                        //setMainImage(it.displayImage)
                    }
                })
            } else {
                articleVm = ViewModelProviders.of(this@ArticleActivity).get(ArticleRoomVM::class.java)
                articleVm.getFetchedItem(dbId).observe(this@ArticleActivity,Observer {
                    if(it != null) {
                        res = it
                        articleFragment?.displayMessage(it)
                        //setMainImage(it.displayImage)
                    }
                })


            }

        } else {
            GetFeed(articletag as String,articleuser as String,articlepermlink as String)
            //}
            //GetFeed(articletag as String,articleuser as String,articlepermlink as String)
        }
    }

    /**
     * sets the header user image
     * @param username the author of the application
     */
    fun setCollImage(username:String){
        //val ani = AnimatedVectorDrawableCompat.create(this,R.drawable.animated_loader)
        val options = RequestOptions()
                .centerCrop()
                .placeholder(ani)
                //.error(R.drawable.error)
                .priority(Priority.HIGH)
                .circleCrop()
        ani?.start()
        Glide.with(this).load(CentralConstants.GetFeedImageUrl(username)).apply(options)
                // .placeholder(R.drawable.common_full_open_on_phone)
                .into(pfp_outer)
    }


    /**
     * Sets the header image of the article
     * @param url the url of the image
     */
    fun setMainImage(url:String?){
        val urlF = if(!TextUtils.isEmpty(url)) url else MiscConstants.getRandDrawable(this)
       // val ani = AnimatedVectorDrawableCompat.create(this,R.drawable.animated_loader)
        val optionss = RequestOptions()
                .centerCrop()
                .placeholder(ani)
                //.error(MiscConstants.getRandDrawable(this))
                .priority(Priority.HIGH)
        ani?.start()
        Glide.with(this).load(urlF).apply(optionss)
                .into(profileFullImage)
    }

    //save variables to state
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLongArray("dbitems",dblist.toLongArray())
        outState.putBoolean("issaved",true)
        outState.putString("articleuser",articleuser)
        outState.putString("articlepermlink",articlepermlink)
        outState.putString("articletag",articletag)
        outState.putString(CentralConstants.passerArticleDefImg,articleCovImg)
        outState.putString(CentralConstants.passerArticleTitle,articleTitle)
        outState.putString(CentralConstants.passerArticleaDate,articleDate)
        outState.putString("permlinkToFind",permlinkToFind)
        outState.putInt("blockNumberToFind",blockNumberToFind)
        outState.putString("usernameToState",usernameToState)
        outState.putString("notifitype",notifitype?.name)
        /*var ar = ArrayList<FeedArticleDataHolder.FeedArticleHolder>()
        ar.add(articleFragment?.holder?.article!!)
        outState?.putSerializable("body",ar as Serializable)
        outState?.putSerializable("bodycomms",commentsFragment?.adapter?.getList() as Serializable)
    */
    }


    /**
     * called when tab related ui has to be setup
     */
    fun mysetup() {
        if (viewPager == null) viewPager = findViewById(R.id.pager)

        setupViewPager(viewPager)
        tabLayout = findViewById(R.id.tabs)
        tabLayout?.setupWithViewPager(viewPager)


        val lis = object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                if(p0?.position == 1 && !autoLoadOnce){
                    GetFeed(articletag as String,articleuser as String,articlepermlink as String,false)
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }
        }

        tabLayout?.addOnTabSelectedListener(lis)

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
            articleFragment = fo
        }

        val fi = fragmentManager.findFragmentByTag(makeFragmentName(vid, 1))
        if (fi != null && fi is CommentsFragment) {
            commentsFragment = fi
        }

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

        if (viewPagerAdapteradapter == null) {

            viewPagerAdapteradapter = ViewPagerAdapter(supportFragmentManager)
            //Chat
            viewPagerAdapteradapter?.addFragment(articleFragment as ArticleFragment, "Article", CentralConstants.FragmentTagFeed)
            viewPagerAdapteradapter?.addFragment(commentsFragment as CommentsFragment,"Comments",CentralConstants.FragmentTagComments)
            //viewPager?.offscreenPageLimit = 4
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


    }

    /**
     * Function to use get_content api to fetch the post for getting root author,permlin.tag attributes
     * @param username username of the person who wrote the post/comment
     * @param permlink permlink of the post/comment
     * This method will redirect to the GetFeed method
     *
     */
    fun GetContent(username:String,permlink:String){
        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = CentralConstants.baseUrl(this)
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

        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = CentralConstants.baseUrl(this)
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
        val url = CentralConstants.baseUrl(this)
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
                            val title = articlepermlink?.replace("-"," ")
                            supportActionBar?.title = title
                            activity_article_title.text = title
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


    /**
     * function called when feed has to be fetched using the state api
     * @param articlepermlink the permlink of the article
     * @param articletag the tag of the article
     * @param articleusername the username of the article
     * @param updateArticle if article has to be updated or not
     */
    fun GetFeed(articletag : String,articleusername : String,articlepermlink : String,updateArticle:Boolean = true){
        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = MiscConstants.getMainApiUrl(this)
        val d = MakeJsonRpc.getInstance()
        val g = Gson()

        val s = JsonObjectRequest(Request.Method.POST,url,d.getArticle(articletag,articleusername,articlepermlink),
                Response.Listener { response ->
                    val con = JsonRpcResultConversion(response,username as String, TypeOfRequest.feed,applicationContext,displayMet)
                    //con.ParseJsonBlog()
                    autoLoadOnce = true
                    val result = con.ParseReplies(articleusername+"/"+articlepermlink)
                    if(!result.isEmpty()){



                        commentsFragment?.clear()
                        commentsFragment?.setPermlinkToFind(permlinkToFind as String)
                        //send true for fragment to save json
                        if(updateArticle){
                            val art = result[0] as FeedArticleDataHolder.FeedArticleHolder
                            articleFragment?.displayMessage(art,true)
                            supportActionBar?.title = art.title
                            activity_article_title.text = art.title

                            if(TextUtils.isEmpty(articleCovImg)){
                                articleCovImg = art.displayImage
                                setMainImage(articleCovImg)
                            }

                            if(TextUtils.isEmpty(articleDate)){
                                articleDate = art.datespan
                                activity_date.text = articleDate
                            }
                        }
                        if(result.size > 2){
                            //var si = result.size - 1
                            val sb = result.subList(1,result.size)
                            //send true to save a list of comments
                            commentsFragment?.displayMessage( sb as List<FeedArticleDataHolder.CommentHolder>,true)
                        }
                        else if(result.size == 2){
                            //send true to save one comment
                            commentsFragment?.displayMessage(result[1] as FeedArticleDataHolder.CommentHolder,true)

                        }
                        else if(result.size == 1){

                        }
                        //stop the comments spinner def.
                        commentsFragment?.swipecommonactionsclass?.makeswipestopDef()

                    }

                }, Response.ErrorListener {
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }



    private fun imageBrowse() {
        //we make a call to our common picker
        ImagePickersWithHelpers.createImagePickDialog(applicationContext,this,this@ArticleActivity)
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //check if activity result is ok
        if (resultCode == Activity.RESULT_OK) {
            //if pickimage request then we open the image editor
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                //we create our own temp file
                imageUri = Uri.fromFile(ImagePickersWithHelpers.createTempFile(this))
                //pass data to ucrop
                UCrop.of(data.data, imageUri!!)
                        .withOptions(ImagePickersWithHelpers.getUcropOptions(this@ArticleActivity))
                        .start(this)
            } else if (requestCode == UCrop.REQUEST_CROP && data != null) {
                //else we begin the upload dialog
                imageUri = UCrop.getOutput(data)
                uploadImage(imageUri,this@ArticleActivity,layoutInflater,username,key,editTextCom,this)
            }

        }

        //check if it is a image click request code, then launch the editor again
        if (requestCode == ImagePickersWithHelpers.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            //pass data to ucrop
            //pass data to ucrop
            UCrop.of(imageUri!!, imageUri!!)
                    .withOptions(ImagePickersWithHelpers.getUcropOptions(this@ArticleActivity))
                    .start(this)
        }
        else if (resultCode == UCrop.RESULT_ERROR) {
            if(data != null){
                val cropError = UCrop.getError(data)
                Log.d("ucrop erro",if(cropError?.message != null) cropError.message else "" )
            }
        }




    }

}
