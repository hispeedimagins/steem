package com.insteem.ipfreely.steem;

import android.content.Context;

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
    public static final String followerCount = "follower_count_follower";
    public static final String followingCount = "following_count_follower";
    
    public static final String currentMedianHistoryBase = "currentMHBaseCon";
    public static final String resultFundRecentClaims = "resultFundRecentClaims";
    public static final String resultFundRewardsBalance = "resultFundRewardsBalance";
    public static final String lastSaveTimeOfMedianandBase = "lastSaveTimeOMAB";
    public static final String dynamicBlockVotePowerReserveRate = "dynamibBVPRR";


    //public static String baseUrl = "https://api.steemit.com/";

    /**
     * routes the call from one central place to the misc contants method to get the main api url
     * @param context application context to access the strings file
     * @return the main api url to fetch
     */
    public static String baseUrl(Context context){
        return MiscConstants.Companion.getMainApiUrl(context);
    }
    /*public static void getBaseUrl(String url){
        baseUrl = url;
    }*/
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

    public static final String ImageDownloadUrlPasser = "imageDownloadUrlPasser";

    public static final int FiveDaysInSeconds = 432000;
    public static final int SteemFullVote = 10000;


    public static String GetFeedImageUrl(String username){
        return "https://steemitimages.com/u/"+username+"/avatar/medium";
    }

    public static String GetProfileUrl(String username){
        return "https://steemit.com/@"+username+".json";
    }


    public final static String DatabaseNameFavourites = "favourites.db";
    public final static int DatabaseVersionFavourites = 2;

    public final static String DatabaseNameFollowers = "followers.db";
    public final static int DatabaseVersionFollowers = 1;

    public final static String DatabaseNameFollowing = "following.db";
    public final static int DatabaseVersionFollowing = 1;


    public final static String passerArticleTitle = "aTitle";
    public final static String passerArticleDefImg = "aImage";
    public final static String passerArticleaDate = "aDate";
}
