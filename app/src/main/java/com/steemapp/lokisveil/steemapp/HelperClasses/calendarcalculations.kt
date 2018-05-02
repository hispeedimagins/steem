package com.steemapp.lokisveil.steemapp.HelperClasses

import android.text.format.DateUtils
import java.time.LocalDate
import java.time.Period
import java.util.*

/**
 * Created by boot on 2/6/2018.
 */
class calendarcalculations {
    private var calendarForData: Calendar? = null
    private var calendarForNow: Calendar? = null
    internal var timeZone: TimeZone? = null
    internal var offset: Int = 0
    init {

        calendarForNow = Calendar.getInstance()
        //timeZone = calendarForNow.getTimeZone();
        calendarForNow?.timeZone = TimeZone.getTimeZone("GMT")
        timeZone = TimeZone.getTimeZone("GMT")
        //timeZone = TimeZone.getDefault()

        offset = TimeZone.getDefault().getOffset(Date().time)
        //SimpleDateFormat format = new SimpleDateFormat();


    }

    fun getGmtToNormal():Calendar?{

        calendarForData?.add(Calendar.MILLISECOND,offset)

        return calendarForData
    }
    /*public calendarcalculations(Date datefdata){
        offset = TimeZone.getDefault().getOffset(new Date().getTime());
        calendarForData = new GregorianCalendar(timeZone);
        calendarForData.setTime(datefdata);
        calendarForData.add(Calendar.MILLISECOND,offset);
        calendarForNow = Calendar.getInstance();
    }*/


    fun setDateOfTheData(datefdatas: Date?) {

        if (datefdatas == null) {
            return
        }
        calendarForData = null
        calendarForData = GregorianCalendar(timeZone)

        calendarForData!!.time = datefdatas


        //calendarForData.add(Calendar.MILLISECOND,offset);
        //int i = 0;
        //i++;

    }


    /*public void setDateOfTheDataOffset(Date dateofdatas){
        calendarForData = null;
        calendarForData = new GregorianCalendar(timeZone);

        calendarForData.setTime(dateofdatas);
        calendarForData.add(Calendar.MILLISECOND,offset);
        int i = 0;
        i++;
    }*/


    fun returnCalendarOfData(): Calendar? {
        return calendarForData
    }


    fun getDateString(): String {
        if (calendarForData == null) {
            return ""
        }
        val ye = calendarForData!!.get(Calendar.YEAR)
        var dateString = calendarForData!!.get(Calendar.DATE).toString() + " " + calendarForData!!.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)

        if (ye != calendarForNow?.get(Calendar.YEAR)) {
            dateString += " " + ye.toString()
        }
        return dateString
    }


    fun getTimeString(): String {
        if (calendarForData == null) {
            return ""
        }
        val h = calendarForData!!.get(Calendar.HOUR_OF_DAY)
        val m = calendarForData!!.get(Calendar.MINUTE)

        return (if (h > 9) h else "0$h").toString() + ":" + (if (m > 9) m else "0$m").toString()
    }

    fun getDateTimeString(): String {


        return getDateString() + " at " + getTimeString()
    }
}