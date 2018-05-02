package com.steemapp.lokisveil.steemapp;

import com.steemapp.lokisveil.steemapp.SteemBackend.Config.CondenserUtils;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.AssetSymbolType;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.AccountName;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.Asset;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.CommentPayoutBeneficiaries;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.Permlink;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.BeneficiaryRouteType;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.CommentOperation;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.CommentOptionsExtension;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.CommentOptionsOperation;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.Operation;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.SteemJConfig;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Utilities;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Date;
import java.text.SimpleDateFormat;

import static com.steemapp.lokisveil.steemapp.SteemBackend.Config.SteemJConfig.MARKDOWN;

/**
 * Created by boot on 3/4/2018.
 */

public class MakeOperationsMine {

    public String getDateAsIsoString(){
        Date d = new Date();
        System.out.println(d.toString());
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss")
            .format(d);
        formatted = formatted + "Z";
        String wrep = formatted.replaceAll("[^a-zA-Z0-9]+", "");
        return wrep;
    }

    public ArrayList<Operation> createPost(AccountName authorThatPublishsThePost, String title, String content,
                                       String[] tags) {
        if (tags == null || tags.length < 1 || tags.length > 10) {
            throw new InvalidParameterException(SteemJConfig.TAG_ERROR_MESSAGE);
        }
        ArrayList<Operation> operations = new ArrayList<>();


        // Generate the permanent link from the title by replacing all unallowed
        // characters.
        Permlink permlink = new Permlink(Utilities.createPermlinkString(title));
        // On new posts the parentPermlink is the main tag.
        Permlink parentPermlink = new Permlink(tags[0]);
        // One new posts the parentAuthor is empty.
        AccountName parentAuthor = new AccountName("");

        String jsonMetadata = CondenserUtils.generateSteemitMetadata(content, tags,
                SteemJConfig.getSteemJAppName() + "/" + SteemJConfig.getSteemJVersion(), MARKDOWN);

        CommentOperation commentOperation = new CommentOperation(parentAuthor, parentPermlink,
                authorThatPublishsThePost, permlink, title, content, jsonMetadata);

        operations.add(commentOperation);

        boolean allowVotes = true;
        boolean allowCurationRewards = true;
        short percentSteemDollars = (short) 10000;
        Asset maxAcceptedPayout = new Asset(1000000000, AssetSymbolType.SBD);

        CommentOptionsOperation commentOptionsOperation;
        // Only add a BeneficiaryRouteType if it makes sense.
        if (SteemJConfig.getInstance().getSteemJWeight() > 0) {
            BeneficiaryRouteType beneficiaryRouteType = new BeneficiaryRouteType(SteemJConfig.getSteemJAccount(),
                    SteemJConfig.getInstance().getSteemJWeight());

            BeneficiaryRouteType beneficiaryRouteTypeD = new BeneficiaryRouteType(SteemJConfig.getDev_ACCOUNT(),
                    SteemJConfig.getInstance().getDevWeight());

            ArrayList<BeneficiaryRouteType> beneficiaryRouteTypes = new ArrayList<>();
            beneficiaryRouteTypes.add(beneficiaryRouteTypeD);
            beneficiaryRouteTypes.add(beneficiaryRouteType);


            CommentPayoutBeneficiaries commentPayoutBeneficiaries = new CommentPayoutBeneficiaries();
            commentPayoutBeneficiaries.setBeneficiaries(beneficiaryRouteTypes);

            ArrayList<CommentOptionsExtension> commentOptionsExtensions = new ArrayList<>();
            commentOptionsExtensions.add(commentPayoutBeneficiaries);

            commentOptionsOperation = new CommentOptionsOperation(authorThatPublishsThePost, permlink,
                    maxAcceptedPayout, percentSteemDollars, allowVotes, allowCurationRewards, commentOptionsExtensions);
            commentOptionsOperation.makeGsonExtensions();
        } else {
            commentOptionsOperation = new CommentOptionsOperation(authorThatPublishsThePost, permlink,
                    maxAcceptedPayout, percentSteemDollars, allowVotes, allowCurationRewards, null);
        }

        operations.add(commentOptionsOperation);

        /*DynamicGlobalProperty globalProperties = this.getDynamicGlobalProperties();

        SignedTransaction signedTransaction = new SignedTransaction(globalProperties.getHeadBlockId(), operations,
                null);

        signedTransaction.sign();

        this.broadcastTransaction(signedTransaction);*/

        return operations;
    }



