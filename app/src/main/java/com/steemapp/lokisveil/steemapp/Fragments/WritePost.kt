package com.steemapp.lokisveil.steemapp.Fragments

import android.app.Activity
import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.steemapp.lokisveil.steemapp.R
import com.steemapp.lokisveil.steemapp.Fragments.dummy.DummyContent
import com.steemapp.lokisveil.steemapp.Fragments.dummy.DummyContent.DummyItem
import com.steemapp.lokisveil.steemapp.HelperClasses.TextInputLayoutErrorHandler
import android.text.Selection.getSelectionEnd
import android.text.Selection.getSelectionStart
import com.steemapp.lokisveil.steemapp.CentralConstants
import com.steemapp.lokisveil.steemapp.Databases.drafts


/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class WritePost : Fragment() {
    // TODO: Customize parameters
    private var mColumnCount = 1
    private var mListener: OnListFragmentInteractionListener? = null
    internal var EditTextMainOne: EditText? = null
    internal var EditTextMainTwo: EditText? = null
    internal var EditTextMainThree: EditText? = null
    internal var CheckBoxMainOne: CheckBox? = null

    internal var EditTextMainOnehandler: TextInputLayoutErrorHandler? = null
    internal var EditTextMainTwohandler: TextInputLayoutErrorHandler? = null
    internal var EditTextMainThreehandler: TextInputLayoutErrorHandler? = null
    internal var view: View? = null
    internal var activity: Activity? = null
    internal var tvv: TextView? = null
    internal var context: Context? = null
    //android.support.v4.widget.ContentLoadingProgressBar progressBar;
    internal var progressBar: ProgressBar? = null
    internal var titleholder: TextView? = null
    var dbid : Long? = -1
    var dbcom : Long? = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            mColumnCount = arguments!!.getInt(ARG_COLUMN_COUNT)
            dbid = arguments?.getInt("db", -1)?.toLong()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list2, container, false)

        setUpCommons(view)
        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            if (mColumnCount <= 1) {
                view.layoutManager = LinearLayoutManager(context)
            } else {
                view.layoutManager = GridLayoutManager(context, mColumnCount)
            }
            //view.adapter = MyItemRecyclerViewAdapter(DummyContent.ITEMS, mListener)
        }
        return view
    }

    fun progress(visibility:Int){
        progressBar?.visibility = visibility
    }

    private fun setUpCommons(v: View) {
        //titleholder = v.findViewById(R.id.titleholder) as TextView
        tvv = v.findViewById(R.id.putErrorForQuestionHere) as TextView
        progressBar = v.findViewById(R.id.mprogressbar)
        //progressBar = (android.support.v4.widget.ContentLoadingProgressBar)v.findViewById(R.id.toolbar_progress_bar_flat);
        //progressBar.hide();
        /*val dab = v.findViewById(R.id.addaquestionfabminebc) as FloatingActionButton
        dab.setOnClickListener { view -> FabIntermediary(view) }*/
        activity = activity
        context = v?.context

        EditTextMainOne = v.findViewById(R.id.TextMainOne) as EditText
        EditTextMainTwo = v.findViewById(R.id.EditMainTextTwo) as EditText
        EditTextMainThree = v.findViewById(R.id.EditMainTextThree) as EditText

        /*EditTextMainThree?.setScroller(Scroller(context))
        EditTextMainThree?.setVerticalScrollBarEnabled(true)
        EditTextMainThree?.setMovementMethod(ScrollingMovementMethod())*/

        CheckBoxMainOne = v.findViewById(R.id.CheckboxMainOne) as CheckBox

        EditTextMainOnehandler = TextInputLayoutErrorHandler(v.findViewById(R.id.TextMainOneTextLayout) as TextInputLayout)
        EditTextMainTwohandler = TextInputLayoutErrorHandler(v.findViewById(R.id.EditMainTextTwoTextLayout) as TextInputLayout)
        EditTextMainThreehandler = TextInputLayoutErrorHandler(v.findViewById(R.id.EditMainTextThreeTextLayout) as TextInputLayout)

        if(dbid != dbcom){
            var dr = drafts(context as Context)
            var ops = dr.Get(dbid?.toInt())
            EditTextMainOne?.setText(ops.title)
            EditTextMainTwo?.setText(ops.tags)
            EditTextMainThree?.setText(ops.content)
        }
        /*cardviewOne = v.findViewById(R.id.cardviewOne) as CardView
        cardviewTwo = v.findViewById(R.id.cardviewTwo) as CardView
        cardviewThree = v.findViewById(R.id.cardviewThree) as CardView
        cardviewFour = v.findViewById(R.id.cardviewFour) as CardView*/
    }


    /*override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("body",univBody)
    }*/

    override fun onStop() {
        super.onStop()
        var tags = gettags()?.trim()
        var title = gettitle()?.trim()
        var content = getedittext()?.trim()
        var posttr = false
        if(content != null && !content.isNullOrEmpty() && content != ""){
            posttr = true

        }
        if(title != null && !title.isNullOrEmpty() && title != ""){
            posttr = true

        }
        if(tags != null && !tags.isNullOrEmpty() && tags != ""){
            posttr = true

        }
        if(posttr){
            var dr = drafts(context as Context)
            if(dbid == dbcom){
                dbid = dr.Insert(title,tags as String,content,"")
                Toast.makeText(context as Context,"Draft saved",Toast.LENGTH_LONG).show()
            } else{
                dr.update(dbid?.toInt()!!,title,tags as String,content,"")
                Toast.makeText(context as Context,"Draft updated",Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getedittext():String{
        return EditTextMainThree?.text.toString()
    }

    fun gettags():String?{

        return EditTextMainTwo?.text?.toString()
    }

    fun addtexturl(url:String,name:String){
        var image = "![$name]($url)"
        val start = Math.max(EditTextMainThree?.selectionStart!!, 0)
        val end = Math.max(EditTextMainThree?.selectionEnd!!, 0)
        EditTextMainThree?.text?.replace(Math.min(start, end), Math.max(start, end),
                image, 0, image.length)
    }

    fun gettitle():String{
        return EditTextMainOne?.text.toString()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            mListener = context
        } else {
            //throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
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
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: DummyItem)
    }

    companion object {

        // TODO: Customize parameter argument names
        private val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        fun newInstance(columnCount: Int): WritePost {
            val fragment = WritePost()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}
