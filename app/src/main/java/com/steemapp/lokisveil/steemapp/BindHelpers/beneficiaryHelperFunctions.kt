package com.steemapp.lokisveil.steemapp.BindHelpers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.commonsware.cwac.anddown.AndDown
import com.steemapp.lokisveil.steemapp.*
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.HelperClasses.ArticlePopUpMenu
import com.steemapp.lokisveil.steemapp.HelperClasses.MyLiTagHandler
import com.steemapp.lokisveil.steemapp.HelperClasses.StaticMethodsMisc
import com.steemapp.lokisveil.steemapp.HelperClasses.VoteWeightThenVote
import com.steemapp.lokisveil.steemapp.Interfaces.arvdinterface
import com.steemapp.lokisveil.steemapp.MyViewHolders.CommentViewHolder
import com.steemapp.lokisveil.steemapp.MyViewHolders.beneficiaryViewHolder
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.AccountName
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.Permlink
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.VoteOperation

//simple helper functions for recycler view in beneficiaries
class beneficiaryHelperFunctions(context : Context, username:String?, adapter: AllRecyclerViewAdapter)  {

    //app context
    val con: Context = context
    //
    var name:String? = username
    val adaptedcomms: arvdinterface = adapter
    //val and = AndDown()
    //internal var scale: Float = scale
    //internal var metrics: DisplayMetrics = metrics
    var selectedPos = -1
    init {
        //get the name if it is null
        if(name == null){
            val sharedpref : SharedPreferences = context.getSharedPreferences(CentralConstants.sharedprefname,0)
            name = sharedpref.getString(CentralConstants.username,null)
        }
    }







    public fun add(article : FeedArticleDataHolder.beneficiariesDataHolder){
        if(article.isdefault == 1){
            article.usenow = 1
        }
        adaptedcomms.add(article)
        //adapter.notifyDataSetChanged();
        adaptedcomms.notifyitemcinserted(adaptedcomms.getSize())
    }

    public fun add(articles:List<FeedArticleDataHolder.beneficiariesDataHolder>){
        for (x in articles){

            add(x)
        }
    }




    fun Bind(mholder: RecyclerView.ViewHolder, position:Int){
        mholder.itemView.setSelected(selectedPos == position)
        var holder : beneficiaryViewHolder = mholder as beneficiaryViewHolder
        holder.article = adaptedcomms.getObject(position) as FeedArticleDataHolder.beneficiariesDataHolder


        holder.artile_default?.isChecked = holder.article?.isdefault != null && holder.article?.isdefault == 1
        holder.artile_use_now?.isChecked = holder.article?.usenow != null && holder.article?.usenow == 1


        holder.article_name?.text = holder.article?.username

        val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_person_white_24px)
                //.error(R.drawable.error)
                .priority(Priority.HIGH)
                .circleCrop()
        //holder.article_date?.text = holder.article?.createdcon
        holder.article_date?.text = holder.article?.dateString
        Glide.with(con).load(CentralConstants.GetFeedImageUrl(holder.article?.username)).apply(options)
                //.placeholder(R.drawable.common_full_open_on_phone)
                .into(holder.article_pfp as ImageView)



        holder.article_percent?.text = (holder.article?.percent!!.toFloat()/2).toString()
        holder.article_tag?.setText(holder.article?.tags)

        holder.artile_use_now?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener{buttonView, isChecked ->
            holder.article?.uncheckedbyuser = 1
            holder.article?.usenow = if(isChecked) 1 else 0
            adaptedcomms.notifyitemcchanged(position,holder.article!!)
        })

        holder.artile_default?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener{buttonView, isChecked ->
            holder.article?.uncheckedbyuser = 1
            holder.article?.usenow = if(isChecked) 1 else 0
            holder.article?.isdefault = if(isChecked) 1 else 0
            adaptedcomms.notifyitemcchanged(position,holder.article!!)
        })
        /*holder.artile_use_now?.setOnClickListener({
            holder.article?.uncheckedbyuser = 1
            holder.article?.usenow = 0
            adaptedcomms.notifyitemcchanged(position,holder.article!!)
        })*/
        //holder.article_title?.text = holder.article?.title
        /*Glide.with(con).load(holder.article?.image?.get(0))
                .placeholder(R.drawable.common_full_open_on_phone)
                .into(holder.article_image)*/

        //var by : in.uncod.android.bypass =

        holder.article_name?.setOnClickListener(View.OnClickListener {
            val i = Intent(con, OpenOtherGuyBlog::class.java)
            i.putExtra(CentralConstants.OtherGuyNamePasser,holder.article?.username)
            con.startActivity(i)
        })


        mholder.article_like?.setOnClickListener(View.OnClickListener {
            holder.artile_default?.isChecked = true
            holder.article?.isdefault = 1

        })

        mholder.article_use?.setOnClickListener(View.OnClickListener {
            holder.artile_use_now?.isChecked = true
            holder.article?.usenow = 1
        })
        holder.dialog_seekbar?.progress = holder.article?.percent!!

        holder.dialog_seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                holder.article?.percent = progress

                holder.article_percent?.text = (holder.article?.percent!!.toFloat()/2).toString()
            }
        })

        if(name == mholder.article?.username){

        }
        else{

        }







    }


}