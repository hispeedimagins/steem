package com.insteem.ipfreely.steem;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by boot on 2/2/2018.
 */

public class VolleyRequest {
    private static VolleyRequest ourInstance ;
    private RequestQueue mRequestQueue;
    //private ImageLoader mImageLoader;
    private static Context mCtx;


    private VolleyRequest(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();


    }



    public static synchronized VolleyRequest getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new VolleyRequest(context);
        }
        return ourInstance;
    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }



}
