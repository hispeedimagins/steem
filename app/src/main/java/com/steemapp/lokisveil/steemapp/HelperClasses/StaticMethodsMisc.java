package com.steemapp.lokisveil.steemapp.HelperClasses;

import android.content.Context;

import com.android.volley.Response;
import com.steemapp.lokisveil.steemapp.CentralConstants;
import com.steemapp.lokisveil.steemapp.CentralConstantsOfSteem;
import com.steemapp.lokisveil.steemapp.Databases.FollowersDatabase;
import com.steemapp.lokisveil.steemapp.Databases.FollowingDatabase;
import com.steemapp.lokisveil.steemapp.jsonclasses.prof;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.toIntExact;
/**
 * Created by boot on 3/10/2018.
 */

public class StaticMethodsMisc {
    public static String CorrectMarkDown(String value,List<String> images){
        if(images != null){
            for(int i = 0; i < images.size(); i++){
                String check = "("+images.get(i)+")";
                String htmlCheck = "src=\""+images.get(i)+"\"";
                String[] spiliatzerozeroarr = images.get(i).split("/0x0/");
                String spiliatzerozero = null;
                if(spiliatzerozeroarr != null && spiliatzerozeroarr.length == 2){
                    spiliatzerozero =  spiliatzerozeroarr[1];
                }
                if(value != null && spiliatzerozero == null && !(value.contains(htmlCheck)) && !(value.contains(check))){
                    String[] spl = images.get(i).split("/");
                    String reps = "!["+spl[spl.length - 1 ]+"]"+check;
                    value = value.replace(images.get(i),reps);
                }
                else if(value != null && spiliatzerozero != null && !value.contains("("+spiliatzerozero+")")){
                    String[] spl = spiliatzerozero.split("/");
                    String reps = "!["+spl[spl.length - 1 ]+"]"+"("+spiliatzerozero+")";
                    value = value.replace(spiliatzerozero,reps);
                }
            }

            return value;
        }

        return value;
    }


    /*public static String CorrectMarkDownAfter(String value,List<String> images){
        if(images != null){
            for(int i = 0; i < images.size(); i++){
                String check = "("+images.get(i)+")";
                String htmlCheck = "src=\""+images.get(i)+"\"";
                String[] spiliatzerozeroarr = images.get(i).split("/0x0/");
                String spiliatzerozero = null;
                if(spiliatzerozeroarr != null && spiliatzerozeroarr.length == 2){
                    spiliatzerozero =  spiliatzerozeroarr[1];
                }
                if(value != null && spiliatzerozero == null && !(value.contains(htmlCheck)) && !(value.contains(check))){
                    String reps = "<img src=\""+images.get(i)+"\"/>";
                    value = value.replace(images.get(i),reps);
                }
                else if(value != null && spiliatzerozero != null && !value.contains("("+spiliatzerozero+")")){
                    //String reps = "![name]"+"("+spiliatzerozero+")";
                    String reps = "<img src=\""+spiliatzerozero+"\"/>";
                    value = value.replace(spiliatzerozero,reps);
                }
            }

            return value;
        }

        return value;
    }*/

    public static String CorrectNewLine(String value){
        return value.replaceAll("[\\r\\n]|\\r\\n","<br/>");
    }

