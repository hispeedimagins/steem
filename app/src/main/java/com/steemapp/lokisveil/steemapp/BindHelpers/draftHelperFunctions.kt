package com.steemapp.lokisveil.steemapp.BindHelpers

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AlertDialog
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import com.commonsware.cwac.anddown.AndDown
import com.steemapp.lokisveil.steemapp.*
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Databases.drafts
import com.steemapp.lokisveil.steemapp.Enums.AdapterToUseFor
import com.steemapp.lokisveil.steemapp.Interfaces.arvdinterface
import com.steemapp.lokisveil.steemapp.MyViewHolders.DraftViewHolder
import com.steemapp.lokisveil.steemapp.jsonclasses.OperationJson
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
        mholder.shareTextView?.setOnClickListener({

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
        })

        //val articlepop = ArticlePopUpMenu(con, mholder.shareTextView, "${CentralConstants.baseUrlView}@${holder?.article?.author}", "${CentralConstants.baseUrlView}${holder?.article?.category}/@${holder?.article?.author}/${holder?.article?.permlink}", null, name, holder.article?.author, adaptedcomms, position, holder?.progressbar, null, holder?.article?.activeVotes, true)

/*        *//* mholder.shareTextView.setOnClickListener(View.OnClickListener{
             *//**//*val sendIntent: Intent =  Intent()
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "${CentralConstants.baseUrl}${holder?.article?.category}/@${holder?.article?.author}/${holder?.article?.permlink}")
            sendIntent.setType("text/plain");
            con.startActivity(Intent.createChooser(sendIntent,"Share Link"))*//**//*



            val popup = PopupMenu(con,mholder.shareTextView)
            //inflating menu from xml resource
            popup.inflate(R.menu.article_dialog_menu)
            //adding click listener
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    when (item.getItemId()) {
                        R.id.article_dialog_share_author_profile -> {
                        }
                        R.id.article_dialog_share_article -> {
                        }
                        *//**//*R.id.menu3 -> {
                        }*//**//*
                    }//handle menu1 click
                    //handle menu2 click
                    //handle menu3 click
                    return false
                }
            })
            //displaying the popup
            popup.show()


        })*//*

        mholder.article_like?.setOnClickListener(View.OnClickListener {
            // Toast.makeText(con,"Processing. Please wait....",Toast.LENGTH_LONG).show()
            //ForReturningQuestionsLite q = item;
            var articles = mholder.article as FeedArticleDataHolder.FeedArticleHolder
            //var s : SteemJ = SteemJ()
            //s.vote(AccountName(holder.article?.author), Permlink(holder.article?.permlink),10000)
            var vop: VoteOperation = VoteOperation(AccountName(name), AccountName(articles.author), Permlink(articles.permlink))
            vop.weight = 100
            var obs = Response.Listener<JSONObject> { response ->
                //var articles = mholder.article as FeedArticleDataHolder.FeedArticleHolder
                val gson = Gson()
                var ress = gson.fromJson<Block.BlockAdded>(response.toString(), Block.BlockAdded::class.java)
                if (ress != null && ress.result != null) {
                    //con.run { Toast.makeText(con,"Upvoted ${vop.permlink}",Toast.LENGTH_LONG).show() }
                    Toast.makeText(con, "Upvoted ${vop.permlink}", Toast.LENGTH_LONG).show()
                    holder.article?.uservoted.to(true)
                    holder.article_likes?.setTextColor(ContextCompat.getColor(con, R.color.colorAccent))
                    adaptedcomms.notifyitemcchanged(mholder.adapterPosition)
                    //Runnable { run {  } }
                    //Toast.makeText(con,"$name has upvoted ${vop.author.name}",Toast.LENGTH_LONG).show()
                }
            }
            //var bloc = GetDynamicAndBlock(con ,adaptedcomms,position,mholder,null,vop,obs,"Upvoted ${vop.permlink.link}",MyOperationTypes.vote)
            *//*var list  = ArrayList<Operation>()
            list.add(vop)
            var bloc = GetDynamicAndBlock(con ,adaptedcomms,position,list,"Upvoted ${vop.permlink.link}",MyOperationTypes.vote)
            bloc.GetDynamicGlobalProperties()*//*
            val weight = VoteWeightThenVote(con, adaptedcomms.getActivity(), vop, adaptedcomms, position, holder.progressbar, null)
            weight.makeDialog()
            *//*globallist.add(bloc)

            Log.d("buttonclick",globallist.toString())*//*
            //GetDynamicGlobalProperties(mholder.article as FeedArticleDataHolder.FeedArticleHolder)

        })

        mholder.article_reblog_now?.setOnClickListener(View.OnClickListener {
            //Toast.makeText(con,"Processing. Please wait....",Toast.LENGTH_LONG).show()
            //ForReturningQuestionsLite q = item;
            var articles = mholder.article as FeedArticleDataHolder.FeedArticleHolder
            var vop = ReblogOperation(AccountName(name), AccountName(articles.author), Permlink(articles.permlink))
            var al = ArrayList<AccountName>()
            al.add(AccountName(name))
            var cus = CustomJsonOperation(null, al, "follow", vop.toJson())


            *//*var l : List<FollowType> = ArrayList()
            l += FollowType.BLOG
            var fop = FollowOperation(AccountName(name), AccountName(articles.author), l)
            var fus = CustomJsonOperation(null,al,"follow",fop.toJson())

            var obs = Response.Listener<JSONObject> { response ->
                //var articles = mholder.article as FeedArticleDataHolder.FeedArticleHolder
                val gson = Gson()
                var ress = gson.fromJson<Block.BlockAdded>(response.toString(),Block.BlockAdded::class.java)
                if(ress != null && ress.result != null ){
                    *//**//*Runnable { run { Toast.makeText(con,"$name has upvoted ${vop.author.name}",Toast.LENGTH_LONG).show() } }*//**//*
                    Toast.makeText(con,"Reblogged ${vop.permlink.link}",Toast.LENGTH_LONG).show()

                }
            }*//*
            var list = ArrayList<Operation>()
            list.add(cus)
            //var bloc = GetDynamicAndBlock(con ,adaptedcomms,position,mholder,null,cus,obs,"Reblogged ${vop.permlink.link}",MyOperationTypes.reblog)
            var bloc = GetDynamicAndBlock(con, adaptedcomms, position, list, "Reblogged ${vop.permlink.link}", MyOperationTypes.reblog)
            bloc.GetDynamicGlobalProperties()

        })*/



    }
}