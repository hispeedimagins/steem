package com.insteem.ipfreely.steem.RoomDatabaseApp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.insteem.ipfreely.steem.DataHolders.FeedArticleDataHolder
import com.insteem.ipfreely.steem.RoomDatabaseApp.RoomConverters.Converters
import com.insteem.ipfreely.steem.RoomDatabaseApp.RoomDaos.*
import com.insteem.ipfreely.steem.jsonclasses.prof

//the entities are passed to the db via the annotatiion
@Database(entities = [FeedArticleDataHolder.FeedArticleHolder::class,
    FeedArticleDataHolder.CommentHolder::class,
    FeedArticleDataHolder.beneficiariesDataHolder::class,
    FeedArticleDataHolder.WidgetArticleHolder::class,
    prof.Resultfp::class],
        version = 16)
//type converters to convert data to and fro from the db
@TypeConverters(Converters::class)
abstract class RoomDatabaseApp : RoomDatabase() {

    //Declare all the DAO interfaces here for access later
    abstract fun articleDao(): ArticleDao
    abstract fun commentDao():CommentDao
    abstract fun beneficiaryDao():BeneficiaryDao
    abstract fun followsDao():FollowsDao
    abstract fun widgetDao():WidgetDao

    companion object {
        //singleton for accessing the db
        @Volatile
        private var INSTANCE: RoomDatabaseApp? = null

        fun getDatabase(context: Context): RoomDatabaseApp {
            if (INSTANCE == null) {
                synchronized(RoomDatabaseApp::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                                context.applicationContext,
                                RoomDatabaseApp::class.java, "app_database_room"
                        ).fallbackToDestructiveMigration()
                                .build()
                    }
                }
            }
            return INSTANCE!!
        }

    }
}