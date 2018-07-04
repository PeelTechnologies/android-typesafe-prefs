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

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This class provides type-safe access to Android preferences. Any arbitrary object
 * that is serializable to JSON using Gson can be used.
 *
 * @author Inderjeet Singh
 */
public class Prefs {

    static final String DEFAULT_PREFS_FILE = "persistent_props";

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
        String json = prefs.getString(key.getName(), null);
        T instance = gson.fromJson(json, key.getTypeOfValue());
        if (instance == null && key.getTypeOfValue() == Boolean.class) {
            return (T) Boolean.FALSE; // default value for Boolean to avoid NPE for flags
        }
        return instance;
    }

    public <T> T get(PrefsKey<T> key, T defaultValue) {
        return contains(key) ? get(key) : defaultValue;
    }

    public <T> boolean contains(PrefsKey<T> key) {
        SharedPreferences prefs = context.getSharedPreferences(prefsFileName, Context.MODE_PRIVATE);
        return prefs.contains(key.getName());
    }

    public <T> void put(PrefsKey<T> key, T value) {
        String json = gson.toJson(value);
        SharedPreferences prefs = context.getSharedPreferences(prefsFileName, Context.MODE_PRIVATE);
        prefs.edit().putString(key.getName(), json).apply();
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
