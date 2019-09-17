package com.khudrosoft.visiontube.Update;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class UpdateHelper {

    public static String KEY_UPDATE_ENABLE = "isUpdate";
    public static String KEY_UPDATE_VERSION = "version";
    public static String KEY_UPDATE_URL = "updateUrl";





    public interface onUpdateCheckListener {

        void onUpdateCheckListener(String urlApp);



    }


    public static Builder with(Context context) {

        return new Builder(context);
    }


    private onUpdateCheckListener onUpdateCheckListener;
    private Context context;

    public UpdateHelper(UpdateHelper.onUpdateCheckListener onUpdateCheckListener, Context context) {
        this.onUpdateCheckListener = onUpdateCheckListener;
        this.context = context;
    }





    private static String getAppVersion(Context context) {
        String result = "";
        try {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static class Builder {

        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public onUpdateCheckListener onUpdateCheckListener;


       public Builder onUpdateCheck(onUpdateCheckListener onUpdateCheckListener){
           this.onUpdateCheckListener = onUpdateCheckListener;
           return this;
       }

        public  void check() {
            FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

            if (firebaseRemoteConfig.getBoolean(KEY_UPDATE_ENABLE)) {


                String currentVersion = firebaseRemoteConfig.getString("KEY_UPDATE_VERSION");
                String appVersion = getAppVersion(context);
                String updateUrl = firebaseRemoteConfig.getString("KEY_UPDATE_URL");

                if (!TextUtils.equals(currentVersion, appVersion) && onUpdateCheckListener != null) {
                    onUpdateCheckListener.onUpdateCheckListener(updateUrl);


                }
            }
        }

    }



}
