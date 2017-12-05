
package zen3.com.photoview.libs.OkhttpOAuth;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import oauth.signpost.AbstractOAuthProvider;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpResponse;

public class OkHttpOAuthProvider extends AbstractOAuthProvider {

    private transient OkHttpClient okHttpClient;

    public OkHttpOAuthProvider(String requestTokenEndpointUrl, String accessTokenEndpointUrl,
                               String authorizationWebsiteUrl) {
        super(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
        this.okHttpClient = new OkHttpClient();
    }

    public OkHttpOAuthProvider(String requestTokenEndpointUrl, String accessTokenEndpointUrl,
                               String authorizationWebsiteUrl, OkHttpClient okHttpClient) {
        super(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
        this.okHttpClient = okHttpClient;
    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    protected HttpRequest createRequest(String endpointUrl) throws Exception {
        Request request = new Request.Builder().url(endpointUrl).build();
        return new OkHttpRequestAdapter(request);
    }

    @Override
    protected HttpResponse sendRequest(HttpRequest request) throws Exception {
        Response response = okHttpClient.newCall((Request) request.unwrap()).execute();
        return new OkHttpResponseAdapter(response);
    }
}
