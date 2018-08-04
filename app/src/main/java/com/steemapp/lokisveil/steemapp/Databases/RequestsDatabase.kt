package com.steemapp.lokisveil.steemapp.Databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.text.format.DateUtils
import com.steemapp.lokisveil.steemapp.DataHolders.Request
import com.steemapp.lokisveil.steemapp.Enums.NotificationType
import com.steemapp.lokisveil.steemapp.jsonclasses.BusyNotificationJson
import java.util.*

//database to store temp files for saveinstances
class RequestsDatabase  (context : Context) : SQLiteOpenHelper(context, DatabaseName,null, DatabaseVersion) {
    companion object {
        val DatabaseName:String = "request"
        val DatabaseVersion:Int = 2
    }
    val DatabaseTableName = "requeststemp"
    val DatabaseColoumnId = "Id"
    val DatabaseColoumnReqId = "reqid"
    val DatabaseColoumnJson = "json"
    val DatabaseColoumnDate = "date"
    val DatabaseColoumnTypeOfRequest = "typeofreq"
    val DatabaseColoumnOtherInfo = "otherinfo"


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseTableName")
        onCreate(db)
    }

    //create db
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTableName + " (" + DatabaseColoumnId + " integer primary key, " +
                DatabaseColoumnJson + " text, " +

                DatabaseColoumnDate + " text, " +
                DatabaseColoumnTypeOfRequest + " text, " +
                DatabaseColoumnOtherInfo + " text, " +

                DatabaseColoumnReqId + " integer" +

                ")")
    }


    @Synchronized
    override fun close() {
        super.close()
    }


    fun Insert(lite: Request): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DatabaseColoumnJson, lite.json)

        values.put(DatabaseColoumnDate, lite.dateLong.toString())

        values.put(DatabaseColoumnReqId, lite.reqnumber)
        values.put(DatabaseColoumnTypeOfRequest, lite.typeOfRequest)
        values.put(DatabaseColoumnOtherInfo, lite.otherInfo)
        values.put(DatabaseColoumnReqId, lite.reqnumber)


        return db.insert(DatabaseTableName, null, values)

        /*if(key > 0){
            return true
        }
        return false*/
    }


    fun GetAllQuestions(): ArrayList<Request> {
        //val questionsLites = ArrayList<FeedArticleDataHolder.FeedArticleHolder>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("select * from $DatabaseTableName", null)
        return HelperQuestionsList(cursor)


    }
    //Returns a single object
    /**
     * @param id database id
     */
    fun GetAllQuestions(id:Long): Request? {
        //val questionsLites = ArrayList<FeedArticleDataHolder.FeedArticleHolder>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("select * from $DatabaseTableName WHERE Id = ? ", arrayOf(id.toString()))
        if(cursor.count > 0){
            cursor.moveToFirst()
            return createRequestDataClass(cursor)
        }
        return null


    }

    //Delete all old references, deletes all which are more than 6 hours old. Still needs to be tested
    fun DeleteOld(){
        //43200000 - 12
        for(x in GetAllQuestions()){
            val sub = Date().time - x.dateLong
            if(sub > 21600000){
                deleteContact(x.dbId)
            }
        }
    }


    fun deleteContact(id: Int?): Int? {
        val db = this.writableDatabase

        return db.delete(DatabaseTableName,
                "Id = ? ",
                arrayOf(Integer.toString(id!!)))
    }

    fun deleteContact(id: Long?): Int? {
        val db = this.writableDatabase

        return db.delete(DatabaseTableName,
                "Id = ? ",
                arrayOf(id?.toString()))
    }

/*    fun deleteContact(permlink: String?): Int? {
        val db = this.writableDatabase

        return db.delete(DatabaseTableName,
                "$DatabaseColoumnPermlink = ? ",
                arrayOf(permlink!!))
    }*/

    fun HelperQuestionsList(cursor: Cursor): ArrayList<Request> {
        val questionsLites = ArrayList<Request>()
        if (cursor.count == 0) {
            return questionsLites
        }
        cursor.moveToFirst()
        //if it is not after the last on continue
        while (!cursor.isAfterLast) {
            var addit = createRequestDataClass(cursor)
            if(addit != null) questionsLites.add(addit)
            //if move to next is false then break
            if(!cursor.moveToNext()) break
        }

        return questionsLites
    }

    //made the creation of the class to a common function as it was needed in another place
    fun createRequestDataClass(cursor:Cursor):Request?{
        //catch exceptions while creating the class with the database cursor
        //return a null and continue working without crashing
        try{
            return Request(

                    json = cursor.getString(cursor.getColumnIndex(DatabaseColoumnJson)),
                    dateLong =  cursor.getString(cursor.getColumnIndex(DatabaseColoumnDate)).toLong(),
                    reqnumber = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnReqId)),
                    dbId = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnId)).toLong(),
                    otherInfo = cursor.getString(cursor.getColumnIndex(DatabaseColoumnOtherInfo)),
                    typeOfRequest = cursor.getString(cursor.getColumnIndex(DatabaseColoumnTypeOfRequest))


            )
        } catch (ex:Exception){

        }
        return null

    }

}