package com.steemapp.lokisveil.steemapp.Databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.text.format.DateUtils
import com.steemapp.lokisveil.steemapp.CentralConstants
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Enums.NotificationType
import com.steemapp.lokisveil.steemapp.jsonclasses.BusyNotificationJson
import java.text.SimpleDateFormat
import java.util.*

class NotificationsBusyDb (context : Context) : SQLiteOpenHelper(context, DatabaseName,null, DatabaseVersion) {
    companion object {
        val DatabaseName:String = "notificationsbusy"
        val DatabaseVersion:Int = 1
    }
    val DatabaseTableName = "notifications"
    val DatabaseColoumnId = "Id"
    val DatabaseColoumnType = "type"
    val DatabaseColoumnParentPermlink = "parentpermlink"
    val DatabaseColoumnAuthor = "author"
    val DatabaseColoumnDate = "date"
    val DatabaseColoumnPermlink = "permlink"
    val DatabaseColoumnBlock = "block"
    val DatabaseColoumnFollower = "follower"
    val DatabaseColoumnUserFrom = "fromthe"
    val DatabaseColoumnCommentAmount = "amount"
    val DatabaseColoumnMemo = "memo"
    val DatabaseColoumnAccount = "account"
    val DatabaseColoumnUniqueOne = "uniqueidentifier"
    /*val DatabaseColoumnReputation = "reputation"
    val DatabaseColoumnNetVoted = "netvotes"*/
    val DatabaseColoumnIsRootPost = "isRootPost"
    val context = context

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseTableName")
        onCreate(db)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTableName + " (" + DatabaseColoumnId + " integer primary key, " +
                DatabaseColoumnType + " text, " +
                DatabaseColoumnParentPermlink + " text, " +
                DatabaseColoumnPermlink + " text, " +
                DatabaseColoumnDate + " text, " +
                DatabaseColoumnAuthor + " text, " +

                DatabaseColoumnFollower + " text, " +
                DatabaseColoumnUserFrom + " text, " +
                DatabaseColoumnCommentAmount + " text, " +

