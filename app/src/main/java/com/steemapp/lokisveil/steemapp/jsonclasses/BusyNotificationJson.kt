package com.steemapp.lokisveil.steemapp.jsonclasses
import android.content.Intent
import com.google.gson.annotations.SerializedName
import com.steemapp.lokisveil.steemapp.Enums.NotificationType
import java.util.*


class BusyNotificationJson {

data class notification(
		@SerializedName("id") val id: Int,
		@SerializedName("result") val result: List<Result>
)

data class Result(
		@SerializedName("type") val type: NotificationType?,
		@SerializedName("parent_permlink") val parentPermlink: String?,
		@SerializedName("author") val author: String?,
		@SerializedName("permlink") val permlink: String?,
		@SerializedName("timestamp") val timestamp: Int?,
		@SerializedName("block") val block: Int?,
        @SerializedName("follower") val follower : String?,
		@SerializedName("voter") val voter : String?,

        @SerializedName("from") val from : String?,
        @SerializedName("amount") val amount : String?,
        @SerializedName("memo") val memo : String?,
        @SerializedName("account") val account : String?,
        @SerializedName("is_root_post") val isRootPost : Boolean?,
        var showdate : String? = "",
		var notiIsRead : Boolean? = false,


		//added to make life easier
		var title:String = "",
		var body : String = "",
		var notiIntent: Intent? = null,
		var date: Date? = null

        /*public bool? is_root_post { get; set; }
    public string follower { get; set; }
    public string from { get; set; }
    public string amount { get; set; }
    public string memo { get; set; }
    public string account { get; set; }*/
        //"type":"reblog","account":"hispeedimagins","permlink":"metaphorically-challenged-kudos-contest-entry","timestamp":1523795688,"block":21588613}
)
}