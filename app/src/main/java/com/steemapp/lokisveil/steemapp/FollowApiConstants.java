package com.steemapp.lokisveil.steemapp;

import com.steemapp.lokisveil.steemapp.jsonclasses.prof;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by boot on 3/17/2018.
 */

public class FollowApiConstants  {
    private static final FollowApiConstants ourInstance = new FollowApiConstants();

    public static FollowApiConstants getInstance() {
        return ourInstance;
    }
    public prof.FollowCount followCount = null;
    public List<prof.Resultfp> followers;
    public List<prof.Resultfp> following;
    private FollowApiConstants() {
        followers = new ArrayList<>();
        following = new ArrayList<>();
    }

    public void AddToFollowing(prof.Resultfp fp){
        this.following.add(fp);
    }




}
