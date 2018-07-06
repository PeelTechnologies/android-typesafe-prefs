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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
public class PrefsTest {

    private Context context;
    private Prefs prefs;
    private static final Gson gson = new Gson();

    @Before
    public void setUp() {
        context = AndroidFixtures.createMockContext();
        prefs = new Prefs(context, gson);
    }

    @Test
    public void booleanDefaultValueOnGet() {
        PrefsKey<Boolean> testKey = new PrefsKey<>("testKey", Boolean.class);
        prefs.remove(testKey);
        assertFalse(prefs.get(testKey));
        prefs.put(testKey, true);
        assertTrue(prefs.get(testKey));
        prefs.remove(testKey);
    }

    @Test
    public void testPut() {
        PrefsKey<String> key = new PrefsKey<>("userId", String.class);
        assertNull(prefs.get(key));
        prefs.put(key, "19999999999");
        assertNotNull(prefs.get(key));
        assertEquals("19999999999", prefs.get(key));
    }

    @Test
    public void testClear() {
        PrefsKey<String> key = new PrefsKey<>("userId", String.class);
        prefs.put(key, "a");
        assertEquals("a", prefs.get(key));
        prefs.clear();
        assertFalse(prefs.contains(key));
    }

    @Test
    public void testStripJsonQuotes() {
        assertEquals(null, Prefs.stripJsonQuotesIfPresent(null));
        assertEquals("\"", Prefs.stripJsonQuotesIfPresent("\""));
        assertEquals("'", Prefs.stripJsonQuotesIfPresent("'"));
        assertEquals("abcd", Prefs.stripJsonQuotesIfPresent("abcd"));
        assertEquals("", Prefs.stripJsonQuotesIfPresent("\"\""));
        assertEquals("a", Prefs.stripJsonQuotesIfPresent("\"a\""));
        assertEquals("abcd", Prefs.stripJsonQuotesIfPresent("\"abcd\""));
    }

    @Test
    public void testCustomPrefFile() {
        prefs = new Prefs(context, gson, "my_props_file");
        SharedPreferences persistPrefs = AndroidFixtures.createMockSharedPreferences(context, null);
        Mockito.when(context.getSharedPreferences("my_props_file", Context.MODE_PRIVATE)).thenReturn(persistPrefs);

        PrefsKey<String> key = new PrefsKey<>("key", String.class);
        assertNull(prefs.get(key));
        prefs.put(key, "19999999999");
        assertNotNull(prefs.get(key));
        assertEquals("19999999999", prefs.get(key));
    }
}
