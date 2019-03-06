package com.insteem.ipfreely.steem.HelperClasses

import java.util.*

/**
 * Created by boot on 2/6/2018.
 */
class calendarcalculations {
    var calendarForData: Calendar? = null
    var calendarForNow: Calendar? = null
    internal var timeZone: TimeZone? = null
    internal var offset: Int = 0
    init {

        calendarForNow = Calendar.getInstance()
        //timeZone = calendarForNow.getTimeZone();
        timeZone = TimeZone.getTimeZone("GMT")
        calendarForNow?.timeZone = timeZone

        //timeZone = TimeZone.getDefault()

        offset = TimeZone.getDefault().getOffset(Date().time)
        //SimpleDateFormat format = new SimpleDateFormat();


    }

    fun getGmtToNormal():Calendar?{
        //var bc = Date(calendarForData?.timeInMillis!!)
        calendarForData?.add(Calendar.MILLISECOND,offset)
        calendarForData?.timeZone = TimeZone.getDefault()
        //var ad = Date(calendarForData?.timeInMillis!!)

        return calendarForData
    }

    fun getGmtToNormal(calender:Calendar?):Calendar?{
        //var bc = Date(calendarForData?.timeInMillis!!)
        calender?.add(Calendar.MILLISECOND,offset)
        calender?.timeZone = TimeZone.getDefault()
        //var ad = Date(calendarForData?.timeInMillis!!)

        return calender
    }
    /*public calendarcalculations(Date datefdata){
        offset = TimeZone.getDefault().getOffset(new Date().getTime());
        calendarForData = new GregorianCalendar(timeZone);
        calendarForData.setTime(datefdata);
        calendarForData.add(Calendar.MILLISECOND,offset);
        calendarForNow = Calendar.getInstance();
    }*/


    fun setDateOfTheData(datefdatas: Date?,useDefault:Boolean = false) {

        if (datefdatas == null) {
            return
        }
        calendarForData = null
        //do not load gmt calender if not default
        if(useDefault) calendarForData = GregorianCalendar(TimeZone.getDefault()) else calendarForData = GregorianCalendar(timeZone)

        calendarForData!!.time = datefdatas


        //calendarForData.add(Calendar.MILLISECOND,offset);
        //int i = 0;
        //i++;

    }

    fun setDateOfTheData(datefdatas: Long?,useDefault:Boolean = false) {

        if (datefdatas == null) {
            return
        }
        calendarForData = null
        //do not load gmt calender if not default
        if(useDefault) calendarForData = GregorianCalendar(TimeZone.getDefault()) else calendarForData = GregorianCalendar(timeZone)


        calendarForData!!.timeInMillis = datefdatas


        //calendarForData.add(Calendar.MILLISECOND,offset);
        //int i = 0;
        //i++;

    }

    fun getDiffBetweenDates():Long{
        return (calendarForNow?.timeInMillis!! - getGmtToNormal(calendarForData)?.timeInMillis!!)
    }


    fun setDateOfTheDataOffset(dateofdatas:Date?){
        if (dateofdatas == null) {
            return
        }
        calendarForData = null
        calendarForData = GregorianCalendar(timeZone)

        calendarForData!!.time = dateofdatas
        getGmtToNormal()
    }


    fun returnCalendarOfData(): Calendar? {
        return calendarForData
    }


    fun getDateString(): String {
        if (calendarForData == null) {
            return ""
        }
        val ye = calendarForData!!.get(Calendar.YEAR)
        var dateString = calendarForData!!.get(Calendar.DATE).toString() + " " + calendarForData!!.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())

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