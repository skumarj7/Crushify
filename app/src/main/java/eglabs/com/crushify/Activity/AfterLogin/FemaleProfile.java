package eglabs.com.crushify.Activity.AfterLogin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import eglabs.com.crushify.Activity.BaseActivity;
import eglabs.com.crushify.R;

public class FemaleProfile extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.nav_menu_get = "drawer_aftersignin";
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crush_profile);
    }
}
