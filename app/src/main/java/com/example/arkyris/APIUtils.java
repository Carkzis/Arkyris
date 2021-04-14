package com.example.arkyris;

public class APIUtils {

    public APIUtils() {
    };

    public static final String API_URL = "http://192.168.0.14:8000/arkyris_api/";

    public static EntryService getEntryService() {
        return RetrofitClient.getClient(API_URL).create(EntryService.class);
    }


}
