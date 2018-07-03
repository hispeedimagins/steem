package com.steemapp.lokisveil.steemapp.HelperClasses

import android.content.Context
import android.content.SharedPreferences
import android.opengl.Visibility
import android.os.AsyncTask
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.steemapp.lokisveil.steemapp.*
import com.steemapp.lokisveil.steemapp.BindHelpers.FeedHelperFunctions
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Databases.FollowingDatabase
import com.steemapp.lokisveil.steemapp.Enums.TypeOfRequest
import com.steemapp.lokisveil.steemapp.Interfaces.GlobalInterface
import com.steemapp.lokisveil.steemapp.Interfaces.arvdinterface
import com.steemapp.lokisveil.steemapp.MyViewHolders.ArticleViewHolder
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.OperationType
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.PrivateKeyType
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.AccountName
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.BlockId
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.Permlink
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.SignedTransaction
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.CustomJsonOperation
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.Operation
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.ReblogOperation
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.VoteOperation
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.SteemJConfig
import com.steemapp.lokisveil.steemapp.jsonclasses.Block
import com.steemapp.lokisveil.steemapp.jsonclasses.feed
import com.steemapp.lokisveil.steemapp.jsonclasses.prof
import org.apache.commons.lang3.tuple.ImmutablePair
import org.json.JSONObject
import java.util.ArrayList
import java.util.concurrent.ExecutionException

/**
 * Created by boot on 2/25/2018.
 */
class GetDynamicAndBlock(context: Context, adapter: arvdinterface?, position : Int, ops : List<Operation>, toastString: String, myOperationTypes: MyOperationTypes) {


    var progressbars:ProgressBar? = null
    var globalInterfaces: GlobalInterface? = null
    var mglobalInterfaces: GlobalInterface? = null
    constructor(context: Context, adapter: arvdinterface?, position : Int, ops : List<Operation>, toastString: String, myOperationTypes: MyOperationTypes,progressbar:ProgressBar?,globalInterface: GlobalInterface?):this(context, adapter, position, ops , toastString, myOperationTypes){
        progressbars = progressbar
        globalInterfaces = globalInterface
    }
    constructor(context: Context, adapter: arvdinterface?, position : Int, ops : List<Operation>, toastString: String, myOperationTypes: MyOperationTypes,progressbar:ProgressBar?,globalInterface: GlobalInterface?,mglobalInterface: GlobalInterface?):this(context, adapter, position, ops , toastString, myOperationTypes){
        progressbars = progressbar
        globalInterfaces = globalInterface
        mglobalInterfaces = mglobalInterface
    }
    /*val articleHolder = mholder
    val commentHolder = commentHolder*/
    val myOperationTypes = myOperationTypes
    val ops = ops
    val adaptedcomms: arvdinterface? = adapter
    val position = position
    val applicationContext : Context = context
    val sharedpref : SharedPreferences = applicationContext.getSharedPreferences(CentralConstants.sharedprefname,0)

    var key = sharedpref.getString(CentralConstants.key,null)
    /*val listner = listner*/
    val toastString = toastString
    val votefullper = 2


