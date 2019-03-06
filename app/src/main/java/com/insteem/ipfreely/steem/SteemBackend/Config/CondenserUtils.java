package com.insteem.ipfreely.steem.SteemBackend.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by boot on 3/4/2018.
 */

public class CondenserUtils {
    private static final String TAGS_FIELD_NAME = "tags";
    private static final String USERS_FIELD_NAME = "users";
    private static final String IMAGES_FIELD_NAME = "image";
    private static final String LINKS_FIELD_NAME = "links";
    private static final String APP_FIELD_NAME = "app";
    private static final String FORMAT_FIELD_NAME = "format";

    /** Add a private constructor to hide the implicit public one. */
    private CondenserUtils() {
    }

    /**
     * Get a list of links that the given <code>content</code> contains.
     *
     * @param content
     *            The content to extract the links from.
     * @return A list of links.
     */
    public static List<String> extractLinksFromContent(String content) {
        List<String> containedUrls = new ArrayList<>();
        Pattern pattern = Pattern.compile(
                "\\b((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])",
                Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(content);

        while (urlMatcher.find()) {
            containedUrls.add(content.substring(urlMatcher.start(0), urlMatcher.end(0)));
        }

        return containedUrls;
    }

    /**
     * Get a list of user names that the given <code>content</code> contains.
     *
     * @param content
     *            The content to extract the user names from.
     * @return A list of user names.
     */
    public static List<String> extractUsersFromContent(String content) {
        List<String> containedUrls = new ArrayList<>();
        Pattern pattern = Pattern.compile("(@{1})([a-z0-9\\.-]{3,16})", Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(content);

        while (urlMatcher.find()) {
            containedUrls.add(content.substring(urlMatcher.start(2), urlMatcher.end(2)));
        }

        return containedUrls;
    }

    /**
     * Use this method to generate the json metadata for new comments or posts
     * required by Condenser.
     *
     * @param content
     *            The body of the comment or post.
     * @param tags
     *            The used tags for this comment or post.
     * @param app
     *            The app name that publishes this comment or post.
     * @param format
     *            The format used by the comment or post.
     * @return The json metadata in its String presentation.
     */
    public static String generateSteemitMetadata(String content, String[] tags, String app, String format) {
        /*ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonMetadata = objectMapper.createObjectNode();
        ArrayNode tagArray = jsonMetadata.arrayNode();*/
        JSONObject jsonMetadata = new JSONObject();

        try{
            JSONArray tagArray = new JSONArray();
            List<String> tagfor = new ArrayList<>();
            for (String tag : tags) {
                tagfor.add("\""+tag+"\"");
                tagArray.put(tag);
            }
            jsonMetadata.put(TAGS_FIELD_NAME, tagArray);

            JSONArray userArray = new JSONArray();
            //ArrayNode userArray = jsonMetadata.arrayNode();
            List<String> users = extractUsersFromContent(content);
            List<String> conusers = new ArrayList<>();
            for (String user : users) {
                conusers.add("\""+user+"\"");
                userArray.put(user);
            }

            if (conusers.size() > 0) {
                jsonMetadata.put(USERS_FIELD_NAME, userArray);
            }


            JSONArray imageArray = new JSONArray();
            JSONArray linksArray = new JSONArray();
        /*ArrayNode imageArray = jsonMetadata.arrayNode();
        ArrayNode linksArray = jsonMetadata.arrayNode();*/
            List<String> links = extractLinksFromContent(content);

            for (String link : links) {
                //https://steemitimages.com/DQmQ9SkdVTyLqwbFy6WvSrKagFAYdWAF6EGSCCVXGp1J3a5/image.png
                if (link.matches("/(https?:\\/\\/.*\\.(?:png|jpg))/i") || link.endsWith(".png") || link.endsWith("jpg")) {
                    imageArray.put(link);
                } else {
                    linksArray.put(link);
                }
            }

            if (imageArray.length() > 0) {
                jsonMetadata.put(IMAGES_FIELD_NAME, imageArray);
            }

            if (linksArray.length() > 0) {
                jsonMetadata.put(LINKS_FIELD_NAME, linksArray);
            }

            jsonMetadata.put(APP_FIELD_NAME, app);
            jsonMetadata.put(FORMAT_FIELD_NAME, format);
        }
        catch (JSONException ex){

        }



        return jsonMetadata.toString();
    }



}
