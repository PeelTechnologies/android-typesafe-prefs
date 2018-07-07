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
public class SharedPrefsTest {

    private Context context;
    private static final Gson gson = new Gson();

    @Before
    public void setUp() {
        context = AndroidFixtures.createMockContext();
        SharedPrefs.init(context, gson);
        SharedPrefs.clear();
    }

    @Test
    public void getPutWithKeyNameAndClass() {
        SharedPrefs.put("userId",  String.class, "1999999");
        assertEquals("1999999", SharedPrefs.get("userId", String.class));
        SharedPrefs.remove("userId", String.class);
        assertNull(SharedPrefs.get("userId", String.class));
    }
}
