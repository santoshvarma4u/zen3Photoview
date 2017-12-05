package zen3.com.photoview.libs.OkhttpOAuth;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import oauth.signpost.exception.OAuthException;

public class SigningInterceptor implements Interceptor {

    private final OkHttpOAuthConsumer consumer;

    public SigningInterceptor(OkHttpOAuthConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        try {
            return chain.proceed((Request) consumer.sign(request).unwrap());
        } catch (OAuthException e) {
            throw new IOException("Could not sign request", e);
        }
    }
}
