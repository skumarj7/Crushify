package eglabs.com.crushify.Activity.BeforeLogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import eglabs.com.crushify.Activity.BaseActivity;
import eglabs.com.crushify.Activity.MainActivity;
import eglabs.com.crushify.Fragments.Newuser;
import eglabs.com.crushify.R;

public class NewUser extends BaseActivity {
    EditText userNameET;
    EditText emailNewET;
    EditText passwordNewET;
    EditText passwordconfirmET;
    Button btn_createaccount;
    @VisibleForTesting
    public ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.nav_menu_get="no_drawer";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newuser);
        hideProgressDialog();
        userNameET=findViewById(R.id.txtusername);
        emailNewET=findViewById(R.id.email);
        passwordNewET=findViewById(R.id.password);
        passwordconfirmET=findViewById(R.id.confirmpassword);
        btn_createaccount=findViewById(R.id.createaccount_b);
        firebaseauth=FirebaseAuth.getInstance();
        setupbuttonListeners();
    }

    private void setupbuttonListeners(){
        btn_createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName=userNameET.getText().toString();
                final String email=emailNewET.getText().toString();
                final String password=passwordNewET.getText().toString();
                final String confirmpassword=passwordconfirmET.getText().toString();
                if (!validateEmailPass(email, password)) {
                    return;
                }
                if (password.equalsIgnoreCase(confirmpassword)){
                    showProgressDialog();
                    registerAccount(email, password);
                }
                else{
                    Toast.makeText(NewUser.this,
                            "Password's do not match! Verify and login again",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean validateEmailPass(String email , String password) {
        boolean valid = true;
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
        return valid;
    }
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait! Creating your account");
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private void registerAccount(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(NewUser.this,
                                    "Account registration Successful!Proceed with Profile Creation",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(NewUser.this,
                                    "account registration failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        //hide progress dialog
                        hideProgressDialog();
                        showAppropriateOptions();
                    }
                });
    }

    private void showAppropriateOptions(){
        hideProgressDialog();
        hideProgressDialog();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
                Intent intent=new Intent(this,GenderSelect.class);
                startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(), "Please SignIn and Continue!cheers", Toast.LENGTH_SHORT).show();
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
