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
    private static final Cache<Object[], Method> methodCache = new SimpleCache<Object[], Method>();
    protected Class<?> clazz;
    protected Object instance;

    private SimpleReflection() {
    }

    public static SimpleReflection reflect(Object instance) {
        SimpleReflection sr = new SimpleReflection();
        sr.clazz = instance.getClass();
        sr.instance = instance;
        return sr;
    }

    public static SimpleReflection reflect(Class<?> clazz) {
        SimpleReflection sr = new SimpleReflection();
        sr.clazz = clazz;
        return sr;
    }

    public static SimpleReflection reflect(String className) {
        try {
            SimpleReflection sr = new SimpleReflection();
            sr.clazz = Class.forName(className);
            return sr;
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public ReflectionMethod method(String methodName) {
        return new ReflectionMethod(methodName);
    }

    @SuppressWarnings("unchecked")
    private Method getMethod(String methodName, Class... parameterTypes) throws RuntimeException {
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

    public class ReflectionMethod {
        Class<?>[] paramtypes;
        String methodName;
        Object instance;

        ReflectionMethod(String methodName) {
            this.methodName = methodName;
        }

        public void withParams(Class<?>... params) {
            this.paramtypes = params;
        }

        public void useInstance(Object instance) {
            this.instance = instance;
        }

        public Object call(Object... params) {
            try {
                Object callInstance = instance == null ? SimpleReflection.this.instance : instance;
                if (params != null) {
                    if ((paramtypes == null || paramtypes.length == 0) && params.length > 0) {
                        paramtypes = SimpleReflection.this.getParameterTypes(params);
                    }
                }
                Method method = SimpleReflection.this.getMethod(methodName, paramtypes);
                return method.invoke(callInstance, params);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (IllegalArgumentException ex) {
                throw new RuntimeException(ex);
            } catch (InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
