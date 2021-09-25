package com.carkzis.arkyris;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class for returning a retrofit client.
 */
public class RetrofitClient {

    private static Retrofit retrofit = null;

    /**
     * Returns a retrofit client using the supplied url (in this case, the url for the
     * REST API), as well as GsonConverterFactory for encoding and decoding JSON.
     */
    public static Retrofit getClient(String url) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
