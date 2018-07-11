package eglabs.com.crushify.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import eglabs.com.crushify.Activity.BaseActivity;
import eglabs.com.crushify.FirestoreActions;
import eglabs.com.crushify.LanguageDetector;

/**
 * Created by selva on 4/25/2018.
 */

public class SaveSharedPreferences{

    Context context;
    //gender file
    public static final String MAINSCREEN="eglabs.com.crushify.mainscreen";
    private static final String GENDER="Gender";
    private static final String FBUSERID="FirebaseuserID";
    private final String mainfile="mainscreen";

    //user details
    public static final String USERFILE="";

    //login file
    public static final String LOGINDETAILS="eglabs.com.crushify.userid";
    private static final String USERID="Userid";
    private static final String PASSWORD="Password";
    private final String username="userid";

    //profile files
    public static final String PROFILEFILE="eglabs.com.crushify.profile";
    private static final String OWNPROFILE="OWNPROFILE";
    private static final String CRUSHPROFILE="CRUSHPROFILE";
    private static final String PROFILECREATED="Profilecreated";

    //language files
    public static final String LANGUAGEFILE="eglabs.com.crushify.language";
    private static final String PACKAGENAME="eglabs.com.crushify";

    //translated language file
    public static final String TRANSLATEDLANGUAGEFILE="eglabs.com.crushify.translatedLanguagefile";
    public static final String DEVICELANGUAGEEGLISH="Devicelanguageenglish";
    private static final String DEVICELANGUAGELOCAL="Devicelanguagelocal";
    private static final String DEVICELANGUAGEPREFERRED="Devicelanguagepreferred";

