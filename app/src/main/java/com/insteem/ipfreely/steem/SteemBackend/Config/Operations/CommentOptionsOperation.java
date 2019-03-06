package com.insteem.ipfreely.steem.SteemBackend.Config.Operations;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.insteem.ipfreely.steem.SteemBackend.Config.Enums.OperationType;
import com.insteem.ipfreely.steem.SteemBackend.Config.Enums.PrivateKeyType;
import com.insteem.ipfreely.steem.SteemBackend.Config.Enums.ValidationType;
import com.insteem.ipfreely.steem.SteemBackend.Config.Interfaces.SignatureObject;
import com.insteem.ipfreely.steem.SteemBackend.Config.Models.AccountName;
import com.insteem.ipfreely.steem.SteemBackend.Config.Models.Asset;
import com.insteem.ipfreely.steem.SteemBackend.Config.Models.CommentPayoutBeneficiaries;
import com.insteem.ipfreely.steem.SteemBackend.Config.Models.Permlink;
import com.insteem.ipfreely.steem.SteemBackend.Config.SteemJConfig;
import com.insteem.ipfreely.steem.SteemBackend.Config.Utilities;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by boot on 3/4/2018.
 */

public class CommentOptionsOperation extends Operation {
    //@JsonProperty("author")

    @Expose
    @SerializedName("author")
    private String Author;

    private AccountName author;
  //  @JsonProperty("permlink")
  @Expose
  @SerializedName("permlink")
  private String Permlink;

  private Permlink permlink;
  //  @JsonProperty("max_accepted_payout")
  @Expose
  @SerializedName("max_accepted_payout")
  private String MaxAcceptedPayout;

  private Asset maxAcceptedPayout;
  //  @JsonProperty("percent_steem_dollars")
  @Expose
  @SerializedName("percent_steem_dollars")
    private Short percentSteemDollars;
   // @JsonProperty("allow_votes")
   @Expose
   @SerializedName("allow_votes")
    private boolean allowVotes;
   // @JsonProperty("allow_curation_rewards")
   @Expose
   @SerializedName("allow_curation_rewards")
    private boolean allowCurationRewards;
    // Original type is "comment_options_extensions_type" which is a list of
    // "comment_options_extension".
   // @JsonProperty("extensions")
    @Expose
    @SerializedName("extensions")
    private List<Object> Extensions;

    //@Expose
    private List<CommentOptionsExtension> extensions;

    /**
     * Create a new comment options operation. Use this operation to define
     * additional options for an already existing post or a comment.
     *
     * Authors of posts may not want all of the benefits that come from creating
     * a post. This operation allows authors to update properties associated
     * with their post.
     *
     * The <code>maxAcceptedPayout</code> may be decreased, but never increased.
     * The <code>percentSteemDollars</code> may be decreased, but never
     * increased
     *
     * @param author
     *            The author of the post this operation should apply to (see
     *            {@link #setAuthor(AccountName)}).
     * @param permlink
     *            The permlink of the post this operation should apply to (see
     *            {@link #setPermlink(Permlink)}).
     * @param maxAcceptedPayout
     *            The maximal excepted payout (see
     *            {@link #setMaxAcceptedPayout(Asset)}).
     * @param percentSteemDollars
     *            The percent of Steem dollars the reward should be paid in (see
     *            {@link #setPercentSteemDollars(short)}).
     * @param allowVotes
     *            Define if votes are allowed (see
     *            {@link #setAllowVotes(boolean)}).
     * @param allowCurationRewards
     *            Define if a curation reward should be paid (see
     *            {@link #setAllowCurationRewards(boolean)}).
     * @param extensions
     *            Additional extensions to set (see
     *            {@link #setExtensions(List)}.
     * @throws InvalidParameterException
     *             If one of the parameters does not fulfill the requirements.
     */
    //@JsonCreator
    public CommentOptionsOperation( AccountName author,
                                    Permlink permlink,  Asset maxAcceptedPayout,
                                    Short percentSteemDollars,
                                    boolean allowVotes,
                                  boolean allowCurationRewards,
                                    List<CommentOptionsExtension> extensions) {
        super(false);

        this.setAuthor(author);
        this.setPermlink(permlink);
        this.setMaxAcceptedPayout(maxAcceptedPayout);
        this.setPercentSteemDollars(percentSteemDollars);
        this.setAllowVotes(allowVotes);
        this.setAllowCurationRewards(allowCurationRewards);
        this.setExtensions(extensions);

        this.Author = author.getName();
        this.Permlink = permlink.getLink();
        this.MaxAcceptedPayout = maxAcceptedPayout.getAssetString();

        this.Extensions = new ArrayList<>();

    }

