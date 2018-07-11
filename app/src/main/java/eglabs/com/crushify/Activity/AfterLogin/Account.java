package eglabs.com.crushify.Activity.AfterLogin;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import eglabs.com.crushify.Activity.BaseActivity;
import eglabs.com.crushify.R;

public class Account extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.nav_menu_get = "drawer_aftersignin";
        super.onCreate(savedInstanceState);


        /*LayoutInflater inflater = (LayoutInflater) this
                      .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentview = inflater.inflate(R.layout.activity_account, null, false);
        drawerLayout.addView(contentview, 0);
*/
    }
}
