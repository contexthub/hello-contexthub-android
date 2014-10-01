package com.contexthub.hellocontexthub;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.chaione.contexthub.sdk.ContextEvent;
import com.chaione.contexthub.sdk.ContextEventListener;
import com.chaione.contexthub.sdk.ContextHub;

/**
 * Created by andy on 9/26/14.
 */
public class HelloContextHubApp extends Application implements ContextEventListener {

    private static Context instance;

    public HelloContextHubApp() {
        instance = this;
    }

    public static Context getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ContextHub.init(this, "YOUR-APP-ID-HERE");
        ContextHub.getInstance().addContextEventListener(this);
    }

    @Override
    public void onContextEvent(ContextEvent contextEvent) {
        Log.d(getClass().getName(), contextEvent.getEventDetails().toString());
    }
}
