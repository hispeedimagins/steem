package com.insteem.ipfreely.steem.SteemBackend.Config.Operations;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.insteem.ipfreely.steem.SteemBackend.Config.Enums.FollowType;
import com.insteem.ipfreely.steem.SteemBackend.Config.Enums.ValidationType;
import com.insteem.ipfreely.steem.SteemBackend.Config.Models.AccountName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by boot on 3/2/2018.
 */

public class FollowOperation extends CustomJsonOperationPayload {
    //@JsonProperty("follower")
    @Expose
    @SerializedName("follower")
    private String _follower;
    private AccountName follower;
    //@JsonProperty("following")
    @Expose
    @SerializedName("following")
    private String _following;
    private AccountName following;
    //@JsonProperty("what")
    @Expose
    private List<FollowType> what;

    /**
     * Create a new follow operation to follow or ignore a user.
     * <b>Attention</b> This operation can't be broadcasted directly. It needs
     * to be added as a payload to a {@link CustomJsonOperation}.
     *
     * @param follower
     *            The account that wants to follow, unfollow or mute the
     *            <code>following</code> account (see
     *            {@link #setFollower(AccountName)}).
     * @param following
     *            The account to follow, unfollow or mute (see
     *            {@link #setFollowing(AccountName)}).
     * @param what
     *            The action to perform (see {@link #setWhat(List)}).
     * @throws InvalidParameterException
     *             If one of the arguments does not fulfill the requirements.
     */
    //@JsonCreator
    public FollowOperation(AccountName follower,
                           AccountName following, List<FollowType> what) {
        this.setFollower(follower);
        this.setFollowing(following);
        this.setWhat(what);
    }

    /**
     * Create an empty follow operation to follow or ignore a user.
     * <b>Attention</b> This operation can't be broadcasted directly. It needs
     * to be added as a payload to a {@link CustomJsonOperation}.
     */
    public FollowOperation() {
        this.setFollower(null);
        this.setFollowing(null);
        this.setWhat(null);
    }

    public String toJson(){
        String whatto = "";
        for (FollowType f:
             what) {
            if(f != FollowType.UNDEFINED)
            whatto += "\""+f.name().toLowerCase()+"\"";
        }
        String json = "[\"follow\",{\"follower\":\""+follower.getName()+"\",\"following\":\""+following.getName()+"\",\"what\":["+whatto+"]}]";
        return json;

    }

    /*public String toJson(){
        try {
            JSONArray array = new JSONArray();
            array.put("follow");

            JSONObject object = new JSONObject();
            object.put("follower",follower);
            object.put("following",following);
            object.put("what",what);

            array.put(object);
            return array.toString();
        }
        catch (JSONException ex){
            return null;
        }

    }*/

    /**
     * Get the account who follows, unfollows or mutes the
     * {@link #getFollowing()} account.
     *
     * @return The account name of the follower.
     */
    public AccountName getFollower() {
        return follower;

    }

    /**
     * Set the account who follows, unfollows or mutes the
     * {@link #getFollowing()} account. <b>Notice:</b> The private posting key
     * of this account needs to be stored in the key storage.
     *
     * @param follower
     *            The account name of the follower.
     */
    public void setFollower(AccountName follower) {
        this.follower = follower;
    }

    /**
     * Get the account the {@link #getFollower()} has followed, unfollowed or
     * muted.
     *
     * @return The account name of the account to follow, unfollow or mute.
     */
    public AccountName getFollowing() {
        return following;
    }

    /**
     * Get the account who followed, unfollowed or muted the
     * {@link #getFollowing()} account.
     *
     * @param following
     *            The account name of the account to follow, unfollow or mute.
     */
    public void setFollowing(AccountName following) {
        this.following = following;
    }

    /**
     * Get the information if the {@link #getFollower()} wants to follow,
     * unfollow or mute the {@link #getFollowing()} account (see
     * {@link FollowType}).
     *
     * @return The follow type.
     */
    public List<FollowType> getWhat() {
        return what;
    }

    /**
     * Define if the {@link #getFollower()} wants to follow, unfollow or mute
     * the {@link #getFollowing()} account (see {@link FollowType}).
     *
     * @param what
     *            The follow type.
     */
    public void setWhat(List<FollowType> what) {
        if (what == null || what.isEmpty()) {
            this.what = new ArrayList<>();
            this.what.add(FollowType.UNDEFINED);
        } else {
            this.what = what;
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public void validate(ValidationType validationType) {
        if (!ValidationType.SKIP_VALIDATION.equals(validationType)) {
            if (this.getFollower() == null) {
                throw new InvalidParameterException("The follower account cannot be null.");
            } else if (this.getFollowing() == null) {
                throw new InvalidParameterException("The following account cannot be null.");
            } else if (this.getFollower().equals(this.getFollowing())) {
                throw new InvalidParameterException("You cannot follow yourself.");
            }
        }
    }
}
