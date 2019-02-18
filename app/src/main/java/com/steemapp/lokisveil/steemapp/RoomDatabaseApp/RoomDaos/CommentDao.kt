package com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDaos


import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(data: FeedArticleDataHolder.CommentHolder)


    //wrap with livedata for observing in the future
    @Query("SELECT * from comment_holder ORDER BY myDbKey ASC")
    fun getAll(): LiveData<List<FeedArticleDataHolder.CommentHolder>>

    @Query("SELECT * from comment_holder where id == :eid")
    fun getArticle(eid:Int): LiveData<FeedArticleDataHolder.CommentHolder>

    /*@Query("SELECT * from events where id == :eid ORDER BY id DESC LIMIT 20")
    fun getPagedList(eid:Int): DataSource.Factory<Int,EventsEntity>*/
    @Query("SELECT * from comment_holder ORDER BY myDbKey DESC")
    fun getPagedList(): DataSource.Factory<Integer, FeedArticleDataHolder.CommentHolder>

    //@Query("SELECT * from training_av_table Limit 5")
    @Query("SELECT * from comment_holder ORDER BY myDbKey DESC LIMIT 20")
    fun getFirstTwenty(): LiveData<List<FeedArticleDataHolder.CommentHolder>>
}