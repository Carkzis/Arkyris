package com.example.arkyris.entries;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EntryService {

    /**
     * This is only for public posts, i.e. Arke
     */
    @GET("public/")
    Call<List<ArkeEntryItem>> getPublicEntries();

    @GET(".")
    Call<List<ArkeEntryItem>> getEntries();

    @POST("create/")
    Call<ArkeEntryItem> addEntry(@Body ArkeEntryItem entry);

    /**
     * This is only for private posts, i.e. Iris
     */

    @PATCH("update/{id}/")
    Call<IrisEntryItem> updatePublic(
            @Path("id") String id, @Body HashMap<String, String> updateFields);

    @GET("private/")
    Call<List<IrisEntryItem>> getPrivateEntries(@Query("user") String user);

    // overloaded method
    @POST("create/")
    Call<IrisEntryItem> addEntry(@Body IrisEntryItem entry);

}
