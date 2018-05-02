package com.steemapp.lokisveil.steemapp.Databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.steemapp.lokisveil.steemapp.CentralConstants
import com.steemapp.lokisveil.steemapp.jsonclasses.prof
import java.util.*

class ImageUploadedUrls(context : Context) : SQLiteOpenHelper(context, DatabaseName,null, DatabaseVersion){

    companion object {
        val DatabaseName:String = "imageurls"
        val DatabaseVersion:Int = 1
    }

    val DatabaseTableName = "imageurl"
    val DatabaseColoumnId = "Id"
    val DatabaseColoumnUrl= "url"
    val DatabaseColoumnDate = "date"


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTableName + " (" + DatabaseColoumnId + " integer primary key, " +
                DatabaseColoumnUrl + " text UNIQUE, " +
                DatabaseColoumnDate + " text " +


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


    fun Insert(url:String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DatabaseColoumnUrl, url)
        var date = Date()

        values.put(DatabaseColoumnDate, date.time.toString())

        val key = db.insert(DatabaseTableName, null, values)
        if(key > 0){
            return true
        }
        return false
    }


    fun GetAllQuestions(): ArrayList<String> {
        //val questionsLites = ArrayList<FeedArticleDataHolder.FeedArticleHolder>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("select * from $DatabaseTableName", null)
        return HelperQuestionsList(cursor)


    }


    fun deleteContact(id: Int?): Int? {
        val db = this.writableDatabase

        return db.delete(DatabaseTableName,
                "id = ? ",
                arrayOf(Integer.toString(id!!)))
    }

    fun deleteContact(permlink: String?): Int? {
        val db = this.writableDatabase

        return db.delete(DatabaseTableName,
                "$DatabaseColoumnUrl = ? ",
                arrayOf(permlink!!))
    }

    fun HelperQuestionsList(cursor: Cursor): ArrayList<String> {
        val questionsLites = ArrayList<String>()
        if (cursor.count == 0) {
            return questionsLites
        }
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            var url = cursor.getString(cursor.getColumnIndex(DatabaseColoumnUrl))


            questionsLites.add(url)
            cursor.moveToNext()
        }

        return questionsLites
    }

    fun simpleSearch(value : String):Boolean{
        //SELECT EXISTS(SELECT 1 FROM myTbl WHERE u_tag="tag" LIMIT 1);
        var db = this.readableDatabase

        val tableColumns = arrayOf(DatabaseColoumnUrl)
        //val whereClause = "$DatabaseColoumnPermlink = ? OR column1 = ?"
        val whereClause = "$DatabaseColoumnUrl = ?"
        val whereArgs = arrayOf("$value")
        var cur = db.query(DatabaseTableName,tableColumns,whereClause,whereArgs,null,null,null)
        var curs = db.rawQuery("SELECT EXISTS(SELECT 1 FROM $DatabaseTableName WHERE $DatabaseColoumnUrl=\"$value\" LIMIT 1)", null)
        cur.count

        return if(cur.count > 0) return true else false

    }

}