package com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.FollowType;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.OperationType;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.PrivateKeyType;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.ValidationType;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Interfaces.SignatureObject;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.AccountName;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Utilities;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by boot on 3/2/2018.
 */

public class CustomJsonOperation extends Operation {
    //@JsonProperty("required_auths")
    @SerializedName("required_auths")
    private List<String> _requiredAuths = new ArrayList<>();
    private List<AccountName> requiredAuths;
   // @JsonProperty("required_posting_auths")
   @SerializedName("required_posting_auths")
   private List<String> _requiredPostingAuths = new ArrayList<>();
    private List<AccountName> requiredPostingAuths;

    public String name = "";
    public String follower = "";
    public List<FollowType> what = new ArrayList<>();
   // @JsonProperty("id")
   @Expose
    private String id;
  //  @JsonProperty("json")
    @Expose
    private String json;

    /**
     * Create a new custom json operation. This operation serves the same
     * purpose as
     * {link eu.bittrade.libs.steemj.base.models.operations.CustomOperation
     * CustomOperation} but also supports required posting authorities. Unlike
     * {link eu.bittrade.libs.steemj.base.models.operations.CustomOperation
     * CustomOperation}, this operation is designed to be human
     * readable/developer friendly.
     *
     * @param requiredAuths
     *            A list of account names whose private active key is required
     *            to sign the transaction (see {@link #setRequiredAuths(List)}).
     * @param requiredPostingAuths
     *            A list of account names whose private posting key is required
     *            to sign the transaction (see
     *            {@link #setRequiredPostingAuths(List)}).
     * @param id
     *            The plugin id (e.g. <code>follow</code>) (see
     *            {@link #setId(String)}).
     * @param json
     *            The payload provided as a valid JSON string (see
     *            {@link #setJson(String)}).
     * @throws InvalidParameterException
     *             If a parameter does not fulfill the requirements.
     */

    public CustomJsonOperation(List<AccountName> requiredAuths,
                               List<AccountName> requiredPostingAuths,
                               String id, String json) {
        super(false);

        this.setRequiredAuths(requiredAuths);
        if(requiredAuths != null){
            for (AccountName x:
                    requiredAuths) {
                _requiredAuths.add(x.getName());
            }
        }

        if(requiredPostingAuths != null){
            for (AccountName x:
                    requiredPostingAuths) {
                _requiredPostingAuths.add(x.getName());
            }
        }
        this.setRequiredPostingAuths(requiredPostingAuths);
        this.setId(id);
        this.setJson(json);
    }


    public CustomJsonOperation(List<AccountName> requiredAuths,
                               List<AccountName> requiredPostingAuths,
                               String id, String json,
                               String name,String follower,List<FollowType> what) {
        super(false);


        this.setRequiredAuths(requiredAuths);
        if(requiredAuths != null){
            for (AccountName x:
                    requiredAuths) {
                _requiredAuths.add(x.getName());
            }
        }

        if(requiredPostingAuths != null){
            for (AccountName x:
                    requiredPostingAuths) {
                _requiredPostingAuths.add(x.getName());
            }
        }
        this.setRequiredPostingAuths(requiredPostingAuths);
        this.setId(id);
        this.setJson(json);
        this.name = name;
        this.follower = follower;
        this.what = what;
    }



    /**
     * Get the list of account names whose private active keys were required to
     * sign this transaction.
     *
     * @return The list of account names whose private active keys were required
     */
    public List<AccountName> getRequiredAuths() {
        return requiredAuths;
    }

    /**
     * Set the list of account names whose private active keys are required to
     * sign this transaction.
     *
     * @param requiredAuths
     *            The account names whose private active keys are required.
     * @throws InvalidParameterException
     *             If the provided <code>requiredAuths</code> is empty and in
     *             addition no {@link #getRequiredPostingAuths()} are provided.
     */
    public void setRequiredAuths(List<AccountName> requiredAuths) {
        if (requiredAuths == null) {
            this.requiredAuths = new ArrayList<>();
        } else {
            this.requiredAuths = requiredAuths;
        }
    }

