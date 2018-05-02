package com.steemapp.lokisveil.steemapp.Interfaces;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.steemapp.lokisveil.steemapp.CentralConstants;
import com.steemapp.lokisveil.steemapp.OpenOtherGuyBlog;

/**
 * Created by boot on 3/24/2018.
 */

public class WebAppInterface {
    Context mContext;
    ArticleActivityInterface articleActivityInterface;
    /** Instantiate the interface and set the context */
    public WebAppInterface(Context c) {
        mContext = c;
    }
    public WebAppInterface(ArticleActivityInterface c) {
        articleActivityInterface = c;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void UserClicked(String user){
        articleActivityInterface.UserClicked(user);
        /*Intent i = new Intent(mContext, OpenOtherGuyBlog.class);
        i.putExtra(CentralConstants.OtherGuyNamePasser,user);
        mContext.startActivity(i);*/
        //Toast.makeText(mContext, user, Toast.LENGTH_SHORT).show();
    }

}
