package jp.mickey305.authtodosample.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesObject {
    protected SharedPreferences sp;
    public SharedPreferencesObject(Context context){
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public SharedPreferencesObject(String fileName, Context context){
        sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }
    public void write(String key, int num){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, num);
        editor.commit();
    }
    public void write(String key, boolean flg){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key , flg);
        editor.commit();
    }
    public void write(String key, String value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public int readInt(String key){
        if(sp != null) return sp.getInt(key, 0);
        else return 0;
    }
    public boolean readBool(String key){
        if(sp != null) return sp.getBoolean(key, false);
        else return false;
    }
    public String readStr(String key){
        if(sp != null) return sp.getString(key, "");
        else return "";
    }
}
