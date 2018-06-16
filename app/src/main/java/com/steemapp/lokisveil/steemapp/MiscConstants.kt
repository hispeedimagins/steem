package com.steemapp.lokisveil.steemapp

import android.content.Context
import android.preference.PreferenceManager
import android.support.v7.view.ContextThemeWrapper
import android.view.View
import android.widget.PopupMenu
import com.steemapp.lokisveil.steemapp.HelperClasses.Links
import com.steemapp.lokisveil.steemapp.HelperClasses.Physhy
import com.steemapp.lokisveil.steemapp.HelperClasses.ProxifyUrl
import java.util.regex.Pattern

class MiscConstants{
    companion object{
        fun ApplyMyTheme(context: Context){
            var sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            //var themval = sharedPref.getInt("theme_list_value",100)
            var themname = sharedPref.getString("theme_list","1")
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
            var sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            //var themval = sharedPref.getInt("theme_list_value",100)
            var themname = sharedPref.getString("theme_list","1")
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
            var sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            //var themval = sharedPref.getInt("theme_list_value",100)
            var themname = sharedPref.getString("theme_list","1")
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
            var sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            //var themval = sharedPref.getInt("theme_list_value",100)
            var themname = sharedPref.getString("theme_list","1")
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
            var sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            //var themval = sharedPref.getInt("theme_list_value",100)
            var themname = sharedPref.getString("theme_list","1")
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
            var s = regex.replace(value,{matchResult ->
                " <a class=\"mylink\" href=\"steemer://@${matchResult.groupValues[3]}\" >@${matchResult.groupValues[3]}</a> "

            })

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
            var s = regex.replace(value,{matchResult ->
                //matchResult.destructured.component2()
                if(Physhy.isPhyshy(matchResult.groupValues[2])){
                    matchResult.groupValues[1]+"<div class=\"phishy\">"+
                           "This seems phishy. Be careful. \n\n"+ matchResult.groupValues[2]+
                    "</div>"
                }
                else if(Links.image().matches(matchResult.value)){

                    if(Links.local().matches(matchResult.groupValues[2].trim())){
                        matchResult.groupValues[1]+"<img src=\"${ProxifyUrl.proxyurl(matchResult.groupValues[2].trim(),true)}\" />"
                    }
                    matchResult.groupValues[1]+"<img src=\"${matchResult.groupValues[2].trim()}\" />"
                } else{
                    matchResult.groupValues[1]+"<a href=\"${matchResult.groupValues[2].trim()}\">${matchResult.groupValues[2].trim()}</a>"
                }
                //" <a class=\"mylink\" href=\"steemer://@${matchResult.groupValues[3]}\" >@${matchResult.groupValues[3]}</a> "

            })

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
            var s = regex.replace(value,{matchResult ->
                if ("/#[\\d]+$/".toRegex().matches(matchResult.value)) matchResult.value; // Don't allow numbers to be tags
                val space = if("^\\s/".toRegex().matches(matchResult.value)) matchResult.groupValues[1] else ""
                val tag2 = matchResult.value.trim().substring(1);
                val tagLower = tag2.toLowerCase();
                "$space<a class=\"mytag\" href=\"steemer://#$tagLower\" >${matchResult.value}</a> "
                //"<a href=\"/trending/$tagLower\">$matchResult.value</a>"

            })

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
            val regex = Regex("[!]\\[([\\w\\d\\s.]*)]\\(($urs)\\)")
            var s = regex.replace(value,{matchResult ->
                "![${matchResult.groupValues[1]}](${ProxifyUrl.proxyurl(matchResult.groupValues[3])})"

            })

            return s
        }




    }
}