/*
 * Copyright (C) 2018 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.proninyaroslav.template;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides functions, that are executed inside the template.
 * Defining the mapping from names to methods (that can be mapping from
 * names to list of overridden methods). Methods can only be static
 */

public class FuncMap {
    static final FuncMap builtins = BuiltinsFuncs.create();
    private final Map<String, List<Method>> funcs = new HashMap<>();

    /**
     * Find static method (or methods, if it is overridden) in
     * the specified class and put it in the map.
     * The alias is used to call function
     *
     * @param alias      alias of method
     * @param methodName method name
     * @param c          class type
     */
    public void put(final String alias, final String methodName, final Class c) {
        findAndPut(alias, methodName, c.getMethods());
    }

    public void put(final Map<String, String> aliasToName, final Class c) {
        final Method[] methods = c.getMethods();
        for (Map.Entry<String, String> i : aliasToName.entrySet())
            findAndPut(i.getKey(), i.getValue(), methods);
    }

    public void put(final FuncMap funcMap) {
        funcs.putAll(funcMap.funcs);
    }

    /**
     * Find function (or functions, if it is overridden)
     * by alias name and returns it
     *
     * @param alias alias of function
     * @return functions list
     */
    public List<Method> get(final String alias) {
        return funcs.get(alias);
    }

    public boolean contains(final String alias) {
        return get(alias) != null;
    }

    public Map<String, List<Method>> getAll() {
        return funcs;
    }

    private void findAndPut(final String alias, final String methodName, final Method[] methods) {
        final List<Method> found = new ArrayList<>();
        for (Method method : methods) {
            if (method.getName().equals(methodName) &&
                    Modifier.isStatic(method.getModifiers())) {
                found.add(method);
            }
        }
        if (found.isEmpty()) {
            throw new IllegalArgumentException(String.format("method '%s' not found, not static or non-public", methodName));
        }
        funcs.put(alias, found);
    }
}

