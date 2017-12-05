package zen3.com.photoview.libs;

import android.app.Application;
import android.content.Context;

/**
 * Created by SantoshT on 12/4/2017.
 */

public class PhotoViewApplication extends Application {
    public static final String TAG = "PhotoViewApplication";
    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
    }

}
