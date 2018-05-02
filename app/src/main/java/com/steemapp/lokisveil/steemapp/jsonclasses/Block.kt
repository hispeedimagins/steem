package com.steemapp.lokisveil.steemapp.jsonclasses
import com.google.gson.annotations.SerializedName


/**
 * Created by boot on 2/17/2018.
 */
class Block {

data class DynamicGlobalProperties(
		@SerializedName("id") val id: String, //40
		@SerializedName("result") val result: Result
)

data class Result(
		@SerializedName("id") val id: Int, //0
		@SerializedName("head_block_number") val headBlockNumber: Int, //19939732
		@SerializedName("head_block_id") val headBlockId: String, //01304194dbd4ec13527c9777736e9c0f90feccb6
		@SerializedName("time") val time: String, //2018-02-17T05:03:06
		@SerializedName("current_witness") val currentWitness: String, //timcliff
		@SerializedName("total_pow") val totalPow: Int, //514415
		@SerializedName("num_pow_witnesses") val numPowWitnesses: Int, //172
		@SerializedName("virtual_supply") val virtualSupply: String, //266358480.154 STEEM
		@SerializedName("current_supply") val currentSupply: String, //264216647.611 STEEM
		@SerializedName("confidential_supply") val confidentialSupply: String, //0.000 STEEM
		@SerializedName("current_sbd_supply") val currentSbdSupply: String, //9017115.007 SBD
		@SerializedName("confidential_sbd_supply") val confidentialSbdSupply: String, //0.000 SBD
		@SerializedName("total_vesting_fund_steem") val totalVestingFundSteem: String, //185393309.996 STEEM
		@SerializedName("total_vesting_shares") val totalVestingShares: String, //379002226742.209270 VESTS
		@SerializedName("total_reward_fund_steem") val totalRewardFundSteem: String, //0.000 STEEM
		@SerializedName("total_reward_shares2") val totalRewardShares2: String, //0
		@SerializedName("pending_rewarded_vesting_shares") val pendingRewardedVestingShares: String, //319483140.566806 VESTS
		@SerializedName("pending_rewarded_vesting_steem") val pendingRewardedVestingSteem: String, //155386.640 STEEM
		@SerializedName("sbd_interest_rate") val sbdInterestRate: Int, //0
		@SerializedName("sbd_print_rate") val sbdPrintRate: Int, //10000
		@SerializedName("maximum_block_size") val maximumBlockSize: Int, //65536
		@SerializedName("current_aslot") val currentAslot: Int, //20002862
		@SerializedName("recent_slots_filled") val recentSlotsFilled: String, //340282366920938463463374607431768211455
		@SerializedName("participation_count") val participationCount: Int, //128
		@SerializedName("last_irreversible_block_num") val lastIrreversibleBlockNum: Int, //19939716
		@SerializedName("vote_power_reserve_rate") val votePowerReserveRate: Int, //10
		@SerializedName("current_reserve_ratio") val currentReserveRatio: Int, //153201515
		@SerializedName("average_block_size") val averageBlockSize: Int, //17526
		@SerializedName("max_virtual_bandwidth") val maxVirtualBandwidth: String //202410724058726400000
)












data class GetBlockResult(
		@SerializedName("id") val id: String, //40
		@SerializedName("jsonrpc") val jsonrpc: String, //2.0
		@SerializedName("result") val result: Results
)

data class Results(
		@SerializedName("previous") val previous: String, //01339ffc7213cf1b29772488f45b9b78a7d3c339
		@SerializedName("timestamp") val timestamp: String, //2018-02-24T21:04:30
		@SerializedName("witness") val witness: String, //ausbitbank
		@SerializedName("transaction_merkle_root") val transactionMerkleRoot: String, //2deb5511e58f4fbb76aace94f0d6dcd0c3c4d6d0
		@SerializedName("extensions") val extensions: List<Any>,
		@SerializedName("witness_signature") val witnessSignature: String, //1f291b83f85dc5d99bf0e9ded54fb2e13035fde8304a0ede251565efb3d21c8ac30e0383c2d4757593e9061761e87be0c87614f6ced70e7d5636ebb7ffe2e6d59c
		@SerializedName("transactions") val transactions: List<Transaction>,
		@SerializedName("block_id") val blockId: String, //01339ffdce87965985abc65b6e31092385ebfd30
		@SerializedName("signing_key") val signingKey: String, //STM6Xhq2qzhS8UviotFrJWqC24BMfpnJtYt3Ma3HMMxXsHWRw5agr
		@SerializedName("transaction_ids") val transactionIds: List<String>
)

data class Transaction(
		@SerializedName("ref_block_num") val refBlockNum: Int, //40937
		@SerializedName("ref_block_prefix") val refBlockPrefix: Long, //3303459734
		@SerializedName("expiration") val expiration: String, //2018-02-24T21:14:21
		@SerializedName("operations") val operations: List<List<Any>>,
		@SerializedName("extensions") val extensions: List<Any>,
		@SerializedName("signatures") val signatures: List<String>
)













data class rewardfund(
		@SerializedName("id") val id: String, //40
		@SerializedName("jsonrpc") val jsonrpc: String, //2.0
		@SerializedName("result") val result: Resultfund
)

data class Resultfund(
		@SerializedName("id") val id: Int, //0
		@SerializedName("name") val name: String, //post
		@SerializedName("reward_balance") val rewardBalance: String, //717624.325 STEEM
		@SerializedName("recent_claims") val recentClaims: String, //433830399045339515
		@SerializedName("last_update") val lastUpdate: String, //2018-03-12T09:21:03
		@SerializedName("content_constant") val contentConstant: String, //2000000000000
		@SerializedName("percent_curation_rewards") val percentCurationRewards: Int, //2500
		@SerializedName("percent_content_rewards") val percentContentRewards: Int, //10000
		@SerializedName("author_reward_curve") val authorRewardCurve: String, //linear
		@SerializedName("curation_reward_curve") val curationRewardCurve: String //square_root
)




data class FeedHistoryPrice(
		@SerializedName("id") val id: String, //40
		@SerializedName("jsonrpc") val jsonrpc: String, //2.0
		@SerializedName("result") val result: ResultFeedHistoryPrice
)

data class ResultFeedHistoryPrice(
		@SerializedName("id") val id: Int, //0
		@SerializedName("current_median_history") val currentMedianHistory: CurrentMedianHistory,
		@SerializedName("price_history") val priceHistory: List<PriceHistory>
)

data class CurrentMedianHistory(
		@SerializedName("base") val base: String, //2.473 SBD
		@SerializedName("quote") val quote: String //1.000 STEEM
)

data class PriceHistory(
		@SerializedName("base") val base: String, //2.639 SBD
		@SerializedName("quote") val quote: String //1.000 STEEM
)
	/*data class operation(
			val opname : String ,
			val opevalue : Operationval,
	)*/









data class BlockAdded(
		@SerializedName("id") val id: String, //11
		@SerializedName("result") val result: Resultb
)

data class Resultb(
		@SerializedName("id") val id: String, //4747568896592aef2e0ce1e99f96662b742b1c2b
		@SerializedName("block_num") val blockNum: Int, //20298853
		@SerializedName("trx_num") val trxNum: Int, //44
		@SerializedName("expired") val expired: Boolean //false
)






}