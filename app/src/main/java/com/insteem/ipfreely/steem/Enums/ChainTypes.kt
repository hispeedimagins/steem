package com.insteem.ipfreely.steem.Enums

/**
 * Created by boot on 2/18/2018.
 */
enum class ChainTypes {
    vote,
    comment,
    transfer,
    transfer_to_vesting,
    withdraw_vesting,
    limit_order_create,
    limit_order_cancel,
    feed_publish,
    convert,
    account_create,
    account_update,
    witness_update,
    account_witness_vote,
    account_witness_proxy,
    pow,
    custom,
    report_over_production,
    delete_comment,
    custom_json,
    comment_options,
    set_withdraw_vesting_route,
    fill_convert_request,
    author_reward,
    curation_reward,
    liquidity_reward,
    interest,
    fill_vesting_withdraw,
    fill_order,
    comment_payout,
    escrow_transfer,
    escrow_approve,
    escrow_dispute,
    escrow_release
}