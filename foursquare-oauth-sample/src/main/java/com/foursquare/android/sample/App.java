package com.foursquare.android.sample;

import android.app.Application;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PersistentTokenStore.init(this);
    }
}
