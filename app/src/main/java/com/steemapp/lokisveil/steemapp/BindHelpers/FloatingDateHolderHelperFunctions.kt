package com.steemapp.lokisveil.steemapp.BindHelpers

import android.app.Activity
import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.TextView
import com.steemapp.lokisveil.steemapp.AllRecyclerViewAdapter
import com.steemapp.lokisveil.steemapp.DataHolders.DateTypeAndStringHolder
import com.steemapp.lokisveil.steemapp.HelperClasses.calendarcalculations
import com.steemapp.lokisveil.steemapp.Interfaces.arvdinterface
import com.steemapp.lokisveil.steemapp.MyViewHolders.DateViewHolder
import com.steemapp.lokisveil.steemapp.R
import java.util.*

class FloatingDateHolder {
    internal var mStickyHeader: TextView
    internal var mStickyHeader2: TextView
    internal var context: Context
    //internal var fontSingleton: FontSingleton
    internal var cardView: CardView? = null
    internal var cardActive = false
    //Activity activity;
    internal var view: View? = null
    internal var recyclerView: RecyclerView
    //List<Object> mValues;
    private var prevdate: Calendar? = null
    //private final AllRecyclerViewAdapter adapter;
    //private DateTypeAndStringHolder holderOldUniv = null;
    private var holderOldUniv: String? = null
    private var holderOldUnivDate: DateTypeAndStringHolder? = null
    /*private TextView mStickyHeader;
    private TextView mStickyHeader2;*/
    //RecyclerView.Adapter adapter = null;
    private var arvdinterface: arvdinterface? = null

    internal var listener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = recyclerView!!.childCount
            //int totalItemCount = mValues.size();
            //int qpastVisiblesItems = recyclerView.findFirstVisibleItemPosition();
            var pastVisiblesItems = 0


            var previsbiggerthannext = true

            //scroll by hand is down
            if (dy > 0) {
                pastVisiblesItems = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                /*for(int i = pastVisiblesItems; i >= mValues.size(); i++){
                        Object re = mValues.get(i);
                        if(re instanceof String){
                            updateStickyHeader((String) mValues.get(i),(String) mValues.get(i),true);
                            break;
                        }
                    }*/

            } else {
                //down
                pastVisiblesItems = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                /*if (mValues.size() > 0) {
                        for(int i = pastVisiblesItems; i >= 0; i--){

                            Object re = mValues.get(i);
                            if(re instanceof String){
                                updateStickyHeader((String) mValues.get(i),(String) mValues.get(i),true);

                                break;
                            }
                        }
                    }*/

                previsbiggerthannext = false
            }


