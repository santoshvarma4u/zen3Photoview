package zen3.com.photoview.services;

/**
 * Created by SantoshT on 12/5/2017.
 */
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;

import java.io.IOException;
import java.security.GeneralSecurityException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class OAuthInterceptor  implements Interceptor {
    private final OAuthParameters oAuthParams;

    public OAuthInterceptor(OAuthParameters oAuthParams) {
        this.oAuthParams = oAuthParams;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request originalRequest = chain.request();
        GenericUrl requestUrl = new GenericUrl(originalRequest.url().toString());
        oAuthParams.computeNonce();
        oAuthParams.computeTimestamp();
        try {
            oAuthParams.computeSignature("GET", requestUrl);
            Request compressedRequest = originalRequest.newBuilder()
                    .header("Authorization", oAuthParams.getAuthorizationHeader())
                    .header("Accept","application/json")
                    .method(originalRequest.method(),originalRequest.body())
                    .build();
            return chain.proceed(compressedRequest);
        } catch (GeneralSecurityException e) {
        }


        return chain.proceed(originalRequest);
    }
}