    public ArrayList<Operation> createComment(AccountName authorThatPublishsTheComment,
                                          AccountName authorOfThePostOrCommentToReplyTo, Permlink permlinkOfThePostOrCommentToReplyTo, String content,
                                          String[] tags)
             {
        if (tags == null || tags.length < 1 || tags.length > 10) {
            throw new InvalidParameterException(SteemJConfig.TAG_ERROR_MESSAGE);
        }
        ArrayList<Operation> operations = new ArrayList<>();


        String parentperms = permlinkOfThePostOrCommentToReplyTo.getLink().replaceAll("(-\\d{8}[tT]\\d{9}[zZ])", "");
        String perms = "re-" + authorOfThePostOrCommentToReplyTo.getName().replaceAll("\\.", "") + "-"
                + parentperms + "-" + getDateAsIsoString();
        if (perms.length() > 255) {
        // STEEMIT_MAX_PERMLINK_LENGTH
        perms = perms.substring(perms.length() - 255, perms.length());
        }
    // only letters numbers and dashes shall survive
        perms = perms.toLowerCase().replaceAll("[^a-z0-9-]+", "");
        /*Date date = new Date();
        String va = date.*//*Date date = new Date();
        String va = date.*/
        // Generate the permanent link by adding the current timestamp and a
        // UUID.
        /*const timeStr = new Date().toISOString().replace(/[^a-zA-Z0-9]+/g, '');
        parent_permlink = parent_permlink.replace(/(-\d{8}t\d{9}z)/g, '');
        permlink = `re-${parent_author}-${parent_permlink}-${timeStr}`;*/
        /*Permlink permlink = new Permlink("re-" + authorOfThePostOrCommentToReplyTo.getName().replaceAll("\\.", "") + "-"
                + perms + "-" + System.currentTimeMillis() + "t"
                + UUID.randomUUID().toString() + "uid");*/
        Permlink permlink = new Permlink(perms);

        String jsonMetadata = CondenserUtils.generateSteemitMetadata(content, tags,
                SteemJConfig.getSteemJAppName() + "/" + SteemJConfig.getSteemJVersion(), MARKDOWN);

        CommentOperation commentOperation = new CommentOperation(authorOfThePostOrCommentToReplyTo,
                permlinkOfThePostOrCommentToReplyTo, authorThatPublishsTheComment, permlink, "", content, jsonMetadata);

        operations.add(commentOperation);

        boolean allowVotes = true;
        boolean allowCurationRewards = true;
        short percentSteemDollars = (short) 10000;
        Asset maxAcceptedPayout = new Asset(1000000000, AssetSymbolType.SBD);

        CommentOptionsOperation commentOptionsOperation;
        // Only add a BeneficiaryRouteType if it makes sense.
        if (SteemJConfig.getInstance().getSteemJWeight() > 0) {
            BeneficiaryRouteType beneficiaryRouteType = new BeneficiaryRouteType(SteemJConfig.getSteemJAccount(),
                    SteemJConfig.getInstance().getSteemJWeight());

            BeneficiaryRouteType beneficiaryRouteTypeD = new BeneficiaryRouteType(SteemJConfig.getDev_ACCOUNT(),
                    SteemJConfig.getInstance().getDevWeight());

            ArrayList<BeneficiaryRouteType> beneficiaryRouteTypes = new ArrayList<>();
            beneficiaryRouteTypes.add(beneficiaryRouteTypeD);
            beneficiaryRouteTypes.add(beneficiaryRouteType);


            CommentPayoutBeneficiaries commentPayoutBeneficiaries = new CommentPayoutBeneficiaries();
            commentPayoutBeneficiaries.setBeneficiaries(beneficiaryRouteTypes);

            ArrayList<CommentOptionsExtension> commentOptionsExtensions = new ArrayList<>();
            commentOptionsExtensions.add(commentPayoutBeneficiaries);

            commentOptionsOperation = new CommentOptionsOperation(authorThatPublishsTheComment, permlink,
                    maxAcceptedPayout, percentSteemDollars, allowVotes, allowCurationRewards, commentOptionsExtensions);
            commentOptionsOperation.makeGsonExtensions();
        } else {
            commentOptionsOperation = new CommentOptionsOperation(authorThatPublishsTheComment, permlink,
                    maxAcceptedPayout, percentSteemDollars, allowVotes, allowCurationRewards, null);
        }

        operations.add(commentOptionsOperation);

        /*DynamicGlobalProperty globalProperties = this.getDynamicGlobalProperties();

        SignedTransaction signedTransaction = new SignedTransaction(globalProperties.getHeadBlockId(), operations,
                null);

        signedTransaction.sign();

        this.broadcastTransaction(signedTransaction);*/

        return operations;
    }





