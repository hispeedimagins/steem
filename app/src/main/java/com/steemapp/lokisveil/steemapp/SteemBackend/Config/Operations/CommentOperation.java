package com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.OperationType;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.PrivateKeyType;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.ValidationType;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Interfaces.SignatureObject;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.AccountName;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.Permlink;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Utilities;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Map;

/**
 * Created by boot on 3/4/2018.
 */

public class CommentOperation extends Operation {
   // @JsonProperty("parent_author")
    @Expose
    private String parent_author;

    private AccountName parentAuthor;
   // @JsonProperty("parent_permlink")
   @Expose
   private String parent_permlink;

    private Permlink parentPermlink;

   @Expose
   @SerializedName("author")
   private String Author;

    private AccountName author;
  //  @JsonProperty("permlink")

    @Expose
    @SerializedName("permlink")
    private String Permlink;

    private Permlink permlink;
  //  @JsonProperty("title")
    @Expose
    private String title;
  //  @JsonProperty("body")
    @Expose
    private String body;
   // @JsonProperty("json_metadata")
    @Expose
    @SerializedName("json_metadata")
    private String jsonMetadata;

    /**
     * Create a new and empty comment operation. This operation can be used to:
     * <ul>
     * <li>Create a post</li>
     * <li>Update a post</li>
     * <li>Write a comment to a post</li>
     * <li>Update a comment</li>
     * </ul>
     *
     * @param parentAuthor
     *            Define the parent author in case of a comment or set this to
     *            an empty account name in case of a new post (see
     *            {@link #setParentAuthor(AccountName)}).
     * @param parentPermlink
     *            Define the parent permlink. In case of a new post, this field
     *            will be used to define the main tag of the post (see
     *            {@link #setParentAuthor(AccountName)}).
     * @param author
     *            Set the author of the comment/post (see
     *            {@link #setAuthor(AccountName)}).
     * @param permlink
     *            Define the permlink of this comment/post (see
     *            {@link #setPermlink(Permlink)}).
     * @param title
     *            Define the title of this comment/post (see
     *            {@link #setTitle(String)}).
     * @param body
     *            Define the body of this comment/post (see
     *            {@link #setBody(String)}).
     * @param jsonMetadata
     *            Define additional JSON meta data like additional tags (see
     *            {@link #setJsonMetadata(String)}).
     * throws InvalidParameterException
     *             If one of the parameters does not fulfill the requirements.
     */
    //@JsonCreator
    public CommentOperation(AccountName parentAuthor,
                            Permlink parentPermlink, AccountName author,
                            Permlink permlink,  String title,
                            String body, String jsonMetadata) {
        super(false);

        this.setParentAuthor(parentAuthor);
        this.setParentPermlink(parentPermlink);
        this.setAuthor(author);
        this.setPermlink(permlink);
        this.setTitle(title);
        this.setBody(body);
        this.setJsonMetadata(jsonMetadata);

        this.parent_author = parentAuthor.getName();
        this.parent_permlink = parentPermlink.getLink();
        this.Author = author.getName();
        this.Permlink = permlink.getLink();

    }

    /**
     * Like
     * {@link #CommentOperation(Permlink, AccountName, Permlink, String, String, String)},
     * but does not require a parent author. This should mainly be used for new
     * posts.
     *
     * @param parentPermlink
     *            Define the parent permlink. In case of a new post, this field
     *            will be used to define the main tag of the post (see
     *            {@link #setParentAuthor(AccountName)}).
     * @param author
     *            Set the author of the comment/post (see
     *            {@link #setAuthor(AccountName)}).
     * @param permlink
     *            Define the permlink of this comment/post (see
     *            {@link #setPermlink(Permlink)}).
     * @param title
     *            Define the title of this comment/post (see
     *            {@link #setTitle(String)}).
     * @param body
     *            Define the body of this comment/post (see
     *            {@link #setBody(String)}).
     * @param jsonMetadata
     *            Define additional JSON meta data like additional tags (see
     *            {@link #setJsonMetadata(String)}).
     * throws InvalidParameterException
     *             If one of the parameters does not fulfill the requirements.
     */
    public CommentOperation(Permlink parentPermlink, AccountName author, Permlink permlink, String title, String body,
                            String jsonMetadata) {
        this(new AccountName(""), parentPermlink, author, permlink, title, body, jsonMetadata);
    }

    /**
     * Like
     * {@link #CommentOperation(Permlink, AccountName, Permlink, String, String, String)},
     * but does not require a title and JSON data. This should mainly be used to
     * write comments.
     *
     * @param parentAuthor
     *            Define the parent author in case of a comment or set this to
     *            an empty account name in case of a new post (see
     *            {@link #setParentAuthor(AccountName)}).
     * @param parentPermlink
     *            Define the parent permlink. In case of a new post, this field
     *            will be used to define the main tag of the post (see
     *            {@link #setParentAuthor(AccountName)}).
     * @param author
     *            Set the author of the comment/post (see
     *            {@link #setAuthor(AccountName)}).
     * @param permlink
     *            Define the permlink of this comment/post (see
     *            {@link #setPermlink(Permlink)}).
     * @param body
     *            Define the body of this comment/post (see
     *            {@link #setBody(String)}).
     * throws InvalidParameterException
     *             If one of the parameters does not fulfill the requirements.
     */
    public CommentOperation(AccountName parentAuthor, Permlink parentPermlink, AccountName author, Permlink permlink,
                            String body) {
        this(parentAuthor, parentPermlink, author, permlink, "", body, "");
    }

