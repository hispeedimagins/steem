package com.steemapp.lokisveil.steemapp.HelperClasses

import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import com.commonsware.cwac.anddown.AndDown
import com.google.gson.*
import com.steemapp.lokisveil.steemapp.Enums.TypeOfRequest
import com.google.gson.reflect.TypeToken
import com.steemapp.lokisveil.steemapp.CentralConstantsOfSteem
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.DataHolders.GetReputationDataHolder
import com.steemapp.lokisveil.steemapp.Databases.FollowersDatabase
import com.steemapp.lokisveil.steemapp.Databases.FollowingDatabase
import com.steemapp.lokisveil.steemapp.FollowApiConstants
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.jsonclasses.feed
import com.steemapp.lokisveil.steemapp.jsonclasses.prof
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.regex.Pattern


/**
 * Created by boot on 2/10/2018.
 */


class JsonRpcResultConversion(val json :JSONObject?,var username :String, val requestType: TypeOfRequest?){
    var contex : Context? = null
    constructor(json :JSONObject?,username :String,requestType : TypeOfRequest?,context: Context) : this(json,username,requestType){
        contex = context
        followersDatabase = FollowersDatabase(context)
        followingDatabase = FollowingDatabase(context)
    }
    //val json = json
    //val username : String = username.toLowerCase()
    var gson : Gson = Gson()
    var followersDatabase : FollowersDatabase? = null
    var followingDatabase : FollowingDatabase? = null
    var nobots = "\\b(?:postpromoter|smartsteem|buildawhale|upme|appreciator|rocky1|booster|boomerang|jerrybanfield|therising|promobot|upmyvote|pushup|sneaky-ninja|minnowbooster)\\b"



    //used to parse through get_reputation api data
    fun ParseRepAndSearch() : List<GetReputationDataHolder>{
        val body = json
        var result = if(body?.has("result")!!) body.getJSONArray("result") else null
        if(result == null){
            return ArrayList()
        }
        var al = ArrayList<GetReputationDataHolder>()
        for(x in 0 until result.length()){
            //get jsonobject
            val curob = result.getJSONObject(x)
            //get account name
            val author = curob.getString("account")
            //get the rep and calculate its int value
            val calcrep = StaticMethodsMisc.CalculateRepScoreRetInt(curob.getString("reputation"))
            var autho = "$author ($calcrep)"
            var mfo = MyOperationTypes.follow
            //check if person follows you from the local db
            //it is used to evaluate if the follow or the
            //unfollow option is presented
            if(followersDatabase != null){
                if(author != username){
                    if(followersDatabase?.simpleSearch(author) as Boolean){
                        "$autho follows you"
                        mfo = MyOperationTypes.unfollow
                    } else{
                        mfo = MyOperationTypes.follow
                    }
                }
            }


            al.add(GetReputationDataHolder(author,calcrep,autho,mfo))


        }
        //close db connections to prevent leakages
        closedb()
        return al
    }


    fun GetTrendingTags():List<String>{
        val body = json
        var result = if(body?.has("result")!!) body?.getJSONObject("result") else null
        if(result == null){
            return ArrayList<String>()
        }
        var tag_idx = result.getJSONObject("tag_idx")
        var trending = tag_idx.getJSONArray("trending")
        var tags = ArrayList<String>()
        if(trending != null){
            for (x in 0 until trending.length()){
                tags.add(trending.getString(x))
            }
        }
        CentralConstantsOfSteem.getInstance().trendingTags = tags
        return tags
    }

