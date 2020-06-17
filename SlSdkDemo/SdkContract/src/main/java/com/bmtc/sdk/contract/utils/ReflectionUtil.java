package com.bmtc.sdk.contract.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ReflectionUtil {
	
    public static Object tryInvoke(Object target, String methodName, Boolean accessible, Object... args) {
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }

        return tryInvoke(target, methodName, accessible, argTypes, args);
    }

    public static Object tryInvoke(Object target, String methodName, Boolean accessible, Class<?>[] argTypes,
            Object... args) {
        try {
        	Method method = null;
			if (target instanceof Class) {
				method = ((Class) target).getMethod(methodName, argTypes);
			} else {
				method = target.getClass().getMethod(methodName, argTypes);
			}
        	
        	if(accessible != null) {
        		method.setAccessible(accessible.booleanValue());
        	}
        	
            return method.invoke(target, args);
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException ignored) {
        }

        return null;
    }

    public static <E> E callWithDefault(Object target, String methodName, E defaultValue) {
        try {
            return (E) target.getClass().getMethod(methodName, (Class[]) null).invoke(target);
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException ignored) {
        }

        return defaultValue;
    }
}