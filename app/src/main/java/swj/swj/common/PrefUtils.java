package swj.swj.common;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {

    public static void putBoolean(String key, boolean value, Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(String key, boolean defValue, Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defValue);
    }

    public static void putString(String key, String value, Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).commit();
    }

    public static String getString(String key, String defValue, Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defValue);
    }

    public static void putInt(String key, int value, Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key, value).commit();
    }

    public static int getInt(String key, int defValue, Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defValue);
    }

    public static void remove(String key, Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(key).commit();
    }

}