    fun GetDynamicGlobalProperties(){
        //val queue = Volley.newRequestQueue(context)

        //swipecommonactionsclass?.makeswiperun()
        Toast.makeText(applicationContext,"Processing. Please wait....", Toast.LENGTH_LONG).show()
        if(progressbars != null){
            progressbars?.visibility = VISIBLE
        }
        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()

        val s = JsonObjectRequest(Request.Method.POST,url,d.globalProperties,
                Response.Listener { response ->

                    val gson = Gson()
                    var parse : Block.DynamicGlobalProperties = gson.fromJson(response.toString(), Block.DynamicGlobalProperties::class.java)
                    if(parse != null && parse.result != null){
                        GetBlock(parse.result.lastIrreversibleBlockNum)
                    }

                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }

    fun GetBlock(blocknumber : Int){
        //val queue = Volley.newRequestQueue(context)

        //swipecommonactionsclass?.makeswiperun()

        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()

        val s = JsonObjectRequest(Request.Method.POST,url,d.getBlock(blocknumber),
                Response.Listener { response ->

                    val gson = Gson()
                    var parse : Block.GetBlockResult = gson.fromJson(response.toString(),Block.GetBlockResult::class.java)
                    if(parse != null){

                        if(key == null){
                            key = sharedpref.getString(CentralConstants.key,null)
                        }
                        var block : BlockId = BlockId(parse.result.blockId)

                        var signedtra = SignedTransaction(block,ops,null)
                        var fors = forewarder(ops, signedtra,key)
                        var sigas = signalrstarterasync().execute(fors)




 /*                       class someTask() : AsyncTask<Void, Void, String>() {
                            override fun doInBackground(vararg params: Void?): String? {
                                // ...
                                var block : BlockId = BlockId(parse.result.blockId)
                                *//*var vop : VoteOperation = VoteOperation(AccountName(name), AccountName(acticle.author), Permlink(acticle.permlink))
                                vop.weight = 10000
                                val operations = ArrayList<Operation>()
                                operations.add(vop)*//*
                                *//*val operations = ArrayList<Operation>()
                                operations.add(ops)*//*
                                var signedtra = SignedTransaction(block,ops,null)
                                var fors = forewarder(ops, signedtra,key)
                                //var sigas = signalrstarterasync().execute(fors)
                                if(key == null){
                                    key = sharedpref.getString(CentralConstants.key,null)
                                }

                                signedtra.signMy(SteemJConfig.getInstance().getChainId(), ImmutablePair(PrivateKeyType.POSTING,key))
                                BroadcastSynchronous(ops,signedtra)
                                return ""
                            }
                        }
                        someTask().execute()*/
                    }

                }, Response.ErrorListener {
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }



    fun BroadcastSynchronous(vop : List<Operation>,signedtra : SignedTransaction){
        //val queue = Volley.newRequestQueue(context)

        //swipecommonactionsclass?.makeswiperun()

        val volleyre : VolleyRequest = VolleyRequest.getInstance(applicationContext)
        //val url = "https://api.steemjs.com/get_feed?account=$username&limit=10"
        val url = CentralConstants.baseUrl
        val d = MakeJsonRpc.getInstance()
        //var re1j : JSONObject? = getOperations(vop[0],signedtra)
        var re1j : JSONObject? = null
        if(myOperationTypes == MyOperationTypes.comment || myOperationTypes == MyOperationTypes.post){
            re1j = getOperations(vop,signedtra)
        }
        else if(myOperationTypes == MyOperationTypes.edit_comment){
            re1j = getOperationsEdit(vop,signedtra)
        }
        else{
            re1j = getOperations(vop[0],signedtra)
        }
        /*try{
            re1j  = d.networkBroadcaseSynchronous(signedtra.expirationDate.dateTime,"vote",vop.author.name.toString(),vop.permlink.link.toString(),vop.voter.name.toString(),"10000",signedtra.refBlockNum.toString(),signedtra.refBlockPrefix.toString(),signedtra.signatures[0])

        }
        catch (exception : java.lang.Exception){
            var ex = exception
            var exm = ex.message
        }*/

        val s = JsonObjectRequest(Request.Method.POST,url,re1j,
                //listner
                Response.Listener { response ->
                    Log.d("dynamicend","responseinvokes " + response.toString())
                    progressbars?.visibility = GONE

                    val gson = Gson()
                    val results = response.toString()
                    var ress = gson.fromJson<Block.BlockAdded>(results,Block.BlockAdded::class.java)
                    if(ress != null && ress.result != null || results.contains("skip & skip_transaction_dupe_check") ){
                        globalInterfaces?.notifyRequestMadeSuccess()
                        mglobalInterfaces?.notifyRequestMadeSuccess()
                        Toast.makeText(applicationContext,toastString, Toast.LENGTH_LONG).show()
                        Log.d("dynamicend","toast was reached for " + toastString)
                        if(myOperationTypes == MyOperationTypes.vote){
                            //vop as VoteOperation
                            if(adaptedcomms != null){

                                val holder = adaptedcomms.getObject(position)
                                if(holder is FeedArticleDataHolder.FeedArticleHolder){
                                    holder?.uservoted = true
                                    holder?.netVotes = holder?.netVotes + 1
                                }
                                else if (holder is FeedArticleDataHolder.CommentHolder){
                                    holder?.uservoted = true
                                    holder?.netVotes = +1
                                }

                                //holder?.uservoted = true
                                //holder.article_likes?.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
                                adaptedcomms.notifyitemcchanged(position,holder)
                                Log.d("dynamicend","link was reached for " + toastString)
                            }

                        }
                        else if(myOperationTypes == MyOperationTypes.follow || myOperationTypes == MyOperationTypes.unfollow){
                            if(adaptedcomms != null){
                                val holder = adaptedcomms.getObject(position)
                                if(holder is FeedArticleDataHolder.FeedArticleHolder){
                                    holder?.useFollow = myOperationTypes

                                }
                                else if (holder is FeedArticleDataHolder.CommentHolder){
                                    holder?.useFollow = myOperationTypes
                                }

                                //holder?.uservoted = true
                                //holder.article_likes?.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
                                adaptedcomms?.notifyitemcchanged(position,holder)


                            }
                            var invo = vop[0] as CustomJsonOperation
                            if(myOperationTypes == MyOperationTypes.follow){
                                var s : List<String> = ArrayList()
                                s += "BLOG"
                                var pfa = prof.Resultfp(invo.name,invo.follower,s)
                                var fc = FollowingDatabase(applicationContext)
                                fc.Insert(pfa)
                                var fpc = FollowApiConstants.getInstance()
                                //fpc.following += pfa
                               // fpc.following.add(pfa)
                                fpc.AddToFollowing(pfa)
                            }
                            else if(myOperationTypes == MyOperationTypes.unfollow){
                                var fc = FollowingDatabase(applicationContext)
                                fc.deleteContact(invo.follower)
                                var fou = FollowApiConstants.getInstance().following.find { f -> f.following == invo.follower }
                                FollowApiConstants.getInstance().following.remove(fou)
                            }

                        }

                    }
                    else if(results.contains("unknown key:unknown key:")){
                        Toast.makeText(applicationContext,"Cannot be edited with this key.", Toast.LENGTH_LONG).show()
                        globalInterfaces?.notifyRequestMadeError()
                        mglobalInterfaces?.notifyRequestMadeError()
                    }
                    else{
                        globalInterfaces?.notifyRequestMadeError()
                        mglobalInterfaces?.notifyRequestMadeError()
                    }

                }, Response.ErrorListener {
            globalInterfaces?.notifyRequestMadeError()
            //swipecommonactionsclassT.makeswipestop()
            //mTextView.setText("That didn't work!");
        }

        )
        //queue.add(s)
        volleyre.addToRequestQueue(s)
    }

    fun getOperations(vop:List<Operation>,signedtra: SignedTransaction) : JSONObject?{
        val d = MakeJsonRpc.getInstance()
        //val gson = Gson()
        val gsons =  GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
       // var jsons = gsons.toJson(vop)
        var obs =  ArrayList<Any>()
        //String von = MyOperationTypes.comment_options.name().toString();
        obs.add(MyOperationTypes.comment.name)
        obs.add(ops[0])

        var obs2 = ArrayList<Any>()
        obs2.add(MyOperationTypes.comment_options.name)
        if(ops.size >= 2){
            obs2.add(ops[1])
        }
        var obss = ArrayList<Any>()
        obss.add(obs)
        if(obs2.any()) obss.add(obs2)

        /*var obssm = ArrayList<Any>()
        obssm.add(obss)*/
        var json = gsons.toJson(obss)
        return d.networkBroadcaseSynchronous(json,signedtra.expirationDate.dateTime,signedtra.refBlockNum.toString(),signedtra.refBlockPrefix.toString(),signedtra.signatures[0])
    }

    fun getOperationsEdit(vop:List<Operation>,signedtra: SignedTransaction) : JSONObject?{
        val d = MakeJsonRpc.getInstance()
        //val gson = Gson()
        val gsons =  GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
        // var jsons = gsons.toJson(vop)
        var obs =  ArrayList<Any>()
        //String von = MyOperationTypes.comment_options.name().toString();
        obs.add(MyOperationTypes.comment.name)
        obs.add(ops[0])

        /*var obs2 = ArrayList<Any>()
        obs2.add(MyOperationTypes.comment_options.name)
        obs2.add(ops[1])*/

        var obss = ArrayList<Any>()
        obss.add(obs)
        //obss.add(obs2)

        /*var obssm = ArrayList<Any>()
        obssm.add(obss)*/
        var json = gsons.toJson(obss)
        return d.networkBroadcaseSynchronous(json,signedtra.expirationDate.dateTime,signedtra.refBlockNum.toString(),signedtra.refBlockPrefix.toString(),signedtra.signatures[0])
    }


    fun getOperations(vop : Operation,signedtra : SignedTransaction) : JSONObject?{
        val d = MakeJsonRpc.getInstance()
        if(myOperationTypes == MyOperationTypes.vote){
            vop as VoteOperation
            return d.networkBroadcaseSynchronous(signedtra.expirationDate.dateTime,"vote",vop.author.name.toString(),vop.permlink.link.toString(),vop.voter.name.toString(),vop.weight.toString(),signedtra.refBlockNum.toString(),signedtra.refBlockPrefix.toString(),signedtra.signatures[0])

        }
        else if(myOperationTypes == MyOperationTypes.unvote){

        }
        else if(myOperationTypes == MyOperationTypes.follow){
            vop as CustomJsonOperation
            //return d.networkBroadcaseSynchronous(vop.requiredPostingAuths[0].name,signedtra.expirationDate.dateTime,"custom_json",vop.id,signedtra.refBlockNum.toString(),signedtra.refBlockPrefix.toString(),vop.json,signedtra.signatures[0])
            return d.networkBroadcaseSynchronousReblog(vop.requiredPostingAuths[0].name,signedtra.expirationDate.dateTime,"custom_json",vop.id,signedtra.refBlockNum.toString(),signedtra.refBlockPrefix.toString(),vop.json,signedtra.signatures[0])
        }
        else if(myOperationTypes == MyOperationTypes.unfollow){
            vop as CustomJsonOperation
            //return d.networkBroadcaseSynchronous(vop.requiredPostingAuths[0].name,signedtra.expirationDate.dateTime,"custom_json",vop.id,signedtra.refBlockNum.toString(),signedtra.refBlockPrefix.toString(),vop.json,signedtra.signatures[0])
            return d.networkBroadcaseSynchronousReblog(vop.requiredPostingAuths[0].name,signedtra.expirationDate.dateTime,"custom_json",vop.id,signedtra.refBlockNum.toString(),signedtra.refBlockPrefix.toString(),vop.json,signedtra.signatures[0])
        }
        else if(myOperationTypes == MyOperationTypes.comment){

        }
        else if(myOperationTypes == MyOperationTypes.post){

        }
        else if(myOperationTypes == MyOperationTypes.reblog){
            vop as CustomJsonOperation
            //return d.networkBroadcaseSynchronous(vop.requiredPostingAuths[0].name,signedtra.expirationDate.dateTime,"custom_json",vop.id,signedtra.refBlockNum.toString(),signedtra.refBlockPrefix.toString(),vop.json,signedtra.signatures[0])
            return d.networkBroadcaseSynchronousReblog(vop.requiredPostingAuths[0].name,signedtra.expirationDate.dateTime,"custom_json",vop.id,signedtra.refBlockNum.toString(),signedtra.refBlockPrefix.toString(),vop.json,signedtra.signatures[0])

        }
        return null
    }





    private inner class signalrstarterasync : AsyncTask<forewarder, forewarder, forewarder>() {


        var mostlyconnected = false
        var ie: InterruptedException? = null
        var ex: ExecutionException? = null


        override fun onPreExecute() {
            super.onPreExecute()
            if(key == null){
                key = sharedpref.getString(CentralConstants.key,null)
            }
            Log.i("startingsignmaasy", "mainasync pre")
            //useIfOne++;
        }

        override fun doInBackground(vararg params: forewarder): forewarder? {


            try {

                //params[0].signedtra.sign(ImmutablePair(PrivateKeyType.POSTING,key))
                params[0].signedtra.signMy(SteemJConfig.getInstance().getChainId(), ImmutablePair(PrivateKeyType.POSTING,params[0].key))
            } catch (e: InterruptedException) {


                mostlyconnected = false
                ie = e

            } catch (e: ExecutionException) {
                mostlyconnected = false
                ex = e
            } catch (ex: Exception) {


                mostlyconnected = false

            }


            //do stuff
            return params[0]
        }

        override fun onPostExecute(result: forewarder) {
            super.onPostExecute(result)
            Log.i("startingsignmaasy", "mainasync post mostly connected is " + mostlyconnected.toString())
            if(!result.signedtra.signatures.isEmpty()){
                //BroadcastSynchronous(result.vop,result.signedtra)
                BroadcastSynchronous(result.vop,result.signedtra)
            }


            if (mostlyconnected) {

                //Toast.makeText(appContext,"SignalRStarted",Toast.LENGTH_LONG).show();
            } else {

                if (ex != null) {
                    Log.i("startingsignmaasy", "mainasync post error may be " + ex!!.message)
                } else {
                    Log.i("startingsignmaasy", "no error i guess")
                }



                //Toast.makeText(appContext,"Please check your connection and try again.",Toast.LENGTH_SHORT).show();
                /*myHubCon.disconnect();
                myHubCon.stop();
                myHubCon = null;*/


            }

        }
    }

    private class forewarder(vop : List<Operation>,signedtra : SignedTransaction,key : String){
        var signedtra : SignedTransaction = signedtra
        var vop = vop
        var key = key
    }

}