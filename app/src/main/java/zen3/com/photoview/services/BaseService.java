package zen3.com.photoview.services;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import zen3.com.photoview.Helpers.Helper;
import zen3.com.photoview.LoginActivity;
import zen3.com.photoview.libs.AppProperties;
import zen3.com.photoview.libs.ConfigurationManager;
import zen3.com.photoview.libs.OkhttpOAuth.OkHttpOAuthConsumer;
import zen3.com.photoview.libs.OkhttpOAuth.SigningInterceptor;
import zen3.com.photoview.libs.PhotoViewApplication;
import com.google.api.client.auth.oauth.OAuthParameters;

/**
 * Created by SantoshT on 12/4/2017.
 */

public class BaseService {

    private static final long DEFAULT_TIMEOUT = 60 * 10000;

    Context mContext;
    public static final String API_BASE_URL = "https://api.500px.com/v1/";
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    protected static HttpLoggingInterceptor httpLoggingInterceptor;
    public static OkHttpClient client;

    static {
        httpLoggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder().connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).addInterceptor(httpLoggingInterceptor).build();
    }

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create());
    protected static Retrofit retrofit = builder.build();


    public static <S> S createService(Class<S> serviceClass, final OAuthParameters oAuthParams) {

        httpClient.addInterceptor(new OAuthInterceptor(oAuthParams));

        builder.client(httpClient.build());
        retrofit = builder.build();
        return retrofit.create(serviceClass);
    }




}
