package com.insteem.ipfreely.steem.DataHolders


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.insteem.ipfreely.steem.SteemBackend.Config.Enums.MyOperationTypes
import org.json.JSONArray
import java.util.*

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
                     isBlog= data.isBlog,
                     saveTime = data.saveTime,
                    displayImage = data.displayImage,
                    sDate = data.sDate
            )

        }
    }

    open class BaseArticleDataHolder(
        val reblogBy: List<String>?,
        val reblogOn: String?, //2018-02-04T13:44:21
        val entryId: Int?, //2363


        var id: Long, //30142261

        @PrimaryKey(autoGenerate = true)
        var myDbKey:Int = 0,

        val author: String, //doghaus
        val permlink: String, //street-view
        val category: String, //poetry
        val title: String, //Street View
        val body: String?, //![15999858_5.jpg](https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg)

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
        //var isBlog:Boolean = false,
        var saveTime:Long = 0L,
        var displayImage:String? = null,
        var sDate: String? = null
    )








    /**
     * declared room entity
     */
    @Entity(tableName = "article_holder" ,indices = [Index(value = ["id"],unique = true), Index(value = ["saveTime"],unique = false)])
    class FeedArticleHolder  (
        reblogBy: List<String>?,
        reblogOn: String?, //2018-02-04T13:44:21
        entryId: Int?, //2363


        id: Long, //30142261


        myDbKey:Int = 0,

        author: String, //doghaus
        permlink: String, //street-view
        category: String, //poetry
        title: String, //Street View
        body: String?, //![15999858_5.jpg](https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg)
        var summary : String?,
           //val jsonMetadata: JsonMetadata, //{"tags":["poetry","writing","life","deceit","mystery"],"users":["doghaus"],"image":["https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg"],"links":["https://goo.gl/images/Jr17QZ"],"app":"steemit/0.1","format":"markdown"}
        lastUpdate: String, //2018-02-03T13:58:18
        created: String, //2018-02-03T13:58:18
        createdcon : String,
        datespan:String,
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
        width : Int = 0,
        useFollow : MyOperationTypes = MyOperationTypes.unfollow,
        replies : JSONArray? = null,
        activeVotes: JSONArray? = null,
        displayName:String = "",
        rootAuthor: String? = null,
        rootPermlink: String? = null,
        date:Date? = null,
        followsYou:Boolean = false,
        var isBlog:Boolean = false,
        saveTime:Long = 0L,
        displayImage:String? = null,
        sDate: String? = null
        ):BaseArticleDataHolder(reblogBy, reblogOn, entryId, id, myDbKey, author, permlink, category, title, body, lastUpdate, created, createdcon,
            datespan, active, lastPayout, depth, children, cashoutTime, netVotes, rootComment, tags, users, image, links, app, format, pending_payout_value,
            total_pending_payout_value, uservoted, authorreputation, promoted, already_paid,width, useFollow, replies, activeVotes, displayName, rootAuthor,
            rootPermlink, date, followsYou, saveTime, displayImage, sDate)



    /**
     * declared room entity same as article
     */
    @Entity(tableName = "widget_holder" ,indices = [Index(value = ["id"],unique = true)])
    class WidgetArticleHolder  (
            reblogBy: List<String>?,
            reblogOn: String?, //2018-02-04T13:44:21
            entryId: Int?, //2363


            id: Long, //30142261


            myDbKey:Int = 0,

            author: String, //doghaus
            permlink: String, //street-view
            category: String, //poetry
            title: String, //Street View
            body: String?, //![15999858_5.jpg](https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg)
            var summary : String?,
            //val jsonMetadata: JsonMetadata, //{"tags":["poetry","writing","life","deceit","mystery"],"users":["doghaus"],"image":["https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg"],"links":["https://goo.gl/images/Jr17QZ"],"app":"steemit/0.1","format":"markdown"}
            lastUpdate: String, //2018-02-03T13:58:18
            created: String, //2018-02-03T13:58:18
            createdcon : String,
            datespan:String,
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
            width : Int = 0,
            useFollow : MyOperationTypes = MyOperationTypes.unfollow,
            replies : JSONArray? = null,
            activeVotes: JSONArray? = null,
            displayName:String = "",
            rootAuthor: String? = null,
            rootPermlink: String? = null,
            date:Date? = null,
            followsYou:Boolean = false,
            var isBlog:Boolean = false,
            saveTime:Long = 0L,
            displayImage:String? = null,
            sDate: String? = null
    ):BaseArticleDataHolder(reblogBy, reblogOn, entryId, id, myDbKey, author, permlink, category, title, body, lastUpdate, created, createdcon,
            datespan, active, lastPayout, depth, children, cashoutTime, netVotes, rootComment, tags, users, image, links, app, format, pending_payout_value,
            total_pending_payout_value, uservoted, authorreputation, promoted, already_paid)


    /**
     * declared room entity for comments
     */
    @Entity(tableName = "comment_holder",indices = [Index(value = ["id"],unique = true)])
    class CommentHolder(
            reblogBy: List<String>?,
            reblogOn: String?, //2018-02-04T13:44:21
            entryId: Int?, //2363
            id: Long, //30142261
            myDbKey:Int = 0,
            author: String, //doghaus
            permlink: String, //street-view
            category: String, //poetry
            title: String, //Street View
            body: String, //![15999858_5.jpg](https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg)
            //@androidx.room.Ignore
            //val lBody:List<Any>? = null,

            //val jsonMetadata: JsonMetadata, //{"tags":["poetry","writing","life","deceit","mystery"],"users":["doghaus"],"image":["https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg"],"links":["https://goo.gl/images/Jr17QZ"],"app":"steemit/0.1","format":"markdown"}
            lastUpdate: String, //2018-02-03T13:58:18
            created: String, //2018-02-03T13:58:18
            createdcon : String,
            datespan:String,
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
            width : Int = 0,
            var widthPx:Int = 0,
            var defaultWidth:Int = 0,
            useFollow : MyOperationTypes = MyOperationTypes.follow,
            replies : JSONArray? = null,
            activeVotes: JSONArray? = null,
            var reply_to_above : Boolean = false,
            displayName:String = "",

            var parent_permlink:String? = "",
            var parent_tag:String? ="",
            var paretn_author:String? = "",
            rootAuthor: String?,
            rootPermlink: String?,
            var highlightThis:Boolean = false,
            date:Date? = null,
            followsYou:Boolean = false,
            saveTime:Long = 0L,
            displayImage:String? = null,
            sDate: String? = null

    ):BaseArticleDataHolder(reblogBy,reblogOn,entryId,id,myDbKey,author,permlink,category,title,body,lastUpdate,created,createdcon,datespan,active,
            lastPayout,depth,children,cashoutTime,netVotes,rootComment,tags,users,image,links,app,format,pending_payout_value,total_pending_payout_value,
            uservoted,authorreputation,promoted,already_paid,width,useFollow,replies,activeVotes,displayName,rootAuthor,rootPermlink,date,followsYou,saveTime,displayImage,sDate)
    {

        @Ignore
        var lBody:List<Any>? = null

        constructor(reblogBy: List<String>?,
                    reblogOn: String?, //2018-02-04T13:44:21
                    entryId: Int?, //2363
                    id: Long, //30142261
                    myDbKey:Int = 0,
                    author: String, //doghaus
                    permlink: String, //street-view
                    category: String, //poetry
                    title: String, //Street View
                    body: String, //![15999858_5.jpg](https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg)
                //@androidx.room.Ignore

                    lBody:List<Any>? = null,
                //val jsonMetadata: JsonMetadata, //{"tags":["poetry","writing","life","deceit","mystery"],"users":["doghaus"],"image":["https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg"],"links":["https://goo.gl/images/Jr17QZ"],"app":"steemit/0.1","format":"markdown"}
                    lastUpdate: String, //2018-02-03T13:58:18
                    created: String, //2018-02-03T13:58:18
                    createdcon : String,
                    datespan:String,
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
                    width : Int = 0,
                    widthPx:Int = 0,
                    defaultWidth:Int = 0,
                    useFollow : MyOperationTypes = MyOperationTypes.follow,
                    replies : JSONArray? = null,
                    activeVotes: JSONArray? = null,
                    reply_to_above : Boolean = false,
                    displayName:String = "",

                    parent_permlink:String? = "",
                    parent_tag:String? ="",
                    paretn_author:String? = "",
                    rootAuthor: String?,
                    rootPermlink: String?,
                    highlightThis:Boolean = false,
                    date:Date? = null,
                    followsYou:Boolean = false,
                    saveTime:Long = 0L,
                    displayImage:String? = null,
                    sDate: String? = null):this(reblogBy,reblogOn,entryId,id,myDbKey,author,permlink,category,title,body,lastUpdate,created,createdcon,
                datespan,active,lastPayout,depth,children,cashoutTime,netVotes,rootComment,tags,users,image,links,app,format,pending_payout_value,
                total_pending_payout_value,uservoted, authorreputation, promoted, already_paid, width, widthPx, defaultWidth, useFollow, replies,
                activeVotes, reply_to_above, displayName, parent_permlink, parent_tag, paretn_author, rootAuthor, rootPermlink, highlightThis, date,
                followsYou, saveTime, displayImage, sDate){
            this.lBody = lBody
        }
    }


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