    /**
     * Like
     * {@link #CommentOptionsOperation(AccountName, Permlink, Asset, Short, boolean, boolean, List)},
     * but sets the maximum payout to the highest possible value, allows votes
     * and curation rewards.
     *
     * @param author
     *            The author of the post this operation should apply to (see
     *            {@link #setAuthor(AccountName)}).
     * @param permlink
     *            The permlink of the post this operation should apply to (see
     *            {@link #setPermlink(Permlink)}).
     * @param percentSteemDollars
     *            The percent of Steem dollars the reward should be paid in (see
     *            {@link #setPercentSteemDollars(short)}).
     * @param extensions
     *            Additional extensions to set (see
     *            {@link #setExtensions(List)}.
     * @throws InvalidParameterException
     *             If one of the parameters does not fulfill the requirements.
     */
    public CommentOptionsOperation(AccountName author, Permlink permlink, short percentSteemDollars,
                                   List<CommentOptionsExtension> extensions) {
        this(author, permlink, new Asset(1000000000, SteemJConfig.getInstance().getDollarSymbol()), percentSteemDollars,
                true, true, extensions);
    }

    /**
     * Like
     * {@link #CommentOptionsOperation(AccountName, Permlink, short, List)}, but
     * does not require extensions.
     *
     * @param author
     *            The author of the post this operation should apply to (see
     *            {@link #setAuthor(AccountName)}).
     * @param permlink
     *            The permlink of the post this operation should apply to (see
     *            {@link #setPermlink(Permlink)}).
     * @param percentSteemDollars
     *            The percent of Steem dollars the reward should be paid in (see
     *            {@link #setPercentSteemDollars(short)}).
     * @throws InvalidParameterException
     *             If one of the parameters does not fulfill the requirements.
     */
    public CommentOptionsOperation(AccountName author, Permlink permlink, Short percentSteemDollars) {
        this(author, permlink, percentSteemDollars, new ArrayList<CommentOptionsExtension>());
    }



    /**
     * Like
     * {@link #CommentOptionsOperation(AccountName, Permlink, Asset, Short, boolean, boolean, List)},
     * but sets the maximum payout to the highest possible value, allows votes,
     * allows curation rewards and sets the <code>percentSteemDollars</code> to
     * 100.0%.
     *
     * @param author
     *            The author of the post this operation should apply to (see
     *            {@link #setAuthor(AccountName)}).
     * @param permlink
     *            The permlink of the post this operation should apply to (see
     *            {@link #setPermlink(Permlink)}).
     * @param extensions
     *            Additional extensions to set (see
     *            {@link #setExtensions(List)}.
     * @throws InvalidParameterException
     *             If one of the parameters does not fulfill the requirements.
     */
    public CommentOptionsOperation(AccountName author, Permlink permlink, List<CommentOptionsExtension> extensions) {
        this(author, permlink, (short) 10000, extensions);
    }

