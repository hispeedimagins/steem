package com.steemapp.lokisveil.steemapp.DataHolders

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.steemapp.lokisveil.steemapp.Enums.FollowInternal
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.jsonclasses.feed
import org.json.JSONArray
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by boot on 2/5/2018.
 */
class FeedArticleDataHolder {
    companion object {

        /**
         * converts articledatholder to widget
         */
        fun feedToWidget(data: FeedArticleHolder):WidgetArticleHolder{
            return WidgetArticleHolder  (
                     reblogBy = data.reblogBy,
                     reblogOn= data.reblogOn, //2018-02-04T13:44:21
                     entryId= data.entryId, //2363


                     id = data.id, //30142261


                     myDbKey= data.myDbKey,

                     author= data.author, //doghaus
                     permlink= data.permlink, //street-view
                     category= data.category, //poetry
                     title= data.title, //Street View
                     body= data.body, //![15999858_5.jpg](https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg)
                     summary = data.summary,
                    //val jsonMetadata: JsonMetadata, //{"tags":["poetry","writing","life","deceit","mystery"],"users":["doghaus"],"image":["https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg"],"links":["https://goo.gl/images/Jr17QZ"],"app":"steemit/0.1","format":"markdown"}
                     lastUpdate= data.lastUpdate, //2018-02-03T13:58:18
                     created= data.created, //2018-02-03T13:58:18
                     createdcon = data.createdcon,
                     datespan= data.datespan,
                     active= data.active, //2018-02-04T13:50:12
                     lastPayout= data.lastPayout, //1970-01-01T00:00:00
                     depth= data.depth, //0
                     children= data.children, //7
                     cashoutTime= data.cashoutTime,
                     netVotes= data.netVotes, //21
                     rootComment= data.rootComment,

                     tags= data.tags,
                     users= data.users,
                     image= data.image,
                     links= data.links,
                     app= data.app, //steemit/0.1
                     format= data.format, //markdown

                     pending_payout_value = data.pending_payout_value,
                     total_pending_payout_value = data.total_pending_payout_value,
                     uservoted = data.uservoted,
                     authorreputation = data.authorreputation,
                     promoted = data.promoted,
                     already_paid = data.already_paid,
                     width = data.width,
                     useFollow = data.useFollow,
                     replies = data.replies,
                     activeVotes= data.activeVotes,
                     displayName= data.displayName,
                     rootAuthor= data.rootAuthor,
                     rootPermlink= data.rootPermlink,
                     date= data.date,
                     followsYou= data.followsYou,
                     isBlog= data.isBlog
            )

        }
    }
/*     class MainHolder(
              reblogBy: List<String>?,
              reblogOn: String?, //2018-02-04T13:44:21
              entryId: Int?, //2363
              id: Int, //30142261
              author: String, //doghaus
              permlink: String, //street-view
              category: String, //poetry
              title: String, //Street View
              body: String, //![15999858_5.jpg](https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg)
              summary : String?,
             //al jsonMetadata: JsonMetadata, //{"tags":["poetry","writing","life","deceit","mystery"],"users":["doghaus"],"image":["https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg"],"links":["https://goo.gl/images/Jr17QZ"],"app":"steemit/0.1","format":"markdown"}
              lastUpdate: String, //2018-02-03T13:58:18
              created: String, //2018-02-03T13:58:18
              createdcon : String,
              active: String, //2018-02-04T13:50:12
              lastPayout: String, //1970-01-01T00:00:00
              depth: Int, //0
              children: Int, //7
              cashoutTime: String,
              netVotes: Int, //21
              rootComment: Int,

              tags: List<String>?,
              users: List<String>?,
              image: List<String>?,
              links: List<String>?,
              app: String?, //steemit/0.1
              format: String?, //markdown

              pending_payout_value : String?,
              total_pending_payout_value : String?,
              uservoted : Boolean?,
              authorreputation :String?,
              promoted : String?,
              already_paid : String?,
              width : Int = 0
     ) {
         var reblogBy: List<String>? = reblogBy
         var reblogOn: String? = reblogOn //2018-02-04T13:44:21
         var entryId: Int? = entryId //2363
         var id: Int = id //30142261
         var author: String = author //doghaus
         var permlink: String = permlink //street-view
         var category: String = category//poetry
         var title: String = title //Street View
         var body: String = body //![15999858_5.jpg](https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg)
         //var summary: String?
         //varal jsonMetadata: JsonMetadata, //{"tags":["poetry","writing","life","deceit","mystery"],"users":["doghaus"],"image":["https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg"],"links":["https://goo.gl/images/Jr17QZ"],"app":"steemit/0.1","format":"markdown"}
         var lastUpdate: String = lastUpdate //2018-02-03T13:58:18
         var created: String = created //2018-02-03T13:58:18
         var createdcon: String = createdcon
         var active: String = active //2018-02-04T13:50:12
         var lastPayout: String = lastPayout //1970-01-01T00:00:00
         var depth: Int = depth //0
         var children: Int = children //7
         var cashoutTime: String = cashoutTime
         var netVotes: Int = netVotes //21
         var rootComment: Int = rootComment

         var tags: List<String>? = tags
         var users: List<String>? = users
         var image: List<String>? = image
         var links: List<String>? = links
         var app: String? = app //steemit/0.1
         var format: String? = format //markdown

         var pending_payout_value: String? = pending_payout_value
         var total_pending_payout_value: String? = total_pending_payout_value
         var uservoted: Boolean? = uservoted
         var authorreputation: String? = authorreputation
         var promoted: String? = promoted
         var already_paid: String? = already_paid
         var width: Int = 0


     }*/

