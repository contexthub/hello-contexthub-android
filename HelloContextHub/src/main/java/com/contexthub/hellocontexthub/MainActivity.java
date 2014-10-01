package com.contexthub.hellocontexthub;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setProgressBarIndeterminate(true);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new GeofencesMapFragment()).commit();
        }
    }
}