    public ArrayList<Operation> updatePost(AccountName authorOfThePostToUpdate, Permlink permlinkOfThePostToUpdate,
                                       String title, String content, String[] tags)
             {
        if (tags == null || tags.length < 1 || tags.length > 5) {
            throw new InvalidParameterException(SteemJConfig.TAG_ERROR_MESSAGE);
        }

        ArrayList<Operation> operations = new ArrayList<>();
        AccountName parentAuthor = new AccountName("");
        Permlink parentPermlink = new Permlink(tags[0]);

        String jsonMetadata = CondenserUtils.generateSteemitMetadata(content, tags,
                SteemJConfig.getSteemJAppName() + "/" + SteemJConfig.getSteemJVersion(), MARKDOWN);

        CommentOperation commentOperation = new CommentOperation(parentAuthor, parentPermlink, authorOfThePostToUpdate,
                permlinkOfThePostToUpdate, title, content, jsonMetadata);

        operations.add(commentOperation);

        /*DynamicGlobalProperty globalProperties = this.getDynamicGlobalProperties();

        SignedTransaction signedTransaction = new SignedTransaction(globalProperties.getHeadBlockId(), operations,
                null);

        signedTransaction.sign();

        this.broadcastTransaction(signedTransaction);*/

        return operations;
    }




    public ArrayList<Operation> updateComment(AccountName originalAuthorOfTheCommentToUpdate, AccountName parentAuthor,
                                          Permlink parentPermlink, Permlink originalPermlinkOfTheCommentToUpdate, String content, String[] tags)
            {
        if (tags == null || tags.length < 1 || tags.length > 5) {
            throw new InvalidParameterException(SteemJConfig.TAG_ERROR_MESSAGE);
        }
        ArrayList<Operation> operations = new ArrayList<>();

        String jsonMetadata = CondenserUtils.generateSteemitMetadata(content, tags,
                SteemJConfig.getSteemJAppName() + "/" + SteemJConfig.getSteemJVersion(), MARKDOWN);

        CommentOperation commentOperation = new CommentOperation(parentAuthor, parentPermlink,
                originalAuthorOfTheCommentToUpdate, originalPermlinkOfTheCommentToUpdate, "", content, jsonMetadata);

        operations.add(commentOperation);
       /* DynamicGlobalProperty globalProperties = this.getDynamicGlobalProperties();

        SignedTransaction signedTransaction = new SignedTransaction(globalProperties.getHeadBlockId(), operations,
                null);

        signedTransaction.sign();

        this.broadcastTransaction(signedTransaction);*/

        return operations;
    }
}
