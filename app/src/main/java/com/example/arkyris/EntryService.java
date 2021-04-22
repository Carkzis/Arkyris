package com.example.arkyris;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EntryService {

    /**
     * This is only for public posts, i.e. Arke
     * @return
     */
    @GET("public/")
    Call<List<ArkeEntryItem>> getPublicEntries();

    @GET(".")
    Call<List<ArkeEntryItem>> getEntries();

    @POST("create/")
    Call<ArkeEntryItem> addEntry(@Body ArkeEntryItem entry);

    @PATCH("update/{id}/")
    Call<IrisEntryItem> updatePublic(@Path("id") String id, @Body HashMap<String, String> updateFields);

    /**
     * This is only for private posts, i.e. Iris
     * @return
     */
    @GET("private/")
    Call<List<IrisEntryItem>> getPrivateEntries();

    // overloaded method
    @POST("create/")
    Call<IrisEntryItem> addEntry(@Body IrisEntryItem entry);

}
