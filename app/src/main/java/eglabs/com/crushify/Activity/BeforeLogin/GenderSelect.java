package eglabs.com.crushify.Activity.BeforeLogin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eglabs.com.crushify.Activity.AfterLogin.CreateMaleProfile;
import eglabs.com.crushify.Activity.BaseActivity;
import eglabs.com.crushify.Activity.MainActivity;
import eglabs.com.crushify.FirestoreActions;
import eglabs.com.crushify.LanguageDetector;
import eglabs.com.crushify.R;
import eglabs.com.crushify.SharedPreferences.SaveSharedPreferences;
import eglabs.com.crushify.Activity.BeforeLogin.LoginActivity;
import eglabs.com.crushify.Utils;
import eglabs.com.crushify.ViewTextmanipulation;

public class GenderSelect extends BaseActivity implements AdapterView.OnItemSelectedListener{
    private RadioGroup rdggender;
    private RadioGroup rdglgbt;
    private RelativeLayout rllgbt;
    private RadioButton rdbtnmale;
    private RadioButton rdbtnfemale;
    private RadioButton rdbtnlgbt;
    private SharedPreferences sp;
    private Button btngender;
    private Spinner spnLanguage;
    private String language;
    FirebaseAuth firebaseauth;
    FirebaseUser firebaseuser;
    FirebaseFirestore db;
    String uid;
    LanguageDetector lgndetector;
    ViewTextmanipulation viewtxtman;
    boolean languageset=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.nav_menu_get="no_drawer";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gender_main);
        lgndetector=new LanguageDetector();
        rdggender=(RadioGroup)findViewById(R.id.rdgrp);
        rdbtnmale=(RadioButton)findViewById(R.id.rdnbtnmale);
        rdbtnfemale=(RadioButton)findViewById(R.id.rdnbtnfemale);
        rdbtnlgbt=(RadioButton)findViewById(R.id.rdnbtnlgbt);
        btngender=(Button)findViewById(R.id.btngender);
        //firebase instance
        firebaseauth=FirebaseAuth.getInstance();
        firebaseuser=firebaseauth.getCurrentUser();
        db=FirebaseFirestore.getInstance();
        //setup language spinner
        setupLangSpinner("Languages");
        //lgbt radio button listener
        rdlbgtlistener();
        //submit button listener
        setBtnClickListener();
        //change view language
        lgndetector=new LanguageDetector();
        if (!(lgndetector.devicelanguage).equalsIgnoreCase("English")){
            ViewGroup viewgroup=(ViewGroup)findViewById(android.R.id.content);
            viewtxtman=new ViewTextmanipulation(viewgroup,"English",getResources().getString(R.string.apikey));}

    }
    private void setupLangSpinner(final String docid){
        //lgndetector=new LanguageDetector();
        spnLanguage=(Spinner)findViewById(R.id.spnlanguage);
        DocumentReference langstats=db.collection("Localization").document(docid);
        langstats.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final List<Object> spinnerlist=new ArrayList<>();
                    DocumentSnapshot document=task.getResult();
                    Map<String,Object> spinnerdata=new HashMap<>();
                    spinnerdata.putAll(document.getData());
                    spinnerlist.add(lgndetector.languageInlocal);
                    for (Map.Entry m:spinnerdata.entrySet()){
                        spinnerlist.add(m.getValue());
                    }
                    ArrayAdapter<Object> spinnerAdapter = new ArrayAdapter<>(GenderSelect.this, android.R.layout.simple_spinner_item,spinnerlist);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnLanguage.setAdapter(spinnerAdapter);
                } else {
                    Log.w("Error", task.getException());
                }

            }

        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("error","error"+e);
                    }});
    }
    protected void rdlbgtlistener(){
        rdbtnlgbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertbuild("Language Confirmation","App language has been set to" +lgndetector.languageInlocal,"Nothing","Change","Leave It!!!" );
                rllgbt=(RelativeLayout)findViewById(R.id.rellgbt);
                rllgbt.setVisibility(View.VISIBLE);
                rdglgbt=(RadioGroup)findViewById(R.id.rdgrplgbt);
            }
        });
    }
    @Override
    public void onBackPressed(){
        alertbuild("Do you want to exit the application?","Click Yes to exit","BackPressed","Yes","No");
    }
    private void alertbuild(String title,String msg,final String alerttype,String positivemsg,String negativemsg){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                GenderSelect.this
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
                            GenderSelect.this.finish();}
                        else{
                            dialog.cancel();
                            Toast.makeText(getApplicationContext(), "Change your preferred language!cheers", Toast.LENGTH_SHORT).show();
                            languageset=true;
                        }

                    }
                })
                .setNegativeButton(negativemsg,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "Your preferred language is "+lgndetector.languageInlocal+"!cheers", Toast.LENGTH_SHORT).show();
                        languageset=true;
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
        setBtnClickListener();
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
        setBtnClickListener();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("lifecycle","onDestroy invoked");
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        String item=parent.getItemAtPosition(i).toString();
        language=item;
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //todo need to think
    }
    protected void save_Gender(){
        if (!languageset){
            alertbuild("Language Confirmation","App language has been set to" +lgndetector.languageInlocal,"Nothing","Change","Ok!!!" );
        }
        final int btnchkid=rdggender.getCheckedRadioButtonId();
        LanguageDetector langdetector=new LanguageDetector();
        final String devicelanguageinengllish=langdetector.devicelanguage;
        final String devicelanguageinlocal=langdetector.languageInlocal;
        //save language in shared preferrence
        final SaveSharedPreferences sp=new SaveSharedPreferences(GenderSelect.super.getApplicationContext());
        sp.savelanguage(devicelanguageinengllish,devicelanguageinlocal,language);
        //save in firestore
        if (firebaseuser!=null){
           uid=firebaseuser.getUid();
        }
        Utils computehash=new Utils();
        final String hashedUserid=computehash.computehash(uid);
        //Log.d("Radio id","RAdio:" + btnchkid);
        Runnable pref_run=new Runnable() {
            @Override
            public void run() {
                String gendersp;
                if (btnchkid==R.id.rdnbtnmale){
                    gendersp="male";
                }
                else if (btnchkid==R.id.rdnbtnfemale) {
                    gendersp="female";
                    Log.d("Gender Save","Female saved");
                }
                else  {
                    final int btnlgbtchkid=rdglgbt.getCheckedRadioButtonId();
                    if (btnlgbtchkid==R.id.rdnbtnlgbtownmale){
                        gendersp="gay";
                        Log.d("Gender save","gay Saved");
                    }
                    else if(btnlgbtchkid==R.id.rdnbtnlgbtownfemale){
                        gendersp="lesbian";
                        Log.d("Gender save","Lgbt Saved");
                    }
                    else{
                        gendersp="ts";
                        Log.d("Gender save","Lgbt Saved");
                    }

                }
                sp.savegender(gendersp,uid);
                Map<Object,Object> userdetails=new HashMap<>();
                userdetails.put("Device Language in local",devicelanguageinlocal);
                userdetails.put("Device Language in English",devicelanguageinengllish);
                userdetails.put("Preferred language",language);
                userdetails.put("Gender",gender);
                userdetails.put("HasedUid",hashedUserid);
                FirestoreActions fsa=new FirestoreActions();
                fsa.saveInFirestore("user Details",uid,userdetails);
                Log.d("Gender Save","Male Saved");
            }
        };
        AsyncTask.execute(pref_run);
    }
    private void setBtnClickListener(){
        btngender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Click","Button click");
                save_Gender();
/*
                Intent intentlogin=new Intent(GenderSelect.this,LoginActivity.class);
                startActivity(intentlogin);*/
            }
        });
    }
}

