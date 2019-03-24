package com.insteem.ipfreely.steem.HelperClasses

import android.content.Context
import android.os.AsyncTask
import android.util.DisplayMetrics
import android.util.Log
import com.commonsware.cwac.anddown.AndDown
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.insteem.ipfreely.steem.CentralConstantsOfSteem
import com.insteem.ipfreely.steem.DataHolders.FeedArticleDataHolder
import com.insteem.ipfreely.steem.DataHolders.GetReputationDataHolder
import com.insteem.ipfreely.steem.Databases.FollowersDatabase
import com.insteem.ipfreely.steem.Databases.FollowingDatabase
import com.insteem.ipfreely.steem.Enums.TypeOfRequest
import com.insteem.ipfreely.steem.Interfaces.JsonRpcResultInterface
import com.insteem.ipfreely.steem.MiscConstants
import com.insteem.ipfreely.steem.R
import com.insteem.ipfreely.steem.SteemBackend.Config.Enums.MyOperationTypes
import com.insteem.ipfreely.steem.jsonclasses.feed
import com.insteem.ipfreely.steem.jsonclasses.prof
import org.json.JSONArray
import org.json.JSONObject
import java.util.regex.Pattern


/**
 * Created by boot on 2/10/2018.
 */


class JsonRpcResultConversion(val json :JSONObject?,var username :String, val requestType: TypeOfRequest?){
    var contex : Context? = null
    var displayMet:DisplayMetrics? = null
    var mAndDown:AndDown? = null
    constructor(json :JSONObject?,username :String,requestType : TypeOfRequest?,context: Context) : this(json,username,requestType){
        contex = context
        followersDatabase = FollowersDatabase(context)
        followingDatabase = FollowingDatabase(context)
    }

    constructor(json :JSONObject?,username :String,requestType : TypeOfRequest?,context: Context,metrics: DisplayMetrics) : this(json,username,requestType,context){
        displayMet = metrics
        mAndDown = AndDown()
    }

    constructor(json :JSONObject?,username :String, requestType: TypeOfRequest?,context: Context,jInterface:JsonRpcResultInterface?,blogData:Boolean): this(json,username,requestType,context){
        jni = jInterface
        this.blogData = blogData
    }
    //val json = json
    //val username : String = username.toLowerCase()
    var gson : Gson = Gson()
    var followersDatabase : FollowersDatabase? = null
    var followingDatabase : FollowingDatabase? = null
    var nobots = "\\b(?:postpromoter|smartsteem|buildawhale|upme|appreciator|rocky1|booster|boomerang|jerrybanfield|therising|promobot|upmyvote|pushup|sneaky-ninja|minnowbooster)\\b"
    var jni : JsonRpcResultInterface? = null
    var blogData:Boolean = false

