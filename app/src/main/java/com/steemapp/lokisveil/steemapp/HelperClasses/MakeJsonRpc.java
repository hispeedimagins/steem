package com.steemapp.lokisveil.steemapp.HelperClasses;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.steemapp.lokisveil.steemapp.jsonclasses.JsonRpcRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by boot on 2/10/2018.
 */

public class MakeJsonRpc {
    private static final MakeJsonRpc ourInstance = new MakeJsonRpc();

    public static MakeJsonRpc getInstance() {
        return ourInstance;
    }

    public int id = 1;



    private MakeJsonRpc() {
    }

    public JsonRpcRequest getJsonBasics(){
        JsonRpcRequest request = new JsonRpcRequest();
        request.id = String.valueOf(id);
        request.jsonrpc = "2.0";
        return request;
    }

    /**
     * get_account_reputations is used to search for usernames - easy and fast
     * @param searchname username to search for
     * @param number number of results to get back
     * @return JSONObject
     * @throws JSONException
     */
    public JSONObject GetAccountRepsAndSearch(String searchname,int number) throws JSONException{

        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"call\",\"params\":[\"follow_api\",\"get_account_reputations\", [\""+searchname+"\",\"" + number + "\"]]}"
        );
        id++;
        return object;
    }

    public JSONObject getTagQuery(String method,String tag,String limit) throws JSONException{

        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\""+method+"\",\"params\":[{\"tag\":\""+tag+"\",\"limit\":\""+limit+"\"}]}"
        );
        id++;
        return object;
    }

    public JSONObject getMoreItems(String startauthor,String startpermlink, String tag,String method) throws JSONException{

        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"call\",\"params\":[\"database_api\",\""+method+"\", [{\"limit\":\"20\",\"start_author\":\""+startauthor+"\",\"start_permlink\":\"" + startpermlink + "\",\"tag\":\"" + tag + "\"}]]}"
        );
        id++;
        return object;
    }

    public JSONObject getState(String page) throws JSONException{
        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"get_state\",\"params\":[\""+page+"\"]}"
        );
        id++;
        return object;
    }

    public JSONObject getContent(String username,String page) throws JSONException{
        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"get_content\",\"params\":[\""+username+"\",\""+page+"\"]}"
        );
        id++;
        return object;
    }

    public JSONObject getFeedJ(String username,String page) throws JSONException{
        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"call\",\"params\":[\"database_api\",\"get_state\", [\"/@"+username+"/"+page+"\"]]}"
        );
        id++;
        return object;
    }

    public JSONObject getFeedJ(String username) throws JSONException{
        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"call\",\"params\":[\"database_api\",\"get_state\", [\"/@"+username+"/feed\"]]}"
        );
        id++;
        return object;
    }



    public JSONObject getBlogJ(String username) throws JSONException{
        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"call\",\"params\":[\"database_api\",\"get_state\", [\"/@"+username+"/blog\"]]}"
        );
        id++;
        return object;
    }


    public JSONObject getMoreItems(String startauthor,String startpermlink, String tag,boolean useBlog) throws JSONException{
        String method = "get_discussions_by_feed";
        if(useBlog){
            method = "get_discussions_by_blog";
        }
        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"call\",\"params\":[\"database_api\",\""+method+"\", [{\"limit\":\"20\",\"start_author\":\""+startauthor+"\",\"start_permlink\":\"" + startpermlink + "\",\"tag\":\"" + tag + "\"}]]}"
        );
        id++;
        return object;
    }

    /*public JSONObject getMoreItems(String startauthor,String startpermlink, String tag,String method) throws JSONException{

        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"call\",\"params\":[\"database_api\",\""+method+"\", [{\"limit\":\"20\",\"start_author\":\""+startauthor+"\",\"start_permlink\":\"" + startpermlink + "\",\"tag\":\"" + tag + "\"}]]}"
        );
        id++;
        return object;
    }*/


    public JSONObject getArticle(String articletag ,String articleusername , String articlepermlink) throws JSONException{
        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"call\",\"params\":[\"database_api\",\"get_state\", [\""+articletag+"/@"+articleusername+"/"+articlepermlink+"\"]]}"
        );
        id++;
        return object;
    }




    public JSONObject getArticle(String limit, String start_author, String start_permlink,String articletagisusername) throws JSONException{
        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"call\",\"params\":[\"database_api\",\"get_discussions_by_feed\", [\""+limit+","+start_author+","+start_permlink+","+articletagisusername+"  \"]]}"
        );
        id++;
        return object;
    }


    public JSONObject getGlobalProperties() throws JSONException{
        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"get_dynamic_global_properties\",\"params\":[]}"
        );
        id++;
        return object;
    }

    public JSONObject getRewardFund() throws JSONException{
        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"get_reward_fund\",\"params\":[\"post\"]}"
        );
        id++;
        return object;
    }

    public JSONObject getPriceFeed() throws JSONException{
        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"get_feed_history\",\"params\":[]}"
        );
        id++;
        return object;
    }

    public JSONObject getBlock(int blocknumber) throws JSONException{
        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"call\",\"params\":[\"database_api\",\"get_block\",[\""+String.valueOf(blocknumber)+"\"]]}"
        );
        id++;
        return object;
    }

    public JSONObject getFollowCount(String name) throws JSONException{
        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"call\",\"params\":[\"follow_api\",\"get_follow_count\",[\""+name+"\"]]}"
        );
        id++;
        return object;
    }

    public JSONObject getFollowing(String name,String start) throws JSONException{
        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"call\",\"params\":[\"follow_api\",\"get_following\",[\""+name+"\",\""+start+"\",\"blog\",\"1000\"]]}"
        );
        id++;
        return object;
    }

    public JSONObject getFollowers(String name,String start) throws JSONException{
        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"call\",\"params\":[\"follow_api\",\"get_followers\",[\""+name+"\",\""+start+"\",\"blog\",\"1000\"]]}"
        );
        id++;
        return object;
    }




    public JSONObject networkBroadcaseSynchronous(String exptime, String operationtype, String author,String permlink,String voter,String weight,String refblock,String refblockprefix,String signature) throws JSONException{
        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"call\",\"params\":[\"network_broadcast_api\",\"broadcast_transaction_synchronous\", [{\"expiration\":\""+exptime+"\",\"extensions\":[],\"operations\":[[\""+operationtype+"\"," +
                        "{\"author\":\""+author+"\",\"permlink\":\""+permlink+"\",\"voter\":\""+voter+"\",\"weight\":\""+weight+"\"}]],\"ref_block_num\":\""+refblock+"\",\"ref_block_prefix\":\""+refblockprefix+"\",\"signatures\":[\""+signature+"\"]}]]}"
        );
        id++;
        return object;
    }

    public JSONObject networkBroadcaseSynchronousReblog(String regpostingauth,String exptime, String operationtype, String innerid,String refblock,String refblockprefix,String json,String signature) throws JSONException{
        JSONObject obj = new JSONObject();
        obj.put("id",id);
        obj.put("jsonrpc","2.0");
        obj.put("method","call");

        JSONArray paramArray = new JSONArray();
        paramArray.put("network_broadcast_api");
        paramArray.put("broadcast_transaction_synchronous");

        JSONArray innerParamArray = new JSONArray();

        JSONObject paramfirstobj = new JSONObject();
        paramfirstobj.put("expiration",exptime);
        paramfirstobj.put("extensions",new JSONArray());

        JSONArray operationsh = new JSONArray();
        operationsh.put(operationtype);


        JSONObject operationso = new JSONObject();
        operationso.put("id",innerid);
        operationso.put("json",json);
        operationso.put("required_auths",new JSONArray());
        ArrayList<String> namelist = new ArrayList<String>();
        namelist.add(regpostingauth);
        operationso.put("required_posting_auths",new JSONArray(namelist));

        operationsh.put(operationso);

        JSONArray inteo = new JSONArray();
        inteo.put(operationsh);
        paramfirstobj.put("operations",inteo);

        paramfirstobj.put("ref_block_num",refblock);
        paramfirstobj.put("ref_block_prefix",refblockprefix);

        ArrayList<String> siglist = new ArrayList();
        siglist.add(signature);
        paramfirstobj.put("signatures",new JSONArray(siglist));


        JSONArray intet = new JSONArray();
        intet.put(paramfirstobj);
        paramArray.put(intet);
        obj.put("params",paramArray);

        String op = obj.toString();

        /*JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"call\",\"params\":[\"network_broadcast_api\",\"broadcast_transaction_synchronous\", [{\"expiration\":\""+exptime+"\",\"extensions\":[],\"operations\":[[\""+operationtype+"\"," +
                        "{\"required_auths\":[],\"required_posting_auths\":[\""+regpostingauth+"\"],\"id\":\""+innerid+"\",\"json\":\""+json+"\"}]],\"ref_block_num\":\""+refblock+"\",\"ref_block_prefix\":\""+refblockprefix+"\",\"signatures\":[\""+signature+"\"]}]]}"
        );*/
        id++;
        return obj;
    }





    public JSONObject networkBroadcaseSynchronous(String operationjson,String exptime, String refblock,String refblockprefix,String signature) throws JSONException{
        JSONObject object = new JSONObject(
                "{\"id\":\""+id+"\",\"jsonrpc\":\"2.0\",\"method\":\"call\",\"params\":[\"network_broadcast_api\",\"broadcast_transaction_synchronous\", [{\"expiration\":\""+exptime+"\",\"extensions\":[],\"operations\":"+operationjson+",\"ref_block_num\":\""+refblock+"\",\"ref_block_prefix\":\""+refblockprefix+"\",\"signatures\":[\""+signature+"\"]}]]}"
        );
        id++;
        return object;
    }


    /*public Map<String,String> getFeed(){
        JsonRpcRequest request = getJsonBasics();
        request.method = "call";




        request.params = "[\"database_api\",\"get_state\", [\"/@hispeedimagins/feed\"]]";
        Map<String,String> params = new HashMap<>();
        params.put("id",request.id);
        params.put("jsonrpc",request.jsonrpc);
        params.put("method",request.method);
        params.put("params",request.params);

        //Gson gson = new Gson();
        return params; //gson.toJson(request);

    }

    public String getFeed(boolean v){
        Gson gson = new Gson();
        List<String> list = new ArrayList<>();
        list.add("database_api");

        list.add("get_state");
        List<String> list1 = new ArrayList<String>();
        list1.add("/@hispeedimagins/feed");
        list.add(gson.toJson(list1));
        JsonRpcRequest request = getJsonBasics();
        request.method = "call";
        //request.params = "[\"database_api\",\"get_state\", [\"/@hispeedimagins/feed\"]]";
        request.params = gson.toJson(list);
        *//*Map<String,String> params = new HashMap<>();
        params.put("id",request.id);
        params.put("jsonrpc",request.jsonrpc);
        params.put("method",request.method);
        params.put("params",request.params);*//*


        return gson.toJson(request);

    }*/





}
