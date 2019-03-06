package com.insteem.ipfreely.steem.SteemBackend.Config.Models;

import com.google.gson.annotations.Expose;
import com.insteem.ipfreely.steem.SteemBackend.Config.Enums.CommentOptionsExtensionsType;
import com.insteem.ipfreely.steem.SteemBackend.Config.Enums.ValidationType;
import com.insteem.ipfreely.steem.SteemBackend.Config.Operations.BeneficiaryRouteType;
import com.insteem.ipfreely.steem.SteemBackend.Config.Operations.CommentOptionsExtension;
import com.insteem.ipfreely.steem.SteemBackend.Config.Utilities;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

/**
 * Created by boot on 3/4/2018.
 */

public class CommentPayoutBeneficiaries extends CommentOptionsExtension {
    //@JsonProperty("beneficiaries")
    @Expose
    private List<BeneficiaryRouteType> beneficiaries;

    /**
     * @return The beneficiaries.
     */
    public List<BeneficiaryRouteType> getBeneficiaries() {
        return beneficiaries;
    }

    /**
     * @param beneficiaries
     *            The beneficiaries to set.
     */
    public void setBeneficiaries(List<BeneficiaryRouteType> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }

    @Override
    public byte[] toByteArray() {
        try (ByteArrayOutputStream serializedCommentPayoutBeneficiaries = new ByteArrayOutputStream()) {
            serializedCommentPayoutBeneficiaries.write(Utilities.transformIntToVarIntByteArray(
                    CommentOptionsExtensionsType.COMMENT_PAYOUT_BENEFICIARIES.ordinal()));

            serializedCommentPayoutBeneficiaries
                    .write(Utilities.transformLongToVarIntByteArray(this.getBeneficiaries().size()));

            for (BeneficiaryRouteType beneficiaryRouteType : this.getBeneficiaries()) {
                serializedCommentPayoutBeneficiaries.write(beneficiaryRouteType.toByteArray());
            }

            return serializedCommentPayoutBeneficiaries.toByteArray();
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
        if (!validationType.equals(ValidationType.SKIP_VALIDATION)) {
            if (this.getBeneficiaries().isEmpty()) {
                throw new InvalidParameterException("Must specify at least one beneficiary.");
            } else if (this.getBeneficiaries().size() >= 128) {
                // Require size serialization fits in one byte.
                throw new InvalidParameterException("Cannot specify more than 127 beneficiaries.");
            }

            // TODO: Verify if the list is sorted correctly.
            // FC_ASSERT( beneficiaries[i - 1] < beneficiaries[i], "Benficiaries
            // must be specified in sorted order (account ascending)" );

            int sum = 0;
            for (BeneficiaryRouteType beneficiaryRouteType : this.getBeneficiaries()) {
                sum += beneficiaryRouteType.getWeight();
                if (sum > 10000) {
                    throw new InvalidParameterException("Cannot allocate more than 100% of rewards to a comment");
                }
            }
        }
    }
}