package com.steemapp.lokisveil.steemapp.jsonclasses

/**
 * Created by boot on 3/5/2018.
 */
class OperationJson {
    /*data class ops(
            val
    )*/
    data class urlfromsteemitimages(
            val url:String
    )

    data class draftholder(
            val dbid:Int,
            val title:String?,
            val tags:String?,
            val content:String?,
            val payouttype:String,
            val date:String
    )
}