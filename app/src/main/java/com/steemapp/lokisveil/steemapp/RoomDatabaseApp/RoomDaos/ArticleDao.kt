package com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDaos

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data:FeedArticleDataHolder.FeedArticleHolder)

    @Query("delete from article_holder")
    fun deleteAll()

    //wrap with livedata for observing in the future
    @Query("SELECT * from article_holder ORDER BY myDbKey ASC")
    fun getAll(): LiveData<List<FeedArticleDataHolder.FeedArticleHolder>>

    @Query("SELECT * from article_holder where id == :eid")
    fun getArticle(eid:Int): LiveData<FeedArticleDataHolder.FeedArticleHolder>

    @Query("SELECT * from article_holder where myDbKey == :eid")
    fun getArticleMy(eid:Int): LiveData<FeedArticleDataHolder.FeedArticleHolder>
    /*@Query("SELECT * from events where id == :eid ORDER BY id DESC LIMIT 20")
    fun getPagedList(eid:Int): DataSource.Factory<Int,EventsEntity>*/
    /*@Query("SELECT * from article_holder ORDER BY myDbKey DESC")
    fun getPagedList(): DataSource.Factory<Int,FeedArticleDataHolder.FeedArticleHolder>*/
    @Query("SELECT id,myDbKey,active,activeVotes,already_paid,app,author,authorreputation,cashoutTime,category,children,created,createdcon,date,datespan,depth,displayName,entryId,followsYou,format,image,lastPayout,lastUpdate,links,pending_payout_value,netVotes,permlink,promoted,reblogBy,reblogOn,replies,rootAuthor,rootComment,rootPermlink,summary,tags,title,total_pending_payout_value,useFollow,users,uservoted,width,isBlog from article_holder where isBlog == :isBlog ORDER BY myDbKey ASC")
    fun getPagedList(isBlog:Boolean = false): DataSource.Factory<Integer,FeedArticleDataHolder.FeedArticleHolder>

    @Query("SELECT id,myDbKey,active,activeVotes,already_paid,app,author,authorreputation,cashoutTime,category,children,created,createdcon,date,datespan,depth,displayName,entryId,followsYou,format,image,lastPayout,lastUpdate,links,pending_payout_value,netVotes,permlink,promoted,reblogBy,reblogOn,replies,rootAuthor,rootComment,rootPermlink,summary,tags,title,total_pending_payout_value,useFollow,users,uservoted,width,isBlog from article_holder where isBlog == :isBlog and myDbKey > :dbkey ORDER BY myDbKey ASC")
    fun getPagedList(dbkey:Int,isBlog:Boolean = false): DataSource.Factory<Integer,FeedArticleDataHolder.FeedArticleHolder>

    @Query("SELECT myDbKey FROM article_holder WHERE myDbKey = (SELECT MAX(myDbKey) FROM article_holder)")
    fun getLastDbId():LiveData<Int>
    //@Query("SELECT * from training_av_table Limit 5")
    @Query("SELECT * from article_holder ORDER BY myDbKey DESC LIMIT 20")
    fun getFirstTwenty(): LiveData<List<FeedArticleDataHolder.FeedArticleHolder>>
}