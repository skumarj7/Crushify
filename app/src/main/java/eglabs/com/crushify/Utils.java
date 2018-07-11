package eglabs.com.crushify;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;

import eglabs.com.crushify.Activity.BeforeLogin.GenderSelect;

public class Utils {
    private Context context;
    Activity activity;
    public EditText emailET,emailNewET,passwordET,passwordNewET;

    public Utils(Context context){
        this.context=context;

    }
    public Utils(){

    }

    public static String computehash(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
    private void alertbuild(String title,String msg,final String alerttype,String positivemsg,String negativemsg){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context
        );

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(positivemsg,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        if ((alerttype).equalsIgnoreCase("Backpressed")){
                            activity.finish();
                        }
                        else{
                            dialog.cancel();
                            Toast.makeText(context, "Change your preferred language!cheers", Toast.LENGTH_SHORT).show();
                            //languageset=true;
                        }

                    }
                })
                .setNegativeButton(negativemsg,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                        Toast.makeText(context, "Your preferred language is "+"lgndetector.languageInlocal"+"!cheers", Toast.LENGTH_SHORT).show();
                        //languageset=true;
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // // show it
        alertDialog.show();
    }
}
