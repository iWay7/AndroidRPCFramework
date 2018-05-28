package site.iway.androidrpcframework;

import android.app.Application;

import site.iway.androidhelpers.RPCEngine;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RPCEngine.initialize(2);
    }

}
