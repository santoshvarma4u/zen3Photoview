package zen3.com.photoview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.googleapis.auth.oauth2.OAuth2Utils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import zen3.com.photoview.Helpers.Helper;
import zen3.com.photoview.Helpers.HttpCallResponse;
import zen3.com.photoview.libs.ConfigurationManager;
import zen3.com.photoview.models.PhotoResponse;
import zen3.com.photoview.models.Photos;
import zen3.com.photoview.services.BaseService;
import zen3.com.photoview.services.PhotoFeedInterFace;
import zen3.com.photoview.services.PhotoService;

public class ShowImagesList extends AppCompatActivity implements View.OnClickListener {

    protected ImageView btnList;
    protected ImageView btnGrid;
    protected RecyclerView rvImages;
    ArrayList<Photos> mPhotosList;
    LinearLayoutManager mLayoutManager;
    public boolean isViewWithCatalog=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_show_images_list);
        initView();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnList) {
            isViewWithCatalog=true;
            rvImages.setLayoutManager(isViewWithCatalog ? new LinearLayoutManager(this) : new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            rvImages.setAdapter(mPhotosAdapter);
        } else if (view.getId() == R.id.btnGrid) {
            isViewWithCatalog=false;
            rvImages.setLayoutManager(isViewWithCatalog ? new LinearLayoutManager(this) : new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            rvImages.setAdapter(mPhotosAdapter);

        }
    }

    private void initView() {
        btnList = (ImageView) findViewById(R.id.btnList);
        btnList.setOnClickListener(ShowImagesList.this);
        btnGrid = (ImageView) findViewById(R.id.btnGrid);
        btnGrid.setOnClickListener(ShowImagesList.this);
        rvImages = (RecyclerView) findViewById(R.id.rv_images);
        mLayoutManager = new LinearLayoutManager(ShowImagesList.this);
        rvImages.setLayoutManager(mLayoutManager);
        rvImages.setItemAnimator(new DefaultItemAnimator());
        mPhotosList=new ArrayList<>();
        mPhotosAdapter=new PhotosAdapter();
        getPhotosFeed();
    }
    PhotosAdapter mPhotosAdapter;
    public class PhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder viewHolder;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View vPhotos = inflater.inflate(R.layout.item_images_list, parent, false);
            viewHolder = new ViewHolderPhotos(vPhotos);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolderPhotos vh2 = (ViewHolderPhotos) holder;
            configureViewHolderPhotos(vh2, position);
        }

        Photos mPhotos;
        @Override
        public int getItemCount() {
            return mPhotosList.size();
        }

        public void configureViewHolderPhotos(final ViewHolderPhotos holder, final int position) {
            mPhotos=mPhotosList.get(position);
            holder.tv_name.setText(mPhotos.getName());
            holder.tv_description.setText(mPhotos.getDescription());
            Picasso.with(ShowImagesList.this)
                    .load(mPhotos.getImage_url())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.iv_photo);
        }
    }

    class ViewHolderPhotos extends RecyclerView.ViewHolder{

        TextView tv_name,tv_description;
        ImageView iv_photo;

        public ViewHolderPhotos(View itemView) {
            super(itemView);
            tv_name=(TextView)itemView.findViewById(R.id.tv_name);
            tv_description=(TextView)itemView.findViewById(R.id.tv_description);
            iv_photo = (ImageView) itemView.findViewById(R.id.iv_photo);
            iv_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(ShowImagesList.this,PhotoDetailView.class);
                    intent.putExtra("photoId",mPhotosList.get(getAdapterPosition()).getId());
                    startActivity(intent);
                }
            });
        }
    }

    public void getPhotosFeed()
    {
        Helper.showLoadingDialog(ShowImagesList.this);
        String nonce = UUID.randomUUID().toString();

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();

        OAuthParameters authorizer = new OAuthParameters();
        authorizer.consumerKey= ConfigurationManager.ConsumerKey;
        authorizer.token=Helper.getToken(ShowImagesList.this);
        OAuthHmacSigner signer = new OAuthHmacSigner();
        authorizer.signer=signer;
        authorizer.nonce= nonce;
        authorizer.timestamp=ts;
        authorizer.signature="e2n6Mca1rhg2%2Bi8ranKKACqzQPs%3D";
        authorizer.signatureMethod=signer.getSignatureMethod();
        authorizer.version="1.0";
        PhotoFeedInterFace mPhotoFeedInterFace = BaseService.createService(PhotoFeedInterFace.class, authorizer);
        Call<PhotoResponse> mcall=mPhotoFeedInterFace.getPhotosFeed("popular","created_at","20",
                "3","store_download",ConfigurationManager.ConsumerKey,"voted");
        mcall.enqueue(new Callback<PhotoResponse>() {
            @Override
            public void onResponse(Call<PhotoResponse> call, Response<PhotoResponse> response) {
                Helper.hideLoadingDialog();
                if (response.isSuccessful())
                {
                    mPhotosList= (ArrayList<Photos>) response.body().getPhotos();
                    rvImages.setAdapter(mPhotosAdapter);
                    mPhotosAdapter.notifyDataSetChanged();
                }
                else
                    try {
                        Log.i("error",response.errorBody().string().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            @Override
            public void onFailure(Call<PhotoResponse> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }
}
