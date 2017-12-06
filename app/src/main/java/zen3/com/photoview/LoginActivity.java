package zen3.com.photoview;
/**
 * Created by SantoshT on 12/4/2017.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import zen3.com.photoview.libs.ConfigurationManager;
import zen3.com.photoview.Helpers.Helper;
import zen3.com.photoview.libs.OkhttpOAuth.OkHttpOAuthConsumer;
import zen3.com.photoview.libs.OkhttpOAuth.SigningInterceptor;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{


    public static String TAG="LoginActivity";
    protected Button btnLogin;
    protected EditText etUsername;
    protected EditText etPassword;


    private String url = "https://api.500px.com/v1";
    private String request_token_url = url+"/oauth/request_token";
    private String access_token_url = url+"/oauth/access_token";
    private String authorize_url = url+"/oauth/authorize";
    private String requestOauthToken;
    private String requestOauthSecret;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_login);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnLogin) {
            //on login with 500px
            try {
                if(etUsername.getText().toString() !=null && etPassword.getText().toString() !=null)
                {
                    signRequest(etUsername.getText().toString(),etPassword.getText().toString());
                }
                else
                {
                    Toast.makeText(this, "Please enter valid username and password", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(LoginActivity.this);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);

        //hardcoded credentials for testing

        etUsername.setText("santoshvarma4u@gmail.com");
        etPassword.setText("sant12345");
    }

    private void signRequest(final String username, final String password){
       Helper.showLoadingDialog(LoginActivity.this);
        try{
            OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(ConfigurationManager.ConsumerKey, ConfigurationManager.ConsumerSecret);

            Request mRequest=new Request.Builder()
                    .url(request_token_url)
                    .build();
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new SigningInterceptor(consumer))
                    .build();

            client.newCall(mRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Helper.hideLoadingDialog();
                    Toast.makeText(LoginActivity.this, "Oops something went wrong,unable to login", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                   Helper.hideLoadingDialog();
                    String responseOut=response.body().string();
                    List<String> oAuthList = Arrays.asList(responseOut.split("&"));
                    List<String> oAuthTokenString= Arrays.asList(oAuthList.get(0).split("="));
                    requestOauthToken=oAuthTokenString.get(1).toString();
                    List<String> oAuthSecretString= Arrays.asList(oAuthList.get(1).split("="));
                    requestOauthSecret=oAuthSecretString.get(1).toString();
                    Log.i("requestToken",requestOauthToken);
                    Log.i("requestOauthSecret",requestOauthSecret);
                    if(requestOauthSecret!=null && requestOauthToken!=null)
                    {
                        LoginService(username,password);
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
    public void LoginService(String username,String password)
    {
       // Helper.showLoadingDialog(LoginActivity.this);
        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(ConfigurationManager.ConsumerKey, ConfigurationManager.ConsumerSecret);
        consumer.setTokenWithSecret(requestOauthToken,requestOauthSecret);
        Long tsLong = System.currentTimeMillis()/1000;
        String nonce = UUID.randomUUID().toString();
        String ts = tsLong.toString();
        RequestBody body = RequestBody.create(mediaType, "oauth_callback=http%3A%2F%2Fzen3.photoview.com&x_auth_mode=client_auth&x_auth_username="+username+"&x_auth_password="+password+"&oauth_consumer_key="+ConfigurationManager.ConsumerKey+"&oauth_token="+requestOauthToken+"&oauth_signature_method=HMAC-SHA1&oauth_timestamp="+ts+"&oauth_nonce="+nonce+"&oauth_version=1.0&oauth_signature=VE6ZCFh9kdxpT1FNCo7JpbKmO94%3D");
        Request mRequest=new Request.Builder()
                .url(access_token_url)
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new SigningInterceptor(consumer))
                .build();
        client.newCall(mRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Helper.hideLoadingDialog();
                Toast.makeText(LoginActivity.this, "Oops something went wrong,unable to login", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
               // Helper.hideLoadingDialog();
                String responseOut=response.body().string();
                List<String> oAuthList = Arrays.asList(responseOut.split("&"));
                List<String> oAuthTokenString= Arrays.asList(oAuthList.get(0).split("="));
                //save auth token for further uses to create services.
                if(oAuthTokenString.get(1).toString() !=null)
                {
                    Helper.setToken(oAuthTokenString.get(1).toString(),LoginActivity.this);
                    startActivity(new Intent(LoginActivity.this,ShowImagesList.class));
                }
                else
                {
                  //  Helper.hideLoadingDialog();
                    Toast.makeText(LoginActivity.this, "Oops something went wrong,unable to login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