    fun ParseJsonBlog() : ArrayList<FeedArticleDataHolder.FeedArticleHolder>{

        //val body = gson.fromJson(json, JsonObject::class.java)
        val body = json
        var result = if(body?.has("result")!!) body?.getJSONObject("result") else null
        if(result == null){
            return ArrayList<FeedArticleDataHolder.FeedArticleHolder>()
        }
        /*var tag_idx = result.getJSONObject("tag_idx")
        var trending = tag_idx.getJSONArray("trending")*/
        if(result?.has("tag_idx")!!){
            GetTrendingTags()
        }

        var content = result.getJSONObject("content")
        var accounts = result.getJSONObject("accounts")
        var user = if(accounts.has(username)) accounts.getJSONObject(username) else null
        if(user == null){
            return ArrayList()
        }
        var getthis = "feed"
        if(requestType == TypeOfRequest.blog){
            getthis = "blog"
        } else if(requestType == TypeOfRequest.comments){
            getthis = "comments"
        } else if(requestType == TypeOfRequest.replies){
            getthis = "recent_replies"
        }

        var returndata : ArrayList<FeedArticleDataHolder.FeedArticleHolder> = ArrayList()
        val arr = if(user.has(getthis))  user.getJSONArray(getthis) else null
        if(arr != null){

            for (x in 0 until arr.length()){

                //var st = x.toString()
                try{
                    var ss = arr.getString(x)
                    if(ss != null){
                        var commstr : JSONObject = content.getJSONObject(ss)
                        returndata.add(getprocessedfeed(commstr)!!)
                    }
                }
                catch (ex : Exception){
                    ex.message
                }


   /*             var si = commstr.toString()
                var acvojs = commstr.getAsJsonArray("active_votes")
                //"voter": "cosminblaga",
                //gson.fromJson<feed.Comment>(commstr.,feed.Comment::class.java)
                var comms = feed.Comment(commstr.get("id").asInt,commstr.get("author").asString,commstr.get("permlink").asString,commstr.get("category").asString,commstr.get("parent_author").asString,
                        commstr.get("parent_permlink").asString,
                        commstr.get("title").asString,
                        commstr.get("body").asString,
                        null,
                        commstr.get("json_metadata").asString,
                        commstr.get("last_update").asString,
                        commstr.get("created").asString,
                        commstr.get("active").asString,
                        commstr.get("last_payout").asString,
                        commstr.get("depth").asInt,
                        commstr.get("children").asInt,
                        commstr.get("net_rshares").asString,
                        commstr.get("abs_rshares").asString,
                        commstr.get("vote_rshares").asString,
                        commstr.get("children_abs_rshares").asString,
                        commstr.get("cashout_time").asString,
                        commstr.get("max_cashout_time").asString,
                        commstr.get("total_vote_weight").asInt,
                        commstr.get("reward_weight").asInt,
                        commstr.get("total_payout_value").asString,
                        commstr.get("curator_payout_value").asString,
                        commstr.get("author_rewards").asInt,
                        commstr.get("net_votes").asInt,
                        commstr.get("root_comment").asInt,
                        commstr.get("max_accepted_payout").asString,
                        commstr.get("percent_steem_dollars").asInt,
                        commstr.get("allow_replies").asBoolean,
                        commstr.get("allow_votes").asBoolean,
                        commstr.get("allow_curation_rewards").asBoolean,
                        commstr.getAsJsonArray("beneficiaries").toList(),
                        commstr.get("url").asString,
                        commstr.get("root_title").asString,
                        commstr.get("pending_payout_value").asString,
                        commstr.get("total_pending_payout_value").asString,
                        )



                var comm : feed.Comment = gson.fromJson<feed.Comment>(si,feed.Comment::class.java)*/

            }
            //close db connections to prevent leakages
            closedb()
           return returndata
        }

        //close db connections to prevent leakages
        closedb()
        return  ArrayList<FeedArticleDataHolder.FeedArticleHolder>()



      /*  val results = body.get("results").getAsJsonArray()
        val firstResult = results.get(0).getAsJsonObject()
        val address = firstResult.get("formatted_address")
        println(address.getAsString())*/
    }

    //close db connections to prevent leakages
    fun closedb(){
        followersDatabase?.close()
        followingDatabase?.close()
    }

    fun ParseJsonBlogMore(checkforbots:Boolean = false) : ArrayList<FeedArticleDataHolder.FeedArticleHolder>{

        //val body = gson.fromJson(json, JsonObject::class.java)
        val body = json
        //check if json has result to preven a crash, if not, return.
        if(!(body!!.has("result"))){
            return ArrayList()
        }
        var result: JSONArray? = body?.getJSONArray("result") ?: return ArrayList()

        var returndata : ArrayList<FeedArticleDataHolder.FeedArticleHolder> = ArrayList()
        //val arr = user.get(getthis)?.asJsonArray
        var skipz = false
        if(result?.length() != null){
            for (x in 0 until result?.length()){
                if(skipz){
                    var commstr : JSONObject = result.getJSONObject(x) //x
                    var s = getprocessedfeed(commstr,false,true)
                    if(s != null){
                        returndata.add(s)
                    }

                }
                else{
                    skipz = true
                }

            }
        }
        //close db connections to prevent leakages
        closedb()
        return returndata

        //return  ArrayList<FeedArticleDataHolder.FeedArticleHolder>()

    }




