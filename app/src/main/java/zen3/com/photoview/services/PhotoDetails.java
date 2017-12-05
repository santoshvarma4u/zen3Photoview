package zen3.com.photoview.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;
import zen3.com.photoview.models.PhotoResponse;
import zen3.com.photoview.models.Photos;

/**
 * Created by SantoshT on 12/5/2017.
 */

public interface PhotoDetails {


    @Headers("Content-Type: application/json; charset=utf-8")
    @GET
    Call<Photos> getPhotosDetails(@Url String Url,
                                  @Query("image_size") String image_size,
                                  @Query("comments") String comments,
                                  @Query("tags") String tags,
                                  @Query("consumer_key") String consumer_key);
}

