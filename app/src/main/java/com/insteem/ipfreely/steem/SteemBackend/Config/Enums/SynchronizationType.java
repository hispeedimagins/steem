package com.insteem.ipfreely.steem.SteemBackend.Config.Enums;

/**
 * Created by boot on 2/23/2018.
 */

public enum SynchronizationType {
    /** Synchronize everything. */
    FULL,
    /** Only synchronize available APIs. */
    APIS_ONLY,
    /** Only synchronize properties. */
    PROPERTIES_ONLY,
    /** Do not synchronize values with the connected Node. */
    NONE
}