                DatabaseColoumnAccount + " text, " +
                DatabaseColoumnIsRootPost + " integer, " +
                DatabaseColoumnBlock + " text, " +
                DatabaseColoumnUniqueOne + " text UNIQUE, " +
                //DatabaseColoumnCommentNumber + " integer, " +
                DatabaseColoumnMemo + " text" +
                ")")
    }


    @Synchronized
    override fun close() {
        super.close()
    }


    fun Insert(lite: BusyNotificationJson.Result): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DatabaseColoumnType, lite.type?.name)
        values.put(DatabaseColoumnParentPermlink, lite.parentPermlink)
        values.put(DatabaseColoumnPermlink, lite.permlink)
        values.put(DatabaseColoumnAuthor, lite.author)
        values.put(DatabaseColoumnDate, lite.timestamp)
        //values.put(DatabaseColoumnRebloggedBy, if(!lite.reblogBy?.isEmpty()!!) lite.reblogBy?.first() else "")
        //values.put(DatabaseColoumnImage, if(!lite.image?.isEmpty()!!) lite.image?.first() else "")
        values.put(DatabaseColoumnFollower, lite.follower)
        values.put(DatabaseColoumnUserFrom, lite.from)
        values.put(DatabaseColoumnCommentAmount, lite.amount)
        var ins = 0
        if(lite.isRootPost != null && lite.isRootPost){
            ins = 1
        }
        values.put(DatabaseColoumnIsRootPost, ins)
        values.put(DatabaseColoumnAccount, lite.account)
        values.put(DatabaseColoumnBlock, if(lite.block != null) lite.block.toString() else "")
        values.put(DatabaseColoumnMemo, lite.memo)
        values.put(DatabaseColoumnUniqueOne,"${lite.timestamp}${lite.block}")
        /*var pay = lite.pending_payout_value
        if((lite.already_paid.orEmpty() != "0.000 SBD")){
            pay = lite.already_paid
        }
        values.put(DatabaseColoumnPayout, pay)*/



        val key = db.insert(DatabaseTableName, null, values)
        //questionsFts.InsertQuestion(values);
        /*if (key > 0) {
            questionsFts.InsertQuestion(values)
        }*/
        //
        if(key > 0){
            return true
        }
        return false
    }


    fun GetAllQuestions(): ArrayList<BusyNotificationJson.Result> {
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

    fun deleteContact(permlink: String?): Int? {
        val db = this.writableDatabase

        return db.delete(DatabaseTableName,
                "$DatabaseColoumnPermlink = ? ",
                arrayOf(permlink!!))
    }

    fun HelperQuestionsList(cursor: Cursor): ArrayList<BusyNotificationJson.Result> {
        val questionsLites = ArrayList<BusyNotificationJson.Result>()
        if (cursor.count == 0) {
            return questionsLites
        }
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {


            var ispo = false
            var ins = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnIsRootPost))
            if(ins == 1){
                ispo = true
            }

            var ty = cursor.getString(cursor.getColumnIndex(DatabaseColoumnType))
            var noty = NotificationType.valueOf(ty)


            var du = DateUtils.getRelativeDateTimeString(context,(Date((cursor.getString(cursor.getColumnIndex(DatabaseColoumnDate))+"000").toLong())).time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0)
            val q = BusyNotificationJson.Result(

                    author = cursor.getString(cursor.getColumnIndex(DatabaseColoumnAuthor)),
                    account= cursor.getString(cursor.getColumnIndex(DatabaseColoumnAccount)),

                    amount = cursor.getString(cursor.getColumnIndex(DatabaseColoumnCommentAmount)),
                    block = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnBlock)),
                    follower = cursor.getString(cursor.getColumnIndex(DatabaseColoumnFollower)),

                    from = cursor.getString(cursor.getColumnIndex(DatabaseColoumnUserFrom)),
                    permlink = cursor.getString(cursor.getColumnIndex(DatabaseColoumnPermlink)),

                    memo = cursor.getString(cursor.getColumnIndex(DatabaseColoumnMemo)),
                    parentPermlink = cursor.getString(cursor.getColumnIndex(DatabaseColoumnParentPermlink)),

                    type = noty,
                    isRootPost = ispo,
                    showdate =  du.toString(),
                    timestamp = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnDate))
            )

            questionsLites.add(q)
            cursor.moveToNext()
        }

        return questionsLites
    }

    fun simpleSearch(value : String):Boolean{
        //SELECT EXISTS(SELECT 1 FROM myTbl WHERE u_tag="tag" LIMIT 1);
        var db = this.readableDatabase

        val tableColumns = arrayOf(DatabaseColoumnPermlink)
        //val whereClause = "$DatabaseColoumnPermlink = ? OR column1 = ?"
        val whereClause = "$DatabaseColoumnPermlink = ?"
        val whereArgs = arrayOf("$value")
        var cur = db.query(DatabaseTableName,tableColumns,whereClause,whereArgs,null,null,null)
        var curs = db.rawQuery("SELECT EXISTS(SELECT 1 FROM $DatabaseTableName WHERE $DatabaseColoumnPermlink=\"$value\" LIMIT 1)", null)
        cur.count
        /*cur.columnCount
        cur.columnNames
        cur.moveToFirst()
        var cus = cur.getString(0)
        curs.count
        curs.columnCount
        curs.columnNames
        curs.moveToFirst()
        var cuss = curs.getString(0)
        var sjs = 0
        sjs = sjs + 100*/
        //var cus = cur.getString(0)
        //var ses = curs.getString(1)
        //var re = HelperQuestionsList(curs)
        return if(cur.count > 0) return true else false

        //var cursor = db.rawQuery("select * from "+ CentralConstants.DatabaseNameFavourites + " where "+DatabaseName + " Match "+search,null);
    }


    /*fun searchDb(search:String) : List<FeedArticleDataHolder.FeedArticleHolder>{
        var questionsLites =  ArrayList<FeedArticleDataHolder.FeedArticleHolder>()
        var db = this.readableDatabase;
        var cursor = db.rawQuery("select * from "+ CentralConstants.DatabaseNameFavourites + " where "+DatabaseName + " Match "+search,null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false){
            ForReturningQuestionsLite q = new ForReturningQuestionsLite();
            q.questionsId = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnQuestionId));

            q.question = cursor.getString(cursor.getColumnIndex(DatabaseColoumnQuestion));
            q.questionTitles = cursor.getString(cursor.getColumnIndex(DatabaseColoumnQuestionTitles));
            q.questionSummarys = cursor.getString(cursor.getColumnIndex(DatabaseColoumnQuestionSummarys));

            q.askedByUser = cursor.getString(cursor.getColumnIndex(DatabaseColoumnAskedByUser));
            q.askedDate = new Date(cursor.getLong(cursor.getColumnIndex(DatabaseColoumnAskedDate)));

            boolean fixer = false;
            if(cursor.getInt(cursor.getColumnIndex(DatabaseColoumnOpen)) == 1){
                fixer = true;
            }

            q.open = fixer;

            fixer = false;

            if(cursor.getInt(cursor.getColumnIndex(DatabaseColoumnAnswered)) == 1){
                fixer = true;
            }

            q.answered = fixer;
            q.TotalNumberOfAnswers = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnTotalNumberOfAnswers));
            questionsLites.add(q);
        }

        return questionsLites;
    }*/


    //uncomment for adding search in the future

