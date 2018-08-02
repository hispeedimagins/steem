package com.steemapp.lokisveil.steemapp;

/**
 * Created by boot on 2/3/2018.
 */

public class CentralConstants {
    public static final String sharedprefname = "sharedpreferencesname";
    public static final String username = "username";
    public static final String key = "key";
    public static final String about = "about";
    public static final String pfp = "pfp";
    public static final String cover = "cover";
    public static final String lastvotetime = "lastvotetime";
    public static final String votingpower = "votingpower";
    public static final String vestingshares = "vestingshares";
    public static final String delegatedvestingshares = "delegatedvestingshares";
    public static final String receivedvestingshares = "receivedvestingshares";
    public static final String accountrep = "accountrep";


    public static final String baseUrl = "https://api.steemit.com/";
    public static final String baseUrlView = "https://steemit.com/";


    public static final String FragmentTagFeed = "Feed";
    public static final String FragmentTagBlog = "Blog";
    public static final String FragmentTagNotifications = "Notifications";
    public static final String FragmentTagWallet = "Wallet";
    public static final String FragmentTagComments = "Comments";
    public static final String FragmentTagUpvotes = "Upvotes";
    public static final String FragmentTagFollowing = "Following";
    public static final String FragmentTagFollowers = "Followers";
    public static final String FragmentTagCommentsNoti = "Comments";
    public static final String FragmentTagReplies = "Replies";

    public static final String OtherGuyNamePasser = "otherguy";
    public static final String OtherGuyUseOtherGuyOnly = "useotherguy";
    public static final String FollowingFragmentUseFollower = "usefollower";
    public static final String MainTag = "maintag";
    public static final String MainRequest = "request";
    public static final String OriginalRequest = "originalrequest";
    public static final String ArticleBlockPasser = "blocknumber";
    public static final String ArticleUsernameToState = "usernameToState";
    public static final String ArticlePermlinkToFindPasser = "permlinkToFind";
    public static final String ArticleNotiType = "notificationtypear";

    public static final int FiveDaysInSeconds = 432000;
    public static final int SteemFullVote = 10000;


    public static String GetFeedImageUrl(String username){
        return "https://cdn.steemitimages.com/u/"+username+"/avatar/medium";
    }


    public final static String DatabaseNameFavourites = "favourites.db";
    public final static int DatabaseVersionFavourites = 2;

    public final static String DatabaseNameFollowers = "followers.db";
    public final static int DatabaseVersionFollowers = 1;

    public final static String DatabaseNameFollowing = "following.db";
    public final static int DatabaseVersionFollowing = 1;

    //public static final String imageurl = "https://steemitimages.com/u/niharikalove/avatar/small";
}
