package com.insteem.ipfreely.steem.jsonclasses

data class ProfileJsonSteemit(
        val status: String?,
        val user: User?
) {
    data class User(
            val active: Active?,
            val balance: String?,
            val can_vote: Boolean?,
            val comment_count: Int?,
            val created: String?,
            val curation_rewards: Int?,
            val delegated_vesting_shares: String?,
            val guest_bloggers: List<Any?>?,
            val id: Int?,
            val json_metadata: JsonMetadata?,
            val last_account_recovery: String?,
            val last_account_update: String?,
            val last_owner_update: String?,
            val last_post: String?,
            val last_root_post: String?,
            val last_vote_time: String?,
            var lastVotingTimeLong:Long,
            val lifetime_vote_count: Int?,
            val market_history: List<Any?>?,
            val memo_key: String?,
            val mined: Boolean?,
            val name: String?,
            val next_vesting_withdrawal: String?,
            val other_history: List<Any?>?,
            val owner: Owner?,
            val pending_claimed_accounts: Int?,
            val post_bandwidth: Int?,
            val post_count: Int?,
            val post_history: List<Any?>?,
            val posting: Posting?,
            val posting_rewards: Int?,
            val proxied_vsf_votes: List<Int?>?,
            val proxy: String?,
            val received_vesting_shares: String?,
            val recovery_account: String?,
            val reputation: String?,
            val reset_account: String?,
            val reward_sbd_balance: String?,
            val reward_steem_balance: String?,
            val reward_vesting_balance: String?,
            val reward_vesting_steem: String?,
            val savings_balance: String?,
            val savings_sbd_balance: String?,
            val savings_sbd_last_interest_payment: String?,
            val savings_sbd_seconds: String?,
            val savings_sbd_seconds_last_update: String?,
            val savings_withdraw_requests: Int?,
            val sbd_balance: String?,
            val sbd_last_interest_payment: String?,
            val sbd_seconds: String?,
            val sbd_seconds_last_update: String?,
            val tags_usage: List<Any?>?,
            val to_withdraw: Int?,
            val transfer_history: List<Any?>?,
            val vesting_balance: String?,
            val vesting_shares: String?,
            val vesting_withdraw_rate: String?,
            val vote_history: List<Any?>?,
            val voting_manabar: VotingManabar?,
            val voting_power: Int?,
            val withdraw_routes: Int?,
            val withdrawn: Long?,
            val witness_votes: List<String?>?,
            val witnesses_voted_for: Int?
    ) {
        data class JsonMetadata(
                val profile: Profile?
        ) {
            data class Profile(
                    val about: String?,
                    val cover_image: String?,
                    val location: String?,
                    val profile_image: String?,
                    val name:String?,
                    val website:String?
            )
        }

        data class Owner(
                val account_auths: List<Any?>?,
                val key_auths: List<List<String?>?>?,
                val weight_threshold: Int?
        )

        data class Posting(
                val account_auths: List<Any?>?,
                val key_auths: List<List<String?>?>?,
                val weight_threshold: Int?
        )

        data class VotingManabar(
                val current_mana: String?,
                val last_update_time: Int?
        )

        data class Active(
                val account_auths: List<Any?>?,
                val key_auths: List<List<String?>?>?,
                val weight_threshold: Int?
        )
    }
}