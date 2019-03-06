package com.insteem.ipfreely.steem.SteemBackend.Config.Operations;

import com.insteem.ipfreely.steem.SteemBackend.Config.Interfaces.Validatable;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by boot on 3/2/2018.
 */

public abstract class CustomJsonOperationPayload  extends BaseOperation implements Validatable {
    /**
     * Transform the operation into its json representation.
     *
     * @return The json representation of this operation.
     * throws SteemTransformationException
     *             If the operation could not be transformed into a valid json
     *             string.
     */
    public String toJson() {
        /*try {

            return CommunicationHandler.getObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new SteemTransformationException("Cannot transform operation into its json representation.", e);
        }*/
        return "";
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
