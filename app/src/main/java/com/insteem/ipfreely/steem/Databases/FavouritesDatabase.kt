package com.insteem.ipfreely.steem.Databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.text.format.DateUtils
import android.text.format.DateUtils.SECOND_IN_MILLIS
import android.text.format.DateUtils.WEEK_IN_MILLIS
import com.insteem.ipfreely.steem.CentralConstants
import com.insteem.ipfreely.steem.DataHolders.FeedArticleDataHolder
import com.insteem.ipfreely.steem.HelperClasses.StaticMethodsMisc
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by boot on 3/27/2018.
 */
class FavouritesDatabase(context : Context) : SQLiteOpenHelper(context,CentralConstants.DatabaseNameFavourites,null,CentralConstants.DatabaseVersionFavourites) {
    val DatabaseTableName = "favouriteList"
    val DatabaseColoumnId = "Id"
    val DatabaseColoumnName = "name"
    val DatabaseColoumnPermlink = "permlink"
    val DatabaseColoumnTag = "tag"
    val DatabaseColoumnDate = "date"
    val DatabaseColoumnRebloggedBy = "rebloggedby"
    val DatabaseColoumnTitle = "title"
    val DatabaseColoumnBody = "body"
    val DatabaseColoumnUserVoted = "voted"
    val DatabaseColoumnCommentNumber = "commentsnumbers"
    val DatabaseColoumnPayout = "payout"
    val DatabaseColoumnIsComment = "iscomment"
    val DatabaseColoumnReputation = "reputation"
    val DatabaseColoumnNetVoted = "netvotes"
    val DatabaseColoumnImage = "images"
    val context = context

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $DatabaseTableName")
        onCreate(db)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTableName + " (" + DatabaseColoumnId + " integer primary key, " +
                DatabaseColoumnName + " text, " +
                DatabaseColoumnTag + " text, " +
                DatabaseColoumnPermlink + " text, " +
                DatabaseColoumnDate + " text, " +
                DatabaseColoumnRebloggedBy + " text, " +
                DatabaseColoumnTitle + " text, " +
                DatabaseColoumnBody + " text, " +
                DatabaseColoumnReputation + " text, " +
                DatabaseColoumnImage + " text, " +

