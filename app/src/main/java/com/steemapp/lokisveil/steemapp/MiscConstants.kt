package com.steemapp.lokisveil.steemapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.preference.PreferenceManager
import android.support.v7.view.ContextThemeWrapper
import android.text.format.DateUtils
import android.util.Log
import com.bumptech.glide.request.FutureTarget
import com.steemapp.lokisveil.steemapp.HelperClasses.Links
import com.steemapp.lokisveil.steemapp.HelperClasses.Physhy
import com.steemapp.lokisveil.steemapp.HelperClasses.ProxifyUrl
import com.steemapp.lokisveil.steemapp.HelperClasses.StaticMethodsMisc
import java.util.*

class MiscConstants{
    companion object{
        fun ApplyMyTheme(context: Context){
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            //var themval = sharedPref.getInt("theme_list_value",100)
            val themname = sharedPref.getString("theme_list","1")
            when(themname){
                "0" ->{
                    context.setTheme(R.style.Plaid_Home_Dark)
                }
                "1" ->{
                    context.setTheme(R.style.Plaid_Home)
                }
            }
        }

        fun ApplyMyThemeSettings(context: Context){
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            //var themval = sharedPref.getInt("theme_list_value",100)
            val themname = sharedPref.getString("theme_list","1")
            when(themname){
                "0" ->{
                    context.setTheme(R.style.Plaid_Alert_Dark)
                }
                "1" ->{
                    context.setTheme(R.style.Plaid_Alert)
                }
            }
        }

        fun ApplyMyThemeArticle(context: Context){
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            //var themval = sharedPref.getInt("theme_list_value",100)
            val themname = sharedPref.getString("theme_list","1")
            when(themname){
                "0" ->{
                    context.setTheme(R.style.Plaid_Translucent_DesignerNewsStory_Dark)
                }
                "1" ->{
                    context.setTheme(R.style.Plaid_Translucent_DesignerNewsStory)
                }
            }
        }

        fun ApplyMyThemePopUp(context: Context):Context {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            //var themval = sharedPref.getInt("theme_list_value",100)
            val themname = sharedPref.getString("theme_list","1")
            when(themname){
                "0" ->{
                    return ContextThemeWrapper(context, R.style.Plaid_Home_Dark)
                    //context.setTheme(R.style.Plaid_Translucent_DesignerNewsStory_Dark)
                }
                "1" ->{
                    return ContextThemeWrapper(context, R.style.Plaid_Home)
                    //context.setTheme(R.style.Plaid_Translucent_DesignerNewsStory)
                }
                else ->{
                    return ContextThemeWrapper(context, R.style.Plaid_Home)
                }
            }
        }


        fun ApplyMyThemeRet(context: Context):Context {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            //var themval = sharedPref.getInt("theme_list_value",100)
            val themname = sharedPref.getString("theme_list","1")
            when(themname){
                "0" ->{
                    return ContextThemeWrapper(context, R.style.Plaid_AlertDialog_AppCompat_Dark)
                    //context.setTheme(R.style.Plaid_Translucent_DesignerNewsStory_Dark)
                }
                "1" ->{
                    return ContextThemeWrapper(context, R.style.Plaid_AlertDialog_AppCompat)
                    //context.setTheme(R.style.Plaid_Translucent_DesignerNewsStory)
                }
                else ->{
                    return ContextThemeWrapper(context, R.style.Plaid_AlertDialog_AppCompat)
                }
            }
        }


        //new function to match users in a post
        /**
         * @param value value in which to find and replace users
         * @return String with all matches replaced
         */
        fun CorrectUsernamesK(value:String):String{
            //val regex = Regex("(@{1})([a-z0-9.-]{3,30})")
            val regex = Regex("(^|[^a-zA-Z0-9_!#\$%&*@＠/]|(^|[^a-zA-Z0-9_+~.-/#]))[@＠]([a-z][-.a-z\\d]+[a-z\\d])")
            val s = regex.replace(value) { matchResult ->
                " <a class=\"mylink\" href=\"steemer://@${matchResult.groupValues[3]}\" >@${matchResult.groupValues[3]}</a> "

            }

            return s
        }


        /**
         * matched links to the link class,
         * determines if image or link
         * checks for phishy links
         * tries to proxify image urls
         * @param value the article to render
         * returns the article rendered by it
         */
        fun CorrectLinks(value:String):String{
            //val regex = Regex("(@{1})([a-z0-9.-]{3,30})")
            val regex = Links.any()
            val s = regex.replace(value) { matchResult ->
                //matchResult.destructured.component2()
                if(Physhy.isPhyshy(matchResult.groupValues[2])){
                    matchResult.groupValues[1]+"<div class=\"phishy\">"+
                            "This seems phishy. Be careful. \n\n"+ matchResult.groupValues[2]+
                            "</div>"
                } else if(Links.image().matches(matchResult.value)){

                    if(Links.local().matches(matchResult.groupValues[2].trim())){
                        matchResult.groupValues[1]+"<img src=\"${ProxifyUrl.proxyurl(matchResult.groupValues[2].trim(),true)}\" />"
                    }
                    matchResult.groupValues[1]+"<img src=\"${matchResult.groupValues[2].trim()}\" />"
                } else{
                    matchResult.groupValues[1]+"<a href=\"${matchResult.groupValues[2].trim()}\">${matchResult.groupValues[2].trim()}</a>"
                }
                //" <a class=\"mylink\" href=\"steemer://@${matchResult.groupValues[3]}\" >@${matchResult.groupValues[3]}</a> "

            }

            return s
        }


        /**
         * matched hash tags in an article and links them
         * @param value article text
         * returns the article with tags rendered
         *
         */
        fun CorrectHashtags(value:String):String{
            val regex = "(^|\\s)(#[-a-z\\d]+)".toRegex()
            val s = regex.replace(value) { matchResult ->
                if ("/#[\\d]+$/".toRegex().matches(matchResult.value)) matchResult.value // Don't allow numbers to be tags
                val space = if("^\\s/".toRegex().matches(matchResult.value)) matchResult.groupValues[1] else ""
                val tag2 = matchResult.value.trim().substring(1)
                val tagLower = tag2.toLowerCase()
                "$space<a class=\"mytag\" href=\"steemer://#$tagLower\" >${matchResult.value}</a> "
                //"<a href=\"/trending/$tagLower\">$matchResult.value</a>"

            }

            return s
        }


        /**
         * Final one to run to clear all other remaining proxy urls
         * like the ones which contain dimensions
         * @param value article text
         * returns proxified article
         */
        fun CorrectOtherImgProxies(value:String):String{
            //val regex = Regex("(@{1})([a-z0-9.-]{3,30})")
            val urs = Links.urlwithoutex()
            val regex = Regex("[!]\\[([\\w\\d\\s().-]*)]\\(($urs)\\)")
            val s = regex.replace(value) { matchResult ->
                "![${matchResult.groupValues[1]}](${ProxifyUrl.proxyurl(matchResult.groupValues[3])})"

            }

            return s
        }




        //This will fetch the btimap image
        fun getBitmap(context:Context, sh: FutureTarget<Drawable>, isPfp:Boolean = false): Bitmap?{
            try{
                val g  = sh.get()
                //make a bitmap wit the height and width of the image
                val bt = Bitmap.createBitmap(g.intrinsicWidth, g.intrinsicHeight, Bitmap.Config.ARGB_8888)
                Log.d("bit map h and w ", "width : ${g.intrinsicWidth}, height: ${g.intrinsicHeight} ")
                //load into the canvas
                val canvas = Canvas(bt)
                //fetch device metrics and height,width
                val metrics = context.getResources().getDisplayMetrics()
                val dWidth = metrics.widthPixels
                val dHeight = metrics.heightPixels
                Log.d("dev height","dHeight:${metrics.widthPixels}, dWidth:${metrics.heightPixels}")

                var height = canvas.height

                var width = canvas.width

                Log.d("canvas height","cheight:$height, cwidth:$width")
                val dwf = dWidth/4
                val hwf = dHeight / 4
                //do not scale the image if it is a pfp
                if(!isPfp){
                    //now all the conditions do the same thing, with more testing
                    //it will be changed later on
                    //reduce the height and width of the image by 4
                    //this is done because the widget will crash if uses too much memory
                    if(width > dwf && height > hwf){
                        width /= 4
                        height /= 4
                    } else if(width > dwf){
                        width /= 4
                        height /= 4
                    } else if(height > hwf){
                        height /= 4
                        width /= 4
                    } else {

                    }
                    /*if(height > dHeight/4){
                        height /= 4
                    }*/
                }

                Log.d("n canvas height","cheight:$height, cwidth:$width")
                g.setBounds(0, 0, canvas.width, canvas.height)
                g.draw(canvas)
                //start recaliming memory after drawing
                canvas.setBitmap(null)
                //var re = Bitmap.createScaledBitmap(bt,width,height,false)
                //scale the bitmap
                val re = getResizedBitmap(bt,width,height)
                //bt.recycle()
                return  re
            } catch (ex:Exception){
                Log.d("getBitmapWidget",ex.message)
            }
            return null

        }

        fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {

            val width = bm.width
            val height = bm.height
            val scaleWidth = newWidth.toFloat() / width
            val scaleHeight = newHeight.toFloat() / height
            // CREATE A MATRIX FOR THE MANIPULATION
            val matrix = Matrix()
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight)

            // "RECREATE" THE NEW BITMAP
            val resizedBitmap = Bitmap.createBitmap(
                    bm, 0, 0, width, height, matrix, false)
            /*val resizedBitmap = Bitmap.createBitmap(
                    bm, 0, 0, newWidth, newHeight, matrix, false)*/
            bm.recycle()
            return resizedBitmap
        }


        /**
         * converts a date to relative date string
         * @param created date string
         * @param context the context passed
         * @return returns a relative date string
         */
        fun dateToRelDate(created:String,context: Context?):String{
            val dd = StaticMethodsMisc.FormatDateGmt(created)
            val du = dateToRelDate(dd.time,context)
            return du.toString()
        }

        /**
         * converts a date to relative date string
         * @param created date in java object
         * @param context the context passed
         * @return returns a relative date string
         */
        fun dateToRelDate(created: Date, context: Context?):String{
            val du = dateToRelDate(created.time,context)
            return du.toString()
        }

        /**
         * converts a date to relative date string
         * @param created date in timestamp
         * @param context the context passed
         * @return returns a relative date string
         */
        fun dateToRelDate(created:Long,context: Context?):CharSequence{
            return DateUtils.getRelativeDateTimeString(context,created, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0)
        }

        fun doubleTheId(id:Long,isBlog:Boolean):Long{
            val reId = Integer.parseInt(isBlog.toString().get(0).toString(),35)
            return (id.toString() + reId).toLong()
        }
    }
}