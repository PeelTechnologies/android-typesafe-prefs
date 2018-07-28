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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

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
public class SharedPrefsTest {

    private static final Gson gson = new Gson();

    @Test
    public void testInitWIthCustomDefaultProps() {
        Context context = AndroidFixtures.createMockContext(null, "my_props");
        SharedPrefs.TestAccess.init(context, gson, "my_props", 3);
        SharedPrefs.put("userId",  String.class, "1999999");
        assertEquals("1999999", SharedPrefs.get("userId", String.class));
        SharedPrefs.remove("userId", String.class);
        assertNull(SharedPrefs.get("userId", String.class));
    }

    @Test
    public void getPutWithKeyNameAndClass() {
        Context context = AndroidFixtures.createMockContext();
        SharedPrefs.TestAccess.init(context, gson);
        SharedPrefs.put("userId",  String.class, "1999999");
        assertEquals("1999999", SharedPrefs.get("userId", String.class));
        SharedPrefs.remove("userId", String.class);
        assertNull(SharedPrefs.get("userId", String.class));
    }

    @Test
    public void testInitWithNonNullDefaultPrefs() {
        Context context = AndroidFixtures.createMockContext(null, "my_props1", "my_props2");
        SharedPrefs.TestAccess.init(new Prefs(context, gson, "my_props1", 2),
                new Prefs(context, gson, "my_props2", 3));
        SharedPrefs.put("key",  String.class, "1");
        assertEquals("1", SharedPrefs.get("key", String.class));

        TypedKey<String> key1 = new TypedKey<>("key1", String.class, null, false);
        SharedPrefs.put(key1,  "1");
        assertEquals("1", SharedPrefs.get(key1));
        assertEquals("1", SharedPrefs.get("key1", String.class)); // side effect, not really intended but works

        TypedKey<String> key2 = new TypedKey<>("key2", String.class, "my_props2", false);
        SharedPrefs.put(key2,  "2");
        assertEquals("2", SharedPrefs.get(key2));
    }

    @Test
    public void testInitWithDuplicateDefaultPrefs() {
        Context context = AndroidFixtures.createMockContext(null, "my_props1");
        try {
            SharedPrefs.TestAccess.init(new Prefs(context, gson),
                    new Prefs(context, gson, null, 2), new Prefs(context, gson, "my_props1", 3));
            fail("Non default-prefs can't have null prefsFileName");
        } catch (IllegalArgumentException expected) {}
    }

    @Test
    public void testInitWithDuplicatePrefs() {
        Context context = AndroidFixtures.createMockContext(null, "my_props1");
        try {
            SharedPrefs.TestAccess.init(new Prefs(context, gson),
                    new Prefs(context, gson, "my_props1", 3), new Prefs(context, gson, "my_props1", 3));
            fail("same pref file name can't be repeated");
        } catch (IllegalArgumentException expected) {}
    }

    @Test
    public void testPut() {
        Context context = AndroidFixtures.createMockContext(null, "my_props1", "my_props2");
        SharedPrefs.TestAccess.init(new Prefs(context, gson),
                new Prefs(context, gson, "my_props1", 3), new Prefs(context, gson, "my_props2", 3));

        TypedKey<String> key1 = new TypedKey<>("key", String.class, "my_props1", false);
        TypedKey<String> key2 = new TypedKey<>("key", String.class, "my_props2", false);
        SharedPrefs.put("key",  String.class, "1");
        SharedPrefs.put(key1,  "2");
        SharedPrefs.put(key2,  "3");

        assertEquals("1", SharedPrefs.get("key", String.class));
        assertEquals("2", SharedPrefs.get(key1));
        assertEquals("3", SharedPrefs.get(key2));
    }

    @Test
    public void testClear() {
        Context context = AndroidFixtures.createMockContext(null, "my_props1", "my_props2");
        SharedPrefs.TestAccess.init(new Prefs(context, gson),
                new Prefs(context, gson, "my_props1", 3), new Prefs(context, gson, "my_props2", 3));

        TypedKey<String> key1 = new TypedKey<>("key", String.class, "my_props1", false);
        TypedKey<String> key2 = new TypedKey<>("key", String.class, "my_props2", false);
        SharedPrefs.put("key",  String.class, "1");
        SharedPrefs.put(key1,  "2");
        SharedPrefs.put(key2,  "3");

        SharedPrefs.clear(null);
        assertNull(SharedPrefs.get("key", String.class));
        assertEquals("2", SharedPrefs.get(key1));
        assertEquals("3", SharedPrefs.get(key2));

        SharedPrefs.clear("my_props1");
        assertNull(SharedPrefs.get(key1));
        assertEquals("3", SharedPrefs.get(key2));

        SharedPrefs.clear("my_props2");
        assertNull(SharedPrefs.get(key2));
    }
}