    /**
     * declared room entity
     */
    @Entity(tableName = "article_holder" ,indices = [Index(value = ["id"],unique = true)])
    data class FeedArticleHolder  (
        val reblogBy: List<String>?,
        val reblogOn: String?, //2018-02-04T13:44:21
        val entryId: Int?, //2363


        val id: Int, //30142261

        @PrimaryKey(autoGenerate = true)
        val myDbKey:Int = 0,

        val author: String, //doghaus
        val permlink: String, //street-view
        val category: String, //poetry
        val title: String, //Street View
        val body: String?, //![15999858_5.jpg](https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg)
        var summary : String?,
           //val jsonMetadata: JsonMetadata, //{"tags":["poetry","writing","life","deceit","mystery"],"users":["doghaus"],"image":["https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg"],"links":["https://goo.gl/images/Jr17QZ"],"app":"steemit/0.1","format":"markdown"}
        val lastUpdate: String, //2018-02-03T13:58:18
        val created: String, //2018-02-03T13:58:18
        val createdcon : String,
        var datespan:String,
        val active: String, //2018-02-04T13:50:12
        val lastPayout: String, //1970-01-01T00:00:00
        val depth: Int, //0
        val children: Int, //7
        val cashoutTime: String,
        var netVotes: Int, //21
        val rootComment: Int,

        val tags: List<String>?,
        val users: List<String>?,
        val image: List<String>?,
        val links: List<String>?,
        val app: String?, //steemit/0.1
        val format: String?, //markdown

        var pending_payout_value : String?,
        val total_pending_payout_value : String?,
        var uservoted : Boolean?,
        val authorreputation :String?,
        val promoted : String?,
        val already_paid : String?,
        var width : Int = 0,
        var useFollow : MyOperationTypes = MyOperationTypes.unfollow,
        var replies : JSONArray? = null,
        var activeVotes: JSONArray? = null,
        var displayName:String = "",
        var rootAuthor: String? = null,
        var rootPermlink: String? = null,
        var date:Date? = null,
        var followsYou:Boolean = false,
        var isBlog:Boolean = false
        )/*: Parcelable{
    constructor(parcel: Parcel) : this(
            parcel.createStringArrayList(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.createStringArrayList(),
            parcel.createStringArrayList(),
            parcel.createStringArrayList(),
            parcel.createStringArrayList(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            MyOperationTypes.valueOf(parcel.readString()),
            if(parcel.readString() != null) Gson().fromJson(parcel.readString(),JSONArray::class.java) else JSONArray(),
            if(parcel.readString() != null) Gson().fromJson(parcel.readString(),JSONArray::class.java) else JSONArray(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeStringList(reblogBy)
        dest?.writeString(reblogOn)
        dest?.writeInt(if(entryId != null) entryId!! else 0)
        dest?.writeInt(id)
        dest?.writeString(author)
        dest?.writeString(permlink)
        dest?.writeString(category)
        dest?.writeString(title)
        dest?.writeString(body)
        dest?.writeString(summary)
        dest?.writeString(lastUpdate)
        dest?.writeString(created)
        dest?.writeString(createdcon)
        dest?.writeString(datespan)
        dest?.writeString(active)
        dest?.writeString(lastPayout)
        dest?.writeInt(depth)
        dest?.writeInt(children)
        dest?.writeString(cashoutTime)
        dest?.writeInt(netVotes)
        dest?.writeInt(rootComment)
        dest?.writeStringList(tags)
        dest?.writeStringList(users)
        dest?.writeStringList(image)
        dest?.writeStringList(links)
        dest?.writeString(app)
        dest?.writeString(format)
        dest?.writeString(pending_payout_value)
        dest?.writeString(total_pending_payout_value)
        dest?.writeString(uservoted?.toString())
        dest?.writeString(authorreputation)
        dest?.writeString(promoted)
        dest?.writeString(already_paid)
        dest?.writeInt(width)
        dest?.writeString(useFollow.name)
        dest?.writeString(replies?.toString())
        dest?.writeString(activeVotes?.toString())
        dest?.writeString(displayName)
        dest?.writeString(rootAuthor)
        dest?.writeString(rootPermlink)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FeedArticleHolder> {
        override fun createFromParcel(parcel: Parcel): FeedArticleHolder {
            return FeedArticleHolder(parcel)
        }

        override fun newArray(size: Int): Array<FeedArticleHolder?> {
            return arrayOfNulls(size)
        }
    }
}*/


