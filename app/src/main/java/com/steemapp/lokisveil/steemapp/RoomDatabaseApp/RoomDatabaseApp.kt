package com.steemapp.lokisveil.steemapp.RoomDatabaseApp

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomConverters.Converters
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomDaos.*
import com.steemapp.lokisveil.steemapp.jsonclasses.prof

@Database(entities = [FeedArticleDataHolder.FeedArticleHolder::class,
    FeedArticleDataHolder.CommentHolder::class,
    FeedArticleDataHolder.beneficiariesDataHolder::class,
    FeedArticleDataHolder.WidgetArticleHolder::class,
    prof.Resultfp::class],
        version = 10)
@TypeConverters(Converters::class)
abstract class RoomDatabaseApp : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun commentDao():CommentDao
    abstract fun beneficiaryDao():BeneficiaryDao
    abstract fun followsDao():FollowsDao
    abstract fun widgetDao():WidgetDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDatabaseApp? = null

        fun getDatabase(context: Context): RoomDatabaseApp {
            if (INSTANCE == null) {
                synchronized(RoomDatabaseApp::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                                context.applicationContext,
                                RoomDatabaseApp::class.java!!, "app_database_room"
                        ).fallbackToDestructiveMigration()
                                .build()
                    }
                }
            }
            return INSTANCE!!
        }

    }
}