            if (arvdinterface != null && arvdinterface!!.getSize() > 0) {
                for (i in pastVisiblesItems downTo 0) {

                    val re = arvdinterface!!.getObject(i)
                    if (re is DateTypeAndStringHolder) {
                        val h = re as DateTypeAndStringHolder
                        updateStickyHeader(h.sDate!!, h.sDate!!, h, previsbiggerthannext)



                        break


                    }
                }
            }

        }
    }

    constructor(con: Context, view: View, recyclerView: RecyclerView) {

        this.view = view

        this.mStickyHeader = view.findViewById(R.id.textview_sticky_header_section) as TextView
        this.mStickyHeader2 = view.findViewById(R.id.textview_sticky_header_section_2) as TextView
        this.context = con

        //this.fontSingleton = f
        this.recyclerView = recyclerView

        //this.mValues = objects;
        this.prevdate = Calendar.getInstance()
        //mStickyHeader = (TextView)view.findViewById(R.id.textview_sticky_header_section);
        //mStickyHeader2 = (TextView)view.findViewById(R.id.textview_sticky_header_section_2);
        //this.adapter = recyclerView.getAdapter();
        animateToEnd()
        this.arvdinterface = null
        this.recyclerView.addOnScrollListener(listener)

    }

    constructor(con: Context, view: View, recyclerView: RecyclerView, recyclerViewAdapter: AllRecyclerViewAdapter) {

        this.view = view

        this.cardView = view.findViewById(R.id.cardviewchat)
        this.mStickyHeader = view.findViewById(R.id.textview_sticky_header_section)
        this.mStickyHeader2 = view.findViewById(R.id.textview_sticky_header_section_2)
        this.context = con

        //this.fontSingleton = f
        this.recyclerView = recyclerView

        //this.mValues = objects;
        this.prevdate = Calendar.getInstance()
        //mStickyHeader = (TextView)view.findViewById(R.id.textview_sticky_header_section);
        //mStickyHeader2 = (TextView)view.findViewById(R.id.textview_sticky_header_section_2);
        //this.adapter = recyclerView.getAdapter();
        animateToEnd()

        this.arvdinterface = recyclerViewAdapter
        this.recyclerView.addOnScrollListener(listener)

    }


    constructor(con: Context, view: Activity, recyclerView: RecyclerView) {


        this.mStickyHeader = view.findViewById(R.id.textview_sticky_header_section) as TextView
        this.mStickyHeader2 = view.findViewById(R.id.textview_sticky_header_section_2) as TextView
        this.context = con

        //this.fontSingleton = f
        this.recyclerView = recyclerView

        //this.mValues = objects;
        this.prevdate = Calendar.getInstance()
        //mStickyHeader = (TextView)view.findViewById(R.id.textview_sticky_header_section);
        //mStickyHeader2 = (TextView)view.findViewById(R.id.textview_sticky_header_section_2);
        //this.adapter = recyclerView.getAdapter();
        animateToEnd()
        this.arvdinterface = null
        //this.adapter = null;
        this.recyclerView.addOnScrollListener(listener)

    }


    fun add(datetosend: String, cd: Calendar): DateTypeAndStringHolder {
        val h = DateTypeAndStringHolder()
        h.cdate = cd
        h.sDate = datetosend
        arvdinterface!!.add(h)
        arvdinterface!!.notifyitemcinserted(arvdinterface!!.getSize())
        /*if(arvdinterface != null){
            arvdinterface.notifydatachanged();
        }*/
        if (!cardActive) {
            cardView?.visibility = View.VISIBLE
        }
        return h
        //adapter.notifyDataSetChanged();
    }


    fun checktimeandadd(oldcc: Date) {
        val oldc = GregorianCalendar(TimeZone.getTimeZone("GMT"))
        oldc.time = oldcc



        if (getZeroDate(prevdate).compareTo(getZeroDate(oldc)) < 0) {
            val nd = oldc.get(Calendar.DATE).toString() + " " + oldc.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
            val pd = prevdate!!.get(Calendar.DATE).toString() + " " + prevdate!!.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)

            val h = add(oldc.get(Calendar.DATE).toString() + " " + oldc.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US), oldc)

            updateStickyHeader(pd, nd, h, false)
        } else if (arvdinterface != null && (arvdinterface!!.getSize() === 0 || arvdinterface!!.getSize() === 1)) {
            val nd = oldc.get(Calendar.DATE).toString() + " " + oldc.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
            val pd = prevdate!!.get(Calendar.DATE).toString() + " " + prevdate!!.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)

            val h = add(oldc.get(Calendar.DATE).toString() + " " + oldc.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US), oldc)

            updateStickyHeader(pd, nd, h, false)
        }
        prevdate = oldc
    }


    fun checktimeandaddQuestions(oldcc: Date) {

        /*Calendar oldc = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        oldc.setTime(oldcc);*/
        val cal = calendarcalculations()
        cal.setDateOfTheData(oldcc)
        val oldc = cal.returnCalendarOfData()
        //oldc.setTime(oldcc);

        var com = getZeroDate(prevdate).compareTo(getZeroDate(oldc))
        if ( com > 0) {
            val nd = oldc?.get(Calendar.DATE).toString() + " " + oldc?.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
            val pd = prevdate!!.get(Calendar.DATE).toString() + " " + prevdate!!.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)

            val h = add(oldc?.get(Calendar.DATE).toString() + " " + oldc?.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US), oldc!!)

            updateStickyHeader(pd, nd, h, false)
        }

        else if ( com < 0) {
            val nd = oldc?.get(Calendar.DATE).toString() + " " + oldc?.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
            val pd = prevdate!!.get(Calendar.DATE).toString() + " " + prevdate!!.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)

            val h = add(oldc?.get(Calendar.DATE).toString() + " " + oldc?.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US), oldc!!)

            updateStickyHeader(pd, nd, h, true)
        }

        else if (arvdinterface != null && (arvdinterface!!.getSize() === 0 || arvdinterface!!.getSize() === 1)) {
            val nd = oldc?.get(Calendar.DATE).toString() + " " + oldc?.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
            val pd = prevdate!!.get(Calendar.DATE).toString() + " " + prevdate!!.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)

            val h = add(oldc?.get(Calendar.DATE).toString() + " " + oldc?.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US), oldc!!)

            updateStickyHeader(pd, nd, h, false)
        }
        prevdate = oldc
    }

    fun getZeroDate(od: Calendar?): Calendar {
        val calendar = Calendar.getInstance()

        calendar.time = od!!.time
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar
    }

    fun BindDateToDateTitle(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder !is DateViewHolder) {
            return
        }
        val my = holder as DateViewHolder
        if (arvdinterface!!.getObject(position) !is DateTypeAndStringHolder) {
            return
        }
        val pd = arvdinterface!!.getObject(position) as DateTypeAndStringHolder
        my.date = pd.sDate
        my.dh = pd.cdate
        //my.dateofchatstart.setTypeface(fontSingleton.getCinzelRegular())
        my.dateofchatstart.setText(pd.sDate)
    }


    private fun animateToEnd() {
        val animation = TranslateAnimation(0f, 0f, 0f, 200f)
        animation.duration = 1000
        animation.fillAfter = true
        mStickyHeader.startAnimation(animation)
        mStickyHeader2.startAnimation(animation)
    }


    private fun updateStickyHeader(prevdate: String, newdate: String, holder: DateTypeAndStringHolder, previsbiggerthannext: Boolean) {

        if (holderOldUnivDate != null && holder.equals(holderOldUnivDate)) {
            val animation = TranslateAnimation(0f, 0f, 0f, 5f)
            animation.duration = 1000
            animation.fillAfter = false
            mStickyHeader.startAnimation(animation)
            return
        }


        //String dateStringprev = String.valueOf(prevdate.get(Calendar.DATE)) +" "+ prevdate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
        //String dateStringnext = String.valueOf(newdate.get(Calendar.DATE)) +" "+ newdate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
        //prev
        //mStickyHeader.typeface = fontSingleton.getCinzelRegular()
        //mStickyHeader2.typeface = fontSingleton.getCinzelRegular()





        mStickyHeader.text = newdate

        //next
        if (holderOldUniv == null) {
            mStickyHeader2.text = prevdate
        } else {
            mStickyHeader2.text = holderOldUniv
        }

        holderOldUniv = holder.sDate

        holderOldUnivDate = holder
        /*int stickyHeaderHeight = mStickyHeader.getHeight();
        if (stickyHeaderHeight == 0) {
            stickyHeaderHeight = mStickyHeader.getMeasuredHeight();
        }*/

        val animation = TranslateAnimation(0f, 0f, 0f, 0f)
        animation.duration = 1000
        animation.fillAfter = true

        val animationz = TranslateAnimation(0f, 0f, 0f, -100f)
        animationz.duration = 1000
        animationz.fillAfter = true

        val animationzd = TranslateAnimation(0f, 0f, 0f, 100f)
        animationzd.duration = 1000
        animationzd.fillAfter = true




        if (previsbiggerthannext) {
            mStickyHeader.startAnimation(animation)
            mStickyHeader2.startAnimation(animationz)

        } else {
            mStickyHeader.startAnimation(animation)
            mStickyHeader2.startAnimation(animationzd)
        }


    }
}