    /**
     * Get the list of account names whose private posting keys were required to
     * sign this transaction.
     *
     * @return The list of account names whose private posting keys were
     *         required.
     */
    public List<AccountName> getRequiredPostingAuths() {
        return requiredPostingAuths;
    }

    /**
     * Set the list of account names whose private posting keys are required to
     * sign this transaction.
     *
     * @param requiredPostingAuths
     *            The account names whose private posting keys are required.
     * @throws InvalidParameterException
     *             If the provided <code>requiredPostingAuths</code> is empty
     *             and in addition no {@link #getRequiredAuths()} are provided.
     */
    public void setRequiredPostingAuths(List<AccountName> requiredPostingAuths) {
        if (requiredPostingAuths == null) {
            this.requiredPostingAuths = new ArrayList<>();
        } else {
            this.requiredPostingAuths = requiredPostingAuths;
        }
    }

    /**
     * @return The plugin id (e.g. <code>follow</code>").
     */
    public String getId() {
        return id;
    }

    /**
     * Set the plugin id for this operation.
     *
     * @param id
     *            The plugin id of this Operation (e.g. <code>follow</code>").
     * @throws InvalidParameterException
     *             If the id has more than 31 characters or has not been
     *             provided.
     */
    public void setId(String id) {
        this.id = setIfNotNull(id, "An ID is required.");
    }

    /**
     * @return The JSON covered by this Operation in its String representation.
     */
    public String getJson() {
        return json;
    }

    /**
     * Set the JSON String that should be send with this Operation.
     *
     * @param json
     *            The JSON to send.
     * @throws InvalidParameterException
     *             If the given <code>json</code> is not valid.
     */
    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public byte[] toByteArray() {
        try (ByteArrayOutputStream serializedCustomJsonOperation = new ByteArrayOutputStream()) {
            serializedCustomJsonOperation
                    .write(Utilities.transformIntToVarIntByteArray(OperationType.CUSTOM_JSON_OPERATION.getOrderId()));

            int s = this.getRequiredAuths().size();
            serializedCustomJsonOperation
                    .write(Utilities.transformLongToVarIntByteArray(this.getRequiredAuths().size()));

            for (AccountName accountName : this.getRequiredAuths()) {
                serializedCustomJsonOperation.write(accountName.toByteArray());
            }

            serializedCustomJsonOperation
                    .write(Utilities.transformLongToVarIntByteArray(this.getRequiredPostingAuths().size()));

            for (AccountName accountName : this.getRequiredPostingAuths()) {
                serializedCustomJsonOperation.write(accountName.toByteArray());
            }

            serializedCustomJsonOperation.write(Utilities.transformStringToVarIntByteArray(this.getId()));
            serializedCustomJsonOperation.write(Utilities.transformStringToVarIntByteArray(this.getJson()));

            return serializedCustomJsonOperation.toByteArray();
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
        Map<SignatureObject, PrivateKeyType> requiredAuthorities = requiredAuthoritiesBase;

        requiredAuthorities = mergeRequiredAuthorities(requiredAuthorities, this.getRequiredAuths(),
                PrivateKeyType.ACTIVE);
        requiredAuthorities = mergeRequiredAuthorities(requiredAuthorities, this.getRequiredPostingAuths(),
                PrivateKeyType.POSTING);

        return requiredAuthorities;
    }

    @Override
    public void validate(ValidationType validationType) {
        if (!ValidationType.SKIP_VALIDATION.equals(validationType)) {
            if (requiredPostingAuths.isEmpty() && requiredAuths.isEmpty()) {
                throw new InvalidParameterException(
                        "At least one authority type (POSTING or ACTIVE) needs to be provided.");
            } else if (id.length() > 32) {
                throw new InvalidParameterException("The ID must be less than 32 characters long.");
                //&& !Utilities.verifyJsonString(json)
            } else if (json == null && json.isEmpty() ) {
                throw new InvalidParameterException("The given String is no valid JSON");
            }
        }
    }
}
