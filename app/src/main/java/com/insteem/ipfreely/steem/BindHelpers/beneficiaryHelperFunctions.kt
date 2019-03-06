package com.insteem.ipfreely.steem.BindHelpers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.insteem.ipfreely.steem.CentralConstants
import com.insteem.ipfreely.steem.DataHolders.FeedArticleDataHolder
import com.insteem.ipfreely.steem.Interfaces.arvdinterface
import com.insteem.ipfreely.steem.MyViewHolders.beneficiaryViewHolder
import com.insteem.ipfreely.steem.OpenOtherGuyBlog
import com.insteem.ipfreely.steem.R

//simple helper functions for recycler view in beneficiaries
class beneficiaryHelperFunctions(context : Context, username:String?, adapter: arvdinterface)  {

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