    public static String CorrectBeforeMainLinks(String value,List<String> links){

        //String newtegex = "\\[(\\w+|[^\\\\]*\\.(\\w+))\\]\\((https?|ftp|file):\\/\\/[-a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#\\/%=~_|]\\)";
        //String newtegex = "\\[(\\w+|[^\\\\]*\\.(\\w+))\\]\\(\\)";
        //String regexmatch = "!\\[[\\w\\W]]\\([\\w\\W]\\)";
        //value = value.replaceAll(newtegex,"<img([\\w\\W]+?)/>");
        /*while (urlMatcher.find()) {
             value = value.replace(value.substring(urlMatcher.start(0), urlMatcher.end(0)),"<img([\\w\\W]+?)/>"); //containedUrls.add(content.substring(urlMatcher.start(0), urlMatcher.end(0)));
        }*/
        if(links != null){
            /*Pattern pattern = Pattern.compile(
                        "\\[(\\w+|[^\\\\]*\\.(\\w+)|\\w+ \\w+)\\]\\(()\\)",
                        Pattern.CASE_INSENSITIVE);
            Matcher urlMatcher = pattern.matcher(value);
            while (urlMatcher.find()) {
                String lin = value.substring(urlMatcher.start(3), urlMatcher.end(3));
                if( !lin.endsWith(".png") && !lin.endsWith("jpg")){
                    String reps = "<a  class=\"links\" href=\"steemer://"+links.get(i)+")\" >"+check.split("()")[0]+"</a>";

                    value = value.replace(value.substring(urlMatcher.start(0), urlMatcher.end(0)),reps); //containedUrls.add(content.substring(urlMatcher.start(0), urlMatcher.end(0)));
                }


            }*/
            for(int i = 0; i < links.size(); i++){
                //String perms = "(-\\d{8}t\\d{9}z)";
                //String check = "[^!?]\\[([-a-zA-Z0-9+&@# ]*)\\]\\(("+links.get(i).replace("/","\\/")+")\\)";
                String check = "[^!]\\[(.*)]\\((.*)\\)";
                //String check = "[^!?]\\[([-a-zA-Z0-9+&@#\\[\\]_\\-.!:,' ]*| |.)\\]\\(("+links.get(i).replace("/","\\/")+")\\)";

                /*Pattern pattern = Pattern.compile(
                        "\\[(\\w+|[^\\\\]*\\.(\\w+)|\\w+ \\w+)\\]\\(("+links.get(i).replace("/","\\/")+")\\)",
                        Pattern.CASE_INSENSITIVE);*/
                /*Pattern pattern = Pattern.compile(
                        "\\[(\\w+|[^\\\\]*\\.(\\w+)|\\w+ \\w+)\\]\\(("+links.get(i).replace("/","\\/")+")\\)",
                        Pattern.CASE_INSENSITIVE);
                Matcher urlMatcher = pattern.matcher(value);

                while (urlMatcher.find()) {
                    String lin = value.substring(urlMatcher.start(3), urlMatcher.end(3));
                    if( !lin.endsWith(".png") && !lin.endsWith("jpg")){
                        String reps = "<a  class=\"links\" href=\"steemer://"+links.get(i)+")\" >"+check.split("()")[0]+"</a>";

                        value = value.replace(value.substring(urlMatcher.start(0), urlMatcher.end(0)),reps); //containedUrls.add(content.substring(urlMatcher.start(0), urlMatcher.end(0)));
                    }


                }*/

                //String check = "@" + users.get(i);
                String reps = "<a  class=\"links\" href=\"steemer://"+links.get(i)+")\" >"+check.split("()")[0]+"</a>";
                value = value.replaceAll(check,reps);
                /*if(value != null && value.matches(check)  ){
                    String reps = "<a  class=\"links\" href=\"steemer://"+links.get(i)+")\" >"+check.split("()")[0]+"</a>";
                    //value = value.replace(check,reps);
                }*/
            }
            return value;
        }
        return value;

       // return value;
    }