    /**
     * Get the author of the parent article you want to comment on.
     *
     * @return The author of the parent article you want to comment on.
     */
    public AccountName getParentAuthor() {
        return parentAuthor;
    }

    /**
     * Set the author of the parent article you want to comment on. If you want
     * to create a new post, set this field to null or an empty account name.
     *
     * @param parentAuthor
     *            The author of the parent article you want to comment on.
     * throws InvalidParameterException
     *             If the <code>parentAuthor</code> is null.
     */
    public void setParentAuthor(AccountName parentAuthor) {
        if (parentAuthor == null) {
            this.parentAuthor = new AccountName("");
        } else {
            this.parentAuthor = parentAuthor;
        }
    }

    /**
     * Get the account name that will publish the comment.
     *
     * @return The account name that will publish the comment.
     */
    public AccountName getAuthor() {
        return author;
    }

    /**
     * Set the account name that will publish the comment. <b>Notice:</b> The
     * private posting key of this account needs to be stored in the key
     * storage.
     *
     * @param author
     *            The account name that will publish the comment.
     * throws InvalidParameterException
     *             If the <code>author</code> is null.
     */
    public void setAuthor(AccountName author) {
        this.author = setIfNotNull(author, "The author can't be null.");
    }

    /**
     * Get the permanent link of this comment.
     *
     * @return The permanent link of this comment.
     */
    public Permlink getPermlink() {
        return permlink;
    }

    /**
     * Set the permanent link of this comment.
     *
     * @param permlink
     *            The permanent link of this comment.
     * throws InvalidParameterException
     *             If the <code>permlink</code> is null.
     */
    public void setPermlink(Permlink permlink) {
        this.permlink = setIfNotNull(permlink, "The permlink can't be null.");
    }

    /**
     * Get the permanent link of the parent comment.
     *
     * @return The permanent link of the parent comment.
     */
    public Permlink getParentPermlink() {
        return parentPermlink;
    }

    /**
     * Set the permanent link of the parent comment. If you wan't to write a new
     * post, the <code>parentPermlink</code> will be used as the first tag.
     *
     * @param parentPermlink
     *            The permanent link of the parent comment.
     * throws InvalidParameterException
     *             If the <code>parentPermlink</code> is null.
     */
    public void setParentPermlink(Permlink parentPermlink) {
        this.parentPermlink = setIfNotNull(parentPermlink, "The permlink can't be null.");
    }

    /**
     * Get the title of this comment.
     *
     * @return The title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title for this comment.
     *
     * @param title
     *            The title of this comment.
     * throws InvalidParameterException
     *             If the title is larger than 255 characters.
     */
    public void setTitle(String title) {
        if (title == null) {
            this.title = "";
        } else {
            this.title = title;
        }
    }

    /**
     * Get the content of this comment.
     *
     * @return The content of this comment.
     */
    public String getBody() {
        return body;
    }

    /**
     * Set the content of this comment.
     *
     * @param body
     *            The content of this comment.
     * throws InvalidParameterException
     *             If no body has been provided.
     */
    public void setBody(String body) {
        if (body == null || body.isEmpty()) {
            throw new InvalidParameterException("The body can't be empty.");
        }

        this.body = body;
    }

    /**
     * Get the additional Json metadata of this comment.
     *
     * @return The additional Json metadata
     */
    public String getJsonMetadata() {
        return jsonMetadata;
    }

    /**
     * Set the additional Json metadata of this comment.
     *
     * @param jsonMetadata
     *            the additional Json metadata
     * @throws InvalidParameterException
     *             If the provided <code>jsonMetadata</code> has been provided,
     *             but the JSON is invalid.
     */
    public void setJsonMetadata(String jsonMetadata) {
        this.jsonMetadata = jsonMetadata;
    }

    @Override
    public byte[] toByteArray() {
        try (ByteArrayOutputStream serializedCommentOperation = new ByteArrayOutputStream()) {
            serializedCommentOperation
                    .write(Utilities.transformIntToVarIntByteArray(OperationType.COMMENT_OPERATION.getOrderId()));
            serializedCommentOperation.write(this.getParentAuthor().toByteArray());
            serializedCommentOperation.write(this.getParentPermlink().toByteArray());
            serializedCommentOperation.write(this.getAuthor().toByteArray());
            serializedCommentOperation.write(this.getPermlink().toByteArray());
            serializedCommentOperation.write(Utilities.transformStringToVarIntByteArray(this.getTitle()));
            serializedCommentOperation.write(Utilities.transformStringToVarIntByteArray(this.getBody()));
            serializedCommentOperation.write(Utilities.transformStringToVarIntByteArray(this.getJsonMetadata()));

            return serializedCommentOperation.toByteArray();
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
            if (title.length() > 255) {
                throw new InvalidParameterException("The title can't have more than 255 characters.");
            } /*else if (!Utilities.verifyJsonString(jsonMetadata)) {
                throw new InvalidParameterException("The given String is no valid JSON");
            }*/
        }
    }
}
