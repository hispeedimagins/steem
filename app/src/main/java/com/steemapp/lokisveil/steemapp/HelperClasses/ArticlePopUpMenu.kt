package com.steemapp.lokisveil.steemapp.HelperClasses

import android.app.Activity
import android.content.Context
import android.content.Intent

import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.PopupMenu
import com.steemapp.lokisveil.steemapp.CentralConstantsOfSteem
import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder
import com.steemapp.lokisveil.steemapp.Databases.FavouritesDatabase
import com.steemapp.lokisveil.steemapp.Interfaces.GlobalInterface
import com.steemapp.lokisveil.steemapp.Interfaces.JsonRpcResultInterface
import com.steemapp.lokisveil.steemapp.Interfaces.arvdinterface
import com.steemapp.lokisveil.steemapp.MiscConstants
import com.steemapp.lokisveil.steemapp.R
import com.steemapp.lokisveil.steemapp.RoomDatabaseApp.RoomRepos.FollowersRepo
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.FollowType
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.MyOperationTypes
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.AccountName
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.CustomJsonOperation
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.FollowOperation
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.Operation
import com.steemapp.lokisveil.steemapp.UserUpvoteActivity
import org.json.JSONArray

/**
 * Created by boot on 3/9/2018.
 */
class ArticlePopUpMenu(val context: Context, val view: View?,var shareurlauthor:String?, var shareurlarticle:String?, var followInternal: MyOperationTypes?, //var customJsonOperation = customJsonOperation
                       var followname: String?, var followingname: String?, val adaptedcomms: arvdinterface?,
                       val position: Int?,var progressBars: ProgressBar?,
                       var globalInterfaces: GlobalInterface?, var jsonArray: JSONArray? = null,
                       var userFavs : Boolean = false, var dbId:Int? = null) : GlobalInterface,JsonRpcResultInterface {

    //callback if person is following
    override fun searchFollowing(name: String, isFollowing: Boolean) {
        if(isFollowing){
            followInternal = MyOperationTypes.unfollow
        } else {
            followInternal = MyOperationTypes.follow
        }
        unHideFollow()
    }


    override fun notifyRequestMadeSuccess() {
        when(followInternal){
            MyOperationTypes.follow ->{
                followType = FollowType.UNDEFINED
                followInternal = MyOperationTypes.unfollow
                //popup?.menu?.findItem(R.id.article_dialog_follow)?.isVisible = true
            }
            MyOperationTypes.unfollow ->{
                followType = FollowType.BLOG
                followInternal = MyOperationTypes.follow
                //popup?.menu?.findItem(R.id.article_dialog_unfollow)?.isVisible = true
            }
            MyOperationTypes.mute ->{

            }
            MyOperationTypes.unmute ->{

            }
        }
        setup()
    }

    override fun notifyRequestMadeError() {

    }

    override fun getObjectMine(): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContextMine(): Context {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActivityMine(): Activity {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var followType = FollowType.BLOG
    var popupMenuItem: Menu? = null
    init {
        setup()

    }

    fun setup(){
        view?.setOnClickListener{
            var semcon = MiscConstants.ApplyMyThemePopUp(context)
            val popup = PopupMenu(semcon,view)
            //inflating menu from xml resource
            popup.inflate(R.menu.article_dialog_menu)

            if(shareurlauthor == null){
                popup.menu.findItem(R.id.article_dialog_share_author_profile).isVisible = false
            }
            if(shareurlarticle == null){
                popup.menu.findItem(R.id.article_dialog_share_article).isVisible = false
            }
            popup.menu.findItem(R.id.article_dialog_follow).isVisible = false
            popup.menu.findItem(R.id.article_dialog_unfollow).isVisible = false
            popup.menu.findItem(R.id.article_dialog_open_likes).isVisible = jsonArray != null || dbId != 0
            popup.menu.findItem(R.id.article_dialog_open_add_to_favourites).isVisible = userFavs
            popup.menu.findItem(R.id.article_dialog_open_Remove_From_favourites).isVisible = false

            var db = FavouritesDatabase(context)
            var adap = adaptedcomms?.getObject(position as Int)
            var glob = globalInterfaces?.getObjectMine()
            var perml = ""

            if(adap != null && adap is FeedArticleDataHolder.FeedArticleHolder) perml = adap.permlink else if(adap != null && adap is FeedArticleDataHolder.CommentHolder) perml = adap.permlink
            else if(glob != null && glob is FeedArticleDataHolder.FeedArticleHolder) perml = glob.permlink else if(glob != null && glob is FeedArticleDataHolder.CommentHolder) perml = glob.permlink

            if((db.simpleSearch(perml))){
                popup.menu.findItem(R.id.article_dialog_open_Remove_From_favourites).isVisible = true
                popup.menu.findItem(R.id.article_dialog_open_add_to_favourites).isVisible = false
            }




            popupMenuItem = popup.menu

            //instantiate a repo and find out if the person follows the user or now
            //we fire an async request
            var repo = FollowersRepo(context,null)
            repo.searchFollowing(followingname,this)
            //adding click listener
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    when (item.getItemId()) {
                        R.id.article_dialog_share_author_profile -> {
                            share(shareurlauthor)
                        }
                        R.id.article_dialog_share_article -> {
                            share(shareurlarticle)
                        }
                        R.id.article_dialog_follow ->{
                            postCustomJson()
                        }
                        R.id.article_dialog_unfollow ->{
                            postCustomJson()
                        }
                        R.id.article_dialog_open_likes ->{
                            CentralConstantsOfSteem.getInstance().jsonArray = jsonArray
                            var intent = Intent(context,UserUpvoteActivity::class.java)
                            intent.putExtra("dbId",dbId)
                            context.startActivity(intent)
                        }
                        R.id.article_dialog_open_add_to_favourites ->{
                            val fav = FavouritesDatabase(context)
                            fav.Insert(if(adaptedcomms != null) adaptedcomms?.getObject(position as Int) as FeedArticleDataHolder.FeedArticleHolder
                            else globalInterfaces?.getObjectMine() as FeedArticleDataHolder.FeedArticleHolder)
                        }
                        R.id.article_dialog_open_Remove_From_favourites ->{
                            val fav = FavouritesDatabase(context)
                            var i = fav.deleteContact(perml)
                            if(i == 1){
                                popup.menu.findItem(R.id.article_dialog_open_Remove_From_favourites).isVisible = false
                                popup.menu.findItem(R.id.article_dialog_open_add_to_favourites).isVisible = true
                            }
                            else if(i == 0){

                            }
                        }

                    }
                    return false
                }
            })
            //displaying the popup
            popup.show()
        }
    }

    /**
     * helps us update the ui when the async task finishes
     */
    fun unHideFollow(){
        popupMenuItem?.findItem(R.id.loading)?.isVisible = false
        when(followInternal){
            MyOperationTypes.follow ->{
                followType = FollowType.BLOG
                popupMenuItem?.findItem(R.id.article_dialog_follow)?.isVisible = true
            }
            MyOperationTypes.unfollow ->{
                followType = FollowType.UNDEFINED
                popupMenuItem?.findItem(R.id.article_dialog_unfollow)?.isVisible = true
            }
            MyOperationTypes.mute ->{

            }
            MyOperationTypes.unmute ->{

            }
        }
    }


    fun attachfollow(popup:PopupMenu?){

        when(followInternal){
            MyOperationTypes.follow ->{
                followType = FollowType.BLOG
                popup?.menu?.findItem(R.id.article_dialog_follow)?.isVisible = true
            }
            MyOperationTypes.unfollow ->{
                followType = FollowType.UNDEFINED
                popup?.menu?.findItem(R.id.article_dialog_unfollow)?.isVisible = true
            }
            MyOperationTypes.mute ->{

            }
            MyOperationTypes.unmute ->{

            }
        }
    }

    fun postCustomJson(){
        /*if(customJsonOperation != null){
            var list  = ArrayList<Operation>()
            list.add(customJsonOperation as CustomJsonOperation)
            //var bloc = GetDynamicAndBlock(con ,adaptedcomms,position,mholder,null,cus,obs,"Reblogged ${vop.permlink.link}",MyOperationTypes.reblog)
            var bloc = GetDynamicAndBlock(contxt ,adaptedcomms,position,list,"Reblogged ${vop.permlink.link}",MyOperationTypes.reblog)
            bloc.GetDynamicGlobalProperties()
        }*/
        if(view == null){
            attachfollow(null)
        }

        if(followname != null){
            var al = ArrayList<AccountName>()
            al.add(AccountName(followname))
            var l : List<FollowType> = ArrayList()
            l += followType
            var fop = FollowOperation(AccountName(followname), AccountName(followingname), l)
            var fus = CustomJsonOperation(null,al,"follow",fop.toJson(),followname,followingname,l)
            var list  = ArrayList<Operation>()
            list.add(fus)
            var ts = "Followed $followingname"
            if(followInternal == MyOperationTypes.unfollow){
                ts = "UnFollowed $followingname"
            }
            var bloc = GetDynamicAndBlock(context ,adaptedcomms,position as Int,list,ts,followInternal as MyOperationTypes,progressBars,globalInterfaces,this)
            bloc.GetDynamicGlobalProperties()
        }

    }

    fun share(sharestring : String?){
        val sendIntent: Intent =  Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, sharestring)
        sendIntent.type = "text/plain"
        context.startActivity(Intent.createChooser(sendIntent,"Share Link"))
    }

}