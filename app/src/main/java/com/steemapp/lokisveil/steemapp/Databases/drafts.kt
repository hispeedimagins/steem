package com.steemapp.lokisveil.steemapp.Databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.steemapp.lokisveil.steemapp.CentralConstants
import com.steemapp.lokisveil.steemapp.HelperClasses.calendarcalculations
import com.steemapp.lokisveil.steemapp.jsonclasses.OperationJson
import com.steemapp.lokisveil.steemapp.jsonclasses.prof
import java.text.SimpleDateFormat
import java.util.*

class drafts(context : Context) : SQLiteOpenHelper(context, DatabaseName,null, DatabaseVersion) {
    companion object {
        val DatabaseName:String = "drafts"
        val DatabaseVersion:Int = 2
    }
    val DatabaseTableName = "draft"
    val DatabaseColoumnId = "Id"
    val DatabaseColoumnTitle = "title"
    val DatabaseColoumnTags = "tags"
    val DatabaseColoumnContent = "content"
    val DatabaseColoumnPostValue = "rewardvalue"
    val DatabaseColoumnDate = "date"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTableName + " (" + DatabaseColoumnId + " integer primary key, " +
                DatabaseColoumnTitle + " text, " +
                DatabaseColoumnTags + " text UNIQUE, " +
                DatabaseColoumnContent + " text, " +
                DatabaseColoumnPostValue + " text, " +
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


    fun Insert(title:String,tags:String,content:String,postvalue:String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DatabaseColoumnTitle, title)
        values.put(DatabaseColoumnTags, tags)
        values.put(DatabaseColoumnContent, content)
        values.put(DatabaseColoumnPostValue, postvalue)
        var date = Date()

        values.put(DatabaseColoumnDate, date.time.toString())
        val key = db.insert(DatabaseTableName, null, values)

        /*if(key > 0){
            return true
        }*/
        return key
    }

    fun update(id:Int,title:String,tags:String,content:String,postvalue:String): Boolean{
        var db = this.writableDatabase
        val values = ContentValues()
        values.put(DatabaseColoumnTitle, title)
        values.put(DatabaseColoumnTags, tags)
        values.put(DatabaseColoumnContent, content)
        values.put(DatabaseColoumnPostValue, postvalue)
        var date = Date()

        values.put(DatabaseColoumnDate, date.time.toString())
        val key = db.update(DatabaseTableName,values,"Id = $id",null)
        if(key > 0){
            return true
        }
        return false
    }


    fun GetAllQuestions(): ArrayList<OperationJson.draftholder> {
        //val questionsLites = ArrayList<FeedArticleDataHolder.FeedArticleHolder>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("select * from $DatabaseTableName", null)
        return HelperQuestionsList(cursor)


    }


    fun deleteContact(id: Int?): Int? {
        val db = this.writableDatabase


        return db.delete(DatabaseTableName,
                "Id = ? ",
                arrayOf(Integer.toString(id!!)))
    }

    /*fun deleteContact(permlink: String?): Int? {
        val db = this.writableDatabase

        return db.delete(DatabaseTableName,
                "$DatabaseColoumnFollower = ? ",
                arrayOf(permlink!!))
    }*/

    fun HelperQuestionsList(cursor: Cursor): ArrayList<OperationJson.draftholder> {
        val questionsLites = ArrayList<OperationJson.draftholder>()
        if (cursor.count == 0) {
            return questionsLites
        }
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {

            var d = calendarcalculations() //2018-02-03T13:58:18
            var date = Date((cursor.getString(cursor.getColumnIndex(DatabaseColoumnDate))).toLong())
            d.setDateOfTheData(date)
            val q = OperationJson.draftholder(
                    title = cursor.getString(cursor.getColumnIndex(DatabaseColoumnTitle)),
                    tags = cursor.getString(cursor.getColumnIndex(DatabaseColoumnTags)),
                    content = cursor.getString(cursor.getColumnIndex(DatabaseColoumnContent)),
                    payouttype = cursor.getString(cursor.getColumnIndex(DatabaseColoumnPostValue)),
                    dbid = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnId)),
                    date = d.getDateTimeString()

            )

            questionsLites.add(q)
            cursor.moveToNext()
        }

        return questionsLites
    }

    fun Get(id: Int?):OperationJson.draftholder{
        //SELECT EXISTS(SELECT 1 FROM myTbl WHERE u_tag="tag" LIMIT 1);
        var db = this.readableDatabase

        val tableColumns = arrayOf(DatabaseColoumnId,DatabaseColoumnContent,DatabaseColoumnPostValue,DatabaseColoumnTags,DatabaseColoumnTitle)
        //val whereClause = "$DatabaseColoumnPermlink = ? OR column1 = ?"
        val whereClause = "id = ? "
        val whereArgs = arrayOf("$id")
        var cursor = db.query(DatabaseTableName,tableColumns,whereClause,whereArgs,null,null,null)
        //var curs = db.rawQuery("SELECT EXISTS(SELECT * FROM $DatabaseTableName WHERE $DatabaseColoumnId=\"$id\" LIMIT 1)", null)
        cursor.count
        cursor.columnCount
        cursor.columnNames
        cursor.moveToFirst()

        val q = OperationJson.draftholder(
                title = cursor.getString(cursor.getColumnIndex(DatabaseColoumnTitle)),
                tags = cursor.getString(cursor.getColumnIndex(DatabaseColoumnTags)),
                content = cursor.getString(cursor.getColumnIndex(DatabaseColoumnContent)),
                payouttype = cursor.getString(cursor.getColumnIndex(DatabaseColoumnPostValue)),
                dbid = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnId)),
                date = Date().time.toString()

        )
        //var cus = cur.getString(0)
        //var ses = curs.getString(1)
        //var re = HelperQuestionsList(curs)
        return q

    }
}