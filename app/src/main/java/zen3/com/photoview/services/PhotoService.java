package zen3.com.photoview.services;

import android.content.Context;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import zen3.com.photoview.Helpers.Helper;
import zen3.com.photoview.Helpers.HttpCallResponse;
import zen3.com.photoview.models.PhotoResponse;

/**
 * Created by tskva on 12/5/2017.
 */

public class PhotoService extends BaseService {




    public static void getPhotos(final String feature,final HttpCallResponse httpCallResponse) {


        GetPhotosInterface mGetPhotosInterface = retrofit.create(GetPhotosInterface.class);
        Call<PhotoResponse> mcall=mGetPhotosInterface.getPhotosFeed(feature,"created_at","5",
                "3","store_download","voted");
        mcall.enqueue(new Callback<PhotoResponse>() {
            @Override
            public void onResponse(Call<PhotoResponse> call, Response<PhotoResponse> response) {
                if (response.isSuccessful())
                    httpCallResponse.OnSuccess(response);
                else
                    httpCallResponse.OnFailure(500);
            }

            @Override
            public void onFailure(Call<PhotoResponse> call, Throwable t) {
                t.printStackTrace();
                httpCallResponse.OnFailure(500);
            }
        });
    }

    public interface GetPhotosInterface
    {
        @Headers("Content-Type: application/json; charset=utf-8")
        @GET("photos")
        Call<PhotoResponse> getPhotosFeed(@Query("feature") String feature,
                                          @Query("sort") String sort,
                                          @Query("rpp") String rpp,
                                          @Query("image_size") String image_size,
                                          @Query("include_store") String include_store,
                                          @Query("include_states") String include_states);


    }
}
