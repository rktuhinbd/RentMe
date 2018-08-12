package rentme.dim.com.rentme.Activity.Data;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesClass {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SharedPreferencesClass(Context context) {
        this.context = context;
    }


    private void initSharedPreferences(String sharedPreferencesName){
        sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setSharedPreferences(String sharedPreferencesName, String sharedPreferencesKey, String sharedPreferencesValue){
        initSharedPreferences(sharedPreferencesName);
        editor.putString(sharedPreferencesKey,sharedPreferencesValue);
        editor.commit();
    }

    public String getSharedPreferences(String sharedPreferencesName, String sharedPreferencesKey, String sharedPreferencesValue){
        initSharedPreferences(sharedPreferencesName);
        String value = sharedPreferences.getString(sharedPreferencesKey, sharedPreferencesValue);
        return value;
    }
}
