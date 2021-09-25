package com.carkzis.arkyris.entries;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Service interface for performing calls to the Django Rest Framework relating
 * to user diary entries.
 */
public interface EntryService {

    /**
     * Retrieves all entries marked as public.
     */
    @GET("public/")
    Call<List<ArkeEntryItem>> getPublicEntries();

    /**
     * Retrieves all entries.
     */
    @GET(".")
    Call<List<ArkeEntryItem>> getEntries();

    /**
     * Creates an entry, but adding it into the backend database.
     */
    @POST("create/")
    Call<ArkeEntryItem> addEntry(@Body ArkeEntryItem entry);


    /**
     * Updates a users entry based on their ID.  The HashMap takes the field to be updated
     * as the key, and the new value as the value.
     */
    @PATCH("update/{id}/")
    Call<IrisEntryItem> updatePublic(
            @Path("id") String id, @Body HashMap<String, String> updateFields);

    /**
     * Retrieves the entries of the specified user.
     */
    @GET("private/")
    Call<List<IrisEntryItem>> getPrivateEntries(@Query("user") String user);

    /**
     * Add an entry, but from IrisFragment.
     */
    @POST("create/")
    Call<IrisEntryItem> addEntry(@Body IrisEntryItem entry);

}
