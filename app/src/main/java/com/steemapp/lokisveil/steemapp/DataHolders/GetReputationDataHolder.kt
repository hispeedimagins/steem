package com.steemapp.lokisveil.steemapp.DataHolders

import com.google.gson.annotations.SerializedName
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes

//data classs for the get_reputations api
data class GetReputationDataHolder(


        var account:String,
        var reputation:Int,
        var displayName:String,
        var followInternal: MyOperationTypes = MyOperationTypes.follow
)