    /*public static String CorrectMarkDownUsers(String value){

        //match pattern from steemit condenser
        *//*Pattern pattern = Pattern.compile(
                "/(^|[^a-zA-Z0-9_!#$%&*@＠/]|(^|[^a-zA-Z0-9_+~.-/#]))[@＠]([a-z][-.a-z\\d]+[a-z\\d])/gi",
                Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(value);
        while (urlMatcher.find()) {
            String name = value.substring(urlMatcher.start(3), urlMatcher.end(3));
            String reps = " <a class=\"mylink\" href=\"steemer://@"+name+"\" >"+name+"</a> ";
            value = value.replace(name,reps);

        }*//*

        Pattern pattern = Pattern.compile("(@{1})([a-z0-9.-]{3,16})", Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(value);
        int fin = 0;
        while (urlMatcher.find(fin)) {
            int end = urlMatcher.end(0);
            fin = end;
            String name = value.substring(urlMatcher.start(0), end);
            String reps = " <a class=\"mylink\" href=\"steemer://@"+name+"\" >"+name+"</a> ";
            value = value.replace(name,reps);
            //containedUrls.add(content.substring(urlMatcher.start(2), urlMatcher.end(2)));
        }

        return value;
    }*/

    public static String CorrectMarkDownUsers(String value,List<String> users){
        if(users != null){
            for(int i = 0; i < users.size(); i++){
                //String reps = "<a  class=\"users\" href=\"steemer://@"+users.get(i)+"\" >"+"@"+users.get(i)+"</a>";
                /*Pattern pattern = Pattern.compile(
                        "[^/|^!]("+"@" + users.get(i)+")[^/|]",
                        Pattern.CASE_INSENSITIVE);
            Matcher urlMatcher = pattern.matcher(value);
            while (urlMatcher.find()) {


            }*/
                //[^/|^!]@insideoutlet[^/|^!]
                String check = "[^/|^!]("+"@" + users.get(i)+")[^/|]";
                String remcheck = "[>]("+"@" + users.get(i)+")[<]";
                String remcheckL = "[>]("+"@" + users.get(i)+")";
                String remcheckR = "("+"@" + users.get(i)+")[<]";
                //String reps = " <a onClick=\"UserClickedK(\'"+users.get(i)+"\')\"  class=\"users\" href=\"steemer://@"+users.get(i)+"\" >"+"@"+users.get(i)+"</a> ";
                String reps = " <a class=\"mylink\" href=\"steemer://@"+users.get(i)+"\" >"+"@"+users.get(i)+"</a> ";
                //String reps = " <a onClick=\"UserClickedK(\'"+users.get(i)+"\')\"  class=\"users\" href=\"#\" >"+"@"+users.get(i)+"</a> ";
                //String reps = " <a onClick=\"UserClickedK(\'"+users.get(i)+"\')\"  class=\"users\" href=\"#\" >"+"@"+users.get(i)+"</a> ";
                //String reps = " <a class=\"mylink\" href=\""+users.get(i)+"\" >"+"@"+users.get(i)+"</a> ";
                //String reps = " <a onClick=\""+users.get(i)+"\"  class=\"users\" href=\"#\" >"+"@"+users.get(i)+"</a> ";
                value = value.replaceAll(remcheck, "> @"+users.get(i)+" <");
                value = value.replaceAll(remcheckL, "> @"+users.get(i));
                value = value.replaceAll(remcheckR, "@"+users.get(i)+" <");
                value = value.replaceAll(check,reps);

                /*if(value != null && value.contains(check)  ){
                    String reps = "<a  class=\"users\" href=\"steemer://@"+users.get(i)+"\" >"+check+"</a>";
                    value = value.replace(check,reps);
                }*/
            }
            return value;
        }
        return value;
    }

    public static String CorrectBr(String value){
        return value.replaceAll("<br>","<br/>");
    }

