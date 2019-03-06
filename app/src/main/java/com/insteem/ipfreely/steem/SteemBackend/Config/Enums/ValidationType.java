package com.insteem.ipfreely.steem.SteemBackend.Config.Enums;

/**
 * Created by boot on 2/23/2018.
 */

public enum ValidationType {
    /**
     * Indicates that all validation methods (except of null pointer checks)
     * should be skipped.
     */
    SKIP_VALIDATION,
    /** Indicates that the validation of assets should be skipped. */
    SKIP_ASSET_VALIDATION,
    /** Indicates that all fields should be validated. */
    ALL
}