    /**
     * Like {@link #CommentOptionsOperation(AccountName, Permlink, List)}, but
     * does not require extensions.
     *
     * @param author
     *            The author of the post this operation should apply to (see
     *            {@link #setAuthor(AccountName)}).
     * @param permlink
     *            The permlink of the post this operation should apply to (see
     *            {@link #setPermlink(Permlink)}).
     * @throws InvalidParameterException
     *             If one of the parameters does not fulfill the requirements.
     */
    public CommentOptionsOperation(AccountName author, Permlink permlink) {
        this(author, permlink, new ArrayList<CommentOptionsExtension>());
    }



    public void makeGsonExtensions(){

        //this.Extensions.add(CommentOptionsExtensionsType.COMMENT_PAYOUT_BENEFICIARIES);
        List<Object> objects = new ArrayList<>();
        objects.add(0);
        //List<BeneficiaryRouteType> ben = new ArrayList<>();
        for(int i = 0; i < extensions.size(); i++){
            CommentOptionsExtension item = extensions.get(i);
            CommentPayoutBeneficiaries items = (CommentPayoutBeneficiaries)item;
            //List<BeneficiaryRouteType> ben = ((CommentPayoutBeneficiaries) item).getBeneficiaries();
            objects.add(items);
        }
        //objects.add(((List<CommentPayoutBeneficiaries>)extensions.get(0)).get(0).getBeneficiaries());
        this.Extensions.add(objects);
        /*this.Extensions.add(0);
        this.Extensions.add(extensions);*/
    }
    /**
     * Get the author of the comment.
     *
     * @return The account name of the author.
     */
    public AccountName getAuthor() {
        return author;
    }

    /**
     * Set the author of the comment. <b>Notice:</b> The private posting key of
     * this account needs to be stored in the key storage.
     *
     * @param author
     *            The account name of the author.
     * @throws InvalidParameterException
     *             If the <code>author</code> is null.
     */
    public void setAuthor(AccountName author) {
        this.author = setIfNotNull(author, "The author can't be null.");
    }

    /**
     * Get the permanent link of this comment.
     *
     * @return The permanent link.
     */
    public Permlink getPermlink() {
        return permlink;
    }

    /**
     * Set the permanent link of this comment.
     *
     * @param permlink
     *            The permanent link.
     * @throws InvalidParameterException
     *             If the <code>permlink</code> is null.
     */
    public void setPermlink(Permlink permlink) {
        this.permlink = setIfNotNull(permlink, "The permanent link can't be null.");
    }

    /**
     * Get the SBD value of the maximum payout this post will receive.
     *
     * @return The SBD value of the maximum payout this post will receive.
     */
    public Asset getMaxAcceptedPayout() {
        return maxAcceptedPayout;
    }

    /**
     * Set the SBD value of the maximum payout this post will receive.
     *
     * @param maxAcceptedPayout
     *            The SBD value of the maximum payout this post will receive.
     * @throws InvalidParameterException
     *             If the <code>maxAcceptedPayout</code> is null, has a
     *             different symbol than SBD or the amount is less than 0.
     */
    public void setMaxAcceptedPayout(Asset maxAcceptedPayout) {
        this.maxAcceptedPayout = setIfNotNull(maxAcceptedPayout, "The maximal accepted payout can't be null.");
    }

    /**
     * Get the information if votes have been allowed on this post or comment.
     *
     * @return True if votes are allowed or false if not.
     */
    public boolean getAllowVotes() {
        return allowVotes;
    }

    /**
     * Define if votes have been allowed on this post or comment.
     *
     * @param allowVotes
     *            The information if votes have been allowed on this post or
     *            comment.
     */
    public void setAllowVotes(boolean allowVotes) {
        this.allowVotes = allowVotes;
    }

    /**
     * Get the information if voters are allowed to receive curation rewards.
     *
     * @return True if votes curation rewards are paid or false if not.
     */
    public boolean getAllowCurationRewards() {
        return allowCurationRewards;
    }

    /**
     * Define if voters are allowed to receive curation rewards.
     *
     * @param allowCurationRewards
     *            True if votes curation rewards are paid or false if not.
     */
    public void setAllowCurationRewards(boolean allowCurationRewards) {
        this.allowCurationRewards = allowCurationRewards;
    }

