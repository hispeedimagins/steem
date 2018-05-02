package com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums;

/**
 * Created by boot on 2/27/2018.
 */

public enum PrivateKeyType {
    /** The owner key type */
    OWNER,
    /** The active key type */
    ACTIVE,
    /** The memo key type */
    MEMO,
    /** The posting key type */
    POSTING,
    /**
     * The 'OTHER' key type is no real key type - It is only used to indicate
     * that an authority needs to be provided.
     */
    OTHER
}