    fun getprocessedfeed(commstr : JSONObject,bodyasis : Boolean = false,checkforbots:Boolean = false) : FeedArticleDataHolder.FeedArticleHolder? {
        //swipecommonactionsclass?.makeswipestop()
        //adapter.questionListFunctions.add(message)

        val gson = Gson()

        var voted = false
        var repl : JSONArray? = null
        if(commstr.has("replies")){
            repl = commstr.getJSONArray("replies")
        }
        /*val collectionTypev = object : TypeToken<List<feed.avtiveVotes>>() {

        }.type*/
        var author = commstr.getString("author")
        //var vot = gson.fromJson<List<feed.avtiveVotes>>(commstr.getAsJsonArray("active_votes"),collectionTypev)
        if(commstr.getJSONArray("active_votes").toString().contains("\"voter\":\"$username\"")) voted = true
        if(checkforbots){
            val pattern = Pattern.compile(
                    nobots,
                    Pattern.CASE_INSENSITIVE)
            if(pattern.matcher(commstr.getJSONArray("active_votes").toString()).find() || author == "haejin"){
                return null
            }
        }

        /*var comv = commstr.getAsJsonArray("active_votes")
        if(comv != null){
            *//*if(commstr.get("active_votes").asString.contains(username)) voted = true*//*
            for(x in comv){
                if(x.asJsonObject.asString.contains(username)) voted = true
                //if(x.voter.equals(username)) voted = true
            }
        }*/
        var rpb = commstr.getJSONArray("reblogged_by")
        val collectionType = object : TypeToken<List<String>>() {

        }.type
        var ls = gson.fromJson<List<String>>(rpb.toString(),collectionType)

        //var st : String = commstr.getString("body")
        var st : String = ""
        //var builder = StringBuilder()
        if(!bodyasis){
            try{
                st = commstr.getString("body").substring(IntRange(0,400))
            }
            catch(ex : StringIndexOutOfBoundsException){
                st = commstr.getString("body")
            }

            /*if(st?.length != null && st?.length > 1000){
                st = st.substring(0,1000)
            }*/
            /*if(st?.length != null && st?.length > 600){
                st = st.substring(0,600)
            }*/
            st = StripMarkDown.stripMd(st,StripMarkDown.Companion.mdOptions()).trim()
            /*var splitstring : List<String> = st.split("\n")

            if(splitstring != null){

                for (x in splitstring){
                    builder.append("${x.trim()} ")
                }
            }*/


        }
        else{
            st  = commstr.getString("body")
        }

        var autho = "$author (${StaticMethodsMisc.CalculateRepScore(commstr.getString("author_reputation"))})"
        var tfollowsYou = false
        if(followersDatabase != null){
            if(author != username){
                tfollowsYou = followersDatabase?.simpleSearch(author) as Boolean
            }
        }

        var jsonMetadata : feed.JsonMetadataInner? = null
        //this is because some apps have tags in a string instead of an array
        //so the app would crash, this will keep the app running
        //will try to have a better solution in the other release
        try{
            jsonMetadata = gson.fromJson<feed.JsonMetadataInner>(commstr.getString("json_metadata"),feed.JsonMetadataInner::class.java)
        } catch (jex : JsonSyntaxException){

        }

        //var du = DateUtils.getRelativeDateTimeString(contex,(SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(commstr.getString("created"))).time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0)

        //var d = calendarcalculations() //2018-02-03T13:58:18
        //var dd = (SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(commstr.getString("created")))
        var dd = StaticMethodsMisc.FormatDateGmt(commstr.getString("created"))
        //d.setDateOfTheData(dd)
        var du = DateUtils.getRelativeDateTimeString(contex,dd.time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0)

        var fd : FeedArticleDataHolder.FeedArticleHolder = FeedArticleDataHolder.FeedArticleHolder(
                displayName = autho,
                reblogBy = ls,
                reblogOn = if(commstr.has("first_reblogged_on")) commstr.getString("first_reblogged_on") else "" ,
                entryId = if(commstr.has("entry_id")) commstr.getInt("entry_id") else 0 ,
                active =  commstr.getString("active"),
                author = author,
                //body = if(bodyasis) st else builder.toString(),
                body = st,
                cashoutTime = commstr.getString("cashout_time"),
                category = commstr.getString("category"),
                children = commstr.getInt("children"),
                date = dd,
                created = commstr.getString("created"),
                createdcon = dd.time.toString(),
                //createdcon = d.getDateTimeString(),
                depth = commstr.getInt("depth"),
                id = commstr.getInt("id"),
                lastPayout = commstr.getString("last_payout"),
                lastUpdate = commstr.getString("last_update"),
                netVotes = commstr.getInt("net_votes"),
                permlink = commstr.getString("permlink"),
                rootComment = if(commstr.has("root_comment")) commstr.getInt("root_comment") else 0,
                title = commstr.getString("title"),
                format = jsonMetadata?.format,
                app = jsonMetadata?.app,
                image = jsonMetadata?.image,
                links = jsonMetadata?.links,
                tags = jsonMetadata?.tags,
                users = jsonMetadata?.users,
                authorreputation = commstr.getString("author_reputation"),
                pending_payout_value = commstr.getString("pending_payout_value"),
                promoted = commstr.getString("promoted"),
                total_pending_payout_value = commstr.getString("total_pending_payout_value"),
                uservoted = voted,
                already_paid = commstr.getString("total_payout_value"),
                summary = null,
                datespan = du.toString(),
                replies = repl,
                activeVotes = commstr.getJSONArray("active_votes"),
                rootAuthor = if(commstr.has("root_author")) commstr.getString("root_author") else null,
                rootPermlink = if(commstr.has("root_permlink")) commstr.getString("root_permlink") else null,
                followsYou = tfollowsYou

        )
        //adapter?.feedHelperFunctions?.add(fd)
        return fd
    }



