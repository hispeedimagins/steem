package com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDaos

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.*
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import org.json.JSONArray

@Dao
interface ArticleDao {

    //insert an item
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(data:FeedArticleDataHolder.FeedArticleHolder):Long

    //update an item
    @Update
    fun update(data:FeedArticleDataHolder.FeedArticleHolder):Int

    //delete all
    @Query("delete from article_holder")
    fun deleteAll()

    @Query("delete from article_holder where isBlog = :isBlog")
    fun deleteAll(isBlog:Boolean)

    //wrap with livedata for observing in the future
    @Query("SELECT * from article_holder ORDER BY myDbKey ASC")
    fun getAll(): LiveData<List<FeedArticleDataHolder.FeedArticleHolder>>

    //get an item matching article id
    @Query("SELECT * from article_holder where id == :eid")
    fun getArticle(eid:Long): LiveData<FeedArticleDataHolder.FeedArticleHolder>

    //get an item matching article id
    @Query("SELECT * from article_holder where id == :eid")
    fun getArticleNormal(eid:Long): FeedArticleDataHolder.FeedArticleHolder?

    //get an article matching internal db
    @Query("SELECT * from article_holder where myDbKey == :eid")
    fun getArticleMy(eid:Int): LiveData<FeedArticleDataHolder.FeedArticleHolder>

    //get a paged list of all items
    @Query("SELECT id,myDbKey,active,already_paid,app,author,authorreputation,cashoutTime,category,children,created,createdcon,date,datespan,depth,displayName,entryId,followsYou,format,image,lastPayout,lastUpdate,links,pending_payout_value,netVotes,permlink,promoted,reblogBy,reblogOn,replies,rootAuthor,rootComment,rootPermlink,summary,tags,title,total_pending_payout_value,useFollow,users,uservoted,width,isBlog,saveTime from article_holder where isBlog == :isBlog ORDER BY myDbKey ASC")
    fun getPagedList(isBlog:Boolean = false): DataSource.Factory<Int,FeedArticleDataHolder.FeedArticleHolder>

    //get a paged list of all items ordered by savetime
    @Query("SELECT id,myDbKey,active,already_paid,app,author,authorreputation,cashoutTime,category,children,created,createdcon,date,datespan,depth,displayName,entryId,followsYou,format,lastPayout,lastUpdate,pending_payout_value,netVotes,permlink,promoted,reblogBy,reblogOn,replies,rootAuthor,rootComment,rootPermlink,displayImage,summary,tags,title,total_pending_payout_value,useFollow,uservoted,width,isBlog,saveTime from article_holder where isBlog == :isBlog ORDER BY saveTime DESC")
    fun getPagedListTime(isBlog:Boolean = false): DataSource.Factory<Int,FeedArticleDataHolder.FeedArticleHolder>

    //get a paged list after an id
    @Query("SELECT id,myDbKey,active,already_paid,app,author,authorreputation,cashoutTime,category,children,created,createdcon,date,datespan,depth,displayName,entryId,followsYou,format,image,lastPayout,lastUpdate,links,pending_payout_value,netVotes,permlink,promoted,reblogBy,reblogOn,replies,rootAuthor,rootComment,rootPermlink,summary,tags,title,total_pending_payout_value,useFollow,users,uservoted,width,isBlog,saveTime from article_holder where isBlog == :isBlog and myDbKey > :dbkey ORDER BY myDbKey ASC")
    fun getPagedList(dbkey:Int,isBlog:Boolean = false): DataSource.Factory<Int,FeedArticleDataHolder.FeedArticleHolder>

    @Query("Select activeVotes from article_holder where myDbKey == :dbKey")
    fun fetchActiveVotes(dbKey:Int):LiveData<JSONArray>
    //get the last items db id
    @Query("SELECT myDbKey FROM article_holder WHERE myDbKey = (SELECT MAX(myDbKey) FROM article_holder)")
    fun getLastDbId():LiveData<Int>

    //get first twenty items
    @Query("SELECT * from article_holder ORDER BY myDbKey DESC LIMIT 20")
    fun getFirstTwenty(): LiveData<List<FeedArticleDataHolder.FeedArticleHolder>>
}