                DatabaseColoumnUserVoted + " integer, " +
                DatabaseColoumnNetVoted + " integer, " +
                DatabaseColoumnIsComment + " integer, " +
                DatabaseColoumnCommentNumber + " integer, " +
                DatabaseColoumnPayout + " text" +
                ")")
    }


    @Synchronized
    override fun close() {
        super.close()
    }


    fun Insert(lite: FeedArticleDataHolder.FeedArticleHolder): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DatabaseColoumnName, lite.author)
        values.put(DatabaseColoumnTag, lite.category)
        values.put(DatabaseColoumnPermlink, lite.permlink)
        values.put(DatabaseColoumnPermlink, lite.permlink)
        values.put(DatabaseColoumnDate, lite.created)
        values.put(DatabaseColoumnRebloggedBy, if(!lite.reblogBy?.isEmpty()!!) lite.reblogBy?.first() else "")
        //check for null
        values.put(DatabaseColoumnImage, if(lite.image != null && !lite.image?.isEmpty()!!) lite.image?.first() else "")
        values.put(DatabaseColoumnTitle, lite.title)
        values.put(DatabaseColoumnReputation, lite.authorreputation)
        values.put(DatabaseColoumnBody, lite.body)
        var ins = 0
        if(lite.uservoted as Boolean){
            ins = 1
        }
        values.put(DatabaseColoumnUserVoted, ins)
        values.put(DatabaseColoumnIsComment, 0)
        values.put(DatabaseColoumnCommentNumber, lite.children)
        values.put(DatabaseColoumnNetVoted, lite.netVotes)
        var pay = lite.pending_payout_value
        if((lite.already_paid.orEmpty() != "0.000 SBD")){
            pay = lite.already_paid
        }
        values.put(DatabaseColoumnPayout, pay)



        val key = db.insert(DatabaseTableName, null, values)
        //questionsFts.InsertQuestion(values);
        /*if (key > 0) {
            questionsFts.InsertQuestion(values)
        }*/
        //
        return true
    }


    fun GetAllQuestions(): ArrayList<FeedArticleDataHolder.FeedArticleHolder> {
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
                "$DatabaseColoumnPermlink = ? ",
                arrayOf(permlink!!))
    }

    fun HelperQuestionsList(cursor: Cursor): ArrayList<FeedArticleDataHolder.FeedArticleHolder> {
        val questionsLites = ArrayList<FeedArticleDataHolder.FeedArticleHolder>()
        if (cursor.count == 0) {
            return questionsLites
        }
        //fetch username and the followers db
        val sharedPreferences = context.getSharedPreferences(CentralConstants.sharedprefname, 0)
        val username = sharedPreferences?.getString(CentralConstants.username, null)
        val followersDatabase = FollowersDatabase(context)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            var rl = ArrayList<String>()
            var rb = cursor.getString(cursor.getColumnIndex(DatabaseColoumnRebloggedBy))
            if(rb != ""){
                rl.add(rb)
            }

            var rli = ArrayList<String>()
            var rbi = cursor.getString(cursor.getColumnIndex(DatabaseColoumnImage))
            if(rbi != ""){
                rli.add(rbi)
            }

            var usv = true
            if(cursor.getInt(cursor.getColumnIndex(DatabaseColoumnUserVoted)) == 0){
                usv = false
            }
            //fetch name and reputation
            val au = cursor.getString(cursor.getColumnIndex(DatabaseColoumnName))
            val rep = cursor.getString(cursor.getColumnIndex(DatabaseColoumnReputation))
            var autho = "$au (${StaticMethodsMisc.CalculateRepScore(rep)})"
            //check if user follows you
            if(au != username){
                autho += if(followersDatabase.simpleSearch(au) as Boolean){
                    " follows you"
                } else{
                    ""
                }
            }
            var du = DateUtils.getRelativeDateTimeString(context,(SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(cursor.getString(cursor.getColumnIndex(DatabaseColoumnDate)))).time,SECOND_IN_MILLIS,WEEK_IN_MILLIS,0)
            //updated the display name
            val q = FeedArticleDataHolder.FeedArticleHolder(
                    reblogBy = rl,
                    reblogOn = "" ,
                    entryId =  0 ,
                    active = "",
                    displayName = autho,
                    author = au,
                    body = cursor.getString(cursor.getColumnIndex(DatabaseColoumnBody)),
                    cashoutTime = "",
                    category = cursor.getString(cursor.getColumnIndex(DatabaseColoumnTag)),
                    children = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnCommentNumber)),
                    created = cursor.getString(cursor.getColumnIndex(DatabaseColoumnDate)),
                    createdcon = "",
                    depth = 0,
                    id = 0,
                    lastPayout ="",
                    lastUpdate = "",
                    netVotes = cursor.getInt(cursor.getColumnIndex(DatabaseColoumnNetVoted)),
                    permlink = cursor.getString(cursor.getColumnIndex(DatabaseColoumnPermlink)),
                    rootComment = 0,
                    title = cursor.getString(cursor.getColumnIndex(DatabaseColoumnTitle)),
                    format = "",
                    app = "",
                    image = rli,
                    links = null,
                    tags = null,
                    users = null,
                    authorreputation = rep ,
                    pending_payout_value = cursor.getString(cursor.getColumnIndex(DatabaseColoumnPayout)),
                    promoted = "",
                    total_pending_payout_value = cursor.getString(cursor.getColumnIndex(DatabaseColoumnPayout)),
                    uservoted = usv,
                    already_paid = cursor.getString(cursor.getColumnIndex(DatabaseColoumnPayout)),
                    summary = null,
                    datespan = du.toString(),
                    replies = null,
                    activeVotes = null
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