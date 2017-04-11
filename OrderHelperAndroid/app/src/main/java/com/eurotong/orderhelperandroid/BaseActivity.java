package com.eurotong.orderhelperandroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class BaseActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {       
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        
        //http://mobile-development.org/index.php/android/android-how-to-run-application-in-fullscreen-mode
        // set window flags to display in fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
       
    }
}
