package eglabs.com.crushify;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import eglabs.com.crushify.SharedPreferences.SaveSharedPreferences;


public class ViewTextmanipulation {
    public ViewGroup viewgroup;
    public String[] stringarray;
    public Button button;
    public EditText edttext;
    public TextView txtview;
    public TextInputLayout txtl;
    public CheckBox chkbox;
    public String viewType;
    public Map<Object,Object> texttotranslatemap;
    public Map<Object,Object> viewtextmapeng;
    public Map<Object,Object> viewtextmaplocal;
    public String targetlanguage;
    public String apikey;
    public ViewTextmanipulation (ViewGroup v,String targetLanguage,String apikey){
        this.viewgroup=v;
        this.targetlanguage=targetLanguage;
        viewtextmapeng=new HashMap<>();
        viewtextmaplocal=new HashMap<>();
        texttotranslatemap=new HashMap<>();
        this.apikey=apikey;
        getViewTypetext(v);
    }
    public ViewTextmanipulation(Map<Object,Object> SpTranslatedtext){
        this.viewtextmaplocal=SpTranslatedtext;
    }
    public ViewTextmanipulation(String[] stringarray){
        this.stringarray=stringarray;
    }
    public void getViewTypetext(ViewGroup viewgrp){
        int count=viewgrp.getChildCount();
        int viewid;
        for (int i=0;i<count;i++){
            View view=viewgrp.getChildAt(i);
            String originalviewtext;
            if(view instanceof TextInputLayout){
                this.txtl=(TextInputLayout)view;
                viewid=txtl.getId();
                originalviewtext=txtl.getHint().toString();
                this.viewtextmapeng.put(viewid,originalviewtext);
            }
            else if (view instanceof ViewGroup){
                getViewTypetext((ViewGroup)view);
            }else{
                String viewtype=view.getClass().getName();
                this.viewType=viewtype;
                if (viewtype.contains("Button")){
                    this.button=(Button)view;
                    viewid=button.getId();
                    originalviewtext=button.getText().toString();
                    this.viewtextmapeng.put(viewid,originalviewtext);
                }
                else if (viewtype.contains("TextView")){
                    this.txtview=(TextView)view;
                    viewid=txtview.getId();
                    originalviewtext=txtview.getText().toString();
                    this.viewtextmapeng.put(viewid,originalviewtext);
                }
                else if(viewtype.contains("EditText")){
                    this.edttext=(EditText)view;
                    viewid=edttext.getId();
                    originalviewtext=edttext.getText().toString();
                    this.viewtextmapeng.put(viewid,originalviewtext);
                }
                else if(viewtype.contains("CheckBox")){
                    this.chkbox=(CheckBox) view;
                    viewid=chkbox.getId();
                    originalviewtext=chkbox.getText().toString();
                    this.viewtextmapeng.put(viewid,originalviewtext);
                }
                /*switch (viewtype){
                    case viewtype.contains("Button"):
                            //"android.widget.Button":
                        this.button=(Button)view;
                        originalviewtext=button.getText().toString();
                        viewtextmapeng.put(button,originalviewtext);
                    case "android.widget.TextView":
                        this.txtview=(TextView)view;
                        originalviewtext=txtview.getText().toString();
                        viewtextmapeng.put(txtview,originalviewtext);
                    case "android.widget.EditText":
                        this.edttext=(EditText)view;
                        originalviewtext=edttext.getText().toString();
                        viewtextmapeng.put(edttext,originalviewtext);
                }*/
            }

        }
        //final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
    }
   /* protected String getLayoutName() {
        return getResources().getResourceEntryName(this.layoutId);
    }*/
    public void translatetextview(){
        LanguageDetector languageDetector=new LanguageDetector(apikey);
        this.viewtextmaplocal=LanguageDetector.LanguageTransalate(languageDetector.devicelanguage,this.viewtextmapeng);
    }
    public void translatetextarray(){
        LanguageDetector languageDetector=new LanguageDetector(apikey);
        this.texttotranslatemap=LanguageDetector.LanguageTransalate(languageDetector.devicelanguage,this.stringarray);

    }
    public void saveinfirestore(String Collection,String docid,Map<Object,Object> translatedtextmap){
        FirestoreActions fsa=new FirestoreActions();
        fsa.saveInFirestore(Collection,docid,translatedtextmap);
    }
    public void setViewText() {
        int count=viewgroup.getChildCount();
        int viewid;
        for (int i=0;i<count;i++){
            View view=viewgroup.getChildAt(i);
            if(view instanceof TextInputLayout){
                this.txtl=(TextInputLayout)view;
                viewid=txtl.getId();
                String translatedText=viewtextmaplocal.get(viewid).toString();
                txtl.setHint(translatedText);
            }
            else if (view instanceof ViewGroup){
                getViewTypetext((ViewGroup)view);
            }else{
                String viewtype=view.getClass().getName();
                this.viewType=viewtype;
                if (viewtype.contains("Button")){
                    this.button=(Button)view;
                    viewid=button.getId();
                    String translatedText=viewtextmaplocal.get(viewid).toString();
                    button.setText(translatedText);
                }
                else if (viewtype.contains("TextView")){
                    this.txtview=(TextView)view;
                    viewid=txtview.getId();
                    String translatedText=viewtextmaplocal.get(viewid).toString();
                    txtview.setText(translatedText);
                }
                else if(viewtype.contains("EditText")){
                    this.edttext=(EditText)view;
                    viewid=edttext.getId();
                    String translatedText=viewtextmaplocal.get(viewid).toString();
                    edttext.setText(translatedText);
                }
                else if(viewtype.contains("CheckBox")){
                    this.chkbox=(CheckBox) view;
                    viewid=chkbox.getId();
                    String translatedText=viewtextmaplocal.get(viewid).toString();
                    chkbox.setText(translatedText);
                }
            }

        }
    }
    public void saveinSP(Map<Object,Object> sptextMap,Context context){
        SaveSharedPreferences sp=new SaveSharedPreferences(context);
        sp.saveTranslatedLanguage(sptextMap);
    }
    public void translatejarapi(){


    }
}
