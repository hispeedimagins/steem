package com.steemapp.lokisveil.steemapp;

import com.steemapp.lokisveil.steemapp.jsonclasses.Block;
import com.steemapp.lokisveil.steemapp.jsonclasses.prof;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by boot on 3/12/2018.
 */

public class CentralConstantsOfSteem {
    private static final CentralConstantsOfSteem ourInstance = new CentralConstantsOfSteem();

    public static CentralConstantsOfSteem getInstance() {
        return ourInstance;
    }
    private int currentvotingpower = 0;
    private Block.Resultfund resultfund = null;
    private Block.CurrentMedianHistory currentMedianHistory = null;
    private Block.Result dynamicglobalprops = null;
    private prof.profile profile = null;
    private prof.Resultfo followcount = null;
    private JSONArray jsonArray = null;
    private prof.FollowCount OtherGuyFollowCount = null;
    private List<String> trendingTags = new ArrayList();
    private CentralConstantsOfSteem() {
    }

    public void setCurrentvotingpower(int power ){
        this.currentvotingpower = power;
    }
    public int getCurrentvotingpower(){
        return this.currentvotingpower;
    }
    public void setResultfund(Block.Resultfund fund){
        this.resultfund = fund;
    }
    public Block.Resultfund getResultfund(){
        return this.resultfund;
    }
    public void setCurrentMedianHistory(Block.CurrentMedianHistory history){
        this.currentMedianHistory = history;
    }
    public Block.CurrentMedianHistory getCurrentMedianHistory(){
        return this.currentMedianHistory;
    }
    public void setDynamicglobalprops(Block.Result props){
        this.dynamicglobalprops = props;
    }
    public Block.Result getDynamicglobalprops(){
        return this.dynamicglobalprops;
    }
    public void setProfile(prof.profile profi){
        this.profile = profi;
    }
    public prof.profile getProfile(){
        return this.profile;
    }

    public void setJsonArray(JSONArray array){
        this.jsonArray = array;
    }
    public JSONArray getJsonArray(){
        return this.jsonArray;
    }
    public void setOtherGuyFollowCount(prof.FollowCount res){
        this.OtherGuyFollowCount = res;
    }
    public prof.FollowCount getOtherGuyFollowCount(){
        return this.OtherGuyFollowCount;
    }
    public void setTrendingTags(List<String> l){
        this.trendingTags = l;
    }
    public List<String> getTrendingTags(){
        return this.trendingTags;
    }


}
