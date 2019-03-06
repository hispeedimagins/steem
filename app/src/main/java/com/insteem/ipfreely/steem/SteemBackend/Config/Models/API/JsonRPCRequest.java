package com.insteem.ipfreely.steem.SteemBackend.Config.Models.API;

import com.insteem.ipfreely.steem.SteemBackend.Config.Enums.RequestMethods;
import com.insteem.ipfreely.steem.SteemBackend.Config.Enums.SteemApiType;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Random;
import com.google.gson.annotations.*;
/**
 * Created by boot on 2/28/2018.
 */

public class JsonRPCRequest {
    @Expose
    private static final String JSONRPC = "2.0";
    @Expose
    private static final String METHOD = "call";

    /**
     * The id of the request (used to identify which answer belongs to which
     * request
     */
   // @JsonIgnore

    private static Random randomGenerator = new Random();
   // @JsonIgnore
    private SteemApiType steemApi;
    //@JsonIgnore
    private RequestMethods apiMethod;
    //@JsonIgnore
    private Object[] additionalParameters;

    @Expose
    private long id;

    /**
     * Instantiate a new RequestObject.
     */
    public JsonRPCRequest() {
        this.id = randomGenerator.nextLong();
    }

    /**
     * Get the api type used for this request.
     *
     * @return The selected steem api name.
     */
    public SteemApiType getSteemApi() {
        return steemApi;
    }

    /**
     * Set the api type you want to request (@see SteemApis)
     *
     * @param steemApi
     *            The name of the api you want to connect to.
     */
    public void setSteemApi(SteemApiType steemApi) {
        this.steemApi = steemApi;
    }

    /**
     * Get the api method used for this request.
     *
     * @return The selected steem api method.
     */
    public RequestMethods getApiMethod() {
        return apiMethod;
    }

    /**
     * Set the API-Method (@see RequestMethods).
     *
     * @param apiMethod
     *            The api method you want to use.
     */
    public void setApiMethod(RequestMethods apiMethod) {
        this.apiMethod = apiMethod;
    }

    /**
     * Get the additional parameters for this request.
     *
     * @return The additional user parameters.
     */
    public Object[] getAdditionalParameters() {
        return additionalParameters;
    }

    /**
     * Add custom parameters to this request.
     *
     * @param userParameters
     *            The additional parameters you want to use.
     */
    public void setAdditionalParameters(Object[] userParameters) {
        this.additionalParameters = userParameters;
    }

    /**
     * Get the complete list of parameters used for this request.
     *
     * @return The final params fields, used for this request.
     */
    public Object[] getParams() {
        Object[] params = new Object[3];
        params[0] = getSteemApi().toString().toLowerCase();
        params[1] = getApiMethod().toString().toLowerCase();
        params[2] = additionalParameters;

        return params;
    }

    /**
     * Get the API-Method that will be used for this request.
     *
     * @return The value of the "method" field.
     */
    public String getMethod() {
        return METHOD;
    }

    /**
     * Get the json-rpc version.
     *
     * @return The used json-rpc version.
     */
    public String getJsonrpc() {
        return JSONRPC;
    }

    /**
     * Get the id of this request.
     *
     * @return The id of this request.
     */
    public long getId() {
        return id;
    }

    /**
     * @return The json representation of this object.
     * throws JsonProcessingException
     *             If the object can not be transformed into valid json.
     */
    public String toJson()  {
        String json = "";
        return json;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
