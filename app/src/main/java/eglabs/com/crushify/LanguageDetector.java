package eglabs.com.crushify;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import eglabs.com.crushify.SharedPreferences.SaveSharedPreferences;

import static java.util.Locale.ENGLISH;


public class LanguageDetector {
    public String devicelanguage;
    public String languageInlocal;
    private static String api_key ;
    public LanguageDetector(String apikey){
        devicelanguage=Locale.getDefault().getDisplayLanguage(ENGLISH);
        languageInlocal=Locale.getDefault().getDisplayLanguage();
        api_key=apikey;
    }
    public LanguageDetector(){
        this.devicelanguage=Locale.getDefault().getDisplayLanguage(ENGLISH);
        languageInlocal=Locale.getDefault().getDisplayLanguage();
    }

    public static Map<Object,Object> LanguageTransalate(final String tolanguage,final Map<Object,Object> engtext){
        final Handler lanTranslateHandler = new Handler();
        final Map<Object,Object> btntranslate=new HashMap<>();
        for (final Map.Entry translatemap:engtext.entrySet()){
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    TranslateOptions options = TranslateOptions.newBuilder()
                            .setApiKey(api_key)
                            .build();
                    Translate translate = options.getService();
                    String translatetext=translatemap.getValue().toString();
                    final Translation translation =
                            translate.translate(translatetext,
                                    Translate.TranslateOption.targetLanguage(tolanguage));
                    lanTranslateHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String translatetolang=translation.getTranslatedText();
                            btntranslate.put(translatemap.getKey().toString(),translatetolang);
                        }
                    });
                    return null;
                }
            }.execute();

        }
        return btntranslate;
    }
    public static Map<Object,Object> LanguageTransalate(final String tolanguage,final String[] engtext){
        final Handler lanTranslateHandler = new Handler();
        final Map<Object,Object> btntranslate=new HashMap<>();
        for (final String tranText:engtext){
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    TranslateOptions options = TranslateOptions.newBuilder()
                            .setApiKey(api_key)
                            .build();
                    Translate translate = options.getService();
                    String translatetext=tranText;
                    final Translation translation =
                            translate.translate(translatetext,
                                    Translate.TranslateOption.targetLanguage(tolanguage));
                    lanTranslateHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String translatetolang=translation.getTranslatedText();
                            btntranslate.put(tranText,translatetolang);
                        }
                    });
                    return null;
                }
            }.execute();

        }
        return btntranslate;
    }


}
