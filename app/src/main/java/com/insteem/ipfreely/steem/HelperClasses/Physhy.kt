package com.insteem.ipfreely.steem.HelperClasses


//implementation of phishy urls form steemit
class Physhy {
    companion object {
        val phy = "\\b(?:steewit.com|\n" +
                "    śteemit.com|\n" +
                "    ŝteemit.com|\n" +
                "    şteemit.com|\n" +
                "    šteemit.com|\n" +
                "    sţeemit.com|\n" +
                "    sťeemit.com|\n" +
                "    șteemit.com|\n" +
                "    sleemit.com|\n" +
                "    aba.ae|\n" +
                "    autobidbot.cf|\n" +
                "    autobidbot.ga|\n" +
                "    autobidbot.gq|\n" +
                "    autobotsteem.cf|\n" +
                "    autobotsteem.ga|\n" +
                "    autobotsteem.gq|\n" +
                "    autobotsteem.ml|\n" +
                "    autosteem.info|\n" +
                "    autosteembot.cf|\n" +
                "    autosteembot.ga|\n" +
                "    autosteembot.gq|\n" +
                "    autosteembot.ml|\n" +
                "    autosteemit.wapka.mobi|\n" +
                "    boostbot.ga|\n" +
                "    boostbot.gq|\n" +
                "    boostwhaleup.cf|\n" +
                "    cutt.us|\n" +
                "    dereferer.me|\n" +
                "    eb2a.com|\n" +
                "    lordlinkers.tk|\n" +
                "    nullrefer.com|\n" +
                "    steeemit.ml|\n" +
                "    steeemitt.aba.ae|\n" +
                "    steemart.ga|\n" +
                "    steemautobot.bid|\n" +
                "    steemautobot.cf|\n" +
                "    steemautobot.trade|\n" +
                "    steemers.aba.ae|\n" +
                "    steemiit.cf|\n" +
                "    steemiit.ga|\n" +
                "    steemij.tk|\n" +
                "    steemik.ga|\n" +
                "    steemik.tk|\n" +
                "    steemil.com|\n" +
                "    steemil.ml|\n" +
                "    steemir.tk|\n" +
                "    steemitservices.ga|\n" +
                "    steemitservices.gq|\n" +
                "    steemnow.cf|\n" +
                "    steemnow.ga|\n" +
                "    steemnow.gq|\n" +
                "    steemnow.ml|\n" +
                "    steempostupper.win|\n" +
                "    steemrewards.ml|\n" +
                "    steemrobot.ga|\n" +
                "    steemrobot.ml|\n" +
                "    steemupgot.cf|\n" +
                "    steemupgot.ga|\n" +
                "    steemupgot.gq|\n" +
                "    steemupper.cf|\n" +
                "    steemupper.ga|\n" +
                "    steemupper.gq|\n" +
                "    steemupper.ml|\n" +
                "    steenit.cf|\n" +
                "    stemit.com|\n" +
                "    stssmater.aba.ae|\n" +
                "    uppervotes.ga|\n" +
                "    uppervotes.gq|\n" +
                "    upperwhaleplus.cf|\n" +
                "    upperwhaleplus.ga|\n" +
                "    upperwhaleplus.gq|\n" +
                "    upvoteme.cf|\n" +
                "    upvoteme.ga|\n" +
                "    upvoteme.gq|\n" +
                "    upvoteme.ml|\n" +
                "    url.rw|\n" +
                "    us.aba.ae|\n" +
                "    whaleboostup.ga|\n" +
                "whaleboostup.ml)\\b"

        fun isPhyshy(url:String):Boolean{

            if(url.toLowerCase().contains(phy.toRegex())) return true
            return false
        }


    }


}