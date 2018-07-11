package eglabs.com.crushify.Activity.AfterLogin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import eglabs.com.crushify.Activity.BaseActivity;
import eglabs.com.crushify.Activity.MainActivity;
import eglabs.com.crushify.FirestoreActions;
import eglabs.com.crushify.LocationTracker;
import eglabs.com.crushify.R;
import eglabs.com.crushify.SharedPreferences.SaveSharedPreferences;

public class CreateMaleProfile extends BaseActivity implements AdapterView.OnItemSelectedListener{
    private Button btndresscolour;
    private Button btntodaydresscolour;
    private Button btncreateprofile;
    private View dresscolour;
    private View todaydresscolour;
    private int currentBackgroundColor = 0xffffffff;
    private Spinner spinnerheight;
    private Spinner spinnerbody;
    private Spinner spinnercomplexion;
    private Spinner spinnermoustache;
    private Spinner spinnerbeard;
    private Spinner spinnerhusualdress;
    private Spinner spinnertodaydress;

    //text views
    private TextView txtvname;
    private TextView txtvheight;
    private TextView txtvbodytype;
    private TextView txtvcomplexion;
    private TextView txtvmoustache;
    private TextView txtvbeard;
    private TextView txtvfavdresscolour;
    private TextView txtvtodaydresscolour;
    private TextView txtvusualdressing;
    private TextView txtvtodaydressing;

    //location services
    LocationTracker locationtracker;
    //switch
    private Switch swhmatch;
    private boolean switchstate;
    //edit text
    private EditText edtname;
    //prompt data
    private String promptdata;
    //view strings
    private String name="";
    private String height;
    private String bodytype;
    private String complexion;
    private String moustache;
    private String beard;
    private int intdresscolour;
    private int inttodaydresscolour;
    private String usualdressing;
    private String todaydress;
    private String addmoustache="";
    private String addbeard="";
    private String addusualdress="";
    private String addtodaydress="";
    private String uid;

    private int intcolour;
    FirebaseAuth firebaseauth;
    FirebaseUser firebaseuser;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.nav_menu_get = "drawer_aftersignin";
        super.onCreate(savedInstanceState);

