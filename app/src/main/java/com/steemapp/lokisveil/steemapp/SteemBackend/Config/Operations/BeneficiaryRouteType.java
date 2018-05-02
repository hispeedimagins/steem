package com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.ValidationType;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Interfaces.ByteTransformable;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Interfaces.Validatable;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.AccountName;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Utilities;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Created by boot on 3/4/2018.
 */

public class BeneficiaryRouteType  implements ByteTransformable, Validatable {
    //@JsonProperty("account")
    @Expose
    @SerializedName("account")
    private String Account;
    private AccountName account;
    // Original type is uint16_t.
    //@JsonProperty("weight")
    @Expose
    private short weight;

    /**
     * Create a new beneficiary route type.
     *
     * @param account
     *            The account who is the beneficiary of this comment (see
     *            {@link #setAccount(AccountName)}).
     * @param weight
     *            The percentage the <code>account</code> will receive from the
     *            comment payout (see {@link #setWeight(short)}).
     * @throws InvalidParameterException
     *             If a parameter does not fulfill the requirements.
     */
   // @JsonCreator
    public BeneficiaryRouteType( AccountName account, short weight) {
        this.setAccount(account);
        this.setWeight(weight);
        this.Account = account.getName();
    }

    /**
     * Get the account who is the beneficiary of this comment.
     *
     * @return The beneficiary.
     */
    public AccountName getAccount() {
        return account;
    }

    /**
     * Define the beneficiary for this comment.
     *
     * @param account
     *            The beneficiary account.
     * @throws InvalidParameterException
     *             If the <code>account</code> is null.
     */
    public void setAccount(AccountName account) {
        if (account == null) {
            throw new InvalidParameterException("The account cannot be null.");
        }

        this.account = account;
    }

    /**
     * Get the percentage the {@link #getAccount() account} will receive from
     * the comment payout.
     *
     * @return The percentage of the payout to relay.
     */
    public short getWeight() {
        return weight;
    }

    /**
     * Set the percentage the {@link #getAccount() account} will receive from
     * the comment payout.
     *
     * @param weight
     *            The percentage of the payout to relay.
     * @throws InvalidParameterException
     *             If the <code>weight</code> is negative.
     */
    public void setWeight(short weight) {
        if (weight < 0) {
            throw new InvalidParameterException("The weight cannot be negative.");
        }

        this.weight = weight;
    }

    @Override
    public byte[] toByteArray()  {
        try (ByteArrayOutputStream serializedBeneficiaryRouteType = new ByteArrayOutputStream()) {
            serializedBeneficiaryRouteType.write(this.getAccount().toByteArray());
            serializedBeneficiaryRouteType.write(Utilities.transformShortToByteArray(this.getWeight()));

            return serializedBeneficiaryRouteType.toByteArray();
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
    public void validate(ValidationType validationType) {
        if (!validationType.equals(ValidationType.SKIP_VALIDATION) && this.getWeight() > 10000) {
            throw new InvalidParameterException("Cannot allocate more than 100% of rewards to one account.");
        }
    }
}
