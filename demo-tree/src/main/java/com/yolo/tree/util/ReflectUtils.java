package com.yolo.tree.util;



import cn.hutool.core.convert.Convert;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;


import java.lang.reflect.*;
import java.util.Date;

/**
 *  反射工具类. 提供调用getter/setter方法, 访问私有变量, 调用私有方法, 获取泛型类型Class, 被AOP过的真实类等工具函数.
 */
public class ReflectUtils {
    private final static Logger logger = LoggerFactory.getLogger(ReflectUtils.class);
    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    /**
     * 调用Getter方法.
     * 支持多级，如：对象名.对象名.方法
     */
    @SuppressWarnings("unchecked")
    public static <E> E invokeGetter(Object obj, String propertyName) {
        Object object = obj;
        for (String name : StringUtils.split(propertyName, ".")) {
            String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(name);
            object = invokeMethod(object, getterMethodName, new Class[]{}, new Object[]{});
        }
        return (E) object;
    }

    /**
     * 调用Setter方法, 仅匹配方法名。
     * 支持多级，如：对象名.对象名.方法
     */
    public static <E> void invokeSetter(Object obj, String propertyName, E value) {
        Object object = obj;
        String[] names = StringUtils.split(propertyName, ".");
        for (int i = 0; i < names.length; i++) {
            if (i < names.length - 1) {
                String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(names[i]);
                object = invokeMethod(object, getterMethodName, new Class[]{}, new Object[]{});
            } else {
                String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(names[i]);
                invokeMethodByName(object, setterMethodName, new Object[]{value});
            }
        }
    }

    /**
     * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
     */
    @SuppressWarnings("unchecked")
    public static <E> E getFieldValue(final Object obj, final String fieldName) {
        Field field = getAccessibleField(obj, fieldName);
        if (field == null) {
            logger.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + fieldName + "] 字段 ");
            return null;
        }
        E result = null;
        try {
            result = (E) field.get(obj);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
        return result;
    }

