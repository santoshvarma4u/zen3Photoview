package zen3.com.photoview.libs.OkhttpOAuth;

import okhttp3.Response;
import oauth.signpost.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;

public class OkHttpResponseAdapter implements HttpResponse {

    private Response response;

    public OkHttpResponseAdapter(Response response) {
        this.response = response;
    }

    @Override
    public int getStatusCode() throws IOException {
        return response.code();
    }

    @Override
    public String getReasonPhrase() throws Exception {
        return response.message();
    }

    @Override
    public InputStream getContent() throws IOException {
        return response.body().byteStream();
    }

    @Override
    public Object unwrap() {
        return response;
    }
}