/*    fun SearchDb(search: String): ArrayList<ForReturningQuestionsLite> {
        //both work
        val go = HelperQuestionsList(questionsFts.getWordMatches(search, null)!!)
        val mine = HelperQuestionsList(questionsFts.SearchDb(search))

        //Collections.sort(mine,new SorterOfDate());
        Collections.sort(go, SorterOfDate())
        return go
        //return HelperQuestionsList(questionsFts.SearchDb(search));
        *//*ArrayList<ForReturningQuestionsLite> questionsLites = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+ DatabaseName + " where "+DatabaseName + " Match "+search,null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false){
            ForReturningQuestionsLite q = new ForReturningQuestionsLite();
            q.questionsId = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnQuestionId));

            q.question = cursor.getString(cursor.getColumnIndex(DatabaseColoumnQuestion));
            q.questionTitles = cursor.getString(cursor.getColumnIndex(DatabaseColoumnQuestionTitles));
            q.questionSummarys = cursor.getString(cursor.getColumnIndex(DatabaseColoumnQuestionSummarys));

            q.askedByUser = cursor.getString(cursor.getColumnIndex(DatabaseColoumnAskedByUser));
            q.askedDate = new Date(cursor.getLong(cursor.getColumnIndex(DatabaseColoumnAskedDate)));

            boolean fixer = false;
            if(cursor.getInt(cursor.getColumnIndex(DatabaseColoumnOpen)) == 1){
                fixer = true;
            }

            q.open = fixer;

            fixer = false;

            if(cursor.getInt(cursor.getColumnIndex(DatabaseColoumnAnswered)) == 1){
                fixer = true;
            }

            q.answered = fixer;
            q.TotalNumberOfAnswers = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnTotalNumberOfAnswers));
            questionsLites.add(q);
        }

        return questionsLites;*//*
    }


    private class QuestionsFts
    //ArrayList<ForReturningQuestionsLite> filler;


    (private val context: Context) : SQLiteOpenHelper(context, Name, null, DatabaseVersion) {
        private var mDatabase: SQLiteDatabase? = null

        init {
            mDatabase = this.readableDatabase
            //filler = fill;
            //FillDb(filler);
        }

        override fun onCreate(sqLiteDatabasee: SQLiteDatabase) {
            sqLiteDatabasee.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS " + DatabaseName + " USING FTS3 (" + DatabaseColoumnId + ", " +
                    DatabaseColoumnQuestionId + ", " +
                    DatabaseColoumnQuestion + ", " +
                    DatabaseColoumnQuestionTitles + ", " +
                    DatabaseColoumnQuestionSummarys + ", " +
                    DatabaseColoumnAskedDate + ", " +
                    DatabaseColoumnAskedByUser + ", " +

                    DatabaseColoumnOpen + ", " +
                    DatabaseColoumnAnswered + ", " +
                    DatabaseColoumnTotalNumberOfAnswers +
                    ")")
            mDatabase = sqLiteDatabasee

            *//*if(filler != null){
                FillDb(filler);
            }*//*

        }

        override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {

        }

        *//*public void FillDb(ArrayList<ForReturningQuestionsLite> list){
            for (ForReturningQuestionsLite item: list) {
                InsertQuestion(item);
            }
            filler = null;
        }*//*


        fun InsertQuestion(lite: ForReturningQuestionsLite): Boolean {

            val values = ContentValues()
            values.put(DatabaseColoumnQuestionId, lite.questionsId)
            values.put(DatabaseColoumnQuestion, lite.question)
            values.put(DatabaseColoumnQuestionTitles, lite.questionTitles)

            values.put(DatabaseColoumnQuestionSummarys, lite.questionSummarys)
            values.put(DatabaseColoumnAskedDate, lite.askedDate.getTime())
            values.put(DatabaseColoumnAskedByUser, lite.askedByUser)

            var liteo = 0
            if (lite.open) {

                liteo = 1
            }
            values.put(DatabaseColoumnOpen, liteo)

            var litea = 0
            if (lite.answered) {

                litea = 1
            }
            values.put(DatabaseColoumnAnswered, litea)
            values.put(DatabaseColoumnTotalNumberOfAnswers, lite.TotalNumberOfAnswers)

            val k = mDatabase!!.insert(DatabaseName, null, values)
            return true
        }


        fun InsertQuestion(values: ContentValues): Boolean {
            val k = mDatabase!!.insert(DatabaseName, null, values)
            return true
        }

        fun getWordMatches(query: String, columns: Array<String>?): Cursor? {
            val selection = "$DatabaseName MATCH ?"
            val selectionArgs = arrayOf("$query*")

            return query(selection, selectionArgs, columns)
        }

        private fun query(selection: String, selectionArgs: Array<String>, columns: Array<String>?): Cursor? {
            val builder = SQLiteQueryBuilder()
            builder.tables = DatabaseName


            val cursor = builder.query(this.readableDatabase,
                    columns, selection, selectionArgs, null, null, null)

            if (cursor == null) {
                return null
            } else if (!cursor.moveToFirst()) {
                cursor.close()
                return null
            }
            return cursor
        }


        fun SearchDb(search: String): Cursor {
            //ArrayList<ForReturningQuestionsLite> questionsLites = new ArrayList<>();
            val db = this.readableDatabase
            val re = arrayOf(search)
            return db.rawQuery("select * from ${DatabaseName} where ${DatabaseName} Match ?", re)
            *//*cursor.moveToFirst();
            while (cursor.isAfterLast() == false){
                ForReturningQuestionsLite q = new ForReturningQuestionsLite();
                q.questionsId = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnQuestionId));

                q.question = cursor.getString(cursor.getColumnIndex(DatabaseColoumnQuestion));
                q.questionTitles = cursor.getString(cursor.getColumnIndex(DatabaseColoumnQuestionTitles));
                q.questionSummarys = cursor.getString(cursor.getColumnIndex(DatabaseColoumnQuestionSummarys));

                q.askedByUser = cursor.getString(cursor.getColumnIndex(DatabaseColoumnAskedByUser));
                q.askedDate = new Date(cursor.getLong(cursor.getColumnIndex(DatabaseColoumnAskedDate)));

                boolean fixer = false;
                if(cursor.getInt(cursor.getColumnIndex(DatabaseColoumnOpen)) == 1){
                    fixer = true;
                }

                q.open = fixer;

                fixer = false;

                if(cursor.getInt(cursor.getColumnIndex(DatabaseColoumnAnswered)) == 1){
                    fixer = true;
                }

                q.answered = fixer;
                q.TotalNumberOfAnswers = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnTotalNumberOfAnswers));
                questionsLites.add(q);
            }

            return questionsLites;*//*
        }

        companion object {
            private val DatabaseName = "questionlistsftsnew"
            private val Name = "questionlistsftsnew.db"
        }
    }*/

}