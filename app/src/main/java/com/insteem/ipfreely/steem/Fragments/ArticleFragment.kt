package com.insteem.ipfreely.steem.Fragments


/*import `in`.uncod.android.bypass.Bypass*/

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import br.tiagohm.markdownview.css.styles.Github
import br.tiagohm.markdownview.js.ExternalScript
import com.android.volley.Response
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.insteem.ipfreely.steem.*
import com.insteem.ipfreely.steem.DataHolders.FeedArticleDataHolder
import com.insteem.ipfreely.steem.Databases.RequestsDatabase
import com.insteem.ipfreely.steem.Databases.drafts
import com.insteem.ipfreely.steem.Enums.TypeOfRequest
import com.insteem.ipfreely.steem.HelperClasses.*
import com.insteem.ipfreely.steem.Interfaces.ArticleActivityInterface
import com.insteem.ipfreely.steem.Interfaces.GlobalInterface
import com.insteem.ipfreely.steem.Interfaces.WebAppInterface
import com.insteem.ipfreely.steem.MyViewHolders.ArticleViewHolder
import com.insteem.ipfreely.steem.SteemBackend.Config.Enums.MyOperationTypes
import com.insteem.ipfreely.steem.SteemBackend.Config.Models.AccountName
import com.insteem.ipfreely.steem.SteemBackend.Config.Models.Permlink
import com.insteem.ipfreely.steem.SteemBackend.Config.Operations.CustomJsonOperation
import com.insteem.ipfreely.steem.SteemBackend.Config.Operations.Operation
import com.insteem.ipfreely.steem.SteemBackend.Config.Operations.ReblogOperation
import com.insteem.ipfreely.steem.SteemBackend.Config.Operations.VoteOperation
import com.insteem.ipfreely.steem.jsonclasses.Block
import org.json.JSONObject
import java.util.*
import java.util.regex.Pattern

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ArticleFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ArticleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ArticleFragment : Fragment() , GlobalInterface {
    override fun notifyRequestMadeSuccess() {

    }

    override fun notifyRequestMadeError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getObjectMine(): Any {
        return holder?.article!!
    }

    override fun getContextMine(): Context {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActivityMine(): Activity {
        return getActivity() as Activity
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var activity: Context? = null
    internal var view: View? = null
    private var loading = false
    var username : String? = null
    var key : String? = null
    var holder : ArticleViewHolder? = null
    //var webview : WebView? = null
    var swipe : swipecommonactionsclass? = null
    private var articleActivityInterface : ArticleActivityInterface? = null
    var univBody:String?=null
    var metrics : DisplayMetrics = DisplayMetrics()
    var texcolormine : Int? = 0
    var cardbackground : Int? = 0
    var dblist = ArrayList<Long>()
    private var adapter: AllRecyclerViewAdapter? = null

    //internal var swipeRefreshLayout: SwipeRefreshLayout? = null
    internal var recyclerView: RecyclerView? = null

    //private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*if(savedInstanceState?.getString("body") != null){
            univBody = savedInstanceState?.getString("body")
            setWebView(univBody as String)
        }*/
        if (arguments != null) {
            mParam1 = arguments?.getString(ARG_PARAM1)
            mParam2 = arguments?.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        //view = inflater!!.inflate(R.layout.fragment_article, container, false)
        view = inflater.inflate(R.layout.fragment_article, container, false)
        //initialize fabhider so the FAB hides on scroll
        var fa = FabHider(null,articleActivityInterface?.getFab(),view?.findViewById(R.id.nestedscroll))
        
        /*if(view == null){
            view = inflater!!.inflate(R.layout.fragment_article, container, false)
            recyclerView = view?.findViewById(R.id.list)
            adapter = AllRecyclerViewAdapter(getActivity() as FragmentActivity, ArrayList(), recyclerView as RecyclerView, view as View, AdapterToUseFor.article,this)
            //adapter?.setEmptyView(view?.findViewById(R.id.toDoEmptyView))

            recyclerView?.setItemAnimator(DefaultItemAnimator())
            recyclerView?.setAdapter(adapter)
        }*/
        var attrs  = intArrayOf(R.attr.textColorMine,R.attr.cardBackgroundColor)
        var ta = context?.obtainStyledAttributes(attrs)
        texcolormine  = ta?.getResourceId(0, android.R.color.black)
        cardbackground = ta?.getResourceId(attrs.size - 1,android.R.color.white)

        ta?.recycle()
        texcolormine = ContextCompat.getColor(context!!,texcolormine!!)
        cardbackground = ContextCompat.getColor(context!!,cardbackground!!)
        //val hexColor = String.format("#%06X", 0xFFFFFF and texcol)
        activity = getActivity()?.applicationContext
        val sharedPreferences = context?.getSharedPreferences(CentralConstants.sharedprefname, 0)
        username = sharedPreferences?.getString(CentralConstants.username, null)
        key = sharedPreferences?.getString(CentralConstants.key, null)

        //check if holder is null before instantiating it
        if(holder == null) holder = ArticleViewHolder(view as View)
        //webview = view?.findViewById(R.id.article_webview)
        //holder.shareTextView = view?.findViewById(R.id.shareTextView) as TextView
        //GetView()
        //var swiplay =

        var sl = view?.findViewById(R.id.activity_feed_swipe_refresh_layout) as SwipeRefreshLayout
        swipe = swipecommonactionsclass(sl)

        swipe?.makeswiperun()
        sl?.setOnRefreshListener {
            articleActivityInterface?.ReloadData()
        }
        metrics = DisplayMetrics()
        getActivity()?.windowManager?.defaultDisplay?.getMetrics(metrics)

        //check if savedinstance exists
        if(savedInstanceState != null){
            //displayMessage(savedInstanceState?.getParcelable("body") as FeedArticleDataHolder.FeedArticleHolder)
            //fetch database ids
            var lar = savedInstanceState.getLongArray("dbitems")
            //re add the to the variable
            dblist.addAll(lar.toList())
            var gson = Gson()
            var db = RequestsDatabase(context!!)
            for(x in lar){
                var req = db.GetAllQuestions(x)
                if(req != null){
                    //var jso = JSONObject(req.json)
                    if(req.otherInfo == "article"){
                        //add the data
                        displayMessage(gson.fromJson(req.json,FeedArticleDataHolder.FeedArticleHolder::class.java))
                    }
                }
            }
        }

        //if saved data is not null display it
        /*var dbData = articleActivityInterface?.getDbData()
        if(dbData != null){
            displayMessage(dbData,false)
        }*/
        articleActivityInterface?.loadDataNow()

        return view
    }

    /*// TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (articleActivityInterface != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }*/

    override fun onStop() {
        super.onStop()
        //webview = null
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        //webview = view?.findViewById(R.id.article_webview)
    }

    //save current dbitems list to fetch later
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean("issaved",true)
        outState.putLongArray("dbitems",dblist.toLongArray())

    }



    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ArticleActivityInterface) {
            articleActivityInterface = context
        } else {
            //throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        articleActivityInterface = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ArticleFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): ArticleFragment {
            val fragment = ArticleFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    fun clear(){
        adapter?.clear()
    }

    fun displayMessage(result: FeedArticleDataHolder.CommentHolder) {

        adapter?.add(result)

    }
    fun displayMessage(result: List<FeedArticleDataHolder.CommentHolder>) {


        loading = false

        for (a in result){

            displayMessage(a)
        }
        swipe?.makeswipestop()
        //adapter.questionListFunctions.add(questionsList)

    }


    fun fabclicked(fragmentManager: FragmentManager){
        var mod = ModalBottomSheetMy()
        mod.show(fragmentManager, "answer dialog")
        mod.showsDialog = true
        mod.setUsername(username)
        mod.setArticleViewHolder(holder?.article)
        mod.context = context
        mod.setMyOperationTypes(MyOperationTypes.comment)
        //mod.makeCommentRequest()
    }

    fun sameasbind(holder : ArticleViewHolder){
        //val holder : ArticleViewHolder = mholder as ArticleViewHolder
        //holder.article = adaptedcomms.getObject(position) as FeedArticleDataHolder.FeedArticleHolder
        var pay = holder.article?.pending_payout_value
        if((holder.article?.already_paid.orEmpty() != "0.000 SBD")){
            pay = holder.article?.already_paid
        }


        holder.article_comments?.text = holder.article?.children.toString()

        if(holder.article?.uservoted != null && holder.article?.uservoted as Boolean == true){
            holder.article_likes?.setTextColor(ContextCompat.getColor(activity as Context, R.color.colorAccent))
            holder.article_likes?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_likeint_24px,0,0,0)
        }



        // decide if edit is to be used
        if(username != null && username == holder.article?.author){
            holder.article_edit?.visibility = View.VISIBLE
            holder.article_edit?.setOnClickListener {
                if(context != null){
                    var tags = ""
                    if(holder.article?.tags != null){
                        var sb = StringBuilder()
                        //flatten the tags
                        for(x in holder.article?.tags!!){
                            sb.append("$x ")
                        }
                        tags = sb.toString().trim()
                        var db = drafts(context!!)
                        //json object for saving additional information
                        var jso = JSONObject()
                        jso.put("isedit",true)
                        jso.put("permlink",holder?.article?.permlink)
                        jso.put("category",holder?.article?.category)
                        //save to db
                        var dbid = db.Insert(holder?.article?.title!!,tags,holder?.article?.body!!,jso.toString())
                        //add to intent and start the view
                        val myIntent = Intent(context, Post::class.java)
                        myIntent.putExtra("db", dbid)
                        myIntent.putExtra("isedit",true)
                        myIntent.putExtra("permlink",holder?.article?.permlink)
                        myIntent.putExtra("category",holder?.article?.category)
                        /* myIntent.putExtra("tag", holder.article?.category)
                         myIntent.putExtra("permlink", holder.article?.permlink)*/
                        context?.startActivity(myIntent)
                    }
                }




            }
        }
        else{
            holder.article_edit?.visibility = View.GONE
        }

        holder.article_likes?.text = holder.article?.netVotes.toString()
        //holder.article_name?.text = holder.article?.author
        holder.article_name?.text = holder.article?.displayName //"${holder.article?.author} (${StaticMethodsMisc.CalculateRepScore(holder.article?.authorreputation)})"
        if(holder.article?.followsYou!!){
            holder.article_name?.setTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        } else{
            holder.article_name?.setTextColor(texcolormine!!)
        }
        holder.article_payout?.text = pay
        //val d = Calendar.getInstance()
        //d.time = holder.article?.createdcon
        //holder.article_date?.text = holder.article?.datespan
        /*val options = RequestOptions()
                .centerCrop()
                //.error(R.drawable.error)
                .priority(Priority.HIGH)
                .circleCrop()
        Glide.with(activity as Context).load(CentralConstants.GetFeedImageUrl(holder.article?.author)).apply(options)
               // .placeholder(R.drawable.common_full_open_on_phone)
                .into(holder.article_pfp as ImageView)*/


        if(holder.article?.reblogBy == null || holder.article?.reblogBy?.isEmpty() as Boolean){
            holder.article_resteemed_by?.visibility = View.GONE
        }
        else{
            holder.article_resteemed_by?.visibility = View.VISIBLE
            holder.article_resteemed_by?.text = "by "+ holder.article?.reblogBy?.get(0)
            holder.article_resteemed_by?.setOnClickListener {
                val i = Intent(context, OpenOtherGuyBlog::class.java)
                i.putExtra(CentralConstants.OtherGuyNamePasser,holder.article?.reblogBy?.get(0))
                context?.startActivity(i)
            }
        }
        //holder.article_tag?.text = "in "+ holder.article?.category
        /*holder.article_tag?.setOnClickListener {
            var it = Intent(context,MainTags::class.java)
            it.putExtra(CentralConstants.MainRequest,"get_discussions_by_trending")
            it.putExtra(CentralConstants.MainTag,holder.article?.category)
            it.putExtra(CentralConstants.OriginalRequest,"trending")
            context?.startActivity(it)

        }*/
        //holder.article_title?.text = holder.article?.title
        //val and = AndDown()
        //val s : String = and.markdownToHtml(holder.article?.body, AndDown.HOEDOWN_EXT_QUOTE, 0)
        //var bod = StaticMethodsMisc.CorrectMarkDown(holder.article?.body,holder.article?.image)


        //adding without anddown
        var bod = holder.article?.body


        /*bod = StaticMethodsMisc.CorrectBeforeMainLinks(bod,holder.article?.links)
        bod = StaticMethodsMisc.CorrectMarkDownUsers(bod,holder.article?.users)*/
        //bod = StaticMethodsMisc.CorrectMarkDownUsers(bod,holder.article?.users)
        /*if(holder.article?.image != null){
            for(img in holder.article?.image as List<String>){
                val check = "($img)"
                if(bod != null && !(bod?.contains(check))){
                    val reps = "![name]$check"
                    bod = bod?.replace(img,reps)
                }
            }
        }*/

        //var s : String = and.markdownToHtml(holder.article?.body, AndDown.HOEDOWN_EXT_AUTOLINK, 0)
        //var s : String = ""

        //Added because some REGEXs do not identify from the start,
        var s = "<br/>"
        //s += "      "
        //var s : String = bod!!
        s += bod!!
        /*if(holder.article?.format == "html"){
            //s = bod
        }
        else{
            *//*val flavour = CommonMarkFlavourDescriptor()
            val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(bod)
            s = HtmlGenerator(bod, parsedTree, flavour).generateHtml()*//*
            //s  = and.markdownToHtml(bod, AndDown.HOEDOWN_EXT_AUTOLINK, 0)
        }*/

        //If tags exist, add them to show them as files under
        if(holder.article?.tags != null){
            s += "<br/>"
            s += "<br/>"
            s += "<hr/>"
            s += "Filed Under The Tags - "
            s += "<br/> "
            var sb = StringBuilder()
            for(x in holder.article?.tags!!){
                sb.append("#$x ")
            }
            s += sb.toString()
        }


        //s = StaticMethodsMisc.CorrectAfterMainImages(s)
        //s = StaticMethodsMisc.CorrectBeforeMainLinks(s,holder.article?.links)
        //s = StaticMethodsMisc.CorrectMarkDownUsers(s)

        //pass through steemit's regexs for links, hash tags, image proxies and username
        s = MiscConstants.CorrectUsernamesK(s)
        s = MiscConstants.CorrectHashtags(s)
        s = MiscConstants.CorrectLinks(s)
        s = MiscConstants.CorrectOtherImgProxies(s)
        //s = StaticMethodsMisc.CorrectBr(s)

        //s = StaticMethodsMisc.CorrectNewLine(s)
        /*s += "<style>*{max-width:100%}</style>"
        s += "<script type=\"text/javascript\">\n" +
                "    function UserClicked(user) {\n" +
                "        Android.UserClicked(user);\n" +
                "    }\n" +
                "</script>"*/
        /*if(holder.article?.image != null){
            for(img in holder.article?.image as List<String>){
                val check = "src=\"$img\""
                if(!s.contains(check)){
                    val reps = "<img $check />"
                    s.replace(img,reps)
                }
            }
        }*/


        holder.article_name?.setOnClickListener {
            val i = Intent(context, OpenOtherGuyBlog::class.java)
            i.putExtra(CentralConstants.OtherGuyNamePasser,holder.article?.author)
            context?.startActivity(i)
        }

        holder.article_like?.setOnClickListener {
            // Toast.makeText(con,"Processing. Please wait....",Toast.LENGTH_LONG).show()
            //ForReturningQuestionsLite q = item;
            var articles = holder.article as FeedArticleDataHolder.FeedArticleHolder
            //var s : SteemJ = SteemJ()
            //s.vote(AccountName(holder.article?.author), Permlink(holder.article?.permlink),10000)
            var vop : VoteOperation = VoteOperation(AccountName(username), AccountName(articles.author), Permlink(articles.permlink))
            vop.weight = 100

            val weight = VoteWeightThenVote(context as Context,getActivity() as FragmentActivity,vop,null, 0,holder.progressbar,null)
            weight.makeDialog()
        }

        holder.article_reblog_now?.setOnClickListener {
            //Toast.makeText(con,"Processing. Please wait....",Toast.LENGTH_LONG).show()
            //ForReturningQuestionsLite q = item;
            var articles = holder.article as FeedArticleDataHolder.FeedArticleHolder
            var vop = ReblogOperation(AccountName(username), AccountName(articles.author), Permlink(articles.permlink))
            var al = ArrayList<AccountName>()
            al.add(AccountName(username))
            var cus = CustomJsonOperation(null,al,"follow",vop.toJson())
            var obs = Response.Listener<JSONObject> { response ->
                //var articles = mholder.article as FeedArticleDataHolder.FeedArticleHolder
                val gson = Gson()
                var ress = gson.fromJson<Block.BlockAdded>(response.toString(), Block.BlockAdded::class.java)
                if(ress != null && ress.result != null ){
                    /*Runnable { run { Toast.makeText(con,"$name has upvoted ${vop.author.name}",Toast.LENGTH_LONG).show() } }*/
                    Toast.makeText(context,"Reblogged ${vop.permlink.link}", Toast.LENGTH_LONG).show()

                }
            }
            var list  = ArrayList<Operation>()
            list.add(cus)
            //var bloc = GetDynamicAndBlock(con ,adaptedcomms,position,mholder,null,cus,obs,"Reblogged ${vop.permlink.link}",MyOperationTypes.reblog)
            var bloc = GetDynamicAndBlock(context as Context ,null, 0,list,"Reblogged ${vop.permlink.link}",MyOperationTypes.reblog,holder.progressbar,null)
            bloc.GetDynamicGlobalProperties()
        }


        var css =  Github()
        //var st = Regex("[!]\\[([\\w\\d\\s.-]*)]\\(((https?:\\/\\/(?:[-a-zA-Z0-9\\._]*[-a-zA-Z0-9])(?::\\d{2,5})?(?:[\\/\\?#](?:[^\"<>\\]\\[\\(\\)]*[^\"<>\\]\\[\\(\\)])?)?))\\)")
        //css.addFontFace("MyFont", "condensed", "italic", "bold", "url('myfont.ttf')");
        /*css.addMedia("screen and (min-width: 1281px)");
        css.addRule("h1", "color: orange");
        css.endMedia();*/
        var texcolin = String.format("#%06X", 0xFFFFFF and texcolormine!!)
        var cardcolin = String.format("#%06X", 0xFFFFFF and cardbackground!!)
        css.addRule("body", "padding: 0 !important")
        css.addRule("*", "color:$texcolin","background:$cardcolin")
        css.addRule("pre","overflow:scroll")
        css.addRule("img","width:100%","height:auto", "vertical-align: middle", "border: 0", "max-width: auto")

        //"sub", "position: relative", "font-size: 75%", "line-height: 0", "vertical-align: baseline", "bottom: -0.25em"
        //css.addRule("sub","vertical-align: sub","font-size: smaller","bottom:-0.5em")
        css.addRule("sub","bottom:-1em","line-height: 1")
        holder.markdownView.addStyleSheet( css)
        holder.markdownView.addJavascriptInterface(WebAppInterface(articleActivityInterface), "Android")

        //var js = ExternalScript("https://cdnjs.cloudflare.com/ajax/libs/remarkable/1.7.1/remarkable.min.js", true, false);

        //this is where we load the remarkable library
        var js = ExternalScript("file:///android_asset/remarkable.js", false, false)
        holder.markdownView.addJavascript(js);

        //all js is now in a seperate file
        var ijs = ExternalScript("file:///android_asset/site.js", false, false)
        holder.markdownView.addJavascript(ijs);

        //holder.markdownView.addJavascript(ExternalScript(sc,false,false,"text/javascript"))

        //load the javascript mess above with a div with the id md, this is where we
        //load the processed markdown
        holder.markdownView.loadMarkdown("<div id=\"md\"></div> \n")
        //holder.markdownView.loadMarkdown("<div id=\"md\"></div> \n" + sc)

        //when loading finished this is called
        //load the markdown in it
        holder.markdownView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                //this makes it an object which is access normally on the js end in the browser
                //did not expect this to happen
                var jso = JsonObject()
                jso.addProperty("body",s)
                var gs = Gson()
                var j = gs.toJson(jso)
                holder.markdownView.loadUrl("javascript:loadremark($j);")
                swipe?.makeswipestop()
            }



            //In the future we can use this method
            /*@TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                var req = request?.url
                return super.shouldOverrideUrlLoading(view, request)
            }*/


            //Method is deprecated but works till nougat and below, Do not know about oreo
            //need to test
            //used for compatibility
            override fun shouldOverrideUrlLoading(view: WebView?, request: String?): Boolean {
                //check if it begins with steemit or busy
                //Should more be added?
                if(request?.startsWith("https://steemit.com")!! ||
                        request?.startsWith("https://busy.org")!!){
                    linkclicker(request)
                    return true
                } else if(request?.startsWith("steemer://#")!!){
                    articleActivityInterface?.TagClicked((request.split("#"))[1])
                    return true
                } else if(request?.startsWith("steemer://@")!!){
                    articleActivityInterface?.UserClicked((request.split("@"))[1])
                    return true
                } else{
                    //If we do not know this link
                    //open in browser
                    var uri = Uri.parse(request)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                    return true
                }
                //return super.shouldOverrideUrlLoading(view, request)
            }
        }
        //holder.markdownView.loadMarkdown(s)
        //holder.markdownView.loadDataWithBaseURL("",s,null,"UTF-8",null)

        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
           // tvDocument.setText(Html.fromHtml(bodyData,Html.FROM_HTML_MODE_LEGACY))
            //holder.article_summary?.text = Html.fromHtml(s,Html.FROM_HTML_MODE_LEGACY)
            webview?.loadDataWithBaseURL("",s,null,"UTF-8",null)
        } else {
            //tvDocument.setText(Html.fromHtml(bodyData))
            //holder.article_summary?.text = Html.fromHtml(s)
            webview?.loadDataWithBaseURL("",s,null,"UTF-8",null)
        }
        webview?.addJavascriptInterface( WebAppInterface(articleActivityInterface), "Android")

        //webview?.settings?.setUseWideViewPort(true)
        //webview?.settings?.setLoadWithOverviewMode(true)
        webview?.settings?.javaScriptEnabled = true*/
        //webview?.addJavascriptInterface()

        /*val bypess = Bypass(context)

        val str = bypess.markdownToSpannable(holder.article?.body)
        holder.article_summary?.text = str
        holder.article_summary?.movementMethod = LinkMovementMethod.getInstance()*/

        //holder.article_summary?.text = and.markdownToHtml(holder.article?.body, AndDown.HOEDOWN_EXT_QUOTE, 0)
        //holder.article_summary_markdown.showMarkdown(holder.article?.body)
        /*Glide.with(activity).load(holder.article?.image?.get(0))
                .placeholder(R.drawable.common_full_open_on_phone)
                .into(holder.article_image)*/
    }


    //extracts link data
    //is not perfect, has to be changed
    fun linkclicker(href:String){
        val pat = Pattern.compile("(https?://)(.*)/(.*)/(.*)/(.*)")
        val matcher = pat.matcher(href)
        var tag: String? = null
        var name: String? = null
        var link: String? = null
        while (matcher.find()) {
            tag = href.substring(matcher.start(3), matcher.end(3))
            name = href.substring(matcher.start(4) + 1, matcher.end(4))
            link = href.substring(matcher.start(5), matcher.end(5))
        }

        if (tag != null && name != null && link != null) {
            articleActivityInterface?.linkClicked(tag, name, link)
        }
    }

    fun setWebView(s:String){
        holder?.openarticle?.removeAllViews()
        univBody = s
        var objects = StaticMethodsMisc.ConvertTextToList(s,holder?.article?.image)
        if(objects != null){
            val lparams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            //var bypass =  Bypass(context)
            for(x in objects){
                if(x is String && context != null){
                    var tx = TextView(context)
                    tx.setTextColor(ContextCompat.getColor(context as Context,R.color.black))
                    lparams.setMargins(GetPx(5f),0,GetPx(5f),0)
                    tx.layoutParams = lparams
                    /*tx.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.result_font))*/
                    tx.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f)
                    //tx.setSingleLine(false)
                    /*var webview = WebView(context)
                    webview.layoutParams = lparams*/

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                        //var string = bypass.markdownToSpannable(x)
                        // tvDocument.setText(Html.fromHtml(bodyData,Html.FROM_HTML_MODE_LEGACY))
                        //tx.text = string
                        tx.text = Html.fromHtml(x,Html.FROM_HTML_MODE_LEGACY,null,MyLiTagHandler())

                        //webview?.loadDataWithBaseURL("",s,null,"UTF-8",null)
                    } else {
                        //var string = bypass.markdownToSpannable(x)
                        //tvDocument.setText(Html.fromHtml(bodyData))
                        //tx.text = string
                        tx.text = Html.fromHtml(x,null,MyLiTagHandler())

                        //webview?.loadDataWithBaseURL("",s,null,"UTF-8",null)
                    }
                    tx.movementMethod = LinkMovementMethod()
                    holder?.openarticle?.addView(tx)
                    //holder?.openarticle?.addView(webview)
                }
                else if(x is Int && context != null){

                    var iamge = ImageView(context)
                    iamge.layoutParams = lparams
                    iamge.adjustViewBounds = true
                    val options = RequestOptions()

                            .placeholder(R.drawable.ic_all_inclusive_black_24px)
                            //.error(R.drawable.error)
                            .priority(Priority.HIGH)
                    holder?.openarticle?.addView(iamge)
                    var url = holder?.article?.image!![x]
                    Glide.with(context as Context).load(url).apply(options)
                            // .placeholder(R.drawable.common_full_open_on_phone)
                            .into(iamge)

                }
            }
        }
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            // tvDocument.setText(Html.fromHtml(bodyData,Html.FROM_HTML_MODE_LEGACY))
            //holder.article_summary?.text = Html.fromHtml(s,Html.FROM_HTML_MODE_LEGACY)
            webview?.loadDataWithBaseURL("",s,null,"UTF-8",null)
        } else {
            //tvDocument.setText(Html.fromHtml(bodyData))
            //holder.article_summary?.text = Html.fromHtml(s)
            webview?.loadDataWithBaseURL("",s,null,"UTF-8",null)
        }
        webview?.addJavascriptInterface( WebAppInterface(articleActivityInterface), "Android")*/

        //webview?.settings?.setUseWideViewPort(true)
        //webview?.settings?.setLoadWithOverviewMode(true)
        //webview?.settings?.javaScriptEnabled = true
    }


    fun SetDisplayMetrics(displayMetrics: DisplayMetrics){

    }
    fun GetPx(dp : Float) : Int{

        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp + 5,
                metrics
        ).toInt()
    }

    fun GetLayourParamsMargin(mar : Int) : LinearLayout.LayoutParams{
        var param : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        param.leftMargin = mar
        //param.setMargins(mar, 0, 5, 0)

        return param
    }
    fun displayMessage(result: FeedArticleDataHolder.FeedArticleHolder,save:Boolean = false) {

        //this is where we save data to the database for saveinstance
        //the id is saved to instance
        if(save){
            if(context != null){
                val req = RequestsDatabase(context!!)
                var ad = req.Insert(com.insteem.ipfreely.steem.DataHolders.Request(json = Gson().toJson(result) ,dateLong = Date().time, typeOfRequest = TypeOfRequest.blog.name,otherInfo = "article"))
                if(ad > 0){
                    dblist.add(ad)
                }
            }
        }
        //swipecommonactionsclass?.makeswipestop()
        //adapter.questionListFunctions.add(message)
        swipe?.makeswipestop()

        var con = FollowApiConstants.getInstance()


        //if holder is null we instantiate it if the new is not null
        //else we save the data and wait for create to be called
        if(holder == null && view != null) {
            holder = ArticleViewHolder(view as View)
        }
        if(holder != null && context != null){
            holder?.article = result
            if(!con.following.isEmpty() && con.following.any { p -> p.following == holder?.article?.author }){
                holder?.article?.useFollow = MyOperationTypes.unfollow
            }
            else {
                holder?.article?.useFollow = MyOperationTypes.follow
            }
            sameasbind(holder as ArticleViewHolder)
            val articlepop = ArticlePopUpMenu(context as Context,holder?.shareTextView as View,"${CentralConstants.baseUrlView}@${holder?.article?.author}","${CentralConstants.baseUrlView}${holder?.article?.category}/@${holder?.article?.author}/${holder?.article?.permlink}",holder?.article?.useFollow,username,holder?.article?.author,null,0,holder?.progressbar,this,holder?.article?.activeVotes,true)

        }


    }










}// Required empty public constructor