    /*fun ParseReplies(UserAndPermlinkWithDash : String?) : ArrayList<feed.Comment>{
        var list = ArrayList<feed.Comment>()
        //val body = gson.fromJson(json, JsonObject::class.java)
        val body = json
        var result = body.getJSONObject("result")
        if(result == null){
            return list
        }
        var content : JSONObject = result.getJSONObject("content")

        var com : JSONObject = content.getJSONObject(UserAndPermlinkWithDash)
        var article : feed.Comment = gson.fromJson<feed.Comment>(com.toString(),feed.Comment::class.java)
        if(article == null) return list

        list.add(article)
        if(article.replies != null){
            for(x in article.replies){
                var comstr = content.getJSONObject(x)
                var reply = gson.fromJson<feed.Comment>(comstr.toString(),feed.Comment::class.java)
                reply.reply_to_above = false
                var s = list.add(reply)
                if(reply.replies != null && reply.replies.any()){
                    _ParseReplies(list,content,reply,true,5)
                }
            }
        }



        return list
    }*/
    fun ParseReplies(UserAndPermlinkWithDash : String?) : ArrayList<Any>{
        var list = ArrayList<Any>()
        //val body = gson.fromJson(json, JsonObject::class.java)
        val body = json
        var result = if(body?.has("result") as Boolean) body?.getJSONObject("result") else null
        if(result == null){
            return list
        }
        var content : JSONObject = result.getJSONObject("content")

        var com : JSONObject = content.getJSONObject(UserAndPermlinkWithDash)
        var article  = getprocessedfeed(com,true) //gson.fromJson<feed.Comment>(com.toString(),feed.Comment::class.java)
        //if(article == null) return list

        list.add(article!!)
        //set wid to zero
        var wid = 0
        if(article != null && article.replies != null){
            for(x in 0 until article.replies!!.length()){
                var comstr = content.getJSONObject(article.replies!!.getString(x))
                //var reply = gson.fromJson<feed.Comment>(comstr.toString(),feed.Comment::class.java)
                var reply = getprocessedcomment(comstr,article.author,article.permlink,article.tags!![0],0,if(comstr.has("replies")) comstr.getJSONArray("replies") else null) //gson.fromJson<feed.Comment>(comstr.toString(),feed.Comment::class.java)
                reply.reply_to_above = false
                var s = list.add(reply)
                if(reply.replies != null && reply.replies!!.length() > 0){
                    //add 5 to increase width
                    _ParseReplies(list,content,reply,true,wid + 5)
                }
            }
            /*for(x in article.replies){
                var comstr = content.getJSONObject(x)
                var reply = gson.fromJson<feed.Comment>(comstr.toString(),feed.Comment::class.java)
                reply.reply_to_above = false
                var s = list.add(reply)
                if(reply.replies != null && reply.replies.any()){
                    _ParseReplies(list,content,reply,true,5)
                }
            }*/
        }

        //close db connections to prevent leakages
        closedb()
        return list
    }

