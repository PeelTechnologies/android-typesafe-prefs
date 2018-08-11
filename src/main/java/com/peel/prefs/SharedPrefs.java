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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.peel.prefs.Prefs.EventListener;

import android.content.Context;

/**
 * This class provides type-safe access to Android preferences. Any arbitrary
 * object that is serializable to JSON using Gson can be used.
 *
 * @author Inderjeet Singh
 */
public class SharedPrefs {

    private static Context context;
    private static Prefs defaultPrefs;
    private static final Map<String, Prefs> namedPrefs = new HashMap<>();

    public static void init(Context context, Gson gson) {
        init(new Prefs(context, gson));
    }

    public static void init(Context context, Gson gson, String prefsFileName, int maxCacheSize) {
        init(new Prefs(context, gson, prefsFileName, maxCacheSize));
    }

    /**
     * Initialize SharedPreferences with multiple Prefs objects each mapping to a
     * different prefs file on the disk.
     *
     * @param defaultPrefs the default prefs which
     * @param prefsList a set of Preferences each with a unique and non-null
     *                  prefsFileName
     * @throws IllegalArgumentException if any prefsList has a non-null
     *                                  prefsFileName or if two prefs have the same
     *                                  prefsFileName.
     */
    public synchronized static void init(Prefs defaultPrefs, Prefs... prefsList) {
        Prefs.requireNonNull(defaultPrefs);
        SharedPrefs.defaultPrefs = defaultPrefs;
        SharedPrefs.context = defaultPrefs.context();
        String defaultPrefsFileName = defaultPrefs.getPrefsFileName(); // can be null
        if (defaultPrefsFileName != null) {
            namedPrefs.put(defaultPrefsFileName, defaultPrefs);
        }
        if (prefsList != null) {
            for (Prefs prefs : prefsList) {
                String prefsFileName = prefs.getPrefsFileName();
                if (prefsFileName == null) {
                    throw new IllegalArgumentException("Only defaultPrefs is allowed to have a null prefsFileName!");
                }
                if (namedPrefs.containsKey(prefsFileName)) {
                    throw new IllegalArgumentException("Already added Prefs for " + prefsFileName + ". Duplicates not allowed.");
                }
                namedPrefs.put(prefsFileName, prefs);
            }
        }
    }

    public synchronized static void addListener(EventListener listener) {
        defaultPrefs.addListener(listener);
        for (Prefs prefs : namedPrefs.values()) {
            prefs.addListener(listener);
        }
    }

    public synchronized static void removeListener(EventListener listener) {
        defaultPrefs.removeListener(listener);
        for (Prefs prefs : namedPrefs.values()) {
            prefs.removeListener(listener);
        }
    }

    public static Context context() {
        return context;
    }

    public static <T> T get(TypedKey<T> key) {
        return prefs(key).get(key);
    }

    public static <T> T get(String keyName, Class<T> keyClass) {
        return defaultPrefs.get(keyName, keyClass);
    }

    public static <T> T get(TypedKey<T> key, T defaultValue) {
        return prefs(key).get(key, defaultValue);
    }

    public static <T> T get(String keyName, Class<T> keyClass, T defaultValue) {
        return defaultPrefs.get(keyName, keyClass, defaultValue);
    }

    public Set<String> keySet(String prefName) {
        return prefs(prefName).keySet();
    }

    public static <T> boolean contains(TypedKey<T> key) {
        return prefs(key).contains(key);
    }

    public static <T> boolean contains(String keyName, Class<T> keyClass) {
        return defaultPrefs.contains(keyName, keyClass);
    }

    public static <T> void put(TypedKey<T> key, T value) {
        prefs(key).put(key, value);
    }

    public static <T> void put(String keyName, Class<T> keyClass, T value) {
        defaultPrefs.put(keyName, keyClass, value);
    }

    public static <T> void remove(TypedKey<T> key) {
        prefs(key).remove(key);
    }

    public static <T> void remove(String keyName, Class<T> keyClass) {
        defaultPrefs.remove(keyName, keyClass);
    }

    public static void clear(String prefsFileName) {
        prefs(prefsFileName).clear();
    }

    private static <T> Prefs prefs(TypedKey<T> key) {
        return prefs(key.getPrefsFileName());
    }

    // visible for testing only
    static <T> Prefs prefs(String prefsFileName) {
        Prefs prefs = prefsFileName == null ? defaultPrefs : namedPrefs.get(prefsFileName);
        Prefs.requireNonNull(prefs, prefsFileName + " not initialized before use!");
        return prefs;
    }

    public static final class TestAccess {
        public static void init(Context context, Gson gson) {
            synchronized (SharedPrefs.class) {
                reset();
                SharedPrefs.init(context, gson);
            }
        }

        public static void init(Context context, Gson gson, String prefsFileName, int maxCacheSize) {
            synchronized (SharedPrefs.class) {
                reset();
                SharedPrefs.init(context, gson, prefsFileName, maxCacheSize);
            }
        }

        public static void init(Prefs defaultPrefs, Prefs... prefsList) {
            synchronized (SharedPrefs.class) {
                reset();
                SharedPrefs.init(defaultPrefs, prefsList);
            }
        }

        private static void reset() {
            SharedPrefs.context = null;
            SharedPrefs.defaultPrefs = null;
            for (Prefs prefs : namedPrefs.values()) {
                prefs.clear();
            }
            namedPrefs.clear();
        }
    }
}
