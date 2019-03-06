package com.insteem.ipfreely.steem.BindHelpers

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.insteem.ipfreely.steem.DataHolders.DateTypeAndStringHolder
import com.insteem.ipfreely.steem.HelperClasses.calendarcalculations
import com.insteem.ipfreely.steem.Interfaces.arvdinterface
import com.insteem.ipfreely.steem.MyViewHolders.DateViewHolder
import com.insteem.ipfreely.steem.R
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
    var prevdate: Calendar? = null
    //private final AllRecyclerViewAdapter adapter;
    //private DateTypeAndStringHolder holderOldUniv = null;
    private var holderOldUniv: String? = null
    private var holderOldUnivDate: DateTypeAndStringHolder? = null
    /*private TextView mStickyHeader;
    private TextView mStickyHeader2;*/
    //RecyclerView.Adapter adapter = null;
    private var arvdinterface: arvdinterface? = null

    internal var listener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }

        //listner to the recycleview so the date can be updated in the frame layout
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = recyclerView!!.childCount
            //int totalItemCount = mValues.size();
            //int qpastVisiblesItems = recyclerView.findFirstVisibleItemPosition();
            var pastVisiblesItems = 0


            var previsbiggerthannext = true

            //scroll by hand is down
            if (dy > 0) {
                pastVisiblesItems = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                previsbiggerthannext = true

            } else {
                //down
                //use the last visible items, may change it back to the first visible items
                pastVisiblesItems = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()


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

    //the only constructor used atm
    constructor(con: Context, view: View, recyclerView: RecyclerView, recyclerViewAdapter: arvdinterface) {

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


    //Used to add a DateYpeAndString into the recyclerview
    /*
    for adding the datetypeandstring object to the class
     */
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


    //Used to check if the date is in the past,now, or for an empty view
    fun checktimeandaddQuestions(oldcc: Date) {

        /*Calendar oldc = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        oldc.setTime(oldcc);*/
        val cal = calendarcalculations()
        cal.setDateOfTheData(oldcc)
        val oldc = cal.returnCalendarOfData()
        //val oldc = cal.getGmtToNormal()
        var ad = oldc?.get(Calendar.DATE)
        //oldc.setTime(oldcc);

        //new behaviour, if the date is the same do not do a thing
        val com = getZeroDate(prevdate).compareTo(getZeroDate(oldc))
        if ( com > 0 && prevdate?.get(Calendar.DATE) != oldc?.get(Calendar.DATE)) {

            AddChangedDate(prevdate!!,oldc!!,false)
        }

        else if ( com < 0  && prevdate?.get(Calendar.DATE) != oldc?.get(Calendar.DATE)) {

            AddChangedDate(prevdate!!,oldc!!,true)
        }

        else if (arvdinterface != null && (arvdinterface!!.getSize() === 0 || arvdinterface!!.getSize() === 1)) {

            AddChangedDate(prevdate!!,oldc!!,false)
        }
        prevdate = oldc
    }


    //calculates and adds the correct date
    fun AddChangedDate(prevdate:Calendar,oldc:Calendar,previsbiggerthannext: Boolean){
        val nd = oldc?.get(Calendar.DATE).toString() + " " + oldc?.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
        val pd = prevdate!!.get(Calendar.DATE).toString() + " " + prevdate!!.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())

        val h = add(nd, oldc)

        updateStickyHeader(pd, nd, h, previsbiggerthannext)
    }

    //for getting only the date back, no time,hour,minute second or milliseconds.
    fun getZeroDate(od: Calendar?): Calendar {
        val calendar = Calendar.getInstance()

        //hour of the day seems to be needed for now
        calendar.timeInMillis = od!!.timeInMillis
        //calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar
    }

    //Bind function called by the main recyclerview adapter to bind the view
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


    //for animating the view
    private fun animateToEnd() {
        val animation = TranslateAnimation(0f, 0f, 0f, 200f)
        animation.duration = 1000
        animation.fillAfter = true
        mStickyHeader.startAnimation(animation)
        mStickyHeader2.startAnimation(animation)
    }


    //called for updating the main sticky frame layout
    private fun updateStickyHeader(prevdate: String, newdate: String, holder: DateTypeAndStringHolder, previsbiggerthannext: Boolean) {

        //this function updates the date while scrolling and animtes it up or down after stopping
        if (holderOldUnivDate != null && holder.equals(holderOldUnivDate)) {
            var animmoy = 10f
            if(previsbiggerthannext){
                animmoy *= -1
            } else{
                /*animmoy = 10f*/
            }
            val animation = TranslateAnimation(0f, 0f, 0f, animmoy)
            animation.duration = 700
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