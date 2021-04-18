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
    Call<List<EntryItemRemote>> getPublicEntries();

    @GET(".")
    Call<List<EntryItemRemote>> getEntries();

    @POST("create/")
    Call<EntryItemRemote> addEntry(@Body EntryItemRemote entry);

    @PATCH("update/{id}/")
    Call<EntryItemRemote> updatePublic(@Path("id") String id, @Body HashMap<String, String> updateFields);

}
