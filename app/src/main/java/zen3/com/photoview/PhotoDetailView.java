package zen3.com.photoview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import zen3.com.photoview.Helpers.Helper;
import zen3.com.photoview.libs.ConfigurationManager;
import zen3.com.photoview.models.Photo;
import zen3.com.photoview.models.PhotoDetailPojo;
import zen3.com.photoview.services.BaseService;
import zen3.com.photoview.services.PhotoDetails;

public class PhotoDetailView extends AppCompatActivity {

    protected TextView photoName;
    protected TextView photoDescription;
    protected ImageView ivPhoto;
    protected TextView tvTags;
    public static String photoID = "";
    protected ImageView ivLike;
    Photo mPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_photo_detail_view);
        initView();
        if (getIntent().getExtras() != null) {
            photoID = getIntent().getExtras().get("photoId").toString();

        }
    }


    private void initView() {
        photoName = (TextView) findViewById(R.id.photo_name);
        photoDescription = (TextView) findViewById(R.id.photo_description);
        ivPhoto = (ImageView) findViewById(R.id.iv_photo);
        tvTags = (TextView) findViewById(R.id.tv_tags);
        ivLike = (ImageView) findViewById(R.id.iv_like);
        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //service to update like on photo with userid
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (photoID != null) {
            getPhotoInfo(photoID);
        }
    }

    public void getPhotoInfo(String photoID) {
        Helper.showLoadingDialog(PhotoDetailView.this);
        String nonce = UUID.randomUUID().toString();

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();

        OAuthParameters authorizer = new OAuthParameters();
        authorizer.consumerKey = ConfigurationManager.ConsumerKey;
        authorizer.token = Helper.getToken(PhotoDetailView.this);
        OAuthHmacSigner signer = new OAuthHmacSigner();
        authorizer.signer = signer;
        authorizer.nonce = nonce;
        authorizer.timestamp = ts;
        authorizer.signature = "e2n6Mca1rhg2%2Bi8ranKKACqzQPs%3D";
        authorizer.signatureMethod = signer.getSignatureMethod();
        authorizer.version = "1.0";

        PhotoDetails mPhotoDetails = BaseService.createService(PhotoDetails.class, authorizer);
        Call<PhotoDetailPojo> mcall = mPhotoDetails.getPhotosDetails("photos/" + photoID, "3", "1", "1", ConfigurationManager.ConsumerKey);
        mcall.enqueue(new Callback<PhotoDetailPojo>() {
            @Override
            public void onResponse(Call<PhotoDetailPojo> call, Response<PhotoDetailPojo> response) {
                Helper.hideLoadingDialog();

                if (response.isSuccessful())
                    Log.e("response", response.body().getPhoto().getName());
                else
                    Log.e("response", response.errorBody().toString());

                mPhotos = response.body().getPhoto();
                photoName.setText(mPhotos.getName());
                photoDescription.setText(mPhotos.getDescription());

                Picasso.with(PhotoDetailView.this)
                        .load(mPhotos.getImage_url())
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(ivPhoto);

                if (mPhotos.getLiked())
                        ivLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_red));
                else
                    ivLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_grey));


                    String tagString = "";
                String[] tags = mPhotos.getTags();
                for (String tag : tags) {
                    tagString = tagString + "," + tag;
                }
                tvTags.setText(tagString);
            }

            @Override
            public void onFailure(Call<PhotoDetailPojo> call, Throwable t) {
                t.printStackTrace();
                Helper.hideLoadingDialog();
            }
        });
    }
}
