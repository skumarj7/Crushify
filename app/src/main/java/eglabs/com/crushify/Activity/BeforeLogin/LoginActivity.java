package eglabs.com.crushify.Activity.BeforeLogin;

import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.app.ProgressDialog;
//import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import eglabs.com.crushify.Activity.AfterLogin.ResetPassword;
import eglabs.com.crushify.Activity.BaseActivity;
import eglabs.com.crushify.Activity.MainActivity;
import eglabs.com.crushify.FirestoreActions;
import eglabs.com.crushify.Fragments.Newuser;
import eglabs.com.crushify.LanguageDetector;
import eglabs.com.crushify.R;
import eglabs.com.crushify.SharedPreferences.SaveSharedPreferences;
import eglabs.com.crushify.Utils;
import eglabs.com.crushify.ViewTextmanipulation;

public class LoginActivity extends BaseActivity {

    private static final String TAG="EmailPasswordAuth";
    private EditText emailET;
    private EditText passwordET;
    private EditText emailNewET;
    private EditText passwordNewET;
    private EditText passwordconfirmET;
    final String username="userid";
    private boolean loggedin;
    private boolean langfileexists;
    private FirebaseAuth firebaseAuth;
    protected String[] userdetails;
    protected String showNextAction;
    private final String LANGUAGEFILE="language";
    //button
    private Button btn_login;
    private Button btn_newuser;
    private Button btn_forgotpass;

