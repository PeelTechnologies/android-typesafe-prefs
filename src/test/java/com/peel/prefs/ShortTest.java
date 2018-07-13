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
public class ShortTest {

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
        prefs.put(new TypedKey<>("key", short.class), (short)1);
        assertEquals(1, persistPrefs.getInt("key", 0));
    }

    @Test
    public void primitiveWrapperStoredAsNumber() {
        prefs.put(new TypedKey<>("key", Short.class), new Short((short)1));
        assertEquals(1, persistPrefs.getInt("key", 0));
    }

    @Test
    public void restoreToPrimitive() {
        persistPrefs.edit().putInt("key", 1).apply();
        assertEquals(1, (short) prefs.get(new TypedKey<>("key", short.class)));
    }

    @Test
    public void restoreToPrimitiveWrapper() {
        persistPrefs.edit().putInt("key", 1).apply();
        assertEquals(1, (short) prefs.get(new TypedKey<>("key", Short.class)));
    }

    @Test
    public void restoreStringToPrimitive() {
        persistPrefs.edit().putString("key", "1").apply();
        assertEquals(1, (short) prefs.get(new TypedKey<>("key", short.class)));
    }

    @Test
    public void restoreStringToPrimitiveWrapper() {
        persistPrefs.edit().putString("key", "1").apply();
        assertEquals(1, (short) prefs.get(new TypedKey<>("key", Short.class)));
    }

    @Test
    public void restoreFromJson() {
        persistPrefs.edit().putString("key", "\"1\"").apply();
        assertEquals(1, (short) prefs.get(new TypedKey<>("key", Short.class)));
        assertEquals(1, (short) prefs.get(new TypedKey<>("key", short.class)));
    }
}