    public static String CorrectAfterMainImages(String value){
        /*Pattern pattern = Pattern.compile(
                "\\b(!\\[\\w \\]\\((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]\\))",
                Pattern.CASE_INSENSITIVE);*/
        //pattern.pattern()
        //Matcher urlMatcher = pattern.matcher(value);
       // String regexmatchm = "<img([\\w\\W]+?)/>";
        //String regexmatch = "\\b(!\\[\\w]\\((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]\\))";
        //String newtegex = "!\\[(\\w+|[^\\\\]*\\.(\\w+))\\]\\((https?|ftp|file):\\/\\/[-a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#\\/%=~_|]\\)";
        //String newtegex = "!\\[(\\w+|\\w*\\.(\\w+))\\]\\((https?|ftp|file):\\/\\/[-a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#\\/%=~_|]\\)";
        String newtegex = "[!]\\[(.*)\\]\\((.*)\\)";
        //String newtegex = "[!]\\[([-a-zA-Z0-9+&@#\\[\\]_. ]*(.\\w+)| |)]\\((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#\\/%=~_|]\\)";
        //String regexmatch = "!\\[[\\w\\W]]\\([\\w\\W]\\)";
        String regexmatch = "<img([\\w\\W]+?)/>";
        value = value.replaceAll(newtegex,regexmatch);
        /*while (urlMatcher.find()) {
             value = value.replace(value.substring(urlMatcher.start(0), urlMatcher.end(0)),"<img([\\w\\W]+?)/>"); //containedUrls.add(content.substring(urlMatcher.start(0), urlMatcher.end(0)));
        }*/

        return value;
    }

    public static int GetImageIndex(String value,List<String> images){
        int i = 0;
        for(String image : images){
            String[] spiliatzerozeroarr = images.get(i).split("/0x0/");
            String spiliatzerozero = null;
            if(spiliatzerozeroarr != null && spiliatzerozeroarr.length == 2){
                spiliatzerozero =  spiliatzerozeroarr[1];
            }
            if(value.contains(image)){
                return i;
            }
            else if(spiliatzerozero != null && value.contains(spiliatzerozero)){
                return i;
            }
            i++;
        }
        return i;
    }

    public static List<Object> ConvertTextToList(String value,List<String> images){

        String regexmatch = "<img([\\w\\W]+?)/>";
        value = value.replaceAll("<img([\\w\\W]+?)>",regexmatch);
        String[] splitstring = value.split(regexmatch);

        int i = 0;
        //StringBuilder builder = StringBuilder()
        List<Object> objects = new ArrayList<>();
        if(splitstring != null){

            for (String x : splitstring){
                objects.add(x);
                if(images != null && images.size() > i){
                    objects.add(i);
                    i++;
                }

                /*if(!x.startsWith("<img" )){
                    objects.add(x);
                }
                else {
                    objects.add(GetImageIndex(x,images));
                    //i++;
                }*/
            }
        }
        return objects;
    }






    /**
     * made respscore common, this returns a string
     * @param reputation string
     * @return string
     */
    public static String CalculateRepScore(String reputation){
        /*Double resp = Double.valueOf(reputation);
        Double replog = Math.log10(resp);
        Double subni = replog - 9;
        Double mulni = subni * 9;
        Double addtf = mulni + 25;
        return String.valueOf( addtf.intValue());*/
        return String.valueOf(CalculateRepScoreRetInt(reputation));
    }

    /**
     * calculates the rep score and returns an int
     * @param reputation string
     * @return int
     */
    public static int CalculateRepScoreRetInt(String reputation){
        Double resp = Double.valueOf(reputation);
        Double replog = Math.log10(resp);
        Double subni = replog - 9;
        Double mulni = subni * 9;
        Double addtf = mulni + 25;
        return addtf.intValue();
    }

    /**
     *
     * @param date string yyyy-MM-dd'T'hh:mm:ss
     * @return java date
     */
    public static Date ConvertSteemDateToDate(String date){
        return FormatDateGmt(date);
    }

    public static Date ConvertSteemDateToDate(Long date){
        return (new Date(date) );
    }

