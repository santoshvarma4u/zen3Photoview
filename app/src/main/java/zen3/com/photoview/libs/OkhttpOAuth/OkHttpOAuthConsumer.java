package zen3.com.photoview.libs.OkhttpOAuth;

import okhttp3.Request;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;


public class OkHttpOAuthConsumer extends AbstractOAuthConsumer {

    public OkHttpOAuthConsumer(String consumerKey, String consumerSecret) {
        super(consumerKey, consumerSecret);
    }

    @Override
    protected HttpRequest wrap(Object request) {
        if (!(request instanceof Request)) {
            throw new IllegalArgumentException("This consumer expects requests of type " + Request.class.getCanonicalName());
        }
        return new OkHttpRequestAdapter((Request) request);
    }

}