        SaveSharedPreferences sp=new SaveSharedPreferences(CreateMaleProfile.this);
        locationtracker=new LocationTracker(CreateMaleProfile.this,CreateMaleProfile.this);
        setContentView(R.layout.activity_create_male_profile);
        db=FirebaseFirestore.getInstance();
        dresscolour = findViewById(R.id.vdresscolour);
        todaydresscolour=findViewById(R.id.vtodaydresscolour);
        changeBackgroundColor(currentBackgroundColor,dresscolour);
        changeBackgroundColor(currentBackgroundColor,todaydresscolour);
        firebaseuser=firebaseauth.getInstance().getCurrentUser();
        setspinnervalue();
        initviews();
        setbuttonlisteners();
    }
    private void initviews(){
        edtname=findViewById(R.id.edtname);
        txtvname=findViewById(R.id.txtvname);
        txtvheight=findViewById(R.id.txtvheight);
        txtvbodytype=findViewById(R.id.txtvbodytype);
        txtvcomplexion=findViewById(R.id.txtvfacialappearance);
        txtvmoustache=findViewById(R.id.txtvmoustache);
        txtvbeard=findViewById(R.id.txtvbeard);
        txtvfavdresscolour=findViewById(R.id.txtvfavdresscolour);
        txtvtodaydresscolour=findViewById(R.id.txtvtodaydress);
        txtvusualdressing=findViewById(R.id.txtvusualdress);
        txtvtodaydressing=findViewById(R.id.txtvtodaydress);
        swhmatch=findViewById(R.id.swtchprof);
    }
    private void setbuttonlisteners(){
        btndresscolour=findViewById(R.id.btndresscolour);
        btndresscolour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intdresscolour=colorpickclick(dresscolour);
            }
        });
        btntodaydresscolour=findViewById(R.id.btntodaydresscolour);
        btntodaydresscolour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inttodaydresscolour=colorpickclick(todaydresscolour);
            }
        });
        btncreateprofile=findViewById(R.id.btncreatemaleprofile);
        btncreateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialog(true);
                createprofile();
                setDialog(false);
                FirestoreActions fsa=new FirestoreActions();
                if (firebaseuser!=null){
                    uid=firebaseuser.getUid();
                    Map<Object,Object> profdata=new HashMap<>();
                    profdata.put("OWNPROFILE","Profilecreated");
                    fsa.saveInFirestore("user Deatils",uid,profdata);
                }
                SaveSharedPreferences sp=new SaveSharedPreferences(CreateMaleProfile.this);
                sp.saveProfileStatus("OWNPROFILE");
                startActivity(new Intent(CreateMaleProfile.this,MainActivity.class));
            }
        });
    }
    private void changeBackgroundColor(int selectedColor,View view) {
        currentBackgroundColor = selectedColor;
        view.setBackgroundColor(selectedColor);
    }

    private int colorpickclick(final View view){
        final Context context=CreateMaleProfile.this;
        ColorPickerDialogBuilder
                .with(context)
                .setTitle(R.string.color_dialog_title)
                .initialColor(currentBackgroundColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorChangedListener(new OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int selectedColor) {
                        // Handle on color change
                        changeBackgroundColor(selectedColor,view);
                        Log.d("ColorPicker", "onColorChanged: 0x" + Integer.toHexString(selectedColor));
                    }
                })
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        changeBackgroundColor(selectedColor,view);
                        intcolour=selectedColor;
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        changeBackgroundColor(selectedColor,view);
                        if (allColors != null) {
                            StringBuilder sb = null;

                            for (Integer color : allColors) {
                                if (color == null)
                                    continue;
                                if (sb == null)
                                    sb = new StringBuilder("Color List:");
                                sb.append("\r\n#" + Integer.toHexString(color).toUpperCase());
                            }

                            if (sb != null)
                                Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .showColorEdit(true)
                .setColorEditTextColor(ContextCompat.getColor(CreateMaleProfile.this, android.R.color.holo_blue_bright))
                .build()
                .show();
        return intcolour;

    }
    private void addspinnerdata(final String docid,final Spinner spinner){
        DocumentReference bodystats=db.collection("User Stats").document(docid);
        bodystats.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final List<Object> spinnerlist=new ArrayList<>();
                    DocumentSnapshot document=task.getResult();
                    Map<String,Object> spinnerdata=new HashMap<>();
                    spinnerdata.putAll(document.getData());
                    spinnerlist.add("Select "+ docid);
                    for (Map.Entry m:spinnerdata.entrySet()){
                        spinnerlist.add(m.getValue());
                    }
                    ArrayAdapter<Object> spinnerAdapter = new ArrayAdapter<>(CreateMaleProfile.this, android.R.layout.simple_spinner_item,spinnerlist);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerAdapter);
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
    private void setspinnervalue(){
        spinnerheight=(Spinner)findViewById(R.id.spnheight);
        addspinnerdata("Height",spinnerheight);
        spinnerheight.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        spinnerbody=(Spinner)findViewById(R.id.spnbodytype);
        addspinnerdata("Bodytype",spinnerbody);
        spinnerbody.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        spinnercomplexion=(Spinner)findViewById(R.id.spnfacial);
        addspinnerdata("Body Appearance",spinnercomplexion);
        spinnercomplexion.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        spinnerbeard=(Spinner)findViewById(R.id.spnbeard);
        addspinnerdata("Beard",spinnerbeard);
        spinnerbeard.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        spinnermoustache=(Spinner)findViewById(R.id.spnmoustache);
        addspinnerdata("Moustache",spinnermoustache);
        spinnermoustache.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        spinnerhusualdress=(Spinner)findViewById(R.id.spnusualdress);
        addspinnerdata("Male Dressing",spinnerhusualdress);
        spinnerhusualdress.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        spinnertodaydress=(Spinner)findViewById(R.id.spntodaydress);
        addspinnerdata("Male Dressing",spinnertodaydress);
        spinnertodaydress.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

    }

    private void createprofile(){
        name=edtname.getText().toString();
        uid=firebaseuser.getUid();
        switchstate=swhmatch.isChecked();
        //LocationTracker locationTracker=new LocationTracker(this,CreateMaleProfile.this);
        String address=locationtracker.getLocationAddress();
        double latitude=locationtracker.getLatitude();
        double longitude=locationtracker.getLongitude();
        Map<Object,Object> userdetails=new HashMap<>();
        userdetails.put("Name",name);
        userdetails.put("Height",height);
        userdetails.put("Body Type",bodytype);
        userdetails.put("Complexion",complexion);
        userdetails.put("Moustache",moustache);
        userdetails.put("Beard",beard);
        userdetails.put("Dress Colour",intdresscolour);
        userdetails.put("Today Dress Colour",inttodaydresscolour);
        userdetails.put("Usual Dress",usualdressing);
        userdetails.put("Today Dress",todaydress);
        userdetails.put("Match Known",switchstate);
        userdetails.put("Present Location Address",address);
        userdetails.put("Present Location Latitude",latitude);
        userdetails.put("Present Location Longitude",longitude);
        FirestoreActions fsa=new FirestoreActions();
        fsa.saveInFirestore("Male Profiles",uid,userdetails);
        /*db.collection("Male Profiles").document(uid).set(userdetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        Log.d("tag","DocumentSnapshot ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
*/

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        String item=parent.getItemAtPosition(i).toString();
        switch (parent.getId()){
            case R.id.spnheight:
                height=item;
                if (height.equals("Not in the list")){
                    prompuserinput();
                }
                break;
            case R.id.spnbodytype:
                bodytype=item;
                if (bodytype.equals("Not in the list")){
                    prompuserinput();
                }
                break;
            case R.id.spnfacial:
                complexion=item;
                if (complexion.equals("Not in the list")){
                    prompuserinput();
                }
                break;
            case R.id.spnbeard:
                beard=item;
                if (beard.equals("Not in the list")){
                    prompuserinput();
                }
                break;
            case R.id.spnmoustache:
                moustache=item;
                if (moustache.equals("Not in the list")){
                    prompuserinput();
                }
                break;
            case R.id.spnusualdress:
                usualdressing=item;
                if (usualdressing.equals("Not in the list")){
                    prompuserinput();
                }
                break;
            case R.id.spntodaydress:
                todaydress=item;
                if (todaydress.equals("Not in the list")){
                    prompuserinput();
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
            //todo need to think
    }
    private void prompuserinput(){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(CreateMaleProfile.this);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                CreateMaleProfile.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                promptdata=(userInput.getText().toString());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("lifecycle","onStart invoked");
    }
    @Override
    protected void onResume() {
        locationtracker=new LocationTracker(CreateMaleProfile.this,CreateMaleProfile.this);
        super.onResume();
        Log.d("lifecycle","onResume invoked");
    }
    @Override
    protected void onPause() {
        super.onPause();
        locationtracker.stopUsingGPS();
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
        locationtracker.stopUsingGPS();
        super.onDestroy();
        finish();
        Log.d("lifecycle","onDestroy invoked");
    }
    private void setDialog(boolean show){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //View view = getLayoutInflater().inflate(R.layout.progress);
        builder.setView(R.layout.progress);
        Dialog dialog = builder.create();
        if (show)dialog.show();
        else dialog.dismiss();
    }
}

    /*Map<String, Object> user = new HashMap<>();
        user.put("first", "Alan");
                user.put("middle", "Mathison");
                user.put("last", "Turing");
                user.put("born", 1912);

// Add a new document with a generated ID
                db.collection("User Stats").add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
@Override
public void onSuccess(DocumentReference documentReference) {
        Log.d("tag","DocumentSnapshot " + documentReference.getId());
        }
        })
        .addOnFailureListener(new OnFailureListener() {
@Override
public void onFailure(@NonNull Exception e) {
        Log.w("TAG", "Error adding document", e);
        }
        });*/