    /**
     * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
     */
    public static <E> void setFieldValue(final Object obj, final String fieldName, final E value) {
        Field field = getAccessibleField(obj, fieldName);
        if (field == null) {
            logger.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + fieldName + "] 字段 ");
            return;
        }
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            logger.error("不可能抛出的异常: {}", e.getMessage());
        }
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符.
     * 用于一次性调用的情况，否则应使用getAccessibleMethod()函数获得Method后反复调用.
     * 同时匹配方法名+参数类型，
     */
    @SuppressWarnings("unchecked")
    public static <E> E invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
                                     final Object[] args) {
        if (obj == null || methodName == null) {
            return null;
        }
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {
            logger.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + methodName + "] 方法 ");
            return null;
        }
        try {
            return (E) method.invoke(obj, args);
        } catch (Exception e) {
            String msg = "method: " + method + ", obj: " + obj + ", args: " + args + "";
            throw convertReflectionExceptionToUnchecked(msg, e);
        }
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符，
     * 用于一次性调用的情况，否则应使用getAccessibleMethodByName()函数获得Method后反复调用.
     * 只匹配函数名，如果有多个同名函数调用第一个。
     */
    @SuppressWarnings("unchecked")
    public static <E> E invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
        Method method = getAccessibleMethodByName(obj, methodName, args.length);
        if (method == null) {
            // 如果为空不报错，直接返回空。
            logger.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + methodName + "] 方法 ");
            return null;
        }
        try {
            // 类型转换（将参数数据类型转换为目标方法参数类型）
            Class<?>[] cs = method.getParameterTypes();
            for (int i = 0; i < cs.length; i++) {
                if (args[i] != null && !args[i].getClass().equals(cs[i])) {
                    if (cs[i] == String.class) {
                        args[i] = Convert.toStr(args[i]);
                        if (StringUtils.endsWith((String) args[i], ".0")) {
                            args[i] = StringUtils.substringBefore((String) args[i], ".0");
                        }
                    } else if (cs[i] == Integer.class) {
                        args[i] = Convert.toInt(args[i]);
                    } else if (cs[i] == Long.class) {
                        args[i] = Convert.toLong(args[i]);
                    } else if (cs[i] == Double.class) {
                        args[i] = Convert.toDouble(args[i]);
                    } else if (cs[i] == Float.class) {
                        args[i] = Convert.toFloat(args[i]);
                    } else if (cs[i] == Date.class) {
                        if (args[i] instanceof String) {
                            args[i] = DateUtils.parseDate((String) args[i]);
                        }
                    }
                }
            }
            return (E) method.invoke(obj, args);
        } catch (Exception e) {
            String msg = "method: " + method + ", obj: " + obj + ", args: " + args + "";
            throw convertReflectionExceptionToUnchecked(msg, e);
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     */
    public static Field getAccessibleField(final Object obj, final String fieldName) {
        // 为空不报错。直接返回 null
        if (obj == null) {
            return null;
        }
        Validate.notBlank(fieldName, "fieldName can't be blank");
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                makeAccessible(field);
                return field;
            } catch (NoSuchFieldException e) {
                continue;
            }
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 匹配函数名+参数类型。
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethod(final Object obj, final String methodName,
                                             final Class<?>... parameterTypes) {
        // 为空不报错。直接返回 null
        if (obj == null) {
            return null;
        }
        Validate.notBlank(methodName, "methodName can't be blank");
        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            try {
                Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
                makeAccessible(method);
                return method;
            } catch (NoSuchMethodException e) {
                continue;
            }
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 只匹配函数名。
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethodByName(final Object obj, final String methodName, int argsNum) {
        // 为空不报错。直接返回 null
        if (obj == null) {
            return null;
        }
        Validate.notBlank(methodName, "methodName can't be blank");
        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName) && method.getParameterTypes().length == argsNum) {
                    makeAccessible(method);
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 改变private/protected的方法为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     */
    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * 改变private/protected的成员变量为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
     */
    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
                || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 通过反射, 获得Class定义中声明的泛型参数的类型, 注意泛型必须定义在父类处
     * 如无法找到, 返回Object.class.
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClassGenricType(final Class clazz) {
        return getClassGenricType(clazz, 0);
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.
     * 如无法找到, 返回Object.class.
     */
    public static Class getClassGenricType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            logger.debug(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            logger.debug("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
                                 + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            logger.debug(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }

        return (Class) params[index];
    }


    /**
     * 将反射时的checked exception转换为unchecked exception.
     */
    public static RuntimeException convertReflectionExceptionToUnchecked(String msg, Exception e) {
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
                || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(msg, e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException(msg, ((InvocationTargetException) e).getTargetException());
        }
        return new RuntimeException(msg, e);
    }


    /**
     * 创建一个无参数的构造函数
     *
     * @param className 类全名
     *
     * @return
     */
    public static Object createObject(String className) {
        Class[] classes = new Class[]{};
        Object[] objects = new Object[]{};
        return createObject(className, classes, objects);
    }

    /**
     * 创建一个无参数的构造函数
     *
     * @param clazz 类对象
     *
     * @return
     */
    public static Object createObject(Class clazz) {
        Class[] classes = new Class[]{};
        Object[] objects = new Object[]{};
        return createObject(clazz, classes, objects);
    }

    /**
     * 创建构造函数只有一个参数的对象
     *
     * @param className
     * @param paramType
     * @param paramValue
     *
     * @return
     */
    public static Object createObject(String className, Class paramType, Object paramValue) {
        Class[] classes = new Class[]{paramType};
        Object[] objects = new Object[]{paramValue};
        return createObject(className, classes, objects);
    }

    /**
     * 创建构造函数只有一个参数的对象
     *
     * @param clazz
     * @param paramType
     * @param paramValue
     *
     * @return
     */
    public static Object createObject(Class clazz, Class paramType, Object paramValue) {
        Class[] classes = new Class[]{paramType};
        Object[] objects = new Object[]{paramValue};
        return createObject(clazz, classes, objects);
    }

    /**
     * 创建一个多参数的对象
     *
     * @param className    类全名
     * @param paramsTypes  构造函数参数
     * @param paramsValues 默认构造函数要传的值
     *
     * @return
     */
    public static Object createObject(String className, Class[] paramsTypes, Object[] paramsValues) {
        try {
            Class clazz = Class.forName(className);
            Constructor constructor = clazz.getDeclaredConstructor(paramsTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(paramsValues);
        } catch (Exception e) {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
        return null;
    }

    /**
     * 创建一个多参数的的对象
     *
     * @param clazz        类对象
     * @param paramsTypes  构造函数参数集合
     * @param paramsValues 构造函数参数值
     *
     * @return
     */
    public static Object createObject(Class clazz, Class[] paramsTypes, Object[] paramsValues) {
        try {
            Constructor constructor = clazz.getDeclaredConstructor(paramsTypes);
            constructor.setAccessible(true);
            Object object = constructor.newInstance(paramsValues);
            return object;
        } catch (Exception e) {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
        return null;
    }


    /**
     * 执行一个多参数的静态方法
     *
     * @param className    类全名
     * @param methodName   方法名
     * @param paramsTypes  方法参数
     * @param paramsValues 方法参数值
     *
     * @return
     */
    public static Object invokeStaticMethod(String className, String methodName, Class[] paramsTypes, Object[] paramsValues) {
        try {
            Class clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod(methodName, paramsTypes);
            method.setAccessible(true);
            return method.invoke(null, paramsValues);
        } catch (Exception e) {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
        return null;
    }

    /**
     * 执行一个无参数的静态方法
     *
     * @param className
     * @param methodName
     *
     * @return
     */
    public static Object invokeStaticMethod(String className, String methodName) {
        Class[] classes = new Class[]{};
        Object[] objects = new Object[]{};
        return invokeStaticMethod(className, methodName, classes, objects);
    }

    /**
     * 执行一个参数的静态方法
     *
     * @param className
     * @param methodName
     * @param paramType
     * @param paramValue
     *
     * @return
     */
    public static Object invokeStaticMethod(String className, String methodName, Class paramType, Object paramValue) {
        Class[] classes = new Class[]{paramType};
        Object[] objects = new Object[]{paramValue};
        return invokeStaticMethod(className, methodName, classes, objects);
    }

    /**
     * 执行一个多参数的静态方法
     *
     * @param methodName   方法名
     * @param paramsTypes  方法参数
     * @param paramsValues 方法参数值
     *
     * @return
     */
    public static <E> E     invokeStaticMethod(Class clazz, String methodName, Class[] paramsTypes, Object[] paramsValues) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, paramsTypes);
            method.setAccessible(true);
            return (E) method.invoke(null, paramsValues);
        } catch (Exception e) {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
        return null;
    }

    /**
     * 执行一个无参数数的静态方法
     *
     * @param methodName
     *
     * @return
     */
    public static <E> E invokeStaticMethod(Class clazz, String methodName) {
        Class[] classes = new Class[]{};
        Object[] objects = new Object[]{};
        return invokeStaticMethod(clazz, methodName, classes, objects);
    }

    /**
     * 执行一个参数的静态方法
     *
     * @param methodName
     * @param paramType
     * @param paramValue
     *
     * @return
     */
    public static Object invokeStaticMethod(Class clazz, String methodName, Class paramType, Object paramValue) {
        Class[] classes = new Class[]{paramType};
        Object[] objects = new Object[]{paramValue};
        return invokeStaticMethod(clazz, methodName, classes, objects);
    }

    /**
     * 获取Field的值
     *
     * @param clazz     类的class
     * @param obj       类的实例对象
     * @param fieldName 属性名称
     *
     * @return
     */
    public static Object getField(Class clazz, Object obj, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
        return null;

    }

    /**
     * 获取属性值
     *
     * @param obj
     * @param fieldName
     *
     * @return
     */
    public static Object getField(Object obj, String fieldName) {
        return getField(obj.getClass(), fieldName);
    }

    public static Object getField(String className, Object obj, String fieldName) {
        try {
            Class clazz = Class.forName(className);
            return getField(clazz, obj, fieldName);
        } catch (Exception e) {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
        return null;
    }

    /**
     * 设置属性的值
     *
     * @param clazz      类对象
     * @param obj        类实例对象
     * @param fieldName  属性名称
     * @param fieldValue 属性值
     *
     * @return
     */
    public static void setField(Class clazz, Object obj, String fieldName, Object fieldValue) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, fieldValue);
        } catch (Exception e) {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
    }

    public static void setField(String className, Object obj, String fieldName, Object fieldValue) {
        try {
            Class clazz = Class.forName(className);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, fieldValue);
        } catch (Exception e) {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
    }

    public static void setField(Object obj, String fieldName, Object fieldValue) {
        setField(obj.getClass(), fieldName, fieldValue);
    }

    /**
     * 获取静态属性
     *
     * @param className
     * @param filedName
     *
     * @return
     */
    public static Object getStaticField(String className, String filedName) {
        return getField(className, null, filedName);
    }

    /**
     * 设置静态属性
     *
     * @param clazz
     * @param filedName
     *
     * @return
     */
    public static Object getStaticField(Class clazz, String filedName) {
        return getField(clazz, null, filedName);
    }

    /**
     * 设置静态属性
     *
     * @param classname
     * @param filedName
     * @param filedVaule
     */
    public static void setStaticField(String classname, String filedName, Object filedVaule) {
        setField(classname, null, filedName, filedVaule);
    }

    /**
     * 获取静态属性
     *
     * @param clazz
     * @param filedName
     * @param filedVaule
     */
    public static void setStaticField(Class clazz, String filedName, Object filedVaule) {
        setField(clazz, null, filedName, filedVaule);
    }

}