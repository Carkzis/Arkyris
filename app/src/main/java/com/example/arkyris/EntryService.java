package com.example.arkyris;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface EntryService {

    /**
     * This is only for public posts, i.e. Arke
     * @return
     */
    @GET("public/")
    Call<List<EntryItemRemote>> getEntries();

    @POST("create/")
    Call<EntryItemRemote> addEntry(@Body EntryItemRemote entry);

}