    @VisibleForTesting
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)    {
        Context context=LoginActivity.super.getApplicationContext();
        super.nav_menu_get="no_drawer";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        hideProgressDialog();
        SaveSharedPreferences sp=new SaveSharedPreferences(context);
        final File uid=sp.filesinit("uid");
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        btn_login= findViewById(R.id.login_b);
        btn_newuser=findViewById(R.id.newuser_b);
        btn_forgotpass=findViewById(R.id.btnforgotpass);

        super.initviewTexts();

        loggedin=sp.CheckSpFile(uid);
        if (loggedin){
            userdetails=sp.getlogin();
            if (userdetails[0]!=null && userdetails[1]!=null){
                emailET.setText(userdetails[0]);
                passwordET.setText(userdetails[1]);
            }
        }

        firebaseAuth = FirebaseAuth.getInstance();

        setButtonListeners();

    }
    private void setButtonListeners(){
        //login button

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRegistrationLogin();

            }
        });

        btn_newuser.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent newuserintent=new Intent(LoginActivity.this,NewUser.class);
                startActivity(newuserintent);
                /*FragmentManager fragmentManager=getFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                Newuser newuserfragment=new Newuser();
                fragmentTransaction.add(R.id.fragframe,newuserfragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
                //setContentView(R.layout.activity_newuser);
                //btn_createaccount();
            }
        });
        //reset password

        btn_forgotpass.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginActivity.this, ResetPassword.class);
                i.putExtra("constraintname","resetpass");
                startActivity(i);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    /*private void btn_createaccount(){
        Button btn_createaccount=findViewById(R.id.createaccount_b);
        btn_createaccount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                emailNewET=(EditText)findViewById(R.id.email);
                passwordNewET=(EditText)findViewById(R.id.password);
                final String email=emailNewET.getText().toString();
                final String password=passwordNewET.getText().toString();
                passwordconfirmET=(EditText)findViewById(R.id.confirmpassword);
                final String confirmpassword=passwordconfirmET.getText().toString();
                if (!validateEmailPass(email, password,"yes")) {
                    return;
                }
                if (password.equalsIgnoreCase(confirmpassword)){
                    showProgressDialog();
                    registerAccount(email, password);
                }
                else{
                    Toast.makeText(LoginActivity.this,
                            "Password's do not match! Verify and login again",
                            Toast.LENGTH_SHORT).show();
                }
            }}
        );
    }*/
    private void  handleRegistrationLogin(){
        final String email = emailET.getText().toString();
        final String password = passwordET.getText().toString();

        if (!validateEmailPass(email, password,"no")) {
            return;
        }

        //show progress dialog
        showProgressDialog();
        checkBoxsave(email,password);
        //perform login and account creation depending on existence of email in firebase
        performLoginOrAccountCreation(email, password);
    }

    protected void performLoginOrAccountCreation(final String email, final String password){
        firebaseAuth.fetchProvidersForEmail(email).addOnCompleteListener(
                this, new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "checking to see if user exists in firebase or not");
                            ProviderQueryResult result = task.getResult();

                            if(result != null && result.getProviders()!= null
                                    && result.getProviders().size() > 0){
                                Log.d(TAG, "User exists, trying to login using entered credentials");
                                performLogin(email, password);
                            }else{
                                Log.d(TAG, "User doesn't exist, creating account");
                                Toast.makeText(LoginActivity.this,
                                        "You have not registered yet!! Create account to proceed",
                                        Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Log.w(TAG, "User check failed", task.getException());
                            Toast.makeText(LoginActivity.this,
                                    "There is a problem, please try again later.",
                                    Toast.LENGTH_SHORT).show();

                        }
                        //hide progress dialog
                        hideProgressDialog();
                        //enable and disable login, logout buttons depending on signin status
                        showAppropriateOptions();
                    }
                });
    }
    private void performLogin(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "login success");
                        } else {
                            Log.e(TAG, "Login fail", task.getException());
                            Toast.makeText(LoginActivity.this,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        //hide progress dialog
                        hideProgressDialog();
                        //enable and disable login, logout buttons depending on signin status
                        showAppropriateOptions();
                    }
                });
    }
   /* private void registerAccount(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "account created");
                        } else {
                            Log.d(TAG, "register account failed", task.getException());
                            Toast.makeText(LoginActivity.this,
                                    "account registration failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        //hide progress dialog
                        hideProgressDialog();
                        //enable and disable login, logout buttons depending on signin status
                        showNextAction="First_launch";
                        showAppropriateOptions();
                    }
                });
    }*/
    private boolean validateEmailPass(String email , String password, String newUser) {
        boolean valid = true;
        if (newUser.equalsIgnoreCase("yes")){
            if (TextUtils.isEmpty(email)) {
                emailET.setError("Required.");
                valid = false;
            }else if(!email.contains("@")){
                emailET.setError("Not an email id.");
                valid = false;
            } else{
                emailET.setError(null);
            }
            if (TextUtils.isEmpty(email)) {
                emailNewET.setError("Required.");
                valid = false;
            }else if(!email.contains("@")){
                emailNewET.setError("Not an email id.");
                valid = false;
            } else{
                emailNewET.setError(null);
            }

            if (TextUtils.isEmpty(password)) {
                passwordNewET.setError("Required.");
                valid = false;
            }else if(password.length() < 6){
                passwordNewET.setError("Min 6 chars.");
                valid = false;
            }else {
                passwordNewET.setError(null);
            }

            if (TextUtils.isEmpty(password)) {
                passwordET.setError("Required.");
                valid = false;
            }else if(password.length() < 6){
                passwordET.setError("Min 6 chars.");
                valid = false;
            }else {
                passwordET.setError(null);}
        }
        else{
            if (TextUtils.isEmpty(email)) {
                emailET.setError("Required.");
                valid = false;
            }else if(!email.contains("@")){
                emailET.setError("Not an email id.");
                valid = false;
            } else{
                emailET.setError(null);
            }

            if (TextUtils.isEmpty(password)) {
                passwordET.setError("Required.");
                valid = false;
            }else if(password.length() < 6){
                passwordET.setError("Min 6 chars.");
                valid = false;
            }else {
                passwordET.setError(null);}

        }

        return valid;
    }
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait!");
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private void updatePassword() {

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        final String newPwd = ((EditText) findViewById(R.id.edtcheckresetpass)).getText().toString();
        if(!validateResetPassword(newPwd)){
            Toast.makeText(LoginActivity.this,
                    "Invalid password, please enter valid password",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        user.updatePassword(newPwd)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    "Password has been updated",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Error in updating passowrd",
                                    task.getException());
                            Toast.makeText(LoginActivity.this,
                                    "Failed to update passwrod.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void logOut() {
        firebaseAuth.signOut();
        showAppropriateOptions();
    }
    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
    private boolean validateResetPassword(String password) {
        boolean valid = true;
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            valid = false;
        }
        return valid;
    }
    private void showAppropriateOptions(){
        hideProgressDialog();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //findViewById(R.id.login_items).setVisibility(View.GONE);
            if (showNextAction.equalsIgnoreCase("First_launch")){
                Intent intent=new Intent(this,GenderSelect.class);
                startActivity(intent);
            }
            else{
                Intent intent=new Intent(this,MainActivity.class);
                startActivity(intent);
            }
        }else{
            Toast.makeText(getApplicationContext(), "Please SignIn and Continue!cheers", Toast.LENGTH_SHORT).show();
        }
    }
    private void checkBoxsave(String email,String password){
        String userid="";
        SaveSharedPreferences sp=new SaveSharedPreferences(this);
        CheckBox rmchkbox;
        rmchkbox=findViewById(R.id.rememberme);
        if (rmchkbox.isChecked()){
            userid=email;
            sp.saveuserid(userid,password);

        }
    }
}
//    private void sendEmailVerificationMsg() {
//        findViewById(R.id.verify_b).setEnabled(false);
//
//        final FirebaseUser user = firebaseAuth.getCurrentUser();
//        user.sendEmailVerification()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        findViewById(R.id.verify_b).setEnabled(true);
//                        if (task.isSuccessful()) {
//                            Toast.makeText(LoginActivity.this,
//                                    "Verification email has been sent to " + user.getEmail(),
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.e(TAG, "Error in sending verification email",
//                                    task.getException());
//                            Toast.makeText(LoginActivity.this,
//                                    "Failed to send verification email.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

