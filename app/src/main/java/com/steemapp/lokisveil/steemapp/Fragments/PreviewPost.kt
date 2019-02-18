package com.steemapp.lokisveil.steemapp.Fragments

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.commonsware.cwac.anddown.AndDown
import com.steemapp.lokisveil.steemapp.Fragments.dummy.DummyContent.DummyItem
import com.steemapp.lokisveil.steemapp.HelperClasses.StaticMethodsMisc
import com.steemapp.lokisveil.steemapp.R
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.CondenserUtils

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [PreviewPost.OnListFragmentInteractionListener] interface.
 */
class PreviewPost : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null
    var openarticle : LinearLayout? = null
    var article_title : TextView? = null
    var metrics : DisplayMetrics = DisplayMetrics()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list3, container, false)

        // Set the adapter
        openarticle  = view.findViewById(R.id.openarticle)
        article_title  = view.findViewById(R.id.article_title)
        metrics = DisplayMetrics()
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                //adapter = MyItemRecyclerViewAdapter2(DummyContent.ITEMS, listener)
            }
        }
        return view
    }


    fun display(s:String,title:String?){

        article_title?.text = title
        val links = CondenserUtils.extractLinksFromContent(s)
        var images:List<String> = ArrayList()
        var linksex:List<String> = ArrayList()
        for (link in links) {
            if (link.matches("/(https?:\\/\\/.*\\.(?:png|jpg))/i".toRegex()) || link.endsWith(".png") || link.endsWith("jpg")) {
                images += link
            } else {
                linksex += link
            }
        }
        setWebView(s,images)
    }



    fun setWebView(s:String,images:List<String>){
        openarticle?.removeAllViews()
        val and = AndDown()
        //val s : String = and.markdownToHtml(holder.article?.body, AndDown.HOEDOWN_EXT_QUOTE, 0)
        var bod = StaticMethodsMisc.CorrectMarkDown(s,images)
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
       /* if(holder.article?.format == "html"){
            s = bod
        }
        else{
            s  = and.markdownToHtml(bod, AndDown.HOEDOWN_EXT_AUTOLINK, AndDown.HOEDOWN_HTML_SKIP_HTML)
        }*/
        var s  = and.markdownToHtml(bod, AndDown.HOEDOWN_EXT_AUTOLINK, AndDown.HOEDOWN_HTML_SKIP_HTML)
        //s = StaticMethodsMisc.CorrectMarkDownUsers(s,holder.article?.users)

        //univBody = s
        var objects = StaticMethodsMisc.ConvertTextToList(s,images)
        if(objects != null){
            val lparams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

            for(x in objects){
                if(x is String){
                    var tx = TextView(context)
                    tx.setTextColor(ContextCompat.getColor(context as Context,R.color.black))
                    lparams.setMargins(GetPx(5f),0,GetPx(5f),0)
                    tx.layoutParams = lparams
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        // tvDocument.setText(Html.fromHtml(bodyData,Html.FROM_HTML_MODE_LEGACY))
                        tx.text = Html.fromHtml(x, Html.FROM_HTML_MODE_LEGACY)

                        //webview?.loadDataWithBaseURL("",s,null,"UTF-8",null)
                    } else {
                        //tvDocument.setText(Html.fromHtml(bodyData))
                        tx.text = Html.fromHtml(s)

                        //webview?.loadDataWithBaseURL("",s,null,"UTF-8",null)
                    }
                    tx.movementMethod = LinkMovementMethod()
                    openarticle?.addView(tx)
                }
                else if(x is Int){

                    var iamge = ImageView(context)
                    iamge.layoutParams = lparams
                    iamge.adjustViewBounds = true
                    val options = RequestOptions()

                            .placeholder(R.drawable.ic_all_inclusive_black_24px)
                            //.error(R.drawable.error)
                            .priority(Priority.HIGH)
                    openarticle?.addView(iamge)
                    var url = images!![x]
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


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            //throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: DummyItem?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                PreviewPost().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
