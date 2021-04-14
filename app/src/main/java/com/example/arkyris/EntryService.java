package com.example.arkyris;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EntryService {

    @POST("create/")
    Call<EntryItemRemote> addEntry(@Body EntryItemRemote entry);

}