    /*private fun _ParseReplies(list : ArrayList<feed.Comment>,content : JSONObject,article : feed.Comment,replytoabove : Boolean,wid : Int){
        for(x in article.replies){
            var comstr = content.getJSONObject(x)
            var reply = gson.fromJson<feed.Comment>(comstr.toString(),feed.Comment::class.java)
            reply.width = wid
            reply.reply_to_above = replytoabove
            list.add(reply)
            if(reply.replies != null && reply.replies.any()){
                _ParseReplies(list,content,reply,true,wid + wid)
            }
        }
    }*/

    private fun _ParseReplies(list : ArrayList<Any>,content : JSONObject,article : FeedArticleDataHolder.CommentHolder,replytoabove : Boolean,wid : Int){
        for(x in 0 until article.replies!!.length()){
            var comstr = content.getJSONObject(article.replies!!.getString(x))
            //var reply = gson.fromJson<feed.Comment>(comstr.toString(),feed.Comment::class.java)
            var st = ""
            if(article.tags != null){
                st = article?.tags!![0]
            }
            var reply = getprocessedcomment(comstr,article.author,article.permlink,st,wid,if(comstr.has("replies")) comstr.getJSONArray("replies") else null)
            //reply.width = wid
            reply.reply_to_above = replytoabove
            list.add(reply)
            if(reply.replies != null && reply.replies!!.length() > 0){
                var nw = wid + 5
                _ParseReplies(list,content,reply,true,nw)
            }
        }

    }



