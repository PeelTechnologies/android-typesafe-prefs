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

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;

import com.google.gson.reflect.TypeToken;

/**
 * A typed key with a name and a bound class/type
 *
 * @param <T> Intended type of the content for the key
 *
 * @author Inderjeet Singh
 */
public class TypedKey<T> {

    private final String name;
    private final Type type;
    private final boolean cacheableInMemory;
    private final String[] tags;
	private final String prefsFileName;

    /**
     * @param name Ensure that this name is Unique.
     * @param clazz the type of this key
     */
    public TypedKey(String name, Class<T> clazz) {
        this(name, clazz, true);
    }

    public TypedKey(String name, Class<T> clazz, String... tags) {
        this(name, clazz, true, tags);
    }

    public TypedKey(String name, Class<T> clazz, boolean cacheableInMemory, String... tags) {
        this(name, clazz, null, cacheableInMemory, tags);
    }

    public TypedKey(String name, Class<T> clazz, String prefsFileName, boolean cacheableInMemory, String... tags) {
        this.name = name;
        this.type = clazz;
        this.prefsFileName = prefsFileName;
        this.cacheableInMemory = cacheableInMemory;
        this.tags = tags;
    }

    /**
     * @param name Ensure that this name is unique across the preference file
     * @param type the type for this key
     */
    public TypedKey(String name, TypeToken<T> type) {
        this(name, type, null, true);
    }

    public TypedKey(String name, TypeToken<T> type, String... tags) {
        this(name, type, null, true, tags);
    }

    /**
     * @param name Ensure that this name is unique across the preference file
     * @param type the type for this key
     * @param prefsFileName the name of the prefs file where this key will be stored. For example, config_props
     *   will be stored in /data/data/<app-package-name>/shared_prefs/config_props.xml
     * @param cacheableInMemory Whether this key can be stored in an in-memory cache
     *   for faster access. If false, the key is loaded form disk on every access
     * @param tags any additional tags to associate with this key
     */
    public TypedKey(String name, TypeToken<T> type, String prefsFileName, boolean cacheableInMemory, String... tags) {
        this.name = name;
        this.prefsFileName = prefsFileName;
        this.type = type.getType();
        this.cacheableInMemory = cacheableInMemory;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public Type getTypeOfValue() {
        return type;
    }

    public String getPrefsFileName() {
        return prefsFileName;
    }

    public boolean isCacheableInMemory() {
        return cacheableInMemory;
    }

    public boolean containsTag(String tagName) {
        if (tags == null) return false;
        for (String tag : tags) {
            if (tag.equals(tagName)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public Iterable<String> tags() {
        return tags == null ? Collections.EMPTY_LIST : Arrays.asList(tags);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        TypedKey<?> other = (TypedKey<?>) obj;
        return name.equals(other.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
