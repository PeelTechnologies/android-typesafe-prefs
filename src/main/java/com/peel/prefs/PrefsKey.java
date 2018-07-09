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

import com.google.gson.reflect.TypeToken;

/**
 * A typed key
 *
 * @param <T> Intended type of the content for the key
 *
 * @author Inderjeet Singh
 */
public class PrefsKey<T> {

    private final String name;
    private final Type type;
    private final boolean cacheableInMemory;

    /**
     * @param name Ensure that this name is Unique.
     * @param clazz the type of this key
     */
    public PrefsKey(String name, Class<T> clazz) {
        this(name, clazz, true);
    }

    public PrefsKey(String name, Class<T> clazz, boolean cacheableInMemory) {
        this.name = name;
        this.type = clazz;
        this.cacheableInMemory = cacheableInMemory;
    }

    /**
     * @param name Ensure that this name is unique across the preference file
     * @param type the type for this key
     */
    public PrefsKey(String name, TypeToken<T> type) {
        this(name, type, true);
    }
    /**
     * @param name Ensure that this name is unique across the preference file
     * @param type the type for this key
     * @param cacheableInMemory Whether this key can be stored in an in-memory cache
     *   for faster access. If false, the key is loaded form disk on every access
     */
    public PrefsKey(String name, TypeToken<T> type, boolean cacheableInMemory) {
        this.name = name;
        this.type = type.getType();
        this.cacheableInMemory = cacheableInMemory;
    }

    public String getName() {
        return name;
    }

    public Type getTypeOfValue() {
        return type;
    }

    public boolean isCacheableInMemory() {
        return cacheableInMemory;
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
        PrefsKey<?> other = (PrefsKey<?>) obj;
        return name.equals(other.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