    public static long CalculateVotingPower(int votingpower,long lastvotetimes){
        //long sys = System.currentTimeMillis();
        //float bl = lastvotetimes/1000;
        //float dybl = (dt/1000);
        //float sysblbl = (sys/1000);
        //float shi = dybl - bl;
        //float bshi = sysblbl - bl;
        //float sub = ((float)((System.currentTimeMillis() - lastvotetimes) / 1000));
        //float subff = votingpower +((((float)((System.currentTimeMillis() - lastvotetimes) / 1000)) / CentralConstants.FiveDaysInSeconds) * 10000);
        //float subf = sub / CentralConstants.FiveDaysInSeconds;
        //float subm = subf * 10000;
        //float subff = votingpower + subf;
        float subff = votingpower +((((float)((System.currentTimeMillis() - lastvotetimes) / 1000)) / CentralConstants.FiveDaysInSeconds) * 10000);
        if(subff > CentralConstants.SteemFullVote){
            return CentralConstants.SteemFullVote;
        } else if(0 > subff){
            return votingpower;
        }
        return (long) subff;
        //return votingpower;
    }

    public static Date FormatDateGmt(String date){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            return sdf.parse(date+"+0000");
        } catch (ParseException pa){
            String ecs = pa.getMessage();
            throw new RuntimeException(pa.getMessage());
           // int i = 0;
        }
        //return new Date();
    }

    public static String FormatDateAsSteemString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    public static String FormatVotingValueToSBD(Double sbshare){
        return String.format("%.3f",sbshare)  + " SBD";
    }

    public static Double CalculateVotingValueRshares(String rshares){
        //CentralConstantsOfSteem ins = CentralConstantsOfSteem.getInstance();
        /*long rshare = Long.valueOf(rshares);
        long recentclaims = Long.valueOf(ins.getResultfund().getRecentClaims());
        long rsharedivclaim = rshare/recentclaims;
        Double rewardbalance = Double.valueOf(ins.getResultfund().getRewardBalance().replace("STEEM",""));
        Double shareSteem = rsharedivclaim * rewardbalance;
        return shareSteem;*/
        return CalculateVotingValueRshares(Double.valueOf(rshares));
    }

    public static Double CalculateVotingValueRshares(Double rshare){
        CentralConstantsOfSteem ins = CentralConstantsOfSteem.getInstance();
        //long rshare = Long.valueOf(rshares);
        if(ins != null && ins.getResultfund() != null && ins.getResultfund().getRecentClaims() != null){
            long recentclaims = Long.valueOf(ins.getResultfund().getRecentClaims());
            Double rsharedivclaim = rshare/recentclaims;
            Double rewardbalance = Double.valueOf(ins.getResultfund().getRewardBalance().replace("STEEM",""));
            Double shareSteem = rsharedivclaim * rewardbalance;
            return shareSteem;
        }
       return rshare;
    }

    public static Double VotingValueSteemToSd(double share){
        return Double.valueOf(CentralConstantsOfSteem.getInstance().getCurrentMedianHistory().getBase().replace("SBD","")) * share;
    }

    public static int CalculateWeightedVotingPower(int votingpower,int voteweight){
        return (voteweight * votingpower)/CentralConstants.SteemFullVote;
    }

    public static Double CalculateEffectiveVestsUser(){
        CentralConstantsOfSteem ins = CentralConstantsOfSteem.getInstance();
        /*Double vesting = Double.valueOf(ins.getProfile().getVestingShares().replace("VESTS",""));
        Double rvesting = Double.valueOf(ins.getProfile().getReceivedVestingShares().replace("VESTS",""));
        Double dvesting = Double.valueOf(ins.getProfile().getDelegatedVestingShares().replace("VESTS",""));

        return (vesting + rvesting - dvesting) * 1000000;*/
        return CalculateEffectiveVestsUser(ins.getProfile().getVestingShares(),ins.getProfile().getDelegatedVestingShares(),ins.getProfile().getReceivedVestingShares());



    }

    public static Double CalculateEffectiveVestsUser(String vesh,String deleves,String receves){
        CentralConstantsOfSteem ins = CentralConstantsOfSteem.getInstance();
        Double vesting = Double.valueOf(vesh.replace("VESTS",""));
        Double rvesting = Double.valueOf(receves.replace("VESTS",""));
        Double dvesting = Double.valueOf(deleves.replace("VESTS",""));

        return (vesting + rvesting - dvesting) * 1000000;



    }

    public static int CalculateUsedVotingPower(int votingpower,int voteweight){
        CentralConstantsOfSteem ins = CentralConstantsOfSteem.getInstance();
        int maxdenom = ins.getDynamicglobalprops().getVotePowerReserveRate() * 5;
        int wvp = CalculateWeightedVotingPower(votingpower,voteweight);
        int usedvotingpower = (wvp + maxdenom - 1) / maxdenom;
        return usedvotingpower;
    }

    public static String CalculateVoteRshares(int votingpower,int voteweight){
        /*CentralConstantsOfSteem ins = CentralConstantsOfSteem.getInstance();
        int maxdenom = ins.getDynamicglobalprops().getVotePowerReserveRate() * 5;
        int wvp = CalculateWeightedVotingPower(votingpower,voteweight);
        int usedvotingpower = (wvp + maxdenom - 1) / maxdenom;*/
        //return String.valueOf((usedvotingpower * CalculateEffectiveVestsUser())/CentralConstants.SteemFullVote);
        return String.valueOf((CalculateUsedVotingPower(votingpower,voteweight) * CalculateEffectiveVestsUser())/CentralConstants.SteemFullVote);
    }

    public static String CalculateVoteRshares(int votingpower,int voteweight,String vesh,String delegves,String receves){


        return String.valueOf((CalculateUsedVotingPower(votingpower,voteweight) * CalculateEffectiveVestsUser(vesh,delegves,receves))/CentralConstants.SteemFullVote);
    }

    public static String FormatVoteAsStringSbd(Double val){
        return String.format("%.3f",val)  + " SBD";
    }



    /*public static boolean MakeFollowRequests (prof.FollowCount followCount, Context context, Response.Listener<JSONObject> listener){
        //int followers = followCount.getResult().getFollowerCount();
        int following = followCount.getResult().getFollowingCount();

        *//*GeneralRequestsFeedIntoConstants con = new GeneralRequestsFeedIntoConstants(context);
        int sfa = 0;
        int tfa = followers + 1000;
        while (tfa > 1000){

            con.GetFollowers(followCount.getResult().getAccount(),sfa,listener);
            sfa += 1000;
            tfa -= 1000;
        }*//*


        GeneralRequestsFeedIntoConstants cons = new GeneralRequestsFeedIntoConstants(context);
        int sfas = 0;
        int tfas = following + 100;
        while (tfas > 100){

            cons.GetFollowing(followCount.getResult().getAccount(),sfas,listener);
            sfas += 100;
            tfas -= 100;
        }

        return true;
    }

    public static boolean MakeFollowRequestsFollowers (prof.FollowCount followCount, Context context, Response.Listener<JSONObject> listener){
        int followers = followCount.getResult().getFollowerCount();
        //int following = followCount.getResult().getFollowingCount();

        GeneralRequestsFeedIntoConstants con = new GeneralRequestsFeedIntoConstants(context);
        int sfa = 0;
        int tfa = followers + 1000;
        while (tfa > 1000){

            con.GetFollowers(followCount.getResult().getAccount(),sfa,listener);
            sfa += 1000;
            tfa -= 1000;
        }


        *//*GeneralRequestsFeedIntoConstants cons = new GeneralRequestsFeedIntoConstants(context);
        int sfas = 0;
        int tfas = following + 100;
        while (tfas > 100){

            cons.GetFollowing(followCount.getResult().getAccount(),sfas,listener);
            sfas += 100;
            tfas -= 100;
        }*//*

        return true;
    }*/


    public static boolean CheckFollowing(String author, Context context){
        FollowingDatabase followingDatbase = new FollowingDatabase(context);
        return followingDatbase.simpleSearch(author);
    }

    public static boolean CheckFollowers(String author, Context context){
        FollowersDatabase followingDatbase = new FollowersDatabase(context);
        return followingDatbase.simpleSearch(author);
    }


}
