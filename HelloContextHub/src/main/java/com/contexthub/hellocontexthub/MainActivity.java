package com.contexthub.hellocontexthub;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setProgressBarIndeterminate(true);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new GeofencesMapFragment()).commit();
        }
    }
}