    /**
     * declared room entity same as article
     */
    @Entity(tableName = "widget_holder" ,indices = [Index(value = ["id"],unique = true)])
    data class WidgetArticleHolder  (
            val reblogBy: List<String>?,
            val reblogOn: String?, //2018-02-04T13:44:21
            val entryId: Int?, //2363


            val id: Int, //30142261

            @PrimaryKey(autoGenerate = true)
            val myDbKey:Int = 0,

            val author: String, //doghaus
            val permlink: String, //street-view
            val category: String, //poetry
            val title: String, //Street View
            val body: String?, //![15999858_5.jpg](https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg)
            var summary : String?,
            //val jsonMetadata: JsonMetadata, //{"tags":["poetry","writing","life","deceit","mystery"],"users":["doghaus"],"image":["https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg"],"links":["https://goo.gl/images/Jr17QZ"],"app":"steemit/0.1","format":"markdown"}
            val lastUpdate: String, //2018-02-03T13:58:18
            val created: String, //2018-02-03T13:58:18
            val createdcon : String,
            var datespan:String,
            val active: String, //2018-02-04T13:50:12
            val lastPayout: String, //1970-01-01T00:00:00
            val depth: Int, //0
            val children: Int, //7
            val cashoutTime: String,
            var netVotes: Int, //21
            val rootComment: Int,

            val tags: List<String>?,
            val users: List<String>?,
            val image: List<String>?,
            val links: List<String>?,
            val app: String?, //steemit/0.1
            val format: String?, //markdown

            var pending_payout_value : String?,
            val total_pending_payout_value : String?,
            var uservoted : Boolean?,
            val authorreputation :String?,
            val promoted : String?,
            val already_paid : String?,
            var width : Int = 0,
            var useFollow : MyOperationTypes = MyOperationTypes.unfollow,
            var replies : JSONArray? = null,
            var activeVotes: JSONArray? = null,
            var displayName:String = "",
            var rootAuthor: String? = null,
            var rootPermlink: String? = null,
            var date:Date? = null,
            var followsYou:Boolean = false,
            var isBlog:Boolean = false
    )


