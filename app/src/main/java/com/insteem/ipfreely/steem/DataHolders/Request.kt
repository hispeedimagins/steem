package com.insteem.ipfreely.steem.DataHolders


//data class for temp request objects
data class Request(
       var dbId:Long = 0L,
       var json:String = "",
       var reqnumber:Int = 0,
       var dateLong:Long = 0L,
       var typeOfRequest:String = "",
       var otherInfo:String = ""
)