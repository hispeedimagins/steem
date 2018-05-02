package com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations;

import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.PrivateKeyType;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.ValidationType;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Interfaces.SignatureObject;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.AccountName;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.Permlink;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.Map;

/**
 * Created by boot on 3/2/2018.
 */

public class ReblogOperation  extends CustomJsonOperationPayload {
    //@JsonProperty("account")
    private AccountName account;
    //@JsonProperty("author")
    private AccountName author;
    //@JsonProperty("permlink")
    private Permlink permlink;

    /**
     * Create a new reblog operation to reblog a comment or a post.
     * <b>Attention</b>
     * <ul>
     * <li>This operation can't be broadcasted directly. It needs to be added as
     * a payload to a {@link CustomJsonOperation}.</li>
     * <li>Once broadcasted, this operation cannot be reverted.</li>
     * </ul>
     *
     * @param account
     *            The account that wants to reblog the post or comment specified
     *            by the <code>permlink</code> and <code>author</code> (see
     *            {@link #setAccount(AccountName)}).
     * @param author
     *            The author that has written the comment or the post to resteem
     *            (see {@link #setAuthor(AccountName)}).
     * @param permlink
     *            The permlink of the comment or post to resteem (see
     *            {@link #setPermlink(Permlink)}).
     * @throws InvalidParameterException
     *             If one of the arguments does not fulfill the requirements.
     */
    //@JsonCreator
    public ReblogOperation(AccountName account,  AccountName author,
                           Permlink permlink) {
        //super(false);
        this.setAccount(account);
        this.setAuthor(author);
        this.setPermlink(permlink);
    }

    /**
     * Create an empty reblog operation to reblog a comment or a post.
     * <b>Attention</b>
     * <ul>
     * <li>This operation can't be broadcasted directly. It needs to be added as
     * a payload to a {@link CustomJsonOperation}.</li>
     * <li>Once broadcasted, this operation cannot be reverted.</li>
     * </ul>
     */
    /*public ReblogOperation() {
    }*/

    /**
     * @return The account that resteemed the comment or the post.
     */
    public AccountName getAccount() {
        return account;
    }

    public String toJson(){
        /*String json = "[\\\"reblog\\\",{\\\"account\\\":\\\""+account.getName()+"\\\",\\\"author\\\":\\\""+author.getName()+"\\\",\\\"permlink\\\":\\\""+permlink.getLink()+"\\\"}]";
        return json;*/
        String json = "[\"reblog\",{\"account\":\""+account.getName()+"\",\"author\":\""+author.getName()+"\",\"permlink\":\""+permlink.getLink()+"\"}]";
        return json;
        /*try {

            JSONArray array = new JSONArray();
            array.put("reblog");

            JSONObject object = new JSONObject();
            object.put("account",account.getName());
            object.put("author",author.getName());
            object.put("permlink",permlink.getLink());

            array.put(object);
            return array.toString();

        }
        catch (JSONException ex){
            return null;
        }*/

    }

    /**
     * Define the account whose want to reblog the post. <b>Notice:</b> The
     * private posting key of this account needs to be stored in the key
     * storage.
     *
     * @param account
     *            The account who wants to reblog the post.
     */
    public void setAccount(AccountName account) {
        this.account = account;
    }

    /**
     * @return The author of the post or comment to resteem.
     */
    public AccountName getAuthor() {
        return author;
    }

    /**
     * @param author
     *            The author of the post or comment to resteem.
     */
    public void setAuthor(AccountName author) {
        this.author = author;
    }

    /**
     * Get the permanent link of the post or comment that has been resteemed.
     *
     * @return The permanent link of the post or comment that has been
     *         resteemed.
     */
    public Permlink getPermlink() {
        return permlink;
    }

    /**
     * Set the permanent link of the post or comment that should be resteemed.
     *
     * @param permlink
     *            The permanent link of the post or comment that should be
     *            resteened.
     */
    public void setPermlink(Permlink permlink) {
        this.permlink = permlink;
    }

    /*@Override
    public Map<SignatureObject, PrivateKeyType> getRequiredAuthorities(Map<SignatureObject, PrivateKeyType> requiredAuthoritiesBase) {
        return null;
    }*/

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public void validate(ValidationType validationType) {
        if (!ValidationType.SKIP_VALIDATION.equals(validationType)) {
            if (this.getAccount() == null) {
                throw new InvalidParameterException("The account cannot be null.");
            } else if (this.getAuthor() == null) {
                throw new InvalidParameterException("The author account cannot be null.");
            } else if (this.getPermlink() == null) {
                throw new InvalidParameterException("The permlink account cannot be null.");
            } else if (this.getAccount().equals(this.getAuthor())) {
                throw new InvalidParameterException("You cannot reblog your own content.");
            }
        }
    }

    /*@Override
    public byte[] toByteArray() {
        return new byte[0];
    }*/
}
