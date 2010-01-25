/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2009 SACI Informática Ltda.
 */

package saci.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SimpleReflection {
    public static final SimpleReflection instance = new SimpleReflection();
    private final Cache<Object[], Method> methodCache = new SimpleCache<Object[], Method>();

    private SimpleReflection() {
    }

    public Object callMethod(Object instance, String methodName, Object... args) {
        return callMethod(instance.getClass(), methodName, instance, args);
    }

    public Object callMethod(String className, String methodName, Object instance, Object... args) {
        try {
            return callMethod(Class.forName(className), methodName, instance, args);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Object callMethod(Class<?> clazz, String methodName, Object instance, Object... args) {
        Method method = getMethod(clazz, methodName, args);
        try {
            return method.invoke(instance, args);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Method getMethod(Class<?> clazz, String methodName, Object[] args) throws RuntimeException {
        Class<?>[] parameterTypes = getParameterTypes(args);
        Object[] key = new Object[]{clazz, methodName, parameterTypes};
        Method method = methodCache.get(key);
        if (method == null) {
            try {
                method = clazz.getMethod(methodName, parameterTypes);
                synchronized (methodCache) {
                    methodCache.put(key, method);
                }
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            } catch (SecurityException ex) {
                throw new RuntimeException(ex);
            }
        }
        return method;
    }

    public Class<?>[] getParameterTypes(Object... args) {
        if (args == null) {
            return null;
        }
        Class<?>[] result = new Class[args.length];
        for (int i = 0; i < result.length; i++) {
            if (args[i] != null) {
                result[i] = args[i].getClass();
            }
        }
        return result;
    }
}
