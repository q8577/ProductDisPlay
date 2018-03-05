package com.cambricon.productdisplay.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by dell on 18-3-3.
 */

public class AppSharePreferenceMgr {
    public static final String FILE_NAME = "Cambricon_apk_data";
    public static void put(Context context, String key, Object object)
    {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof String)
        {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer)
        {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean)
        {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float)
        {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long)
        {
            editor.putLong(key, (Long) object);
        } else
        {
            editor.putString(key, object.toString());
        }
        editor.commit();
        //SharedPreferencesCompat.apply(editor);
    }

    public static Object get(Context context, String key, Object defaultObject)
    {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        if (defaultObject instanceof String)
        {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer)
        {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean)
        {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float)
        {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long)
        {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    /*public static void apply(SharedPreferences.Editor editor)
    {
        try
        {
            if (sApplyMethod != null)
            {
                sApplyMethod.invoke(editor);
                return;
            }
        } catch (IllegalArgumentException e)
        {
        } catch (IllegalAccessException e)
        {
        } catch (InvocationTargetException e)
        {
        }
        editor.commit();
    }*/
}
