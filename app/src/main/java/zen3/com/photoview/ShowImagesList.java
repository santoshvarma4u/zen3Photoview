package zen3.com.photoview;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.googleapis.auth.oauth2.OAuth2Utils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import zen3.com.photoview.Helpers.Helper;
import zen3.com.photoview.Helpers.HttpCallResponse;
import zen3.com.photoview.libs.ConfigurationManager;
import zen3.com.photoview.listner.OnLoadMoreListener;
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
    private int visibleThreshold = 20;
    public boolean isViewWithCatalog=true;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    int mLoadStartCount=0;
    int mLoadEndCount=0;

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
            rvImages.setLayoutManager(isViewWithCatalog ? mLayoutManager : staggeredGridLayoutManager);
            rvImages.setAdapter(mPhotosAdapter);
        } else if (view.getId() == R.id.btnGrid) {
            isViewWithCatalog=false;
            rvImages.setLayoutManager(isViewWithCatalog ? mLayoutManager : staggeredGridLayoutManager);
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
        staggeredGridLayoutManager =new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        getPhotosFeed(0,20);

        mPhotosAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("inloadmore", isLoading+"Load More");
                if(isLoading)
                {
                    mPhotosList.add(null);
                    mPhotosAdapter.notifyItemInserted(mPhotosList.size() - 1);
                    getPhotosFeed(mLoadStartCount,mLoadEndCount);
                }

            }
        });
    }
    PhotosAdapter mPhotosAdapter;
    int[] lastPositions=null;
    public boolean isLoading=false;
    public class PhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        private OnLoadMoreListener mOnLoadMoreListener;


        private int lastVisibleItem, totalItemCount;


        public PhotosAdapter()
        {
            rvImages.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if(isViewWithCatalog)
                    {
                        totalItemCount = mLayoutManager.getItemCount();

                        lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

                        if (!isLoading && totalItemCount <= (lastVisibleItem + 1)) {
                            if (mOnLoadMoreListener != null) {
                                isLoading = true;
                                mOnLoadMoreListener.onLoadMore();
                            }

                            //  Log.i("reached last",isLoading+","+lastVisibleItem);
                        }

                    }
                    else
                    {

                        totalItemCount = staggeredGridLayoutManager.getItemCount();
                        if (lastPositions == null)
                            lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                        lastPositions = staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(lastPositions);
                        lastVisibleItem = Math.max(lastPositions[0], lastPositions[1]);
                        Log.i("reached last",totalItemCount+","+lastVisibleItem);
                        if (!isLoading && totalItemCount <= (lastVisibleItem + 1)) {
                            if (mOnLoadMoreListener != null) {
                                isLoading = true;
                                mOnLoadMoreListener.onLoadMore();
                            }

                        }

                    }


                }
            });
        }

        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                RecyclerView.ViewHolder viewHolder;
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View vPhotos = inflater.inflate(R.layout.item_images_list, parent, false);
                viewHolder = new ViewHolderPhotos(vPhotos);
                return viewHolder;
            }
            else if (viewType == VIEW_TYPE_LOADING)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
                return new LoadingViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ViewHolderPhotos) {
                ViewHolderPhotos vh2 = (ViewHolderPhotos) holder;
                configureViewHolderPhotos(vh2, position);
            }
            else
            {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }

        }

        @Override
        public int getItemViewType(int position) {
            return mPhotosList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        Photos mPhotos;
        @Override
        public int getItemCount() {
            return mPhotosList== null ? 0 : mPhotosList.size();
        }

        public void setLoaded() {
            isLoading = false;
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

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
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

    public void getPhotosFeed(final int startCount, final int endCount)
    {
        if(!isLoading)
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
        Call<PhotoResponse> mcall=mPhotoFeedInterFace.getPhotosFeed("popular","created_at",endCount+"",
                "3","store_download",ConfigurationManager.ConsumerKey,"voted");
        mcall.enqueue(new Callback<PhotoResponse>() {
            @Override
            public void onResponse(Call<PhotoResponse> call, Response<PhotoResponse> response) {
                if(!isLoading)
                    Helper.hideLoadingDialog();


                if (response.isSuccessful())
                {
                    mLoadStartCount=startCount+20;
                    mLoadEndCount=endCount+20;

                    if(isLoading && mPhotosList.size() > 0)
                    {
                        mPhotosList.remove(mPhotosList.size()-1);
                        mPhotosAdapter.notifyItemRemoved(mPhotosList.size());
                    }
                    ArrayList<Photos> mResponsePhotos= (ArrayList<Photos>) response.body().getPhotos();

                    //insted of adding direct list.. add items into list for load multiple times
                    for(Photos mPhotos:mResponsePhotos)
                    {
                      mPhotosList.add(mPhotos);
                    }
                    rvImages.setAdapter(mPhotosAdapter);
                    mPhotosAdapter.notifyDataSetChanged();


                    if(isLoading) {
                        rvImages.scrollToPosition(endCount - 20);
                        mPhotosAdapter.setLoaded();
                    }
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
