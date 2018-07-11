package eglabs.com.crushify.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import eglabs.com.crushify.Activity.BeforeLogin.LoginActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eglabs.com.crushify.Activity.AfterLogin.*;

import eglabs.com.crushify.FirestoreActions;
import eglabs.com.crushify.LanguageDetector;
import eglabs.com.crushify.LocationTracker;
import eglabs.com.crushify.R;
import eglabs.com.crushify.SharedPreferences.SaveSharedPreferences;
import eglabs.com.crushify.Utils;
import eglabs.com.crushify.ViewTextmanipulation;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;


/**
 * Created by selva on 4/23/2018.
 */

public class BaseActivity extends AppCompatActivity {


    protected DrawerLayout drawerLayout;
    //protected DrawerLayout drawerlayoutsignin;
    protected NavigationView navigationview;
    //protected NavigationView navigationViewlogin;
    protected String nav_menu_get;
    protected Toolbar toolbar;
    protected MenuItem menuitem;
    protected FrameLayout view_stub;
    public boolean userExists=false;
    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME ="Home";
    private static final String TAG_OWN = "OwnProfile";
    private static final String TAG_CRUSH= "CrushProfile";
    private static final String TAG_LOGIN= "Login";
    private static final String TAG_MATCH = "MatchStatus";
    private static final String TAG_CHAT = "Chat";
    private static final String TAG_CREATEC="CreateCrushProfile";
    private static final String TAG_LOCATION="LocationInformation";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    protected TextView txtname;
    private View navheader;
    protected String gender;
    public FirebaseAuth firebaseauth;
    public FirebaseUser firebaseUser;
    public String uid;
    public int resid;
    protected boolean ownprofileexists=false;
    protected boolean crushprofileexists=false;
    public String layoutname;
    //language file
    private final String LANGUAGEFILE="language";
    public String language;
    //fire store db
    FirebaseFirestore db;
    //firestore collection details
    final private String MALEPROFILE="Male Profiles";
    final private String FEMALEPROFILE="Female Profile";
    final private String CRUSHPRFILE="Crush Profiles";
    final private String TSPROFILE="Ts Profiles";
    final private String USERSTATS="User Stats";

    //view text manipulation variables
    ViewTextmanipulation viewtxtman;
    LanguageDetector languageDetector;
    //sp file
    protected SaveSharedPreferences sp=new SaveSharedPreferences(BaseActivity.this);
    //profile file
    final String profilefilename="profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //checkspfiles();
        gender=sp.getgender();
        db=FirebaseFirestore.getInstance();
        if (nav_menu_get.equals("drawer_aftersignin")){
            super.setContentView(R.layout.drawer_main);
            view_stub=(FrameLayout) findViewById(R.id.content_frame);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionBar=getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            //drawer
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_main_drawer);
            navigationview = (NavigationView) findViewById(R.id.nav_view);
            //navigation header view
            navheader=navigationview.getHeaderView(0);
            txtname=(TextView)navheader.findViewById(R.id.name);

            // load toolbar titles from string resources
            activityTitles = getResources().getStringArray(R.array.nav_item_afterlogin_activity_titles);

            // load nav menu header data
            loadNavHeader();

            // initializing navigation menu
            setUpNavigationView();

