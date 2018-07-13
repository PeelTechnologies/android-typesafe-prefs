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

import com.google.gson.Gson;
import com.peel.prefs.Prefs.EventListener;

import android.content.Context;

/**
 * This class provides type-safe access to Android preferences. Any arbitrary object
 * that is serializable to JSON using Gson can be used.
 *
 * @author Inderjeet Singh
 */
public class SharedPrefs {
    private static Prefs prefs;

    public static void init(Context context, Gson gson) {
        init(new Prefs(context, gson));
    }

    public static void init(Context context, Gson gson, String prefsFileName, int maxCacheSize) {
        init(new Prefs(context, gson, prefsFileName, maxCacheSize));
    }

    public static void init(Prefs prefs) {
        SharedPrefs.prefs = prefs;
    }

    public static void addListener(EventListener listener) {
        prefs.addListener(listener);
    }

    public static void removeListener(EventListener listener) {
        prefs.removeListener(listener);
    }

    public static Context context() {
        return prefs.context();
    }

    public static <T> T get(TypedKey<T> key) {
        return prefs.get(key);
    }

    public static <T> T get(String keyName, Class<T> keyClass) {
        return prefs.get(keyName, keyClass);
    }

    public static <T> T get(TypedKey<T> key, T defaultValue) {
        return prefs.get(key, defaultValue);
    }

    public static <T> T get(String keyName, Class<T> keyClass, T defaultValue) {
        return prefs.get(keyName, keyClass, defaultValue);
    }

    public static <T> boolean contains(TypedKey<T> key) {
        return prefs.contains(key);
    }

    public static <T> boolean contains(String keyName, Class<T> keyClass) {
        return prefs.contains(keyName, keyClass);
    }

    public static <T> void put(TypedKey<T> key, T value) {
        prefs.put(key, value);
    }

    public static <T> void put(String keyName, Class<T> keyClass, T value) {
        prefs.put(keyName,  keyClass, value);
    }

    public static <T> void remove(TypedKey<T> key) {
        prefs.remove(key);
    }

    public static <T> void remove(String keyName, Class<T> keyClass) {
        prefs.remove(keyName, keyClass);
    }

    public static void clear() {
        prefs.clear();
    }
}
