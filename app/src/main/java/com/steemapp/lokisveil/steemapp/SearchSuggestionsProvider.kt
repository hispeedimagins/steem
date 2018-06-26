package com.steemapp.lokisveil.steemapp

import android.content.SearchRecentSuggestionsProvider
import android.database.Cursor
import android.net.Uri
import android.os.CancellationSignal

//app for search suggestions, will add it in the next release
class SearchSuggestionsProvider : SearchRecentSuggestionsProvider() {
    companion object {
        val AUTHORITY = "com.steemapp.lokisveil.steemapp.MySuggestionProvider"
        //val MODE = SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES
        val MODE = SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES or SearchRecentSuggestionsProvider.DATABASE_MODE_2LINES


    }

    init {
        setupSuggestions(AUTHORITY, MODE)
    }



    /*override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        val query = uri?.getLastPathSegment()?.toLowerCase()
        return super.query(uri, projection, selection, selectionArgs, sortOrder)
    }*/

    /*override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?, cancellationSignal: CancellationSignal?): Cursor {
        return super.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal)
    }*/

}