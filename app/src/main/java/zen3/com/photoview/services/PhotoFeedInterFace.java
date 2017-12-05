package zen3.com.photoview.services;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import zen3.com.photoview.models.PhotoResponse;

/**
 * Created by tskva on 12/5/2017.
 */

public interface PhotoFeedInterFace {

    @Headers("Content-Type: application/json; charset=utf-8")
    @GET("photos")
    Call<PhotoResponse> getPhotosFeed(@Query("feature") String feature,
                                      @Query("sort") String sort,
                                      @Query("rpp") String rpp,
                                      @Query("image_size") String image_size,
                                      @Query("include_store") String include_store,
                                      @Query("consumer_key") String consumer_key,
                                      @Query("include_states") String include_states);
}
