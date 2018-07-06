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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.Gson;

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
public class BooleanTest {

    private Context context;
    private Prefs prefs;
    private static final Gson gson = new Gson();
    private SharedPreferences persistPrefs;

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
    public void primitiveStoredAsNumber() {
        prefs.put(new PrefsKey<>("key", boolean.class), true);
        assertTrue(persistPrefs.getBoolean("key", false));
    }

    @Test
    public void primitiveWrapperStoredAsNumber() {
        prefs.put(new PrefsKey<>("key", Boolean.class), true);
        assertTrue(persistPrefs.getBoolean("key", false));
    }

    @Test
    public void restoreToPrimitive() {
        persistPrefs.edit().putBoolean("key", true).apply();
        assertTrue(prefs.get(new PrefsKey<>("key", boolean.class)));
    }

    @Test
    public void restoreToPrimitiveWrapper() {
        persistPrefs.edit().putBoolean("key", Boolean.TRUE).apply();
        assertTrue(prefs.get(new PrefsKey<>("key", Boolean.class)));

        persistPrefs.edit().putBoolean("key", false).apply();
        assertFalse(prefs.get(new PrefsKey<>("key", Boolean.class)));
    }

    @Test
    public void restoreStringToPrimitive() {
        persistPrefs.edit().putString("key", "true").apply();
        assertTrue(prefs.get(new PrefsKey<>("key", boolean.class)));
    }

    @Test
    public void restoreStringToPrimitiveWrapper() {
        persistPrefs.edit().putString("key", "true").apply();
        assertTrue(prefs.get(new PrefsKey<>("key", Boolean.class)));
    }

    @Test
    public void restoreFromJson() {
        persistPrefs.edit().putString("key", "\"true\"").apply();
        assertTrue(prefs.get(new PrefsKey<>("key", Boolean.class)));
        assertTrue(prefs.get(new PrefsKey<>("key", boolean.class)));
    }
}
