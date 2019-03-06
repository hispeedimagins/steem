package com.insteem.ipfreely.steem.Databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.insteem.ipfreely.steem.CentralConstants
import com.insteem.ipfreely.steem.jsonclasses.prof
import java.util.ArrayList

/**
 * Created by boot on 3/31/2018.
 */
class FollowersDatabase(context : Context) : SQLiteOpenHelper(context, CentralConstants.DatabaseNameFollowers,null,CentralConstants.DatabaseVersionFollowers){

    val DatabaseTableName = "followersList"
    val DatabaseColoumnId = "Id"
    val DatabaseColoumnFollower = "follower"
    val DatabaseColoumnFollowing = "following"
    val DatabaseColoumnWhat = "what"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTableName + " (" + DatabaseColoumnId + " integer primary key, " +
                DatabaseColoumnFollower + " text UNIQUE, " +
                DatabaseColoumnFollowing + " text, " +
                DatabaseColoumnWhat + " text" +

                ")")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseTableName")
        onCreate(db)
    }

    @Synchronized
    override fun close() {
        super.close()
    }


    fun Insert(lite: prof.Resultfp): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DatabaseColoumnFollower, lite.follower)
        values.put(DatabaseColoumnFollowing, lite.following)
        values.put(DatabaseColoumnWhat, if(!lite.what?.isEmpty()!!) lite.what?.first() else "")

        val key = db.insert(DatabaseTableName, null, values)
        if(key > 0){
            return true
        }
        return false
    }


    fun GetAllQuestions(): ArrayList<prof.Resultfp> {
        //val questionsLites = ArrayList<FeedArticleDataHolder.FeedArticleHolder>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("select * from $DatabaseTableName", null)
        return HelperQuestionsList(cursor)


    }


    //For deleting with Id
    fun deleteContact(id: Long?): Int? {
        val db = this.writableDatabase

        return db.delete(DatabaseTableName,
                "Id = ? ",
                arrayOf(id?.toString()))
    }

    fun deleteContact(permlink: String?): Int? {
        val db = this.writableDatabase

        return db.delete(DatabaseTableName,
                "$DatabaseColoumnFollower = ? ",
                arrayOf(permlink!!))
    }

    fun HelperQuestionsList(cursor: Cursor): ArrayList<prof.Resultfp> {
        val questionsLites = ArrayList<prof.Resultfp>()
        if (cursor.count == 0) {
            return questionsLites
        }
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            var rl = ArrayList<String>()
            var rb = cursor.getString(cursor.getColumnIndex(DatabaseColoumnWhat))
            if(rb != ""){
                rl.add(rb)
            }

            val q = prof.Resultfp(
                follower = cursor.getString(cursor.getColumnIndex(DatabaseColoumnFollower)),
                following = cursor.getString(cursor.getColumnIndex(DatabaseColoumnFollowing)),
                    what = rl,
                    //return dbid as well for deletion
                    dbid = cursor.getLong(cursor.getColumnIndex(DatabaseColoumnId)),
                    uniqueName = cursor.getString(cursor.getColumnIndex(DatabaseColoumnFollower)),
                    isFollower = true
            )

            questionsLites.add(q)
            cursor.moveToNext()
        }

        return questionsLites
    }

    fun simpleSearch(value : String):Boolean{
        //SELECT EXISTS(SELECT 1 FROM myTbl WHERE u_tag="tag" LIMIT 1);
        var db = this.readableDatabase

        val tableColumns = arrayOf(DatabaseColoumnFollower)
        //val whereClause = "$DatabaseColoumnPermlink = ? OR column1 = ?"
        val whereClause = "$DatabaseColoumnFollower = ?"
        val whereArgs = arrayOf("$value")
        var cur = db.query(DatabaseTableName,tableColumns,whereClause,whereArgs,null,null,null)
        var curs = db.rawQuery("SELECT EXISTS(SELECT 1 FROM $DatabaseTableName WHERE $DatabaseColoumnFollower=\"$value\" LIMIT 1)", null)
        cur.count

        return if(cur.count > 0) return true else false

    }

}