            if (savedInstanceState == null) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeclass();
            }
            firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
            uid=firebaseUser.getUid();
            //language
            LanguageDetector languageDetector=new LanguageDetector(getResources().getString(R.string.apikey));
            String deviceLanguage=languageDetector.devicelanguage;
            if (!(deviceLanguage).equalsIgnoreCase("English")){
                ViewGroup viewgroup=(ViewGroup)findViewById(android.R.id.content);
                viewtxtman=new ViewTextmanipulation(viewgroup,deviceLanguage,getResources().getString(R.string.apikey));
            }
            //change view language
        }
        else if (nav_menu_get.equals("no_drawer")){
            super.setContentView(R.layout.no_drawer);
            view_stub=findViewById(R.id.frame_no_drawer);
        }
        else{super.setContentView(R.layout.drawer_beforesignin);
            view_stub=findViewById(R.id.content_frame_beforesigin);
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionBar=getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            //navigation header view
            drawerLayout=(DrawerLayout) findViewById(R.id.drawer_login);
            navigationview=(NavigationView) findViewById(R.id.nav_view_login);
            navheader=navigationview.getHeaderView(0);
            txtname=(TextView)navheader.findViewById(R.id.name);
            // load toolbar titles from string resources
            activityTitles = getResources().getStringArray(R.array.nav_item_beforesignin_titles);

            // load nav menu header data
            loadNavHeader();

            // initializing navigation menu
            setUpNavigationView();

            if (savedInstanceState == null) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeclass();
            }
        }

        Runnable checkinternetconn=new Runnable() {
            @Override
            public void run() {
                try {
                    if(!isConnected()){
                        Toast.makeText(getApplicationContext(), "Not Connected to internet!Try Again", Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        AsyncTask.execute(checkinternetconn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d("NICK", "CWECNEWKVNERIPNVIEWNFVIPEWNVIPEWNVPIEWNVPIEWNVPIEWNVPIRWNVPRWVPO");
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
                    //drawerLayout = (DrawerLayout) findViewById(R.id.drawer_main);
                    drawerLayout.openDrawer(GravityCompat.START);
                    return true;
            }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void setContentView(int layoutResID) {
        if (view_stub != null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            View stubView = inflater.inflate(layoutResID, view_stub, false);
            view_stub.addView(stubView, lp);
            this.resid=layoutResID;
            this.layoutname=getResources().getResourceEntryName(layoutResID);
        }
    }

    @Override
    public void setContentView(View view) {
        if (view_stub != null) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            view_stub.addView(view, lp);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (view_stub != null) {
            view_stub.addView(view, params);
        }
    }

    private void loadNavHeader(){
        txtname.setText(R.string.headertxt);
    }
    /***
     * Returns respected activity that user
     * selected from navigation menu
     */
    private void loadHomeclass(){
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getClass().getSimpleName()==CURRENT_TAG) {

            drawerLayout.closeDrawers();

            return;
        }


        //Closing drawer on item click
        drawerLayout.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }


    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }
    private void selectNavMenu() {
        navigationview.getMenu().getItem(navItemIndex).setChecked(true);
    }
    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (nav_menu_get.equals("drawer_aftersignin")){
                //Check to see which item was being clicked and perform appropriate action
                    switch (menuItem.getItemId()) {
                        //Replacing the main content with ContentFragment Which is our Inbox View;
                        case R.id.home:
                            //start home activity
                            navItemIndex = 0;
                            startActivity(new Intent(BaseActivity.this,MainActivity.class));
                            drawerLayout.closeDrawers();
                            return true;
                        case R.id.ownprofile:
                            if (gender.equals("male")){
                                navItemIndex = 1;
                                startActivity(new Intent(BaseActivity.this,MaleProfile.class));
                                drawerLayout.closeDrawers();
                                return true;
                            }
                            else if(gender.equals("female")){
                                navItemIndex = 1;
                                startActivity(new Intent(BaseActivity.this,FemaleProfile.class));
                                drawerLayout.closeDrawers();
                                return true;
                            }
                            else if (gender.equals("gay")){
                                navItemIndex = 1;
                                startActivity(new Intent(BaseActivity.this,MaleProfile.class));
                                drawerLayout.closeDrawers();
                                return true;
                            }
                            else if (gender.equals("lesbian")){
                                navItemIndex = 1;
                                startActivity(new Intent(BaseActivity.this,FemaleProfile.class));
                                drawerLayout.closeDrawers();
                                return true;
                            }
                            else{
                                //ts activity
                                navItemIndex = 1;
                                startActivity(new Intent(BaseActivity.this,TsProfile.class));
                                drawerLayout.closeDrawers();
                                return true;
                            }
                        case R.id.crushprofile:
                            if (gender.equals("male")){
                                navItemIndex = 2;
                                startActivity(new Intent(BaseActivity.this,FemaleProfile.class));
                                drawerLayout.closeDrawers();
                                return true;
                            }
                            else if(gender.equals("female")){
                                navItemIndex = 2;
                                startActivity(new Intent(BaseActivity.this,MaleProfile.class));
                                drawerLayout.closeDrawers();
                                return true;
                            }
                            else if(gender.equals("gay")){
                                navItemIndex = 2;
                                startActivity(new Intent(BaseActivity.this,MaleProfile.class));
                                drawerLayout.closeDrawers();
                                return true;
                            }
                            else if (gender.equals("lesbian")){
                                navItemIndex = 2;
                                startActivity(new Intent(BaseActivity.this,FemaleProfile.class));
                                drawerLayout.closeDrawers();
                                return true;
                            }
                            else{
                                //ts activity
                                navItemIndex = 2;
                                startActivity(new Intent(BaseActivity.this,TsProfile.class));
                                drawerLayout.closeDrawers();
                                return true;
                            }
                        case R.id.createownprofile:
                            //check for the profile status for own and crush profile
                            checkprofilesp();
                            if (gender.equals("male")){
                                if (ownprofileexists==true && crushprofileexists==true){
                                    drawerLayout.closeDrawers();
                                    profileexistdialog(getString(R.string.ownyescrushyes));
                                    return true;
                                }else if(ownprofileexists==true && crushprofileexists==false){
                                    drawerLayout.closeDrawers();
                                    profileexistdialog(getString(R.string.ownyescurshno));
                                    return true;
                                }else{
                                    navItemIndex = 4;
                                    drawerLayout.closeDrawers();
                                    startActivity(new Intent(BaseActivity.this,CreateMaleProfile.class));
                                    return true;
                                }
                            }
                            else if(gender.equals("female")){
                                if (ownprofileexists==true && crushprofileexists==true){
                                    drawerLayout.closeDrawers();
                                    profileexistdialog(getString(R.string.ownyescrushyes));
                                    return true;
                                }else if(ownprofileexists==true && crushprofileexists==false){
                                    profileexistdialog(getString(R.string.ownyescurshno));
                                    return true;
                                }else{
                                navItemIndex = 4;
                                    drawerLayout.closeDrawers();
                                startActivity(new Intent(BaseActivity.this,CreateFemaleProfile.class));
                                return true;}
                            }
                            else if (gender.equals("gay")){
                                if (ownprofileexists==true && crushprofileexists==true){
                                    drawerLayout.closeDrawers();
                                    profileexistdialog(getString(R.string.ownyescrushyes));
                                    return true;
                                }else if(ownprofileexists==true && crushprofileexists==false){
                                    drawerLayout.closeDrawers();
                                    profileexistdialog(getString(R.string.ownyescurshno));
                                    return true;
                                }else{
                                    navItemIndex = 4;
                                    drawerLayout.closeDrawers();
                                    startActivity(new Intent(BaseActivity.this,CreateMaleProfile.class));
                                    return true;
                                }
                            }
                            else if (gender.equals("lesbian")){
                                if (ownprofileexists==true && crushprofileexists==true){
                                    drawerLayout.closeDrawers();
                                    profileexistdialog(getString(R.string.ownyescrushyes));
                                    return true;
                                }else if(ownprofileexists==true && crushprofileexists==false){
                                    drawerLayout.closeDrawers();
                                    profileexistdialog(getString(R.string.ownyescurshno));
                                    return true;
                                }else{
                                navItemIndex = 4;
                                    drawerLayout.closeDrawers();
                                startActivity(new Intent(BaseActivity.this,CreateFemaleProfile.class));
                                return true;}
                            }
                            else{
                                //ts activity
                                if (ownprofileexists==true && crushprofileexists==true){
                                    drawerLayout.closeDrawers();
                                    profileexistdialog(getString(R.string.ownyescrushyes));
                                    return true;
                                }else if(ownprofileexists==true && crushprofileexists==false){
                                    drawerLayout.closeDrawers();
                                    profileexistdialog(getString(R.string.ownyescurshno));
                                    return true;
                                }else{
                                navItemIndex = 4;
                                    drawerLayout.closeDrawers();
                                startActivity(new Intent(BaseActivity.this,CreateTsProfile.class));

                                return true;}
                            }
                        case R.id.createadditionalcrushprofile:
                            //check for the profile status for own and crush profile
                            checkprofilesp();
                            if (crushprofileexists==true){
                                drawerLayout.closeDrawers();
                                profileexistdialog(getString(R.string.addionalcrushexist));
                                return true;
                            }else{
                            navItemIndex = 6;
                            startActivity(new Intent(BaseActivity.this,CreateAdditionalCrushProfiles.class));
                            drawerLayout.closeDrawers();
                            return true;}
                        case R.id.createcrushprofile:
                            //check for the profile status for own and crush profile
                            checkprofilesp();
                            if (gender.equals("male")){
                                if (ownprofileexists==true && crushprofileexists==true){
                                    drawerLayout.closeDrawers();
                                    profileexistdialog(getString(R.string.ownyescrushyes));
                                    return true;
                                }else if(ownprofileexists==true && crushprofileexists==false){
                                    navItemIndex = 7;
                                    startActivity(new Intent(BaseActivity.this,CreateFemaleProfile.class));
                                    drawerLayout.closeDrawers();
                                    return true;
                                }else{  drawerLayout.closeDrawers();
                                        profileexistdialog(getString(R.string.ownnocrushno));
                                    return true;}
                            }
                            else if(gender.equals("female")){
                                if (ownprofileexists==true && crushprofileexists==true){
                                    drawerLayout.closeDrawers();
                                    profileexistdialog(getString(R.string.ownyescrushyes));
                                    return true;
                                }else if(ownprofileexists==true && crushprofileexists==false){
                                    navItemIndex = 7;
                                    startActivity(new Intent(BaseActivity.this,CreateMaleProfile.class));
                                    drawerLayout.closeDrawers();
                                    return true;
                                }else{  drawerLayout.closeDrawers();
                                        profileexistdialog(getString(R.string.ownnocrushno));
                                        return true;}
                            }
                            else if (gender.equals("gay")){
                                if (ownprofileexists==true && crushprofileexists==true){
                                    drawerLayout.closeDrawers();
                                    profileexistdialog(getString(R.string.ownyescrushyes));
                                    return true;
                                }else if(ownprofileexists==true && crushprofileexists==false){
                                    navItemIndex = 7;
                                    startActivity(new Intent(BaseActivity.this,CreateMaleProfile.class));
                                    drawerLayout.closeDrawers();
                                    return true;
                                }else{  drawerLayout.closeDrawers();
                                        profileexistdialog(getString(R.string.ownnocrushno));
                                        return true;}
                            }
                            else if (gender.equals("lesbian")){
                                if (ownprofileexists==true && crushprofileexists==true){
                                    drawerLayout.closeDrawers();
                                    profileexistdialog(getString(R.string.ownyescrushyes));
                                    return true;
                                }else if(ownprofileexists==true && crushprofileexists==false){
                                    navItemIndex = 7;
                                    startActivity(new Intent(BaseActivity.this,CreateFemaleProfile.class));
                                    drawerLayout.closeDrawers();
                                    return true;
                                }else{
                                    drawerLayout.closeDrawers();
                                    profileexistdialog(getString(R.string.ownnocrushno));
                                    return true;}
                            }
                            else{
                                //ts activity
                                if (ownprofileexists==true && crushprofileexists==true){
                                    drawerLayout.closeDrawers();
                                    profileexistdialog(getString(R.string.ownyescrushyes));
                                    return true;
                                }else if(ownprofileexists==true && crushprofileexists==false){
                                    navItemIndex = 7;
                                    startActivity(new Intent(BaseActivity.this,CreateTsProfile.class));
                                    drawerLayout.closeDrawers();
                                    return true;
                                }else{drawerLayout.closeDrawers();
                                        profileexistdialog(getString(R.string.ownnocrushno));
                                    return true;}
                            }
                        case R.id.match_status:
                            navItemIndex = 3;
                            startActivity(new Intent(BaseActivity.this,MatchStatus.class));
                            drawerLayout.closeDrawers();
                            return true;

                        case R.id.chat:
                            navItemIndex = 5;
                            startActivity(new Intent(BaseActivity.this,Chat.class));
                            drawerLayout.closeDrawers();
                            return true;
                        case R.id.logout:
                            navItemIndex = 8;
                            FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                            firebaseAuth.signOut();
                            Intent intent=new Intent(BaseActivity.this,MainActivity.class);
                            startActivity(intent);
                            return true;
                        default:
                            navItemIndex = 0;
                }}else{
                    switch (menuItem.getItemId()) {
                        //Replacing the main content with ContentFragment Which is our Inbox View;
                        case R.id.Login:
                            //start home activity
                            navItemIndex = 0;
                            startActivity(new Intent(BaseActivity.this,LoginActivity.class));
                            drawerLayout.closeDrawers();
                            return true;
                    }
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeclass();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeclass();
                return;
            }
        }

        super.onBackPressed();
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    //checking internet connection
    public boolean isConnected() throws InterruptedException, IOException
    {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);}

    private boolean userexists(String userid,String collection){
        DocumentReference userref=db.collection(collection).document(userid);
        userref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userExists=true;
                    } else {
                        userExists=false;
                    }
                } else {
                    Log.d("tag", "get failed with ", task.getException());
                }
            }
        });

        return userExists;
    }
    private void profileexistdialog(String dialogmessage){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                BaseActivity.this
        );

        // set title
        alertDialogBuilder.setTitle("Profile");

        // set dialog message
        alertDialogBuilder
                .setMessage(dialogmessage)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("OK", "OK clikced");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        Log.d("OK", "OK clikced");
                        dialog.cancel();
                    }
                });

        // create alert dialog
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        // // show it
        alertDialog.show();
    }
    private void checkprofilesp(){
        Context context=BaseActivity.this;
        final File profilefile = new File(context.getFilesDir().getPath() + getPackageName() + "/shared_prefs/" + getPackageName() + "." + profilefilename + ".xml");
        //profile sp file
        boolean profilefileexists=sp.CheckSpFile(profilefile);
        if (profilefileexists){
            ownprofileexists=sp.fetchprofilestatus("OWNPROFILE");
            crushprofileexists=sp.fetchprofilestatus("CRUSHPROFILE");
        }else{
            //if user unistalled and re-installed app
            checkuser();
        }
    }
    protected void checkuser(){
        if (gender.equals("male")){
            ownprofileexists=userexists(uid,MALEPROFILE);
            crushprofileexists=userexists(uid,CRUSHPRFILE);
        }else if(gender.equals("female")){
            ownprofileexists=userexists(uid,FEMALEPROFILE);
            crushprofileexists=userexists(uid,CRUSHPRFILE);
        }else if(gender.equals("gay")){
            ownprofileexists=userexists(uid,MALEPROFILE);
            crushprofileexists=userexists(uid,CRUSHPRFILE);
        }else if(gender.equals("lesbian")){
            ownprofileexists=userexists(uid,FEMALEPROFILE);
            crushprofileexists=userexists(uid,CRUSHPRFILE);
        }else{
            ownprofileexists=userexists(uid,TSPROFILE);
            crushprofileexists=userexists(uid,CRUSHPRFILE);
        }
    }
    protected void checkLangfile(){
        final File languagefile = sp.filesinit("translatedfile");
        LanguageDetector languageDetector=new LanguageDetector();
        FirestoreActions fsa=new FirestoreActions();
        String deviceLanguage=languageDetector.devicelanguage;
        if (!deviceLanguage.equalsIgnoreCase("English")){
            if (!sp.CheckSpFile(languagefile)){
                if (fsa.fireStoreDocExists("Localization",deviceLanguage)){
                    Map<Object,Object> translatedtextmap=new HashMap<>();
                    translatedtextmap=fsa.getFirestoredata("Localization",deviceLanguage);
                    sp.saveTranslatedLanguage(translatedtextmap);
                }
            }
        }
    }
    protected void validateuser(){
        final File genderfile=sp.filesinit("gender");
        FirebaseUser user;
        firebaseauth=FirebaseAuth.getInstance();
        user= firebaseauth.getCurrentUser();
        uid=user.getUid();
        String hasedFBuid= Utils.computehash(uid);
        boolean genderfileexists=sp.CheckSpFile(genderfile);
        if (!genderfileexists){
            checkspfiles();
        }else{
            String haseduid=sp.gethaseduid();
            if (!haseduid.equalsIgnoreCase(hasedFBuid)){
                checkspfiles();
            }
        }
    }
    protected void checkspfiles(){
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        FirestoreActions fsa=new FirestoreActions();
        if (firebaseUser !=null){
            uid=firebaseUser.getUid();
            if (fsa.fireStoreDocExists("user Details",uid)){
                sp.savespfiles(uid,"user Details");
            }
        }
    }
    protected void initviewTexts(){
        File languagefile=sp.filesinit("translatedfile");
        LanguageDetector languageDetector=new LanguageDetector(getResources().getString(R.string.apikey));
        if (!(languageDetector.devicelanguage).equalsIgnoreCase("English")){
            boolean langfileexists=sp.CheckSpFile(languagefile);
            if (langfileexists){
                ViewTextmanipulation vm=new ViewTextmanipulation(sp.getTranslatedViewtext());
                vm.setViewText();
            }else{ FirestoreActions firestoreActions=new FirestoreActions();
                if (firestoreActions.fireStoreDocExists("Localization",languageDetector.devicelanguage)) {
                    checkLangfile();
                    initviewTexts();
                }else{
                    ViewTextmanipulation viewtxtman;
                    //change view language
                    ViewGroup viewgroup =findViewById(android.R.id.content);
                    viewtxtman = new ViewTextmanipulation(viewgroup,languageDetector.devicelanguage, getResources().getString(R.string.apikey));
                    viewtxtman.translatetextview();
                    viewtxtman.setViewText();
                    viewtxtman.saveinfirestore("Localization",languageDetector.devicelanguage,viewtxtman.viewtextmaplocal);
                    viewtxtman.saveinSP(viewtxtman.viewtextmaplocal,BaseActivity.super.getApplicationContext());
                }
            }
            }

    }
    protected void onStart() {
        super.onStart();
        Log.d("lifecycle","onStart invoked");
        //locationtracker=new LocationTracker(this,BaseActivity.this);
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
 /* public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                URL url = new URL("http://www.google.com/");
                HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000); // mTimeout is in seconds
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                Log.i("warning", "Error checking internet connection", e);
                return false;
            }
        }

        return false;

    }*/
//    private void loadHomeFragment() {
//        // selecting appropriate nav menu item
//        selectNavMenu();
//
//        // set toolbar title
//        setToolbarTitle();
//
//        // if user select the current navigation menu again, don't do anything
//        // just close the navigation drawer
//        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
//            drawerLayout.closeDrawers();
//
//            return;
//        }
//        Runnable mPendingRunnable = new Runnable() {
//            @Override
//            public void run() {
//                // update the main content by replacing fragments
//                Fragment fragment = getHomeFragment();
//                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
//                        android.R.anim.fade_out);
//                fragmentTransaction.replace(R.id.content_frame, fragment, CURRENT_TAG);
//                fragmentTransaction.commitAllowingStateLoss();
//            }
//        };
//
//        // If mPendingRunnable is not null, then add to the message queue
//        if (mPendingRunnable != null) {
//            mHandler.post(mPendingRunnable);
//        }
//
//
//        //Closing drawer on item click
//        drawerLayout.closeDrawers();
//
//        // refresh toolbar menu
//        invalidateOptionsMenu();
//    }



//    private Fragment getHomeFragment() {
//        switch (navItemIndex) {
//            case 0:
//                // home
//                Home home = new Home();
//                return home;
//            case 1:
//                // photos
//                CrushProfile crushprofile = new CrushProfile();
//                return crushprofile;
//            case 2:
//                // movies fragment
//                LocationInformation locationinfo = new LocationInformation();
//                return locationinfo;
//            case 3:
//                // notifications fragment
//                MatchStatus matchstatus = new MatchStatus();
//                return matchstatus;
//
//            case 4:
//                // settings fragment
//                Chat chat = new Chat();
//                return chat;
//            case 5:
//                // settings fragment
//                Login login = new Login();
//                return login;
//            case 6:
//                OwnProfile ownprofile = new OwnProfile();
//                return ownprofile;
//            case 7:
//                CreateCrushProfile createcprofile=new CreateCrushProfile();
//                return createcprofile;
//            default:
//                return new Home();
//        }
//    }
/*protected void replaceContentLayout(int sourceId, int destinationId) {
        View contentLayout = findViewById(destinationId);

        ViewGroup parent = (ViewGroup) contentLayout.getParent();
        int index = parent.indexOfChild(contentLayout);

        parent.removeView(contentLayout);
        contentLayout = getLayoutInflater().inflate(sourceId, parent, false);
        parent.addView(contentLayout, index);
    }*/
