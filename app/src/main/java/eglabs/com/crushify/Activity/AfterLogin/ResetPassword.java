package eglabs.com.crushify.Activity.AfterLogin;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import eglabs.com.crushify.Activity.BaseActivity;
import eglabs.com.crushify.Activity.BeforeLogin.LoginActivity;
import eglabs.com.crushify.R;

public class  ResetPassword extends AppCompatActivity {
    private ConstraintLayout reset_email_constraint;
    private ConstraintLayout validate_passcode_constraint;
    private ConstraintLayout reset_password_constraint;
    private String constraintnamefrmintent;
    FirebaseAuth firebaseAuth;
    private static final String TAG="EmailPasswordAuth";
    //reset email
    private EditText edtresetemail;
    private Button btn_Sendresetemail;

    //validate passcode
    private EditText edtpasscode;
    private Button btn_Validatepasscode;

    //changepassword
    private EditText edtnewpass;
    private EditText edtconfirmpass;
    private Button btn_Updatepass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        constraintnamefrmintent=getIntent().getStringExtra("constraintname");

        reset_email_constraint=(ConstraintLayout) findViewById(R.id.reset_email_contraint);
        validate_passcode_constraint=(ConstraintLayout) findViewById(R.id.validatepass_contraint);
        reset_password_constraint=(ConstraintLayout) findViewById(R.id.resetpassword_constraint);
        showAppropriateconstraint(constraintnamefrmintent);
        setButtonlisteners();
    }
    protected void setButtonlisteners(){
        btn_Sendresetemail.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                sendResetPasswordEmail();
                showAppropriateconstraint("passcode");
            }
        });
        btn_Validatepasscode.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

            }
        });
    }
    protected void showAppropriateconstraint(String constraintname){
        switch(constraintname){
            case "resetpass":
                reset_email_constraint.setVisibility(View.VISIBLE);
                validate_passcode_constraint.setVisibility(View.GONE);
                reset_password_constraint.setVisibility(View.GONE);
                edtresetemail=(EditText)findViewById(R.id.edtresetemail);
                btn_Sendresetemail=(Button)findViewById(R.id.btnreset);
                break;
            case "updatepasswordonly":
                reset_email_constraint.setVisibility(View.GONE);
                validate_passcode_constraint.setVisibility(View.GONE);
                reset_password_constraint.setVisibility(View.VISIBLE);
                edtnewpass=(EditText)findViewById(R.id.edtnewpass);
                edtconfirmpass=(EditText)findViewById(R.id.edtconfirmpass);
                btn_Updatepass=(Button)findViewById(R.id.btnresetpass);
                break;
            case "passcode":
                reset_email_constraint.setVisibility(View.GONE);
                validate_passcode_constraint.setVisibility(View.VISIBLE);
                reset_password_constraint.setVisibility(View.GONE);
                break;
            case "passcodeandupdate":
                reset_email_constraint.setVisibility(View.GONE);
                validate_passcode_constraint.setVisibility(View.VISIBLE);
                reset_password_constraint.setVisibility(View.VISIBLE);
                break;

        }
    }
    private void sendResetPasswordEmail() {
        final String email = ((EditText) findViewById(R.id.edtresetemail))
                .getText().toString();
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPassword.this,
                                    "Reset password code has been emailed to "
                                            + email,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Error in sending reset password code",
                                    task.getException());
                            Toast.makeText(ResetPassword.this,
                                    "There is a problem with reset password, try later.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void updatePassword() {

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        final String newPwd = ((EditText) findViewById(R.id.edtcheckresetpass)).getText().toString();
        if(!validateResetPassword(newPwd)){
            Toast.makeText(ResetPassword.this,
                    "Invalid password, please enter valid password",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        user.updatePassword(newPwd)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPassword.this,
                                    "Password has been updated",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Error in updating passowrd",
                                    task.getException());
                            Toast.makeText(ResetPassword.this,
                                    "Failed to update passwrod.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private boolean validateResetPassword(String password) {
        boolean valid = true;
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            valid = false;
        }
        return valid;
    }
}
