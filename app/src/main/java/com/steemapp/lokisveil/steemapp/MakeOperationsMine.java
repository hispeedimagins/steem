package com.steemapp.lokisveil.steemapp;

import com.steemapp.lokisveil.steemapp.DataHolders.FeedArticleDataHolder;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.CondenserUtils;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.AssetSymbolType;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.AccountName;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.Asset;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.CommentPayoutBeneficiaries;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.Permlink;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.SignedTransaction;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.BeneficiaryRouteType;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.ClaimRewardBalanceOperation;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.CommentOperation;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.CommentOptionsExtension;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.CommentOptionsOperation;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.Operation;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.SteemJConfig;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Utilities;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
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
                                           String[] tags, List<FeedArticleDataHolder.beneficiariesDataHolder> benlist) {
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

        //check if beneficiary list is greater than 0
        if (benlist.size() > 0) {
            /*BeneficiaryRouteType beneficiaryRouteType = new BeneficiaryRouteType(SteemJConfig.getSteemJAccount(),
                    SteemJConfig.getInstance().getSteemJWeight());

            BeneficiaryRouteType beneficiaryRouteTypeD = new BeneficiaryRouteType(SteemJConfig.getDev_ACCOUNT(),
                    SteemJConfig.getInstance().getDevWeight());*/


            ArrayList<BeneficiaryRouteType> beneficiaryRouteTypes = new ArrayList<>();
            //beneficiaryRouteTypes.add(beneficiaryRouteTypeD);
            //beneficiaryRouteTypes.add(beneficiaryRouteType);


            // build the class
            for(int i = 0; i < benlist.size(); i++){
                FeedArticleDataHolder.beneficiariesDataHolder item = benlist.get(i);
                if(item != null && item.getUsenow() == 1){
                    //Float val = Float.parseFloat(String.valueOf(item.getPercent()));
                    Integer mid = (item.getPercent() * 100) / 2;
                    Short sh = Short.parseShort(mid.toString());
                    BeneficiaryRouteType brt = new BeneficiaryRouteType(new AccountName(item.getUsername()),
                            sh );

                    beneficiaryRouteTypes.add(brt);
                }
            }



            //add to extra operations if there are some beneficiaries.
            //The should be presorted to be alphabetical
            if(beneficiaryRouteTypes.size() > 0){
                CommentPayoutBeneficiaries commentPayoutBeneficiaries = new CommentPayoutBeneficiaries();
                commentPayoutBeneficiaries.setBeneficiaries(beneficiaryRouteTypes);

                ArrayList<CommentOptionsExtension> commentOptionsExtensions = new ArrayList<>();
                commentOptionsExtensions.add(commentPayoutBeneficiaries);

                commentOptionsOperation = new CommentOptionsOperation(authorThatPublishsThePost, permlink,
                        maxAcceptedPayout, percentSteemDollars, allowVotes, allowCurationRewards, commentOptionsExtensions);
                commentOptionsOperation.makeGsonExtensions();
            }
            else {
                commentOptionsOperation = new CommentOptionsOperation(authorThatPublishsThePost, permlink,
                        maxAcceptedPayout, percentSteemDollars, allowVotes, allowCurationRewards, null);
            }

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
                                          String[] tags,boolean useBeneficiaries)
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

        // Only add if it is true. Cannot be changed for now
        if (useBeneficiaries) {
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
        if (tags == null || tags.length < 1 || tags.length > 10) {
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









    /**
     * Claim all available Steem, SDB and VEST (Steam Power) rewards for the
     * specified account.
     *
     * <b>Attention</b> This method will write data on the blockchain if a
     * reward balance is available to be claimed. As with all writing
     * operations, a private key is required to sign the transaction. See
     * {@link SteemJConfig#getPrivateKeyStorage() PrivateKeyStorage}.
     *
     * @param accountName
     *            The account to claim rewards for.
     * @return The ClaimOperation for reward balances found. This will only have
     *         been broadcast if one of the balances is non-zero.
     * throws SteemCommunicationException
     *             <ul>
     *             <li>If the server was not able to answer the request in the
     *             given time (see
     *             {link eu.bittrade.libs.steemj.configuration.SteemJConfig#setResponseTimeout(int)
     *             setResponseTimeout}).</li>
     *             <li>If there is a connection problem.</li>
     *             </ul>
     * throws SteemResponseException
     *             <ul>
     *             <li>If the SteemJ is unable to transform the JSON response
     *             into a Java object.</li>
     *             <li>If the Server returned an error object.</li>
     *             </ul>
     * throws SteemInvalidTransactionException
     *             If there is a problem while signing the transaction.
     */
//throws SteemCommunicationException, SteemResponseException, SteemInvalidTransactionException
    public ClaimRewardBalanceOperation claimRewards(AccountName accountName,String sbd,String steem,String vesting,String vestingsteemsp) {
        // Get extended account info to determine reward balances
        //ExtendedAccount extendedAccount = this.getAccounts(Lists.newArrayList(accountName)).get(0);
        Asset steemReward = new Asset(steem);
        Asset sbdReward = new Asset(sbd);
        Asset vestingReward = new Asset(vesting);

        // Create claim operation based on available reward balances
        ClaimRewardBalanceOperation claimOperation = new ClaimRewardBalanceOperation(accountName, steemReward,
                sbdReward, vestingReward);

        // Broadcast claim operation if there are any balances available
        /*if (steemReward.getAmount() > 0 || sbdReward.getAmount() > 0 || vestingReward.getAmount() > 0) {
            ArrayList<Operation> operations = new ArrayList<>();
            operations.add(claimOperation);
            DynamicGlobalProperty globalProperties = this.getDynamicGlobalProperties();
            SignedTransaction signedTransaction = new SignedTransaction(globalProperties.getHeadBlockId(), operations,
                    null);
            signedTransaction.sign();
            this.broadcastTransaction(signedTransaction);
        }*/

        return claimOperation;
    }







}
