package com.steemapp.lokisveil.steemapp.Databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.HelperClasses.calendarcalculations
import com.steemapp.lokisveil.steemapp.jsonclasses.OperationJson
import java.util.*


//Database for holding beneficiaries added or edited
class beneficiariesDatabase(context : Context) : SQLiteOpenHelper(context, DatabaseName,null, DatabaseVersion){

    companion object {
        val DatabaseName:String = "beneficiaries"
        val DatabaseVersion:Int = 1
    }

    val DatabaseTableName = "beneficiary"
    val DatabaseColoumnId = "Id"
    val DatabaseColoumnName= "name"
    val DatabaseColoumnDate = "date"
    val DatabaseColoumnPercent = "percent"
    val DatabaseColoumnIsDeveloper = "developers"
    val DatabaseColoumnIsDefault = "isdefault"
    val DatabaseColoumnTagsToUseOn = "tags"
    val DatabaseColoumnIsBuiltIn = "isbuiltin"


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTableName + " (" + DatabaseColoumnId + " integer primary key, " +
                DatabaseColoumnName + " text UNIQUE, " +
                DatabaseColoumnPercent + " integer, " +
                DatabaseColoumnIsDeveloper + " integer, " +
                DatabaseColoumnIsDefault + " integer, " +
                DatabaseColoumnIsBuiltIn + " integer, " +
                DatabaseColoumnTagsToUseOn + " text, " +
                DatabaseColoumnDate + " text " +


                ")")

        // Default ones we add. Only when the db is created.
        Insert(FeedArticleDataHolder.beneficiariesDataHolder("hispeedimagins",15,1,1,"",Date().time,"11:08",1,1,1),db)
        Insert(FeedArticleDataHolder.beneficiariesDataHolder("steemj",5,1,1,"",Date().time,"11:08",1,1,2),db)
        Insert(FeedArticleDataHolder.beneficiariesDataHolder("utopian-io",30,0,0,"utopian-io",Date().time,"11:08",1,0,3),db)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseTableName")
        onCreate(db)
    }

    @Synchronized
    override fun close() {
        super.close()
    }




    fun Insert(ben:FeedArticleDataHolder.beneficiariesDataHolder,dbs: SQLiteDatabase? = null): Long {
        val db = if(dbs == null) this.writableDatabase else dbs
        val values = ContentValues()
        values.put(DatabaseColoumnName, ben.username)
        values.put(DatabaseColoumnTagsToUseOn, ben.tags)
        values.put(DatabaseColoumnDate, ben.dateLong)
        values.put(DatabaseColoumnIsDefault, ben.isdefault)
        values.put(DatabaseColoumnIsBuiltIn, ben.isbuiltin)
        values.put(DatabaseColoumnIsDeveloper, ben.isdeveloper)
        values.put(DatabaseColoumnPercent, ben.percent)


        val key = db.insert(DatabaseTableName, null, values)

        /*if(key > 0){
            return true
        }*/
        return key
    }

    fun update(ben:FeedArticleDataHolder.beneficiariesDataHolder): Boolean{
        var db = this.writableDatabase
        val values = ContentValues()
        values.put(DatabaseColoumnName, ben.username)
        values.put(DatabaseColoumnTagsToUseOn, ben.tags)
        values.put(DatabaseColoumnDate, ben.dateLong)
        values.put(DatabaseColoumnIsDefault, ben.isdefault)
        values.put(DatabaseColoumnIsBuiltIn, ben.isbuiltin)
        values.put(DatabaseColoumnIsDeveloper, ben.isdeveloper)
        values.put(DatabaseColoumnPercent, ben.percent)
        val key = db.update(DatabaseTableName,values,"Id = ${ben.dbid}",null)
        if(key > 0){
            return true
        }
        return false
    }


    fun GetAllQuestions(): ArrayList<FeedArticleDataHolder.beneficiariesDataHolder> {
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

    fun HelperQuestionsList(cursor: Cursor): ArrayList<FeedArticleDataHolder.beneficiariesDataHolder> {
        val questionsLites = ArrayList<FeedArticleDataHolder.beneficiariesDataHolder>()
        if (cursor.count == 0) {
            return questionsLites
        }
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {

            //var d = calendarcalculations() //2018-02-03T13:58:18
            //var date = Date((cursor.getString(cursor.getColumnIndex(DatabaseColoumnDate))).toLong())
            //d.setDateOfTheData(date)
            var isdef = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnIsDefault))
            var isdev = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnIsDeveloper))
            val q = FeedArticleDataHolder.beneficiariesDataHolder(
                    username = cursor.getString(cursor.getColumnIndex(DatabaseColoumnName)),
                    tags = cursor.getString(cursor.getColumnIndex(DatabaseColoumnTagsToUseOn)),
                    dateLong = cursor.getLong(cursor.getColumnIndex(DatabaseColoumnDate)),
                    dbid = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnId)),
                    isbuiltin = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnIsBuiltIn)),
                    isdefault = isdef,
                    isdeveloper = isdev,
                    percent = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnPercent)),
                    usenow = if(isdef == 1 || isdev == 1) 1 else 0,
                    dateString = ""
                    //date = d.getDateTimeString()

            )

            questionsLites.add(q)
            cursor.moveToNext()
        }

        return questionsLites
    }

    /*fun Get(id: Int?): OperationJson.draftholder{
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

    }*/


}