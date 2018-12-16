package com.steemapp.lokisveil.steemapp.jsonclasses
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes


/**
 * Created by boot on 2/2/2018.
 */
class prof {

    data class profilelist(
        val profiles : List<profile>
    )

data class profile(
		@SerializedName("id") val id: Int, //408676
		@SerializedName("name") val name: String, //hispeedimagins
		@SerializedName("owner") val owner: Owner,
		@SerializedName("active") val active: Active,
		@SerializedName("posting") val posting: Posting,
		@SerializedName("memo_key") val memoKey: String, //STM5h1M7d79JazPMPqU4vQYHYKHPUm2sUAiEM7ytnQ2gNiJeSXNci
		@SerializedName("json_metadata") val jsonMetadata: String, //{"profile":{"profile_image":"https://drive.google.com/file/d/1T_NtA9ksfKwEicW2FL6ml2sut1ovfo8N/view","cover_image":"https://otw1ng.bl3302.livefilestore.com/y4mPZpWAv9JxFSbwRkhgL_VYPxr01CK1xgsjj70QTtnD8NrUVhXKSohqOo3ZzmmQe7PtZ5EPsiOPkfjHA8UyQumS5RY42OCF8YrfW2ryXy8SbYuO0TrgzWDPyqc2p1XdOftDaQfLPQV3IkCG4TlneuN3AvIXjMBEOy175jsCbfvAsIk8YaGJEFafvFcvU9rroePeO97VK2E3dQafbmCCbQrjw?width=1024&height=507&cropmode=none","about":"Rise, rise and rise again, until lambs become lions.","location":"Omaumau"}}
		@SerializedName("proxy") val proxy: String,
		@SerializedName("last_owner_update") val lastOwnerUpdate: String, //1970-01-01T00:00:00
		@SerializedName("last_account_update") val lastAccountUpdate: String, //2018-02-02T06:27:06
		@SerializedName("created") val created: String, //2017-10-12T07:08:30
		@SerializedName("mined") val mined: Boolean, //false
		@SerializedName("owner_challenged") val ownerChallenged: Boolean, //false
		@SerializedName("active_challenged") val activeChallenged: Boolean, //false
		@SerializedName("last_owner_proved") val lastOwnerProved: String, //1970-01-01T00:00:00
		@SerializedName("last_active_proved") val lastActiveProved: String, //1970-01-01T00:00:00
		@SerializedName("recovery_account") val recoveryAccount: String, //steem
		@SerializedName("last_account_recovery") val lastAccountRecovery: String, //1970-01-01T00:00:00
		@SerializedName("reset_account") val resetAccount: String, //null
		@SerializedName("comment_count") val commentCount: Int, //0
		@SerializedName("lifetime_vote_count") val lifetimeVoteCount: Int, //0
		@SerializedName("post_count") val postCount: Int, //67
		@SerializedName("can_vote") val canVote: Boolean, //true
		@SerializedName("voting_power") var votingPower: Int, //9659
		@SerializedName("last_vote_time") val lastVoteTime: String, //2018-02-01T16:43:06
		@SerializedName("balance") val balance: String, //0.001 STEEM
		@SerializedName("savings_balance") val savingsBalance: String, //0.000 STEEM
		@SerializedName("sbd_balance") val sbdBalance: String, //13.913 SBD
		@SerializedName("sbd_seconds") val sbdSeconds: String, //5856712665
		@SerializedName("sbd_seconds_last_update") val sbdSecondsLastUpdate: String, //2018-02-01T20:38:18
		@SerializedName("sbd_last_interest_payment") val sbdLastInterestPayment: String, //2018-01-21T07:10:21
		@SerializedName("savings_sbd_balance") val savingsSbdBalance: String, //0.000 SBD
		@SerializedName("savings_sbd_seconds") val savingsSbdSeconds: String, //0
		@SerializedName("savings_sbd_seconds_last_update") val savingsSbdSecondsLastUpdate: String, //1970-01-01T00:00:00
		@SerializedName("savings_sbd_last_interest_payment") val savingsSbdLastInterestPayment: String, //1970-01-01T00:00:00
		@SerializedName("savings_withdraw_requests") val savingsWithdrawRequests: Int, //0
		@SerializedName("reward_sbd_balance") val rewardSbdBalance: String, //0.042 SBD
		@SerializedName("reward_steem_balance") val rewardSteemBalance: String, //0.000 STEEM
		@SerializedName("reward_vesting_balance") val rewardVestingBalance: String, //22.504796 VESTS
		@SerializedName("reward_vesting_steem") val rewardVestingSteem: String, //0.011 STEEM
		@SerializedName("vesting_shares") val vestingShares: String, //19238.245561 VESTS
		@SerializedName("delegated_vesting_shares") val delegatedVestingShares: String, //10235.179421 VESTS
		@SerializedName("received_vesting_shares") val receivedVestingShares: String, //11450.570230 VESTS
		@SerializedName("vesting_withdraw_rate") val vestingWithdrawRate: String, //0.000000 VESTS
		@SerializedName("next_vesting_withdrawal") val nextVestingWithdrawal: String, //1969-12-31T23:59:59
		@SerializedName("withdrawn") val withdrawn: Long, //0
		@SerializedName("to_withdraw") val toWithdraw: Long, //0
		@SerializedName("withdraw_routes") val withdrawRoutes: Long, //0
		@SerializedName("curation_rewards") val curationRewards: Long, //5
		@SerializedName("posting_rewards") val postingRewards: Long, //2150
		@SerializedName("proxied_vsf_votes") val proxiedVsfVotes: List<Long>,
		@SerializedName("witnesses_voted_for") val witnessesVotedFor: Int, //2
		@SerializedName("average_bandwidth") val averageBandwidth: String, //19703875586
		@SerializedName("lifetime_bandwidth") val lifetimeBandwidth: String, //104856000000
		@SerializedName("last_bandwidth_update") val lastBandwidthUpdate: String, //2018-02-02T06:27:06
		@SerializedName("average_market_bandwidth") val averageMarketBandwidth: String, //5073017776
		@SerializedName("lifetime_market_bandwidth") val lifetimeMarketBandwidth: String, //27500000000
		@SerializedName("last_market_bandwidth_update") val lastMarketBandwidthUpdate: String, //2018-01-27T08:06:57
		@SerializedName("last_post") val lastPost: String, //2018-02-01T03:19:30
		@SerializedName("last_root_post") val lastRootPost: String, //2018-01-31T10:49:09
		@SerializedName("vesting_balance") val vestingBalance: String, //0.000 STEEM
		@SerializedName("reputation") val reputation: String, //224708183241
		@SerializedName("transfer_history") val transferHistory: List<Any>,
		@SerializedName("market_history") val marketHistory: List<Any>,
		@SerializedName("post_history") val postHistory: List<Any>,
		@SerializedName("vote_history") val voteHistory: List<Any>,
		@SerializedName("other_history") val otherHistory: List<Any>,
		@SerializedName("witness_votes") val witnessVotes: List<String>,
		@SerializedName("tags_usage") val tagsUsage: List<Any>,
		@SerializedName("guest_bloggers") val guestBloggers: List<Any>,
		var lastVoteTimeLong:Long = 0
)

data class Active(
		@SerializedName("weight_threshold") val weightThreshold: Int, //1
		@SerializedName("account_auths") val accountAuths: List<Any>,
		@SerializedName("key_auths") val keyAuths: List<List<String>>
)

data class Owner(
		@SerializedName("weight_threshold") val weightThreshold: Int, //1
		@SerializedName("account_auths") val accountAuths: List<Any>,
		@SerializedName("key_auths") val keyAuths: List<List<String>>
)

data class Posting(
		@SerializedName("weight_threshold") val weightThreshold: Int, //1
		@SerializedName("account_auths") val accountAuths: List<List<String>>,
		@SerializedName("key_auths") val keyAuths: List<List<String>>
)

data class profiledata(
		@SerializedName("profile") val profile: myprofile
)

data class myprofile(
		@SerializedName("profile_image") val profileImage: String, //https://drive.google.com/file/d/1T_NtA9ksfKwEicW2FL6ml2sut1ovfo8N/view
		@SerializedName("cover_image") val coverImage: String, //https://otw1ng.bl3302.livefilestore.com/y4mPZpWAv9JxFSbwRkhgL_VYPxr01CK1xgsjj70QTtnD8NrUVhXKSohqOo3ZzmmQe7PtZ5EPsiOPkfjHA8UyQumS5RY42OCF8YrfW2ryXy8SbYuO0TrgzWDPyqc2p1XdOftDaQfLPQV3IkCG4TlneuN3AvIXjMBEOy175jsCbfvAsIk8YaGJEFafvFcvU9rroePeO97VK2E3dQafbmCCbQrjw?width=1024&height=507&cropmode=none
		@SerializedName("about") val about: String, //Rise, rise and rise again, until lambs become lions.
		@SerializedName("location") val location: String, //Omaumau
		@SerializedName("name") val name: String, //ThinkZombie
		@SerializedName("website") val website: String //Omaumau

)








data class FollowCount(
		@SerializedName("id") val id: String, //40
		@SerializedName("result") val result: Resultfo
)

data class Resultfo(
		@SerializedName("account") val account: String, //hispeedimagins
		@SerializedName("follower_count") val followerCount: Int, //100
		@SerializedName("following_count") val followingCount: Int //45
)





data class FollowNames(
		@SerializedName("id") val id: String, //40
		@SerializedName("result") val result: List<Resultfp>?
)


@Entity(tableName = "follow_tables",indices = [Index(value = ["uniqueName"],unique = true)])
data class Resultfp(
		@SerializedName("follower") val follower: String, //hispeedimagins
		@SerializedName("following") val following: String, //amazink-fem
		@SerializedName("what") val what: List<String>,
		var followInternal: MyOperationTypes = MyOperationTypes.follow,

		@PrimaryKey(autoGenerate = true)
		var dbid : Long = 0,
		var isFollower:Boolean = false,
        var uniqueName:String = ""
		)


}