    fun getprocessedcomment(commstr: JSONObject,parentAuthor:String?,parentPermlink:String?,parentTag:String?,wid : Int,replies : JSONArray?) : FeedArticleDataHolder.CommentHolder {
        //swipecommonactionsclass?.makeswipestop()
        //adapter.questionListFunctions.add(message)

        val gson = Gson()

        var voted = false
        var con = FollowApiConstants.getInstance()

        var fol = MyOperationTypes.unfollow
        if(followingDatabase != null){
            fol = if(followingDatabase?.simpleSearch(commstr.getString("author")) as Boolean){
                MyOperationTypes.unfollow
            }
            else{
                MyOperationTypes.follow
            }
        }

        /*fol = if(!con.following.isEmpty() && con.following.any { p -> p.following == commstr.getString("author") }){
            MyOperationTypes.unfollow
        }
        else {
           MyOperationTypes.follow
        }*/
        /*for(x in 0 until com.getJSONArray("active_votes").length()){

        }*/
        var avs = commstr.getJSONArray("active_votes").toString()
        if(avs.contains("\"voter\":\"$username\"")) voted = true
        var jsonMetadata : feed.JsonMetadataInner? = null
        try{
            jsonMetadata = gson.fromJson<feed.JsonMetadataInner>(commstr.getString("json_metadata"),feed.JsonMetadataInner::class.java)
        }
        catch (ex: JsonSyntaxException){
            Log.e("jsonerror",commstr.getString("json_metadata"))
        }
        catch (ex:IllegalStateException){
            Log.e("jsonerror",commstr.getString("json_metadata"))
        }

        //var du = DateUtils.getRelativeDateTimeString(contex,(SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(commstr.getString("created"))).time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0)

        //var d = calendarcalculations() //2018-02-03T13:58:18
        //var dd = (SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(commstr.getString("created")) )
        var dd = StaticMethodsMisc.FormatDateGmt(commstr.getString("created"))
        //d.setDateOfTheData(dd)
        var du = DateUtils.getRelativeDateTimeString(contex,dd.time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0)

        var rpb = commstr.getJSONArray("reblogged_by")
        val collectionType = object : TypeToken<List<String>>() {

        }.type
        var ls = gson.fromJson<List<String>>(rpb.toString(),collectionType)
       /* var d = calendarcalculations() //2018-02-03T13:58:18
        d.setDateOfTheData((SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(result.created) ))*/
        var bod = StaticMethodsMisc.CorrectMarkDown(commstr.getString("body"),jsonMetadata?.image)
        //and = AndDown()
        //var du = DateUtils.getRelativeDateTimeString(context,(SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(result.created)).time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0)
        //var and = AndDown()
        //val s : String = and.markdownToHtml(bod, AndDown.HOEDOWN_EXT_AUTOLINK, 0)
        var author = commstr.getString("author")
        var autho = "$author (${StaticMethodsMisc.CalculateRepScore(commstr.getString("author_reputation"))})"
        var tfollowsYou = false
        if(followersDatabase != null){
            if(author != username){
                tfollowsYou = followersDatabase?.simpleSearch(author) as Boolean
            }
        }




        var fd : FeedArticleDataHolder.CommentHolder = FeedArticleDataHolder.CommentHolder(
                displayName = autho,
                reblogBy = ls,
                reblogOn = if(commstr.has("first_reblogged_on")) commstr.getString("first_reblogged_on") else "" ,
                entryId = if(commstr.has("entry_id")) commstr.getInt("entry_id") else 0 ,
                active =  commstr.getString("active"),
                author = author,
                body = bod,
                cashoutTime = commstr.getString("cashout_time"),
                category = commstr.getString("category"),
                children = commstr.getInt("children"),
                created = commstr.getString("created"),
                createdcon = dd.toString(),
                date = dd,
                depth = commstr.getInt("depth"),
                id = commstr.getInt("id"),
                lastPayout = commstr.getString("last_payout"),
                lastUpdate = commstr.getString("last_update"),
                netVotes = commstr.getInt("net_votes"),
                permlink = commstr.getString("permlink"),
                rootComment = if(commstr.has("root_comment")) commstr.getInt("root_comment") else 0,
                title = commstr.getString("title"),
                format = jsonMetadata?.format,
                app = jsonMetadata?.app,
                image = jsonMetadata?.image,
                links = jsonMetadata?.links,
                tags = jsonMetadata?.tags,
                users = jsonMetadata?.users,
                authorreputation = StaticMethodsMisc.CalculateRepScore(commstr.getString("author_reputation")),
                pending_payout_value = commstr.getString("pending_payout_value"),
                promoted = commstr.getString("promoted"),
                total_pending_payout_value = commstr.getString("total_pending_payout_value"),
                uservoted = voted,
                already_paid = commstr.getString("total_payout_value"),
                //summary = null,
                datespan = du.toString(),
                width = wid,
                activeVotes = commstr.getJSONArray("active_votes"),
                replies = replies,
                useFollow = fol,
                parent_permlink = parentPermlink,
                parent_tag = parentTag,
                paretn_author = parentAuthor,
                rootAuthor = if(commstr.has("root_author")) commstr.getString("root_author") else null,
                rootPermlink = if(commstr.has("root_permlink")) commstr.getString("root_permlink") else null,
                followsYou = tfollowsYou
                //datespan = du.toString()
        )
        //adapter?.add(fd)
        return fd

    }


    fun processfollowlist(list:List<prof.Resultfp>,useFollower: Boolean):List<prof.Resultfp>{
        for(x in list){
            //var name: String
            var name = if(useFollower){
                x.follower
            } else{
                x.following
            }
            var fol = MyOperationTypes.unfollow
            if(followingDatabase != null){
                fol = if(followingDatabase?.simpleSearch(name) as Boolean){
                    MyOperationTypes.unfollow
                }
                else{
                    MyOperationTypes.follow
                }
            }
            x.followInternal = fol

        }
        return list
    }

}