package com.steemapp.lokisveil.steemapp

import android.content.Context
import android.preference.PreferenceManager
import android.support.v7.view.ContextThemeWrapper
import android.view.View
import android.widget.PopupMenu

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


    }
}