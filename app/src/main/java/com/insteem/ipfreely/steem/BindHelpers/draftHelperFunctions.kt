package com.insteem.ipfreely.steem.BindHelpers

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.commonsware.cwac.anddown.AndDown
import com.insteem.ipfreely.steem.CentralConstants
import com.insteem.ipfreely.steem.Databases.drafts
import com.insteem.ipfreely.steem.Enums.AdapterToUseFor
import com.insteem.ipfreely.steem.Interfaces.arvdinterface
import com.insteem.ipfreely.steem.MiscConstants
import com.insteem.ipfreely.steem.MyViewHolders.DraftViewHolder
import com.insteem.ipfreely.steem.Post
import com.insteem.ipfreely.steem.jsonclasses.OperationJson
import org.json.JSONObject

class draftHelperFunctions (context : Context, username:String?, adapter: arvdinterface, adpterType: AdapterToUseFor) {
    val con: Context = context
    var name: String? = username
    val adaptedcomms: arvdinterface = adapter
    val and = AndDown()
    private var selectedPos = -1
    val sharedpref: SharedPreferences = context.getSharedPreferences(CentralConstants.sharedprefname, 0)

    var key = sharedpref.getString(CentralConstants.key, null)
    val globallist = ArrayList<Any>()
    val adaptype = adpterType

    init {
        if (name == null) {
            name = sharedpref.getString(CentralConstants.username, null)
        }
    }

    public fun add(article: OperationJson.draftholder) {
        adaptedcomms.add(article)
        //adapter.notifyDataSetChanged();
        adaptedcomms.notifyitemcinserted(adaptedcomms.getSize())
    }

    public fun add(articles: List<OperationJson.draftholder>) {
        for (x in articles) {
            add(x)
        }
    }

    /*fun viewclicked(vhf : RecyclerView.ViewHolder){
        adaptedcomms.notifyitemcchanged(selectedPos)
        selectedPos = vhf.layoutPosition
        adaptedcomms.notifyitemcchanged(selectedPos)
        var intent : Intent = Intent()

        *//*VhUniv = vhf as OpenAQuestionDataHolder
        adaptedcomms.SetData(VhUniv.mItem)
        SetVisibilityOfButtons(VhUniv)*//*
    }*/

    public fun Bind(mholder: RecyclerView.ViewHolder, position: Int) {
        mholder.itemView.setSelected(selectedPos == position)
        val holder: DraftViewHolder = mholder as DraftViewHolder
        holder.article = adaptedcomms.getObject(position) as OperationJson.draftholder


        holder.article_date?.text = holder.article?.date
        //val d = Calendar.getInstance()
        //d.time = holder.article?.createdcon

        holder.article_title?.text = holder.article?.title


        holder.article_summary?.text = holder.article?.content

        holder.article_tags?.text = holder.article?.tags

        //var by : in.uncod.android.bypass =


        //mholder.openarticle
        mholder.openarticle?.setOnClickListener(View.OnClickListener {
            //ForReturningQuestionsLite q = item;
            //var db = drafts(con)

            //var dbid = db.Insert(holder?.article?.title!!,tags,holder?.article?.body!!,jso.toString())
            val myIntent = Intent(con, Post::class.java)
            myIntent.putExtra("db", holder.article?.dbid)
            //check for older version saved data
            if(holder.article?.payouttype != ""){
                //simply put the string in the constructor to get JSOnObject
                var jso = JSONObject(holder.article?.payouttype)
                var ised:Boolean = if(jso.has("isedit")) jso.getBoolean("isedit") else false
                if(ised){
                    //add additional parameters to intent
                    myIntent.putExtra("isedit",ised)
                    myIntent.putExtra("permlink",jso.getString("permlink"))
                    myIntent.putExtra("category",jso.getString("category"))
                }

            }


            /*val myIntent = Intent(con, Post::class.java)
            myIntent.putExtra("db", holder.article?.dbid)*/
           /* myIntent.putExtra("tag", holder.article?.category)
            myIntent.putExtra("permlink", holder.article?.permlink)*/
            con.startActivity(myIntent)
            //creating a popup menu

        })

        //event for draft delete
        mholder.shareTextView?.setOnClickListener {

            //use a dialogue for miss clicks
            val alertDialogBuilder = AlertDialog.Builder(MiscConstants.ApplyMyThemeRet(con))
            //val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
            alertDialogBuilder.setTitle("Delete this draft?")


            alertDialogBuilder.setPositiveButton("ok", DialogInterface.OnClickListener{ diin, num ->
                //vote.weight = numberPicker.value as Short
                //if yes remove from db first
                try{
                    var db = drafts(con)
                    db.deleteContact(mholder?.article?.dbid)
                    adaptedcomms?.removeAt(position)
                } catch (ex:Exception){
                    adaptedcomms?.notifydatachanged()
                }

            })

            alertDialogBuilder.setNegativeButton("No", DialogInterface.OnClickListener { diin, num ->

            })
            val alertDialog = alertDialogBuilder.create()

            alertDialog.show()
        }
    }
}