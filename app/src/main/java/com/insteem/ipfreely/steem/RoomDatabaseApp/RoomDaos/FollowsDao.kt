package com.insteem.ipfreely.steem.RoomDatabaseApp.RoomDaos


import androidx.paging.DataSource
import androidx.room.*
import com.insteem.ipfreely.steem.jsonclasses.prof

@Dao
interface FollowsDao {
    //insert an item
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(lite: prof.Resultfp):Long

    @Update
    fun update(lite:prof.Resultfp)

    //insert a list
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(list:List<prof.Resultfp>)


    @Query("SELECT * from follow_tables where isFollower == :isFollower ORDER BY uniqueName ASC")
    fun getPagedList(isFollower:Boolean = true): DataSource.Factory<Integer,prof.Resultfp>

    //get all
    @Query("select * from follow_tables")
    fun getAll():List<prof.Resultfp>

    //delete all the records
    @Query("delete from follow_tables")
    fun deleteAll()

    //delete a single follower/following
    @Query("delete from follow_tables where dbid ==:id")
    fun delete(id:Int)

    //check if someone is following the uer
    @Query("SELECT EXISTS(SELECT 1 FROM follow_tables WHERE uniqueName == :name LIMIT 1)")
    fun searchFollower(name:String):Boolean

    //checks if a user is following someone
    @Query("SELECT EXISTS(SELECT 1 FROM follow_tables WHERE uniqueName == :name LIMIT 1)")
    fun searchFollowing(name:String):Boolean

    //get the total number of items from the db
    @Query("SELECT COUNT(dbid) FROM follow_tables")
    fun getCount():Int

}