package com.steemapp.lokisveil.steemapp.jsonclasses

// data classes for the asksteem search result
class AskSteemSearch {

    data class search(
            val results: List<Result>,
            val hits: Int, //71
            val error: Boolean, //false
            val pages: Pages,
            val time: Double //0.017
    )

    data class Pages(
            val has_next: Boolean, //true
            val has_previous: Boolean, //false
            val current: Int //1
    )

    data class Result(
            val meta : feed.JsonMetadataInner?,
            val payout: Double, //80.646
            val permlink: String, //a-run-to-freedom
            val created: String, //2018-01-31T10:49:09
            val title: String, //A run to freedom
            val children: Int, //6
            val net_votes: Int, //115
            val tags: List<String>,
            val author: String, //hispeedimagins
            val type: String, //post
            val summary: String //

    )


}