    SharedPreferences sp;
    public SaveSharedPreferences(Context context){
        this.context=context;
    }
    public File filesinit(String fileName){
        String userId="user";
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        LanguageDetector languageDetector=new LanguageDetector();
        String devicelanguage=languageDetector.devicelanguage;
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if (firebaseUser!=null){
            userId=firebaseUser.getUid();
        }
        if (fileName.equalsIgnoreCase("gender")){
            final File gender = new File(context.getFilesDir().getPath() + PACKAGENAME + "/shared_prefs/" + PACKAGENAME  + "." + mainfile + ".xml");
            return gender;
        }else if (fileName.equalsIgnoreCase("language")) {
            final File languagefile = new File(context.getFilesDir().getPath() + PACKAGENAME + "/shared_prefs/" + PACKAGENAME + "." + userId + "." + LANGUAGEFILE + ".xml");
            return languagefile;
        }else if(fileName.equalsIgnoreCase("uid")){
            final File uid=new File(context.getFilesDir().getPath()+ PACKAGENAME+"/shared_prefs/"+PACKAGENAME+"."+ userId+"."+ username +".xml");
            return uid;
        }else if(fileName.equalsIgnoreCase("translatedfile")){
            final File translatedfile=new File(context.getFilesDir().getPath()+ PACKAGENAME+"/shared_prefs/"+PACKAGENAME+"."+ devicelanguage+ "."+devicelanguage +".xml");
            return translatedfile;
        }
        else{
            return null;
        }
    }
    public SaveSharedPreferences(){

    }
    public void savegender(String gender,String userid){
        sp=context.getSharedPreferences(MAINSCREEN,Context.MODE_PRIVATE);
        SharedPreferences.Editor genderedit=sp.edit();
        genderedit.putString(GENDER,gender);
        genderedit.putString(FBUSERID,userid);
        genderedit.apply();
        //Log.d("Gender",sp.getString(GENDER,null));

    }
    public Map<Object,Object> getTranslatedViewtext(){
        String keyS;
        String valueS;
        Object keyO;
        Object valueO;
        Map<String,?> translatedtextmap;
        Map<Object,Object> translateObjectMap=new HashMap<>();
       sp=context.getSharedPreferences(TRANSLATEDLANGUAGEFILE,Context.MODE_PRIVATE);
       translatedtextmap=sp.getAll();
       for (Map.Entry m:translatedtextmap.entrySet()){
           keyO=m.getKey();
           valueO=m.getValue();
           translateObjectMap.put(keyO,valueO);
       }
       return translateObjectMap;
    }
    public void saveTranslatedLanguage(Map<Object,Object> languagetranslate){
        sp=context.getSharedPreferences(TRANSLATEDLANGUAGEFILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editlang=sp.edit();
        for (Map.Entry m:languagetranslate.entrySet()){
                editlang.putString(m.getKey().toString(),m.getValue().toString());
        }
        editlang.apply();
    }
    public void savelanguage(String deviceenglanguage,String locallanguage,String preferredlanguage){
        sp=context.getSharedPreferences(LANGUAGEFILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor savelang=sp.edit();
        savelang.putString(DEVICELANGUAGEEGLISH,deviceenglanguage);
        savelang.putString(DEVICELANGUAGELOCAL,locallanguage);
        savelang.putString(DEVICELANGUAGEPREFERRED,preferredlanguage);
        savelang.apply();
    }
    public String getgender(){
       String gender;
       sp=context.getSharedPreferences(MAINSCREEN,Context.MODE_PRIVATE);
       gender=sp.getString(GENDER,null);
       return gender;
    }
    public String gethaseduid(){
        String haseduid;
        sp=context.getSharedPreferences(MAINSCREEN,Context.MODE_PRIVATE);
        haseduid=sp.getString(FBUSERID,null);
        return haseduid;
    }
    public void saveuserid(String userid, String password){
        sp=context.getSharedPreferences(LOGINDETAILS,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sp.edit();
        edit.putString(USERID,userid);
        edit.putString(PASSWORD,password);
        edit.apply();
        /*sp.edit().putString(USERID,userid).commit();
        sp.edit().putString(PASSWORD,password).commit();
*/
    }
    public String[] getlogin(){
        String[] userdetails=new String[2];
        sp=context.getSharedPreferences(LOGINDETAILS,Context.MODE_PRIVATE);
        userdetails[0]=sp.getString(USERID,null);
        userdetails[1]=sp.getString(PASSWORD,null);
        return userdetails;
    }
    public Boolean CheckSpFile(File filename){
        if (filename.exists()){
            return true;}
        else {
            return false;}
    }
    public void saveProfileStatus(String profileType){
        sp=context.getSharedPreferences(PROFILEFILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sp.edit();
        switch (profileType){
            case ("OWNPROFILE"):
                edit.putString(OWNPROFILE,PROFILECREATED);
                break;
            case("CRUSHPROFILE"):
                edit.putString(CRUSHPROFILE,PROFILECREATED);
                break;
        }
        edit.apply();
    }
    public boolean fetchprofilestatus(String profiletype){
        sp=context.getSharedPreferences(PROFILEFILE,Context.MODE_PRIVATE);
        if(sp.contains(profiletype)){
            String result=sp.getString(profiletype,null);
            if (result!=null){
                return true;
            }else{
                return false;
            }
        }else{return false;}

    }
    public void savespfiles(String userId,String collection){
        String profilestatusOwn;
        String devicelangEng=null;
        String devicelanglocal=null;
        String devpreflang=null;

        FirestoreActions fsa=new FirestoreActions();
        Map<Object,Object> userdetails=new HashMap<>();
        userdetails=fsa.getFirestoredata(collection,userId);
        //saving gender
        String Gender=userdetails.get("Gender").toString();
        String hasedid=userdetails.get("HasedUid").toString();
        savegender(Gender,hasedid);
        //saving profile status
        if (userdetails.containsKey(OWNPROFILE)){
            profilestatusOwn=userdetails.get(OWNPROFILE).toString();
            if (profilestatusOwn.equalsIgnoreCase(PROFILECREATED)){
                saveProfileStatus(OWNPROFILE);
            }
        }
        if (userdetails.containsKey(CRUSHPROFILE)){
            String profilestatuscrush=userdetails.get(CRUSHPROFILE).toString();
            if (profilestatuscrush.equalsIgnoreCase(PROFILECREATED)){
                saveProfileStatus(CRUSHPROFILE);
            }
        }

        //saving device language details
        if (userdetails.containsKey(DEVICELANGUAGEEGLISH)){
            devicelangEng=userdetails.get(DEVICELANGUAGEEGLISH).toString();
        }
        if (userdetails.containsKey(DEVICELANGUAGELOCAL)){
            devicelanglocal=userdetails.get(DEVICELANGUAGELOCAL).toString();
        }
        if (userdetails.containsKey(DEVICELANGUAGEPREFERRED)){
            devpreflang=userdetails.get(DEVICELANGUAGEPREFERRED).toString();
        }
        savelanguage(devicelangEng,devicelanglocal,devpreflang);
        if (!devicelangEng.equalsIgnoreCase("English")){
            writeLangdata(devicelangEng);
        }
    }
    public void writeLangdata(String devicelang){
        FirestoreActions fsa=new FirestoreActions();
        Map<Object,Object> langdata=new HashMap<>();
        if (fsa.fireStoreDocExists("Localization",devicelang)){
            langdata=fsa.getFirestoredata("Localization",devicelang);
            saveTranslatedLanguage(langdata);
        }
    }
    public void deletespfile(File filename){
        filename.deleteOnExit();
    }
    public void clearspfiledata(){
    }
}