    /**
     * declared room entity for comments
     */
    @Entity(tableName = "comment_holder",indices = [Index(value = ["id"],unique = true)])
    data class CommentHolder(
            val reblogBy: List<String>?,
            val reblogOn: String?, //2018-02-04T13:44:21
            val entryId: Int?, //2363

            val id: Int, //30142261

            @PrimaryKey(autoGenerate = true)
            val myDbKey:Int = 0,
            val author: String, //doghaus
            val permlink: String, //street-view
            val category: String, //poetry
            val title: String, //Street View
            val body: String, //![15999858_5.jpg](https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg)
            //val jsonMetadata: JsonMetadata, //{"tags":["poetry","writing","life","deceit","mystery"],"users":["doghaus"],"image":["https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg"],"links":["https://goo.gl/images/Jr17QZ"],"app":"steemit/0.1","format":"markdown"}
            val lastUpdate: String, //2018-02-03T13:58:18
            val created: String, //2018-02-03T13:58:18
            val createdcon : String,
            var datespan:String,
            val active: String, //2018-02-04T13:50:12
            val lastPayout: String, //1970-01-01T00:00:00
            val depth: Int, //0
            val children: Int, //7
            val cashoutTime: String,
            var netVotes: Int, //21
            val rootComment: Int,

            val tags: List<String>?,
            val users: List<String>?,
            val image: List<String>?,
            val links: List<String>?,
            val app: String?, //steemit/0.1
            val format: String?, //markdown

            var pending_payout_value : String?,
            val total_pending_payout_value : String?,
            var uservoted : Boolean?,
            val authorreputation :String?,
            val promoted : String?,
            val already_paid : String?,
            var width : Int = 0,
            var useFollow : MyOperationTypes = MyOperationTypes.follow,
            var replies : JSONArray? = null,
            var activeVotes: JSONArray? = null,
            var reply_to_above : Boolean = false,
            var displayName:String = "",

            var parent_permlink:String? = "",
            var parent_tag:String? ="",
            var paretn_author:String? = "",
            var rootAuthor: String?,
            var rootPermlink: String?,
            var highlightThis:Boolean = false,
            var date:Date? = null,
            var followsYou:Boolean = false
    )/*: Parcelable{
        constructor(parcel: Parcel) : this(
                parcel.createStringArrayList(),
                parcel.readString(),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readInt(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readInt(),
                parcel.readInt(),
                parcel.readString(),
                parcel.readInt(),
                parcel.readInt(),
                parcel.createStringArrayList(),
                parcel.createStringArrayList(),
                parcel.createStringArrayList(),
                parcel.createStringArrayList(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readInt(),
                MyOperationTypes.valueOf(parcel.readString()),
                if(parcel.readString() != null) Gson().fromJson(parcel.readString(),JSONArray::class.java) else JSONArray(),
                if(parcel.readString() != null) Gson().fromJson(parcel.readString(),JSONArray::class.java) else JSONArray(),
                parcel.readByte() != 0.toByte(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readByte() != 0.toByte()) {
        }

        override fun writeToParcel(dest: Parcel?, flags: Int) {
            dest?.writeStringList(reblogBy)
            dest?.writeString(reblogOn)
            dest?.writeInt(if(entryId != null) entryId!! else 0)
            dest?.writeInt(id)
            dest?.writeString(author)
            dest?.writeString(permlink)
            dest?.writeString(category)
            dest?.writeString(title)
            dest?.writeString(body)

            dest?.writeString(lastUpdate)
            dest?.writeString(created)
            dest?.writeString(createdcon)
            dest?.writeString(datespan)
            dest?.writeString(active)
            dest?.writeString(lastPayout)
            dest?.writeInt(depth)
            dest?.writeInt(children)
            dest?.writeString(cashoutTime)
            dest?.writeInt(netVotes)
            dest?.writeInt(rootComment)
            dest?.writeStringList(tags)
            dest?.writeStringList(users)
            dest?.writeStringList(image)
            dest?.writeStringList(links)
            dest?.writeString(app)
            dest?.writeString(format)
            dest?.writeString(pending_payout_value)
            dest?.writeString(total_pending_payout_value)
            dest?.writeString(uservoted?.toString())
            dest?.writeString(authorreputation)
            dest?.writeString(promoted)
            dest?.writeString(already_paid)
            dest?.writeInt(width)
            dest?.writeString(useFollow.name)
            dest?.writeString(replies?.toString())
            dest?.writeString(activeVotes?.toString())
            dest?.writeString(reply_to_above?.toString())
            dest?.writeString(displayName)
            dest?.writeString(parent_permlink)
            dest?.writeString(parent_tag)
            dest?.writeString(paretn_author)
            dest?.writeString(rootAuthor)
            dest?.writeString(rootPermlink)
            dest?.writeString(highlightThis?.toString())
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<CommentHolder> {
            override fun createFromParcel(parcel: Parcel): CommentHolder {
                return CommentHolder(parcel)
            }

            override fun newArray(size: Int): Array<CommentHolder?> {
                return arrayOfNulls(size)
            }
        }

    }*/


    //data class for beneficiaries
    @Entity(tableName = "beneficiary_holder")
    data class beneficiariesDataHolder(
            var username:String,
            var percent:Int,
            var isdeveloper:Int,
            var isdefault:Int,
            var tags:String,
            var dateLong:Long,
            var dateString:String,
            var isbuiltin:Int,
            var usenow : Int = 0,

            @PrimaryKey(autoGenerate = true)
            var dbid:Int,
            var uncheckedbyuser:Int = 0

    )
}