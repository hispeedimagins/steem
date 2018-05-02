package com.steemapp.lokisveil.steemapp.jsonclasses
import com.google.gson.annotations.SerializedName
import com.steemapp.lokisveil.steemapp.Enums.FollowInternal
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes


/**
 * Created by boot on 2/4/2018.
 */
class feed {


	/*"replies": [],
	"author_reputation": "9088515194732",
	"promoted": "0.000 SBD",
	"body_length": 0,
	"reblogged_by": [],
	"first_reblogged_on": "2017-10-29T21:19:06"*/


	/*"replies": [],
	"author_reputation": "11043089423341",
	"promoted": "0.000 SBD",
	"body_length": 0,
	"reblogged_by": [
	"minnowsupport"
	],
	"first_reblogged_by": "minnowsupport",
	"first_reblogged_on": "2018-02-10T15:27:12"*/

    data class FeedList(
            val feedList :List<FeedData>
    )

	data class Post(
			@SerializedName("post")val article : Comment
	)

data class FeedData(
		val authorreputation :String?,
		val promoted : String?,
		val bodylength : String?,
		val firstrebloggedby: String?,
		@SerializedName("comment") val comment: Comment,
		@SerializedName("reblog_by") val reblogBy: List<String>,
		@SerializedName("reblog_on") val reblogOn: String, //2018-02-04T13:44:21
		@SerializedName("entry_id") val entryId: Int //2363
)

	data class FeedMoreItems(
			@SerializedName("id") val Id: Int, //2363,
			@SerializedName("result") val comment: List<Comment>
	)

data class Comment(
		@SerializedName("id") val id: Int, //30142261
		@SerializedName("author") val author: String, //doghaus
		@SerializedName("permlink") val permlink: String, //street-view
		@SerializedName("category") val category: String, //poetry
		@SerializedName("parent_author") val parentAuthor: String,
		@SerializedName("parent_permlink") val parentPermlink: String, //poetry
		@SerializedName("title") val title: String, //Street View
		@SerializedName("body") val body: String, //![15999858_5.jpg](https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg)

		var jsonMetadata: JsonMetadataInner?,
		@SerializedName("json_metadata") val jsonMetadataString: String, //{"tags":["poetry","writing","life","deceit","mystery"],"users":["doghaus"],"image":["https://steemitimages.com/DQmTfZ5h3DEUuwgC4763Z7SwjuxbZ7KpKc5RWkE9ZVDLQ4o/15999858_5.jpg"],"links":["https://goo.gl/images/Jr17QZ"],"app":"steemit/0.1","format":"markdown"}
		@SerializedName("last_update") val lastUpdate: String, //2018-02-03T13:58:18
		@SerializedName("created") val created: String, //2018-02-03T13:58:18
		@SerializedName("active") val active: String, //2018-02-04T13:50:12
		@SerializedName("last_payout") val lastPayout: String, //1970-01-01T00:00:00
		@SerializedName("depth") val depth: Int, //0
		@SerializedName("children") val children: Int, //7
		@SerializedName("net_rshares") val netRshares: String, //373052936537
		@SerializedName("abs_rshares") val absRshares: String, //373052936537
		@SerializedName("vote_rshares") val voteRshares: String, //373052936537
		@SerializedName("children_abs_rshares") val childrenAbsRshares: String, //385445892613
		@SerializedName("cashout_time") val cashoutTime: String, //2018-02-10T13:58:18
		@SerializedName("max_cashout_time") val maxCashoutTime: String, //1969-12-31T23:59:59
		@SerializedName("total_vote_weight") val totalVoteWeight: Int, //617915
		@SerializedName("reward_weight") val rewardWeight: Int, //10000
		@SerializedName("total_payout_value") val totalPayoutValue: String, //0.000 SBD
		@SerializedName("curator_payout_value") val curatorPayoutValue: String, //0.000 SBD
		@SerializedName("author_rewards") val authorRewards: Int, //0
		@SerializedName("net_votes") val netVotes: Int, //21
		@SerializedName("root_comment") val rootComment: Int, //30142261
		@SerializedName("root_author") val rootAuthor: String?,
		@SerializedName("root_permlink") val rootPermlink: String?,
		/*"root_author": "dreemsteem",
	"root_permlink": "think-ye-can-survive-the-plank-2-sbd-prize-for-tomorrow",*/

		@SerializedName("max_accepted_payout") val maxAcceptedPayout: String, //1000000.000 SBD
		@SerializedName("percent_steem_dollars") val percentSteemDollars: Int, //10000
		@SerializedName("allow_replies") val allowReplies: Boolean, //true
		@SerializedName("allow_votes") val allowVotes: Boolean, //true
		@SerializedName("allow_curation_rewards") val allowCurationRewards: Boolean, //true
		@SerializedName("beneficiaries") val beneficiaries: List<Any>,
		@SerializedName("url") val url : String?,

		@SerializedName("root_title") val root_title : String?,
		@SerializedName("pending_payout_value") val pending_payout_value : String?,
		@SerializedName("total_pending_payout_value") val total_pending_payout_value : String?,
		@SerializedName("active_votes") val active_voted : List<avtiveVotes>?,

		@SerializedName("author_reputation")val authorreputation :String?,
		val promoted : String?,
		@SerializedName("body_length")val bodylength : String?,
		val firstrebloggedby: String?,
		//@SerializedName("comment") val comment: Comment,
		@SerializedName("reblogged_by") val reblogBy: List<String>,
		@SerializedName("first_reblogged_on") val reblogOn: String, //2018-02-04T13:44:21
		@SerializedName("entry_id") val entryId: Int, //2363
		val first_reblogged_by :String?,
		var reply_to_above : Boolean?,
		@SerializedName("replies") val replies : List<String>,
		var width : Int = 0



)


data class JsonMetadata(
		@SerializedName("json_metadata") val jsonMetadata: JsonMetadataInner?
)

data class JsonMetadataInner(
		@SerializedName("tags") val tags: List<String>?,
		@SerializedName("users") val users: List<String>?,
		@SerializedName("image") val image: List<String>?,
		@SerializedName("links") val links: List<String>?,
		@SerializedName("app") val app: String?, //steemit/0.1
		@SerializedName("format") val format: String? //markdown
)

	data class avtiveVotes(
			@SerializedName("voter") var voter : String?,
			@SerializedName("weight") var weight : String?,
			@SerializedName("rshares") var rshares : String?,
			@SerializedName("percent") var percent : String?,
			@SerializedName("reputation") var reputation : String?,
			@SerializedName("time") var time : String?,
			var calculatedrep:String = "",
			var namewithrep : String = "",
			var calculatedtime : String = "",
			var calculatedpercent : String = "",
			var calculatedrshares : String = "",
			var calculatedvotepercent : String = "",
			var followInternal: MyOperationTypes = MyOperationTypes.follow,
			var dateString : String = ""
	)































}