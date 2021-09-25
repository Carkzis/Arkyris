package com.carkzis.arkyris;

import com.carkzis.arkyris.accounts.AccountService;
import com.carkzis.arkyris.entries.EntryService;

/**
 * Utilities allowing the app to connect to the API.
 */
public class APIUtils {

    public APIUtils() {
    }

    /**
     * This is the URL for the API (a Django Rest Framework).
     */
    public static final String API_URL = "http://arkyris.herokuapp.com/arkyris_api/";

    /**
     * Provides the associated service for both entries and account related server calls,
     * respectively, using retrofit.
     */
    public static EntryService getEntryService() {
        return RetrofitClient.getClient(API_URL).create(EntryService.class);
    }
    public static AccountService getAccountService() {
        return RetrofitClient.getClient(API_URL).create(AccountService.class);
    }

}
