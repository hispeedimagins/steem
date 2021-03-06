package com.insteem.ipfreely.steem.RoomDatabaseApp.RoomDaos


import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.insteem.ipfreely.steem.DataHolders.FeedArticleDataHolder

@Dao
interface WidgetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(data: FeedArticleDataHolder.WidgetArticleHolder):Long

    //update an item
    @Update
    fun update(data:FeedArticleDataHolder.WidgetArticleHolder):Int

    //delete all items
    @Query("delete from widget_holder")
    fun deleteAll()

    //wrap with livedata for observing in the future
    @Query("SELECT * from widget_holder ORDER BY myDbKey ASC")
    fun getAll(): LiveData<List<FeedArticleDataHolder.FeedArticleHolder>>

    //get a single item article id
    @Query("SELECT * from widget_holder where id == :eid")
    fun getArticle(eid:Long): LiveData<FeedArticleDataHolder.FeedArticleHolder>

    //get an item matching article id
    @Query("SELECT * from widget_holder where id == :eid")
    fun getArticleNormal(eid:Long): FeedArticleDataHolder.FeedArticleHolder?

    //get an item matching the dbkey
    @Query("SELECT * from widget_holder where myDbKey == :eid")
    fun getArticleMy(eid:Int): LiveData<FeedArticleDataHolder.FeedArticleHolder>
    /*@Query("SELECT * from events where id == :eid ORDER BY id DESC LIMIT 20")
    fun getPagedList(eid:Int): DataSource.Factory<Int,EventsEntity>*/
    /*@Query("SELECT * from article_holder ORDER BY myDbKey DESC")
    fun getPagedList(): DataSource.Factory<Int,FeedArticleDataHolder.FeedArticleHolder>*/

    //get a paged list
    @Query("SELECT id,myDbKey,active,activeVotes,already_paid,app,author,authorreputation,cashoutTime,category,children,created,createdcon,date,datespan,depth,displayName,entryId,followsYou,format,image,lastPayout,lastUpdate,links,pending_payout_value,netVotes,permlink,promoted,reblogBy,reblogOn,replies,rootAuthor,rootComment,rootPermlink,summary,tags,title,total_pending_payout_value,useFollow,users,uservoted,width,isBlog,saveTime,sDate from widget_holder where isBlog == :isBlog ORDER BY myDbKey ASC")
    fun getPagedList(isBlog:Boolean = false): DataSource.Factory<Int, FeedArticleDataHolder.FeedArticleHolder>


    //get a paged list paged list
    @Query("SELECT id,myDbKey,active,activeVotes,already_paid,app,author,authorreputation,cashoutTime,category,children,created,createdcon,date,datespan,depth,displayName,entryId,followsYou,format,image,lastPayout,lastUpdate,links,pending_payout_value,netVotes,permlink,promoted,reblogBy,reblogOn,replies,rootAuthor,rootComment,rootPermlink,summary,tags,title,total_pending_payout_value,useFollow,users,uservoted,width,isBlog,saveTime,sDate from widget_holder where isBlog == :isBlog & myDbKey > :dbkey ORDER BY myDbKey ASC")
    fun getPagedList(dbkey:Int,isBlog:Boolean = false): DataSource.Factory<Int, FeedArticleDataHolder.FeedArticleHolder>


    //get all items
    @Query("SELECT id,myDbKey,active,activeVotes,already_paid,app,author,authorreputation,cashoutTime,category,children,created,createdcon,date,datespan,depth,displayName,entryId,followsYou,format,image,lastPayout,lastUpdate,links,pending_payout_value,netVotes,permlink,promoted,reblogBy,reblogOn,replies,rootAuthor,rootComment,rootPermlink,summary,tags,title,total_pending_payout_value,useFollow,users,uservoted,width,isBlog,saveTime,sDate from widget_holder where isBlog == :isBlog & myDbKey > :dbkey ORDER BY myDbKey ASC")
    fun getList(dbkey:Int,isBlog:Boolean = false): List<FeedArticleDataHolder.FeedArticleHolder>

    //get the last items db id
    @Query("SELECT myDbKey FROM widget_holder WHERE myDbKey = (SELECT MAX(myDbKey) FROM article_holder)")
    fun getLastDbId(): LiveData<Int>

    @Query("SELECT myDbKey FROM widget_holder WHERE myDbKey = (SELECT MAX(myDbKey) FROM article_holder)")
    fun getLastDbIdInt(): Int

    //@Query("SELECT * from training_av_table Limit 5")
    @Query("SELECT * from widget_holder ORDER BY myDbKey DESC LIMIT 20")
    fun getFirstTwenty(): LiveData<List<FeedArticleDataHolder.FeedArticleHolder>>
}