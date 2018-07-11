package eglabs.com.crushify.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

import eglabs.com.crushify.Activity.AfterLogin.Activity_MissedConnection;
import eglabs.com.crushify.Activity.AfterLogin.Activity_PublicCrush;
import eglabs.com.crushify.Activity.AfterLogin.Activity_crush;
import eglabs.com.crushify.Activity.BeforeLogin.GenderSelect;
import eglabs.com.crushify.Activity.BeforeLogin.LoginActivity;

import eglabs.com.crushify.R;
import eglabs.com.crushify.SharedPreferences.SaveSharedPreferences;


public class MainActivity extends BaseActivity {
    private final String mainfile="mainscreen";
    private final String username="userid";
    private boolean genderfile;
    //view declaration
    private Button btncrush;
    private Button btnmissedconnection;
    private Button btnpubliccrush;
    //private boolean loggedin;
    private FirebaseUser user;
    private FirebaseAuth firebaseauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context=MainActivity.super.getApplicationContext();
        SaveSharedPreferences sp=new SaveSharedPreferences(context);
        final File gender =sp.filesinit("gender");
        genderfile=sp.CheckSpFile(gender);
        firebaseauth=FirebaseAuth.getInstance();
        user= firebaseauth.getCurrentUser();
        if (user != null) {
            super.nav_menu_get = "drawer_aftersignin";
            super.onCreate(savedInstanceState);
            super.validateuser();
            super.setContentView(R.layout.activity_home);
            viewinit();
            initviewTexts();

            Log.d("Preffile", "pref file and uid file exist");
        }
        else{
            if (genderfile){
                Log.d("UID file","UID file doesn't exist");
                super.nav_menu_get="drawerbeforesignin";
                super.onCreate(savedInstanceState);
                super.setContentView(R.layout.activity_home);
                viewinit();
                initviewTexts();
            }
            else {
                //first time launch
                super.nav_menu_get="no_drawer";
                super.onCreate(savedInstanceState);
                /*LanguageDetector sysLang=new LanguageDetector(getResources().getString(R.string.apikey));
                String systemLanguage=sysLang.devicelanguage;*/
                Intent intentlogin=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intentlogin);
            }
        }
    }
    private void viewinit(){
        btncrush=findViewById(R.id.btncrush);
        btnmissedconnection=findViewById(R.id.btnmissed);
        btnpubliccrush=findViewById(R.id.btnpublic);
        setclicklisteners();
    }
    private boolean checkUsersignin(){
        if (user==null){
            Toast.makeText(getApplicationContext(), "Please SignIn and Continue!cheers", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }
    private void setclicklisteners(){
        btncrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkUsersignin()){
                    Intent intent=new Intent(MainActivity.this,Activity_crush.class);
                    startActivity(intent);
                }
            }
        });
        btnmissedconnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkUsersignin()){
                    Intent intent=new Intent(MainActivity.this,Activity_MissedConnection.class);
                    startActivity(intent);
                }
            }
        });
        btnpubliccrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkUsersignin()){
                    Intent intent=new Intent(MainActivity.this,Activity_PublicCrush.class);
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this
        );

        // set title
        alertDialogBuilder.setTitle("Do you want to exit the application");

        // set dialog message
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // // show it
        alertDialog.show();


    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("lifecycle","onStart invoked");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("lifecycle","onResume invoked");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("lifecycle","onPause invoked");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("lifecycle","onStop invoked");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("lifecycle","onRestart invoked");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
        Log.d("lifecycle","onDestroy invoked");
    }

}
