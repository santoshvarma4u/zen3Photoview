package zen3.com.photoview.Helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tskva on 12/5/2017.
 */

public class Helper {
    static ProgressDialog pDialog;
    private static SharedPreferences mSharedPreferences;

    public static void showLoadingDialog(Context mContext) {
        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Please wait..");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public static void hideLoadingDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }
    public static boolean setToken(String token, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("oAuthToken", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("oAuthToken", token);
        return editor.commit();
    }

    public static String getToken(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("oAuthToken", 0);
        return mSharedPreferences.getString("oAuthToken", null);
    }



}
