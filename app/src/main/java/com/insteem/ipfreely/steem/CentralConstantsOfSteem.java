package com.insteem.ipfreely.steem;

import com.insteem.ipfreely.steem.jsonclasses.Block;
import com.insteem.ipfreely.steem.jsonclasses.prof;

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
    //private Block.Resultfund resultfund = null;
    private Double resultFundRewards = null;
    private Long resultFundRecentClaims = null;
    private Double currentMedianHistoryBase = null;
    private Integer dynamicVotePowerReserveRate = null;
    //private Block.CurrentMedianHistory currentMedianHistory = null;
    //private Block.Result dynamicglobalprops = null;
    private prof.profile profile = null;
    private prof.Resultfo followcount = null;
    private JSONArray jsonArray = null;
    private prof.FollowCount OtherGuyFollowCount = null;
    private double voteval = 0;
    private double rshares = 0;
    private List<String> urls = null;
    //private int vpercent = 0;
    private List<String> trendingTags = new ArrayList();
    private CentralConstantsOfSteem() {
    }

    public void setCurrentvotingpower(int power ){
        this.currentvotingpower = power;
    }
    public int getCurrentvotingpower(){
        return this.currentvotingpower;
    }


    public void setCurrentMedianHistoryBase(Double currentMedianHistoryBase) {
        this.currentMedianHistoryBase = currentMedianHistoryBase;
    }

    public void setResultFundRecentClaims(Long resultFundRecentClaims) {
        this.resultFundRecentClaims = resultFundRecentClaims;
    }

    public void setResultFundRewards(Double resultFundRewards) {
        this.resultFundRewards = resultFundRewards;
    }

    public void setDynamicVotePowerReserveRate(Integer dynamicVotePowerReserveRate) {
        this.dynamicVotePowerReserveRate = dynamicVotePowerReserveRate;
    }

    public Double getResultFundRewards() {
        return resultFundRewards;
    }

    public Double getCurrentMedianHistoryBase() {
        return currentMedianHistoryBase;
    }

    public Long getResultFundRecentClaims() {
        return resultFundRecentClaims;
    }

    public Integer getDynamicVotePowerReserveRate() {
        return dynamicVotePowerReserveRate;
    }

    /*public void setDynamicglobalprops(Block.Result props){
            this.dynamicglobalprops = props;
        }
        public Block.Result getDynamicglobalprops(){
            return this.dynamicglobalprops;
        }*/
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
    public void setVoteval(Double vote){
        this.voteval = vote;
    }
    public Double getVoteval(){
        return this.voteval;
    }
    public void setRshares(Double rshare){
        this.rshares = rshare;
    }
    public Double getRshares(){
        return this.rshares;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getUrls() {
        return urls;
    }
}
