/*
 * Copyright (C) 2018 Peel Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.peel.prefs;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * This class provides type-safe access to Android preferences. Any arbitrary object
 * that is serializable to JSON using Gson can be used.
 *
 * @author Inderjeet Singh
 */
public class Prefs {

    static final String DEFAULT_PREFS_FILE = "persistent_props";
    private static final Type STRING_SET_TYPE = new TypeToken<Set<String>>() {}.getType();

    public interface EventListener {
        <T> void onPut(PrefsKey<T> key, T value);
        <T> void onRemove(PrefsKey<T> key);
    }
    private final List<EventListener> listeners = new ArrayList<>();

    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(EventListener listener) {
        listeners.remove(listener);
    }

    private final Context context;
    private final Gson gson;
    private final String prefsFileName;

    public Prefs(Context context, Gson gson) {
        this(context, gson, DEFAULT_PREFS_FILE);
    }

    public Prefs(Context context, Gson gson, String prefsFileName) {
        this.context = context;
        this.gson = gson;
        this.prefsFileName = prefsFileName;
    }

    public Context context() {
        return context;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(PrefsKey<T> key) {
        SharedPreferences prefs = context.getSharedPreferences(prefsFileName, Context.MODE_PRIVATE);
        String name = key.getName();
        Type type = key.getTypeOfValue();
        T instance = null;
        try {
            if (prefs.contains(name)) {
                if (type == Boolean.class || type == boolean.class) {
                    boolean value = prefs.getBoolean(name, false);
                    instance = (T) Boolean.valueOf(value);
                } else if (type == String.class) {
                    String str = prefs.getString(name, null);
                    str = stripJsonQuotesIfPresent(str);
                    instance = (T) str;
                } else if (type == Integer.class || type == int.class) {
                    int value = prefs.getInt(name, 0);
                    instance = (T) Integer.valueOf((int)value);
                } else if (type == Long.class || type == long.class) {
                    long value = prefs.getLong(name, 0L);
                    instance = (T) Long.valueOf((long)value);
                } else if (type == Float.class || type == float.class) {
                    float value = prefs.getFloat(name, 0f);
                    instance = (T) Float.valueOf((float)value);
                } else if (type == Double.class || type == double.class) {
                    float value = prefs.getFloat(name, 0f);
                    instance = (T) Double.valueOf((double)value);
                } else if (type == Short.class || type == short.class) {
                    int value = prefs.getInt(name, 0);
                    instance = (T) Short.valueOf((short)value);
                } else if (type == Byte.class || type == byte.class) {
                    int value = prefs.getInt(name, 0);
                    instance = (T) Byte.valueOf((byte)value);
                } else if (type.equals(STRING_SET_TYPE)) {
                    Set<String> value = prefs.getStringSet(name, null);
                    instance = (T) value;
                }
            }
        } catch (ClassCastException ignored) {
            // This can happen if the integer was previously stored as String
        }
        if (instance == null) {
            String json = prefs.getString(name, null);
            instance = gson.fromJson(json, type);
        }
        if (instance == null && type == Boolean.class) {
            return (T) Boolean.FALSE; // default value for Boolean to avoid NPE for flags
        }
        return instance;
    }

    // Visible for testing only
    static String stripJsonQuotesIfPresent(String str) {
        if (str == null) return str;
        int lastCharIndex = str.length() - 1;
        if (lastCharIndex < 1) return str; // one char string
        if (str.charAt(0) == '"' && str.charAt(lastCharIndex) == '"') {
            str = str.substring(1, lastCharIndex);
        }
        return str;
    }

    public <T> T get(PrefsKey<T> key, T defaultValue) {
        return contains(key) ? get(key) : defaultValue;
    }

    public <T> boolean contains(PrefsKey<T> key) {
        SharedPreferences prefs = context.getSharedPreferences(prefsFileName, Context.MODE_PRIVATE);
        return prefs.contains(key.getName());
    }

    @SuppressWarnings("unchecked")
    public <T> void put(PrefsKey<T> key, T value) {
        SharedPreferences prefs = context.getSharedPreferences(prefsFileName, Context.MODE_PRIVATE);
        String name = key.getName();
        Type type = key.getTypeOfValue();
        Editor editor = prefs.edit();
        if (type == Boolean.class || type == boolean.class) {
            editor.putBoolean(name, (Boolean) value);
        } else if (type == String.class) {
            editor.putString(name, (String) value);
        } else if (type == Integer.class || type == int.class) {
            editor.putInt(name, (Integer) value);
        } else if (type == Long.class || type == long.class) {
            editor.putLong(name, (Long) value);
        } else if (type == Float.class || type == float.class) {
            editor.putFloat(name, (Float) value);
        } else if (type == Double.class || type == double.class) {
            Double wrapper = value instanceof Double ? (Double) value : new Double((Double) value);
            editor.putFloat(name, wrapper.floatValue());
        } else if (type == Short.class || type == short.class) {
            editor.putInt(name, (Short) value);
        } else if (type == Byte.class || type == byte.class) {
            editor.putInt(name, (Byte) value);
        } else if (type.equals(STRING_SET_TYPE)) {
            editor.putStringSet(name, (Set<String>)value);
        } else {
            String json = gson.toJson(value);
            editor.putString(name, json);
        }
        editor.apply();
        for (EventListener listener : listeners) listener.onPut(key, value);
    }

    /**
     * Removes a provider as well as any registered instances with this name
     * @param <T> the type of the {@code TypedKey}
     * @param key the key that was previously bound as an instance or a provider. If the key was not bound previously, nothing is done
     */
    public <T> void remove(PrefsKey<T> key) {
        SharedPreferences prefs = context.getSharedPreferences(prefsFileName, Context.MODE_PRIVATE);
        prefs.edit().remove(key.getName()).apply();
        for (EventListener listener : listeners) listener.onRemove(key);
    }

    public void clear() {
        SharedPreferences prefs = context.getSharedPreferences(prefsFileName, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
