package com.insteem.ipfreely.steem.DataHolders

import com.insteem.ipfreely.steem.SteemBackend.Config.Enums.MyOperationTypes

//data classs for the get_reputations api
data class GetReputationDataHolder(


        var account:String,
        var reputation:Int,
        var displayName:String,
        var followInternal: MyOperationTypes = MyOperationTypes.follow
)