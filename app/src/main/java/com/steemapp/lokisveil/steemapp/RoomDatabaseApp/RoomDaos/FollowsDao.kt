package com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDaos

import android.arch.persistence.room.*
import com.steemapp.lokisveil.steemapp.jsonclasses.prof

@Dao
interface FollowsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(lite: prof.Resultfp):Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(list:List<prof.Resultfp>)

    @Query("select * from follow_tables")
    fun getAll():List<prof.Resultfp>

    @Query("delete from follow_tables")
    fun deleteAll()

    @Query("delete from follow_tables where dbid ==:id")
    fun delete(id:Int)

    @Query("SELECT EXISTS(SELECT 1 FROM follow_tables WHERE uniqueName == :name LIMIT 1)")
    fun searchFollower(name:String):Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM follow_tables WHERE uniqueName == :name LIMIT 1)")
    fun searchFollowing(name:String):Boolean

    @Query("SELECT COUNT(dbid) FROM follow_tables")
    fun getCount():Int

}