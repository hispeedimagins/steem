package com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDaos

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder

@Dao
interface BeneficiaryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(data: FeedArticleDataHolder.beneficiariesDataHolder)


    //wrap with livedata for observing in the future
    @Query("SELECT * from beneficiary_holder ORDER BY dbid ASC")
    fun getAll(): LiveData<List<FeedArticleDataHolder.beneficiariesDataHolder>>

    @Query("SELECT * from beneficiary_holder where dbid == :eid")
    fun getArticle(eid:Int): LiveData<FeedArticleDataHolder.beneficiariesDataHolder>

    /*@Query("SELECT * from events where id == :eid ORDER BY id DESC LIMIT 20")
    fun getPagedList(eid:Int): DataSource.Factory<Int,EventsEntity>*/
    @Query("SELECT * from beneficiary_holder ORDER BY dbid DESC")
    fun getPagedList(): DataSource.Factory<Integer, FeedArticleDataHolder.beneficiariesDataHolder>

    //@Query("SELECT * from training_av_table Limit 5")
    @Query("SELECT * from beneficiary_holder ORDER BY dbid DESC LIMIT 20")
    fun getFirstTwenty(): LiveData<List<FeedArticleDataHolder.beneficiariesDataHolder>>
}