package com.github.unknowjava;

import java.lang.instrument.Instrumentation;

/**
 * @author caoyanfei
 * @Classname PrintMethodAgent
 * @Date 2022/6/16 10:20
 */
public class PrintMethodAgent {

    public static void premain(String agentArgs, Instrumentation inst) {

        String className = "com.baeldung.instrumentation.application.MyAtm";
        transformClass(className,inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        String className = "com.baeldung.instrumentation.application.MyAtm";
        transformClass(className,inst);
    }

    private static void transformClass(String className, Instrumentation instrumentation) {
        Class<?> targetCls = null;
        ClassLoader targetClassLoader = null;
        // see if we can get the class using forName
        try {
            targetCls = Class.forName(className);
            targetClassLoader = targetCls.getClassLoader();
            transform(targetCls, targetClassLoader, instrumentation);
            return;
        } catch (Exception ex) {
            // TODO
        }
        // otherwise iterate all loaded classes and find what we want
        for(Class<?> clazz: instrumentation.getAllLoadedClasses()) {
            if(clazz.getName().equals(className)) {
                targetCls = clazz;
                targetClassLoader = targetCls.getClassLoader();
                transform(targetCls, targetClassLoader, instrumentation);
                return;
            }
        }
        throw new RuntimeException("Failed to find class [" + className + "]");
    }

    private static void transform(Class<?> clazz, ClassLoader classLoader, Instrumentation instrumentation) {
        AtmTransformer dt = new AtmTransformer(clazz.getName(), classLoader);
        instrumentation.addTransformer(dt, true);
        try {
            instrumentation.retransformClasses(clazz);
        } catch (Exception ex) {
            throw new RuntimeException("Transform failed for class: [" + clazz.getName() + "]", ex);
        }
    }
}
