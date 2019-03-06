package com.insteem.ipfreely.steem.BindHelpers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.commonsware.cwac.anddown.AndDown
import com.insteem.ipfreely.steem.*
import com.insteem.ipfreely.steem.Enums.NotificationType
import com.insteem.ipfreely.steem.Interfaces.arvdinterface
import com.insteem.ipfreely.steem.MyViewHolders.NotificationBusyViewHolder
import com.insteem.ipfreely.steem.jsonclasses.BusyNotificationJson
import java.util.*

class NotificationsBusyHelperFundtions(context : Context, username:String?, adapter: arvdinterface,dateHolder: FloatingDateHolder? = null)  {

    val con: Context = context
    var name:String? = username
    val adaptedcomms: arvdinterface = adapter
    val and = AndDown()
    var floatingDateHolder:FloatingDateHolder? = dateHolder
    /*internal var scale: Float = scale
    internal var metrics: DisplayMetrics = metrics*/
    var selectedPos = -1
    init {
        if(name == null){
            val sharedpref : SharedPreferences = context.getSharedPreferences(CentralConstants.sharedprefname,0)
            name = sharedpref.getString(CentralConstants.username,null)
        }
    }




    fun add(article : BusyNotificationJson.Result){

        if(article.date != null) floatingDateHolder?.checktimeandaddQuestions(article.date!!) else floatingDateHolder?.checktimeandaddQuestions(Date((article.timestamp!!*1000).toLong()))
        adaptedcomms.add(article)
        //adapter.notifyDataSetChanged();
        adaptedcomms.notifyitemcinserted(adaptedcomms.getSize())
    }

    fun add(articles:List<BusyNotificationJson.Result>){
        for (x in articles){
            add(x)
        }
    }


    fun resetDate(){
        floatingDateHolder?.prevdate = Calendar.getInstance()
    }


    fun Bind(mholder: RecyclerView.ViewHolder, position:Int) {
        mholder.itemView.setSelected(selectedPos == position)
        var holder: NotificationBusyViewHolder = mholder as NotificationBusyViewHolder
        holder.article = adaptedcomms.getObject(position) as BusyNotificationJson.Result

        //holder.cardviewchat?.layoutParams = GetLayourParamsMargin(GetPx(holder.article?.width?.toFloat() as Float))
        var title = ""
        var body = ""
        var author = ""
        var resultIntent = Intent(con, MainActivity::class.java)
        when (holder.article?.type) {
            NotificationType.reply -> {
                resultIntent = Intent(con, ArticleActivity::class.java)
                resultIntent.putExtra("permlinkToFind", holder.article?.permlink)
                resultIntent.putExtra(CentralConstants.ArticleBlockPasser, holder.article?.block)
                resultIntent.putExtra(CentralConstants.ArticleUsernameToState, holder.article?.author)
                resultIntent.putExtra(CentralConstants.ArticleNotiType,holder.article?.type.toString())
                //title = "${holder.article?.author} replied to your post"
                title = "replied to your post"
                body = holder.article?.parentPermlink?.replace("-", " ") as String
                author = holder.article?.author as String
            }
            NotificationType.mention -> {
                resultIntent = Intent(con, ArticleActivity::class.java)
                resultIntent.putExtra("permlinkToFind", holder.article?.permlink)
                resultIntent.putExtra(CentralConstants.ArticleBlockPasser, holder.article?.block)
                resultIntent.putExtra(CentralConstants.ArticleUsernameToState, holder.article?.author)
                resultIntent.putExtra(CentralConstants.ArticleNotiType,holder.article?.type.toString())
                //title = "${holder.article?.author} mentioned you"
                title = "mentioned you"
                author = holder.article?.author as String
                body = holder.article?.permlink?.replace("-", " ") as String
            }
            NotificationType.reblog -> {
                resultIntent = Intent(con, ArticleActivity::class.java)
                resultIntent.putExtra("permlinkToFind", holder.article?.permlink)
                resultIntent.putExtra(CentralConstants.ArticleBlockPasser, holder.article?.block)
                resultIntent.putExtra(CentralConstants.ArticleUsernameToState, holder.article?.account)
                resultIntent.putExtra(CentralConstants.ArticleNotiType,holder.article?.type.toString())
                //title = "${holder.article?.account} reblogged your post"
                title = "reblogged your post"
                author = holder.article?.account as String
                body = holder.article?.permlink?.replace("-", " ") as String
            }
            NotificationType.follow -> {
                resultIntent = Intent(con, OpenOtherGuyBlog::class.java)
                resultIntent.putExtra(CentralConstants.ArticleNotiType,holder.article?.type.toString())
                //title = "${holder.article?.follower} followed you"
                title = "followed you"
                author = holder.article?.follower as String
                resultIntent.putExtra(CentralConstants.OtherGuyNamePasser, holder.article?.follower)
                body = title
            }
            NotificationType.transfer -> {
                //title = "From ${holder.article?.from} ${holder.article?.amount}"
                title = "Sent you ${holder.article?.amount}"
                author = holder.article?.from as String
                body = "${holder.article?.memo}"
            }
            NotificationType.vote -> {
                resultIntent = Intent(con, ArticleActivity::class.java)
                resultIntent.putExtra("permlinkToFind", holder.article?.permlink)
                resultIntent.putExtra(CentralConstants.ArticleBlockPasser, holder.article?.block)
                resultIntent.putExtra(CentralConstants.ArticleUsernameToState, holder.article?.voter)
                resultIntent.putExtra(CentralConstants.ArticleNotiType,holder.article?.type.toString())
                //title = "${holder.article?.author} replied to your post"
                title = "Voted on your post"
                body = holder.article?.permlink?.replace("-", " ") as String
                author = holder.article?.voter as String
            }
        }


        holder.article_name?.text = author
        holder.article_title?.text = title
        val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_person_white_24px)
                //.error(R.drawable.error)
                .priority(Priority.HIGH)
                .circleCrop()
        //holder.article_date?.text = holder.article?.createdcon
        holder.article_date?.text = holder.article?.showdate
        Glide.with(con).load(CentralConstants.GetFeedImageUrl(author)).apply(options)
                //.placeholder(R.drawable.common_full_open_on_phone)
                .into(holder.article_pfp as ImageView)

        holder.article_name?.setOnClickListener(View.OnClickListener {
            val i = Intent(con, OpenOtherGuyBlog::class.java)
            i.putExtra(CentralConstants.OtherGuyNamePasser, author)
            con.startActivity(i)
        })

        holder.openarticle?.setOnClickListener({
            con.startActivity(resultIntent)
        })

        holder.article_summary?.text = body


    }
    }