package mcp.mobius.waila.addons.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BaseModule {

    protected static final Map<String, Class<?>> classes = new HashMap<>();
    protected static final Map<String, Field> fields = new HashMap<>();
    protected static final Map<String, Method> methods = new HashMap<>();

    protected void registerClass(String className) throws Exception {
        if (classes.containsKey(className)) return;
        Class<?> clazz = Class.forName(className);
        classes.put(clazz.getSimpleName(), clazz);
    }

    protected void registerField(String className, String fieldName) throws Exception {
        if (fields.containsKey(className + "." + fieldName)) return;
        Class<?> clazz = classes.get(className);
        Field field = clazz.getField(fieldName);
        fields.put(clazz.getSimpleName() + "." + fieldName, field);
    }

    protected void registerMethod(String className, String methodName, Class<?>... parameterTypes) throws Exception {
        if (methods.containsKey(className + "." + methodName)) return;
        Class<?> clazz = classes.get(className);
        Method method = clazz.getMethod(methodName, parameterTypes);
        methods.put(clazz.getSimpleName() + "." + methodName, method);
    }

    public static Object invokeMethod(String methodKey, Object instance, Object... args) throws Exception {
        Method method = methods.get(methodKey);
        if (method == null) {
            throw new IllegalArgumentException("Method " + methodKey + " is not registered.");
        }

        return method.invoke(instance, args);
    }

    public static Object getField(String fieldKey, Object instance) throws Exception {
        Field field = fields.get(fieldKey);
        if (field == null) {
            throw new IllegalArgumentException("Field " + fieldKey + " is not registered.");
        }

        return field.get(instance);
    }

    public static Class<?> getClass(String className) {
        Class<?> clazz = classes.get(className);
        if (clazz == null) {
            throw new IllegalArgumentException("Class " + className + " is not registered.");
        }
        return clazz;
    }
}
