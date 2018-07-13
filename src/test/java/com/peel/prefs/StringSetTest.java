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

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Unit tests for {@link Prefs}
 *
 * @author Inderjeet Singh
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class, SharedPreferences.class, PreferenceManager.class})
public class StringSetTest {

    private Context context;
    private Prefs prefs;
    private static final Gson gson = new Gson();
    private SharedPreferences persistPrefs;
    private static final TypeToken<Set<String>> stringSetType = new TypeToken<Set<String>>() {}; 

    @Before
    public void setUp() {
        context = AndroidFixtures.createMockContext(new AndroidFixtures.PrefsListener() {
            @Override public void onInit(SharedPreferences persist) {
                persistPrefs = persist;
            }
        });
        prefs = new Prefs(context, gson);
    }

    @Test
    public void store() {
        Set<String> values = Collections.singleton("abcd");
        prefs.put(new TypedKey<>("key", stringSetType), values);
        assertEquals("abcd", persistPrefs.getStringSet("key", null).iterator().next());
    }

    @Test
    public void restore() {
        Set<String> values = Collections.singleton("abcd");
        System.out.println("Values: " + persistPrefs.getStringSet("key", null));
        persistPrefs.edit().putStringSet("key", values).apply();
        assertEquals("abcd", prefs.get(new TypedKey<>("key", stringSetType)).iterator().next());
    }

    @Test
    public void restoreFromJson() {
        persistPrefs.edit().putString("key", "[\"abcd\"]").apply();
        assertEquals("abcd", prefs.get(new TypedKey<>("key", stringSetType)).iterator().next());
    }
}
