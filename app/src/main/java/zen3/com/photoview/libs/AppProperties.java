package zen3.com.photoview.libs;

/**
 * Created by SantoshT on 12/4/2017.
 */

public class AppProperties {
    private static AppProperties mInstance=null;

    public String UserToken;

    public String UserId;

    public static synchronized AppProperties getmInstance() {
        if (null == mInstance){
            mInstance = new AppProperties();
        }
        return mInstance;
    }
}
