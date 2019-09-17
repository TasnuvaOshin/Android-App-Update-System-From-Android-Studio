package com.khudrosoft.visiontube.Update;

import android.app.Application;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        Map<String,Object> defaultValue = new HashMap<>();
        defaultValue.put(UpdateHelper.KEY_UPDATE_ENABLE,false);
        defaultValue.put(UpdateHelper.KEY_UPDATE_VERSION,1.0);
        defaultValue.put(UpdateHelper.KEY_UPDATE_URL,"https://play.google.com/store/apps/details?id=apps.searchme.sm");

   firebaseRemoteConfig.setDefaults(defaultValue);
   firebaseRemoteConfig.fetch(5).addOnCompleteListener(new OnCompleteListener<Void>() {
       @Override
       public void onComplete(@NonNull Task<Void> task) {
             if(task.isSuccessful()){

                 firebaseRemoteConfig.activateFetched();
             }
       }
   });
    }
}
