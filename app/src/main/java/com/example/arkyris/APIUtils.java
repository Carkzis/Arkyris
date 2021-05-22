package com.example.arkyris;

import com.example.arkyris.accounts.AccountService;
import com.example.arkyris.entries.EntryService;

public class APIUtils {

    public APIUtils() {
    }

    public static final String API_URL = "http://arkyris.herokuapp.com/arkyris_api/";

    public static EntryService getEntryService() {
        return RetrofitClient.getClient(API_URL).create(EntryService.class);
    }

    public static AccountService getAccountService() {
        return RetrofitClient.getClient(API_URL).create(AccountService.class);
    }


}
