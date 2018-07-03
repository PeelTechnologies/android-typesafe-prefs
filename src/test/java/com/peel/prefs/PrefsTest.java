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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Unit tests for {@link Prefs}
 *
 * @author Inderjeet Singh
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class, SharedPreferences.class })
public class PrefsTest {

    private Context context;
    private static final Gson gson = new Gson();

    @Before
    public void setUp() {
        context = AndroidFixtures.createMockContext();
        Prefs.init(context, gson);
        Prefs.clear();
    }

    @Test
    public void booleanDefaultValueOnGet() {
        PrefsKey<Boolean> testKey = new PrefsKey<>("testKey", Boolean.class);
        Prefs.remove(testKey);
        assertFalse(Prefs.get(testKey));
        Prefs.put(testKey, true);
        assertTrue(Prefs.get(testKey));
        Prefs.remove(testKey);
    }

    @Test
    public void testPut() throws Exception {
        PrefsKey<String> key = new PrefsKey<>("userId", String.class);
        assertNull(Prefs.get(key));
        Prefs.put(key, "19999999999");
        assertNotNull(Prefs.get(key));
        assertEquals("19999999999", Prefs.get(key));
    }

    @Test
    public void testPutIfNew() throws Exception {
        PrefsKey<String> key = new PrefsKey<>("userId", String.class);
        Prefs.put(key, "19999999999");
        assertEquals("19999999999", Prefs.get(key));
        Prefs.bindIfAbsent(key, "16506953562");
        assertNotEquals("19999999999", "16506953562");
        assertNotEquals("16506953562", Prefs.get(key));
        assertEquals("19999999999", Prefs.get(key));
    }

    @Test
    public void testClear() throws Exception {
        PrefsKey<String> key = new PrefsKey<>("userId", String.class);
        Prefs.put(key, "a");
        assertEquals("a", Prefs.get(key));
        Prefs.clear();
        assertFalse(Prefs.has(key));
    }
}