    /**
     * Get the percent of Steem Dollars to keep, unkept amounts will be received
     * as Steem Power. The default value is 10000 which is equal to 100%.
     *
     * @return The percent of Steem Dollars.
     */
    public int getPercentSteemDollars() {
        return (int) percentSteemDollars;
    }

    /**
     * @param percentSteemDollars
     *            The percent of Steem Dollars.
     * @throws InvalidParameterException
     *             If the <code>percentSteemDollars</code> is higher than 10000
     *             which is equal to 100.00%.
     */
    public void setPercentSteemDollars(short percentSteemDollars) {
        this.percentSteemDollars = percentSteemDollars;
    }

    /**
     * Get the list of configured extensions.
     *
     * @return All extensions.
     */
    public List<CommentOptionsExtension> getExtensions() {
        return extensions;
    }

    /**
     * Extensions are currently not supported and will be ignored.
     *
     * @param extensions
     *            Define a list of extensions.
     */
    public void setExtensions(List<CommentOptionsExtension> extensions) {
        if (extensions == null) {
            // Create a new ArrayList to avoid a NullPointerException.
            this.extensions = new ArrayList<>();
        } else {
            this.extensions = extensions;
        }
    }

    @Override
    public byte[] toByteArray() {
        try (ByteArrayOutputStream serializedCommentOptionsOperation = new ByteArrayOutputStream()) {
            serializedCommentOptionsOperation.write(
                    Utilities.transformIntToVarIntByteArray(OperationType.COMMENT_OPTIONS_OPERATION.getOrderId()));
            serializedCommentOptionsOperation.write(this.getAuthor().toByteArray());
            serializedCommentOptionsOperation.write(this.getPermlink().toByteArray());
            serializedCommentOptionsOperation.write(this.getMaxAcceptedPayout().toByteArray());
            serializedCommentOptionsOperation
                    .write(Utilities.transformShortToByteArray(this.getPercentSteemDollars()));
            serializedCommentOptionsOperation.write(Utilities.transformBooleanToByteArray(this.getAllowVotes()));
            serializedCommentOptionsOperation
                    .write(Utilities.transformBooleanToByteArray(this.getAllowCurationRewards()));

            serializedCommentOptionsOperation
                    .write(Utilities.transformLongToVarIntByteArray(this.getExtensions().size()));

            for (CommentOptionsExtension commentOptionsExtension : this.getExtensions()) {
                serializedCommentOptionsOperation.write(commentOptionsExtension.toByteArray());
            }

            return serializedCommentOptionsOperation.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(
                    "A problem occured while transforming the operation into a byte array.", e);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public Map<SignatureObject, PrivateKeyType> getRequiredAuthorities(
            Map<SignatureObject, PrivateKeyType> requiredAuthoritiesBase) {
        return mergeRequiredAuthorities(requiredAuthoritiesBase, this.getAuthor(), PrivateKeyType.POSTING);
    }

    @Override
    public void validate(ValidationType validationType) {
        if (!ValidationType.SKIP_VALIDATION.equals(validationType)) {
            if (!ValidationType.SKIP_ASSET_VALIDATION.equals(validationType)) {
                if (!maxAcceptedPayout.getSymbol().equals(SteemJConfig.getInstance().getDollarSymbol())) {
                    throw new InvalidParameterException("The maximal accepted payout must be in SBD.");
                } else if (maxAcceptedPayout.getAmount() < 0) {
                    throw new InvalidParameterException("Cannot accept less than 0 payout.");
                }
            }

            if (percentSteemDollars > 10000) {
                throw new InvalidParameterException(
                        "The percent of steem dollars can't be higher than 10000 which is equivalent to 100%.");
            }

            if (extensions != null) {
                for (CommentOptionsExtension commentOptionsExtension : extensions) {
                    commentOptionsExtension.validate(validationType);
                }
            }
        }
    }
}
