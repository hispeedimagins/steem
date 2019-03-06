package com.insteem.ipfreely.steem.HelperClasses;

import android.content.Context;

import com.insteem.ipfreely.steem.CentralConstants;
import com.insteem.ipfreely.steem.CentralConstantsOfSteem;
import com.insteem.ipfreely.steem.Databases.FollowersDatabase;
import com.insteem.ipfreely.steem.Databases.FollowingDatabase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

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
     * converts it to  yyyy-MM-dd'T'hh:mm:ssZ and return gmt date
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
        float subff = votingpower +((((float)((System.currentTimeMillis() - lastvotetimes) / 1000)) / CentralConstants.FiveDaysInSeconds) * 10000);
        if(subff > CentralConstants.SteemFullVote){
            return CentralConstants.SteemFullVote;
        } else if(0 > subff){
            return votingpower;
        }
        return (long) subff;
        //return votingpower;
    }

    /**
     * Function adds +0000 to the steem date to make it compatible yyyy-MM-dd'T'HH:mm:ssZ
     * @param date format yyyy-MM-dd'T'HH:mm:ss
     * @return Date
     */
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

    /**
     * converts current date to mirror steem date format
     * @param date java date
     * @return String
     */
    public static String FormatDateAsSteemString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    public static String FormatVotingValueToSBD(Double sbshare){
        return String.format("%.3f",sbshare)  + " SBD";
    }

    public static Double CalculateVotingValueRshares(String rshares){

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

        return String.valueOf((CalculateUsedVotingPower(votingpower,voteweight) * CalculateEffectiveVestsUser())/CentralConstants.SteemFullVote);
    }

    public static String CalculateVoteRshares(int votingpower,int voteweight,String vesh,String delegves,String receves){


        return String.valueOf((CalculateUsedVotingPower(votingpower,voteweight) * CalculateEffectiveVestsUser(vesh,delegves,receves))/CentralConstants.SteemFullVote);
    }

    public static String FormatVoteAsStringSbd(Double val){
        return String.format("%.3f",val)  + " SBD";
    }


    public static boolean CheckFollowing(String author, Context context){
        FollowingDatabase followingDatbase = new FollowingDatabase(context);
        return followingDatbase.simpleSearch(author);
    }

    public static boolean CheckFollowers(String author, Context context){
        FollowersDatabase followingDatbase = new FollowersDatabase(context);
        return followingDatbase.simpleSearch(author);
    }

}