    //used to parse through get_reputation api data
    fun ParseRepAndSearch() : List<GetReputationDataHolder>{
        val body = json
        val result = if(body?.has("result")!!) body.getJSONArray("result") else null
        if(result == null){
            return ArrayList()
        }
        val al = ArrayList<GetReputationDataHolder>()
        for(x in 0 until result.length()){
            //get jsonobject
            val curob = result.getJSONObject(x)
            //get account name
            val author = curob.getString("account")
            //get the rep and calculate its int value
            val calcrep = StaticMethodsMisc.CalculateRepScoreRetInt(curob.getString("reputation"))
            val autho = "$author ($calcrep)"
            var mfo = MyOperationTypes.follow
            //check if person follows you from the local db
            //it is used to evaluate if the follow or the
            //unfollow option is presented
            if(followersDatabase != null){
                if(author != username){
                    if(followersDatabase?.simpleSearch(author) as Boolean){
                        //"$autho follows you"
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
        val result = if(body?.has("result")!!) body?.getJSONObject("result") else null
        if(result == null){
            return ArrayList<String>()
        }
        val tag_idx = result.getJSONObject("tag_idx")
        val trending = tag_idx.getJSONArray("trending")
        val tags = ArrayList<String>()
        if(trending != null){
            for (x in 0 until trending.length()){
                tags.add(trending.getString(x))
            }
        }
        CentralConstantsOfSteem.getInstance().trendingTags = tags
        return tags
    }

    fun ParseJsonBlog() : ArrayList<FeedArticleDataHolder.FeedArticleHolder>{
        val body = json
        val result = (if(body?.has("result")!!) body?.getJSONObject("result") else null) ?: return ArrayList()
        if(result.has("tag_idx")){
            GetTrendingTags()
        }

        val content = (if(result.has("content")) result.getJSONObject("content") else null) ?: return ArrayList()
        val accounts = result.getJSONObject("accounts")
        val user = (if(accounts.has(username)) accounts.getJSONObject(username) else null) ?: return ArrayList()
        var getthis = "feed"
        if(requestType == TypeOfRequest.blog){
            getthis = "blog"
        } else if(requestType == TypeOfRequest.comments){
            getthis = "comments"
        } else if(requestType == TypeOfRequest.replies){
            getthis = "recent_replies"
        }
        var runTimes = 0
        var returndata : ArrayList<FeedArticleDataHolder.FeedArticleHolder> = ArrayList()
        val arr = if(user.has(getthis))  user.getJSONArray(getthis) else null
        if(arr != null){

            for (x in 0 until arr.length()){

                //var st = x.toString()
                try{
                    val ss = arr.getString(x)
                    if(ss != null){
                        val commstr : JSONObject = content.getJSONObject(ss)
                        //if jni is not null we do a callback for saving to db,
                        //else accumulate in the list and return
                        if(jni != null){
                            jni?.insert(getprocessedfeed(commstr)!!)
                        } else {
                            returndata.add(getprocessedfeed(commstr)!!)
                        }
                        runTimes++

                    }
                }
                catch (ex : Exception){
                    ex.message
                }
            }
            //close db connections to prevent leakages
            jni?.processingDone(runTimes)
            closedb()
           return returndata
        }

        //close db connections to prevent leakages
        closedb()
        return  ArrayList()
    }

    fun parseJsonBlogGetMain():JSONObject?{
        return if(json?.has("result")!!) json?.getJSONObject("result") else null
    }

    fun parseJsonBlogGetMainAsArray():JSONArray?{
        return if(json?.has("result")!!) json?.getJSONArray("result") else null
    }

    private fun parseJsonBlogGetContent():JSONObject?{
        val result = parseJsonBlogGetMain()
        if(result?.has("tag_idx")!!){
            GetTrendingTags()
        }
        val content = result.getJSONObject("content")
        return content
    }

    private fun parseJsonBlogGetUser():JSONArray?{
        val result = parseJsonBlogGetMain() ?: return null
        val accounts = result.getJSONObject("accounts")
        val user = (if(accounts.has(username)) accounts.getJSONObject(username) else null) ?: return null
        var getthis = "feed"
        if(requestType == TypeOfRequest.blog){
            getthis = "blog"
        } else if(requestType == TypeOfRequest.comments){
            getthis = "comments"
        } else if(requestType == TypeOfRequest.replies){
            getthis = "recent_replies"
        }
        return if(user.has(getthis))  user.getJSONArray(getthis) else null
    }

    //close db connections to prevent leakages
    fun closedb(){
        followersDatabase?.close()
        followingDatabase?.close()
    }

    fun ParseJsonBlogMore(checkforbots:Boolean = false,comms:JsonRpcResultInterface? = null) : ArrayList<FeedArticleDataHolder.FeedArticleHolder>{

        //val body = gson.fromJson(json, JsonObject::class.java)
        val body = json
        //check if json has result to preven a crash, if not, return.
        var runtime = 0
        if(!(body!!.has("result"))){
            return ArrayList()
        }

        //we have to check for errors in the new api. result will be an object rather than an array
        //hence check accordingly and parse the error
        val res = body.get("result")
        if(res is JSONObject){
            comms?.errorWhileParsing(res.getJSONObject("error").getString("message"))
            return ArrayList()
        }
        val result: JSONArray? = res as JSONArray

        val returndata : ArrayList<FeedArticleDataHolder.FeedArticleHolder> = ArrayList()
        //val arr = user.get(getthis)?.asJsonArray
        var skipz = false
        if(result?.length() != null){
            for (x in 0 until result?.length()){
                if(skipz){
                    val commstr : JSONObject = result.getJSONObject(x) //x
                    val s = getprocessedfeed(commstr,false,true)
                    if(s != null){
                        //if jni is not null we do a callback for saving to db,
                        //else accumulate in the list and return
                        if(jni != null){
                            jni?.insert(getprocessedfeed(commstr)!!)
                        } else {
                            returndata.add(getprocessedfeed(commstr)!!)
                        }
                    }
                    runtime++
                }
                else{
                    skipz = true
                }

            }
        }
        //close db connections to prevent leakages
        jni?.processingDone(runtime)
        closedb()
        return returndata

        //return  ArrayList<FeedArticleDataHolder.FeedArticleHolder>()

    }




    fun getprocessedfeed(commstr : JSONObject,bodyasis : Boolean = false,checkforbots:Boolean = false) : FeedArticleDataHolder.FeedArticleHolder? {
        val gson = Gson()



        var voted = false
        var repl : JSONArray? = null
        if(commstr.has("replies")){
            repl = commstr.getJSONArray("replies")
        }
        val author = commstr.getString("author")
        if(commstr.getJSONArray("active_votes").toString().contains("\"voter\":\"$username\"")) voted = true
        if(checkforbots){
            val pattern = Pattern.compile(
                    nobots,
                    Pattern.CASE_INSENSITIVE)
            if(pattern.matcher(commstr.getJSONArray("active_votes").toString()).find() || author == "haejin"){
                return null
            }
        }
        val ls = _getReblogBy(commstr)
        var st = ""
        if(!bodyasis){
            try{
                st = commstr.getString("body").substring(IntRange(0,400))
            }
            catch(ex : StringIndexOutOfBoundsException){
                st = commstr.getString("body")
            }
            st = StripMarkDown.stripMd(st,StripMarkDown.Companion.mdOptions()).trim()


        }
        else{
            //st  = commstr.getString("body")
        }

        val autho = "$author (${StaticMethodsMisc.CalculateRepScore(commstr.getString("author_reputation"))})"

        var jsonMetadata : feed.JsonMetadataInner? = null
        //this is because some apps have tags in a string instead of an array
        //so the app would crash, this will keep the app running
        //will try to have a better solution in the other release
        try{
            jsonMetadata = gson.fromJson<feed.JsonMetadataInner>(commstr.getString("json_metadata"),feed.JsonMetadataInner::class.java)
        } catch (jex : JsonSyntaxException){

        }

        /*var dd = StaticMethodsMisc.FormatDateGmt(commstr.getString("created"))
        var du = DateUtils.getRelativeDateTimeString(contex,dd.time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0)*/
        val dd = StaticMethodsMisc.FormatDateGmt(commstr.getString("created"))
        val du = MiscConstants.dateToRelDate(dd,contex)
        //val sdf = SimpleDateFormat("dd MMMM", Locale.getDefault())
        val fd : FeedArticleDataHolder.FeedArticleHolder = FeedArticleDataHolder.FeedArticleHolder(
                displayName = autho,
                reblogBy = ls,
                reblogOn = _getString("first_reblogged_on",commstr),
                entryId = if(commstr.has("entry_id")) commstr.getInt("entry_id") else 0 ,
                active = _getString("active",commstr,true)!!,
                author = author,
                //body = if(bodyasis) st else builder.toString(),
                body = commstr.getString("body"),
                summary = st,
                cashoutTime = _getString("cashout_time",commstr,true)!!,
                category = _getString("category",commstr,true)!!,
                children = commstr.getInt("children"),
                date = dd,
                created = _getString("created",commstr,true)!!,
                createdcon = dd.time.toString(),
                depth = commstr.getInt("depth"),
                id = commstr.getLong("post_id"),
                lastPayout = _getString("last_payout",commstr,true)!!,
                lastUpdate = _getString("last_update",commstr,true)!!,
                netVotes = _getNetVotes(commstr),
                permlink = _getString("permlink",commstr,true)!!,
                rootComment = if(commstr.has("root_comment")) commstr.getInt("root_comment") else 0,
                title = commstr.getString("title"),
                format = jsonMetadata?.format,
                app = jsonMetadata?.app,
                image = jsonMetadata?.image,
                links = jsonMetadata?.links,
                tags = jsonMetadata?.tags,
                users = jsonMetadata?.users,
                authorreputation = _getString("author_reputation",commstr),
                pending_payout_value = _getString("pending_payout_value",commstr),
                promoted = _getString("promoted",commstr),
                total_pending_payout_value = _getString("total_pending_payout_value",commstr),
                uservoted = voted,
                already_paid = _getString("total_payout_value",commstr),
                datespan = du,
                replies = repl,
                activeVotes = commstr.getJSONArray("active_votes"),
                rootAuthor = if(commstr.has("root_author")) commstr.getString("root_author") else null,
                rootPermlink = if(commstr.has("root_permlink")) commstr.getString("root_permlink") else null,
                followsYou = false,
                isBlog = blogData,
                displayImage = jsonMetadata?.image?.firstOrNull(),
                sDate = MiscConstants.dateToDisplayHolder(dd.time)

        )
        return fd
    }

    private fun _getReblogBy(commstr : JSONObject):List<String>{
        if(commstr.has("reblogged_by")){
            val rpb = commstr.getJSONArray("reblogged_by")
            val collectionType = object : TypeToken<List<String>>() {

            }.type
            return gson.fromJson<List<String>>(rpb.toString(),collectionType)
        }
        return ArrayList<String>()
    }

    private fun _getString(str:String,commstr : JSONObject,noNull:Boolean = false):String?{
        if(commstr.has(str)) return commstr.getString(str)
        return if(noNull) "" else null
    }

    private fun _getInt(str:String,commstr : JSONObject,noNull:Boolean = false):Int?{
        if(commstr.has(str)) return commstr.getInt(str)
        return if(noNull) 0 else null
    }

    private fun _getNetVotes(commstr : JSONObject):Int{
        if(commstr.has("active_votes")){
            return commstr.getJSONArray("active_votes").length()
        }
        return 0
    }

    fun ParseReplies(UserAndPermlinkWithDash : String?) : ArrayList<Any>{
        val list = ArrayList<Any>()
        //val body = gson.fromJson(json, JsonObject::class.java)
        val body = json
        val result = (if(body?.has("result") as Boolean) body?.getJSONObject("result") else null)
                ?: return list
        val content : JSONObject = if(result?.has("content")) result.getJSONObject("content") else return list

        val com : JSONObject = content.getJSONObject(UserAndPermlinkWithDash)
        val article  = getprocessedfeed(com,true) //gson.fromJson<feed.Comment>(com.toString(),feed.Comment::class.java)
        //if(article == null) return list

        list.add(article!!)
        //set wid to zero
        val wid = 0
        if(article?.replies != null){
            for(x in 0 until article.replies!!.length()){
                val comstr = content.getJSONObject(article.replies!!.getString(x))
                //var reply = gson.fromJson<feed.Comment>(comstr.toString(),feed.Comment::class.java)
                val reply = getprocessedcomment(comstr,article.author,article.permlink,article.tags!![0],0,if(comstr.has("replies")) comstr.getJSONArray("replies") else null) //gson.fromJson<feed.Comment>(comstr.toString(),feed.Comment::class.java)
                reply.reply_to_above = false
                list.add(reply)
                if(reply.replies != null && reply.replies!!.length() > 0){
                    //add 5 to increase width
                    _ParseReplies(list,content,reply,true,wid + 5)
                }
            }
        }

        //close db connections to prevent leakages
        closedb()
        return list
    }



    private fun _ParseReplies(list : ArrayList<Any>,content : JSONObject,article : FeedArticleDataHolder.CommentHolder,replytoabove : Boolean,wid : Int){
        for(x in 0 until article.replies!!.length()){
            val comstr = content.getJSONObject(article.replies!!.getString(x))
            //var reply = gson.fromJson<feed.Comment>(comstr.toString(),feed.Comment::class.java)
            var st = ""
            if(article.tags != null){
                st = article?.tags!![0]
            }
            val reply = getprocessedcomment(comstr,article.author,article.permlink,st,wid,if(comstr.has("replies")) comstr.getJSONArray("replies") else null)
            //reply.width = wid
            reply.reply_to_above = replytoabove
            list.add(reply)
            if(reply.replies != null && reply.replies!!.length() > 0){
                val nw = wid + 5
                _ParseReplies(list,content,reply,true,nw)
            }
        }

    }



    fun getprocessedcomment(commstr: JSONObject,parentAuthor:String?,parentPermlink:String?,parentTag:String?,wid : Int,replies : JSONArray?) : FeedArticleDataHolder.CommentHolder {

        val gson = Gson()

        var voted = false

        var fol = MyOperationTypes.unfollow
        if(followingDatabase != null){
            fol = if(followingDatabase?.simpleSearch(commstr.getString("author")) as Boolean){
                MyOperationTypes.unfollow
            }
            else{
                MyOperationTypes.follow
            }
        }
        val avs = commstr.getJSONArray("active_votes").toString()
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

        val dd = StaticMethodsMisc.FormatDateGmt(commstr.getString("created"))
        val du = MiscConstants.dateToRelDate(dd,contex)
       /* var rpb = commstr.getJSONArray("reblogged_by")
        val collectionType = object : TypeToken<List<String>>() {

        }.type
        var ls = gson.fromJson<List<String>>(rpb.toString(),collectionType)*/
        val ls = _getReblogBy(commstr)
        val bod = StaticMethodsMisc.CorrectMarkDown(commstr.getString("body"),jsonMetadata?.image)
        val author = commstr.getString("author")
        val autho = "$author (${StaticMethodsMisc.CalculateRepScore(commstr.getString("author_reputation"))})"
        var tfollowsYou = false
        if(followersDatabase != null){
            if(author != username){
                tfollowsYou = followersDatabase?.simpleSearch(author) as Boolean
            }
        }




        val fd : FeedArticleDataHolder.CommentHolder = FeedArticleDataHolder.CommentHolder(
                displayName = autho,
                reblogBy = ls,
                reblogOn = _getString("first_reblogged_on",commstr),
                entryId = if(commstr.has("entry_id")) commstr.getInt("entry_id") else 0 ,
                active = _getString("active",commstr,true)!!,
                author = author,
                body = if(username == author) bod else "",
                lBody = convertCommentBodyToList(bod,jsonMetadata?.format,jsonMetadata?.image),
                cashoutTime = _getString("cashout_time",commstr,true)!!,
                category = _getString("category",commstr,true)!!,
                children = commstr.getInt("children"),
                created =  _getString("created",commstr,true)!!,
                createdcon = dd.toString(),
                date = dd,
                depth = commstr.getInt("depth"),
                id = commstr.getLong("post_id"),
                lastPayout = _getString("last_payout",commstr,true)!!,
                lastUpdate = _getString("last_update",commstr,true)!!,
                netVotes = _getNetVotes(commstr),
                permlink = _getString("permlink",commstr,true)!!,
                rootComment = if(commstr.has("root_comment")) commstr.getInt("root_comment") else 0,
                title = commstr.getString("title"),
                format = jsonMetadata?.format,
                app = jsonMetadata?.app,
                image = jsonMetadata?.image,
                links = jsonMetadata?.links,
                tags = jsonMetadata?.tags,
                users = jsonMetadata?.users,
                authorreputation = StaticMethodsMisc.CalculateRepScore(commstr.getString("author_reputation")),
                pending_payout_value = _getString("pending_payout_value",commstr),
                promoted = _getString("promoted",commstr),
                total_pending_payout_value = _getString("total_pending_payout_value",commstr),
                uservoted = voted,
                already_paid =  _getString("total_payout_value",commstr),
                //summary = null,
                datespan = du,
                width = wid,
                widthPx = ImagePickersWithHelpers.GetPx(wid.toFloat(),displayMet),
                defaultWidth = contex?.resources?.getDimension(R.dimen.default_comment_margin_sides)?.toInt() ?: 0,
                activeVotes = commstr.getJSONArray("active_votes"),
                replies = replies,
                useFollow = fol,
                parent_permlink = parentPermlink,
                parent_tag = parentTag,
                paretn_author = parentAuthor,
                rootAuthor = if(commstr.has("root_author")) commstr.getString("root_author") else null,
                rootPermlink = if(commstr.has("root_permlink")) commstr.getString("root_permlink") else null,
                followsYou = tfollowsYou,
                sDate = MiscConstants.dateToDisplayHolder(dd.time)
                //datespan = du.toString()
        )
        //adapter?.add(fd)
        return fd

    }

    fun convertCommentBodyToList(body:String?,format:String?,images:List<String>?):List<Any>{
        var s : String? = ""
        s = if(format == "html"){
            if(body != null) body else ""
        }
        else{
            mAndDown?.markdownToHtml(body, AndDown.HOEDOWN_EXT_AUTOLINK, AndDown.HOEDOWN_HTML_SKIP_HTML)
        }
        s = StaticMethodsMisc.CorrectAfterMainImages(s)
        //s = StaticMethodsMisc.CorrectMarkDownUsers(s,holder.article?.users)
        //added new username catcher
        s = MiscConstants.CorrectUsernamesK(s)
        s = StaticMethodsMisc.CorrectNewLine(s)
        return StaticMethodsMisc.ConvertTextToList(s,images)
    }


    fun processfollowlist(list:List<prof.Resultfp>,useFollower: Boolean):List<prof.Resultfp>{
        for(x in list){
            //var name: String
            val name = if(useFollower){
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



    fun processBlogAsync(jni:JsonRpcResultInterface?){
        val content = parseJsonBlogGetContent()
        val arr = parseJsonBlogGetUser()
        class task : AsyncTask<Void, Void, ResultPasser>(){
            override fun doInBackground(vararg params: Void): ResultPasser? {
                if(arr != null){
                    var runTimes = 0
                    var returndata : ArrayList<FeedArticleDataHolder.FeedArticleHolder> = ArrayList()
                    for (x in 0 until arr.length()){

                        //var st = x.toString()
                        try{
                            val ss = arr.getString(x)
                            if(ss != null){
                                val commstr : JSONObject? = content?.getJSONObject(ss)

                                //if jni is not null we do a callback for saving to db,
                                //else accumulate in the list and return
                                if(commstr != null){
                                    if(jni != null){
                                        jni.insert(getprocessedfeed(commstr)!!)
                                    } else {
                                        returndata.add(getprocessedfeed(commstr)!!)
                                    }
                                    runTimes++
                                }


                            }
                        }
                        catch (ex : Exception){
                            ex.message
                        }
                    }
                    //jni?.processingDone(runTimes)
                    return ResultPasser(runTimes,returndata)
                }

                return null
            }

            override fun onPostExecute(result: ResultPasser?) {
                if(result == null) return
                jni?.processingDone(result.times)
                jni?.processedArticles(result.list)
                super.onPostExecute(result)
            }
        }
        task().execute()
    }


    fun processBlogMoreAsync(jni:JsonRpcResultInterface?){
        class task : AsyncTask<Void, Void, ResultPasser>(){
            override fun doInBackground(vararg params: Void): ResultPasser? {
                //val body = gson.fromJson(json, JsonObject::class.java)
                val result: JSONArray? = parseJsonBlogGetMainAsArray()
                val returndata : ArrayList<FeedArticleDataHolder.FeedArticleHolder> = ArrayList()
                //val arr = user.get(getthis)?.asJsonArray
                var skipz = false
                var runTimes = 0
                if(result?.length() != null){
                    for (x in 0 until result?.length()){
                        if(skipz){
                            val commstr : JSONObject = result.getJSONObject(x) //x
                            val s = getprocessedfeed(commstr,false,true)
                            if(s != null){
                                //if jni is not null we do a callback for saving to db,
                                //else accumulate in the list and return
                                if(jni != null){
                                    jni?.insert(getprocessedfeed(commstr)!!)
                                } else {
                                    returndata.add(getprocessedfeed(commstr)!!)
                                }
                            }
                            runTimes++
                        }
                        else{
                            skipz = true
                        }

                    }
                }
                return ResultPasser(runTimes,returndata)

                //return  ArrayList<FeedArticleDataHolder.FeedArticleHolder>()
            }

            override fun onPostExecute(result: ResultPasser?) {
                if(result == null) return
                jni?.processingDone(result.times)
                jni?.processedArticles(result.list)
                super.onPostExecute(result)
            }
        }
        task().execute()
    }

    private data class ResultPasser(val times:Int,val
    list:List<FeedArticleDataHolder.FeedArticleHolder>)
}