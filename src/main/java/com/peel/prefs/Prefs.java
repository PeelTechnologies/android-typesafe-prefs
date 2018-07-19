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
import android.preference.PreferenceManager;
import android.util.LruCache;

/**
 * This class provides type-safe access to Android preferences. Any arbitrary object
 * that is serializable to JSON using Gson can be used.
 *
 * @author Inderjeet Singh
 */
public class Prefs {

    private static final Type STRING_SET_TYPE = new TypeToken<Set<String>>() {}.getType();

    public interface EventListener {
        <T> void onPut(TypedKey<T> key, T value);
        <T> void onRemove(TypedKey<T> key);
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
    // Visible for testing only
    final LruCache<String, Object> cache;

    public Prefs(Context context, Gson gson) {
        this(context, gson, null, 25);
    }

    public Prefs(Context context, Gson gson, String prefsFileName, int maxCacheSize) {
        this.context = context;
        this.gson = gson;
        this.prefsFileName = prefsFileName;
        this.cache = new LruCache<>(maxCacheSize);
    }

    public Context context() {
        return context;
    }

    public <T> T get(TypedKey<T> key) {
        return getInternal(key.getName(), key.getTypeOfValue());
    }

    public <T> T get(String key, Class<T> keyClass) {
        return getInternal(key, keyClass);
    }

    public <T> T get(TypedKey<T> key, T defaultValue) {
        return contains(key) ? get(key) : defaultValue;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String keyName, Class<T> keyClass, T defaultValue) {
        boolean contains = getPrefs().contains(keyName);
        return contains ? (T) getInternal(keyName, keyClass) : defaultValue;
    }

    @SuppressWarnings("unchecked")
    private <T> T getInternal(String name, Type type) {
        SharedPreferences prefs = getPrefs();
        T instance = (T) cache.get(name);
        if (instance == null) {
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
        }
        if (instance == null) {
            String json = prefs.getString(name, null);
            instance = gson.fromJson(json, type);
        }
        if (instance == null && (type == Boolean.class || type == boolean.class)) {
            return (T) Boolean.FALSE; // default value for Boolean to avoid NPE for flags
        }
        return instance;
    }

    private SharedPreferences getPrefs() {
        return prefsFileName == null
                ? PreferenceManager.getDefaultSharedPreferences(context)
                : context.getSharedPreferences(prefsFileName, Context.MODE_PRIVATE);
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

    public <T> boolean contains(TypedKey<T> key) {
        String name = key.getName();
        return cache.get(name) != null || getPrefs().contains(name);
    }

    public <T> boolean contains(String keyName, Class<T> keyClass) {
        return getPrefs().contains(keyName);
    }

    public <T> void put(TypedKey<T> key, T value) {
        putInternal(key.getName(), key.getTypeOfValue(), value, key.isCacheableInMemory());
        for (EventListener listener : listeners) listener.onPut(key, value);
    }

    public <T> void put(String keyName, Class<T> keyClass, T value) {
        putInternal(keyName, keyClass, value, false);
        if (!listeners.isEmpty()) {
            TypedKey<T> key = new TypedKey<>(keyName, keyClass, false);
            for (EventListener listener : listeners) listener.onPut(key, value);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void putInternal(String name, Type type, T value, boolean cacheable) {
        if (cacheable) cache.put(name, value);
        SharedPreferences prefs = getPrefs();
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
    }

    /**
     * Removes any registered instances with this name
     * @param <T> the type of the {@code TypedKey}
     * @param key the key that was previously bound as an instance. If the key was not bound previously, nothing is done
     */
    public <T> void remove(TypedKey<T> key) {
        String keyName = key.getName();
        boolean wasPresent = cache.get(keyName) != null;
        SharedPreferences prefs = getPrefs();
        wasPresent = wasPresent || prefs.contains(keyName);
        if (wasPresent) {
            cache.remove(keyName);
            prefs.edit().remove(keyName).apply();
            for (EventListener listener : listeners) listener.onRemove(key);
        }
    }

    /**
     * Removes any registered instances with this name
     * @param <T> the type of the {@code TypedKey}
     * @param key the key that was previously bound as an instance. If the key was not bound previously, nothing is done
     */
    public <T> void remove(String keyName, Class<T> keyClass) {
        boolean wasPresent = cache.get(keyName) != null;
        SharedPreferences prefs = getPrefs();
        wasPresent = wasPresent || prefs.contains(keyName);
        if (wasPresent) {
            cache.remove(keyName);
            prefs.edit().remove(keyName).apply();
            if (!listeners.isEmpty()) {
                TypedKey<T> key = new TypedKey<>(keyName, keyClass, false);
                for (EventListener listener : listeners) listener.onRemove(key);
            }
        }
    }

    public synchronized void clear() {
        cache.evictAll();
        SharedPreferences prefs = getPrefs();
        prefs.edit().clear().apply();
    }
}
