package com.github.unknowjava;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.*;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.stream.Collectors;

import javassist.*;

/**
 * @author caoyanfei
 * @Classname AtmTransformer
 * @Date 2022/6/16 10:26
 */
public class AtmTransformer implements ClassFileTransformer {
//    static {
//        if(!new File(LOG_FILE).exists()) {
//            // TODO
//        }
//    }

    private static Integer LEVEL = 0;
    private static String LOG_FILE = "/tmp/1.txt";

    private String targetPackage;
    private ClassLoader targetClassLoader;

    public AtmTransformer(String targetPackage, ClassLoader targetClassLoader) {
        this.targetPackage = targetPackage;
        this.targetClassLoader = targetClassLoader;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        byte[] byteCode = classfileBuffer;

        String finalTargetClassName = this.targetPackage.replaceAll("\\.", "/"); //replace . with /
        if (!className.startsWith(finalTargetClassName)) {
            return byteCode;
        }
        if (className.startsWith(finalTargetClassName) && loader.equals(targetClassLoader)) {
            try {
                ClassPool cp = ClassPool.getDefault();
                CtClass cc = cp.get(className);

                CtMethod[] methods = cc.getMethods();
                for(CtMethod m: methods) {
                    String methodName = m.getLongName();
//                    m.insertBefore(BEFORE_INVOKE.formatted(LOG_FILE,methodName));
//                    m.insertAfter(AFTER_INVOKE.formatted(LOG_FILE,methodName));
                    m.insertAfter(PRINT_METHOD.formatted(m.getName()));
                }
                byteCode = cc.toBytecode();
                cc.detach();
            } catch (NotFoundException | CannotCompileException | IOException e) {
//                LOGGER.error("Exception", e);
            }
        }
        return byteCode;
    }

    private static String BEFORE_INVOKE = """
            String ___fileName = "%s";
            String ___methodName = "%s";
            String ___content = MethodLogInterceptor.beforeInvoke(___methodName);
            try{
                java.nio.file.Files.write(java.nio.file.Paths.get(___fileName), ___content.getBytes(), java.nio.file.StandardOpenOption.APPEND);
            }catch(IOException e) {
            }
            """;
    private static String AFTER_INVOKE = """
            String ___fileName = "%s";
            String ___methodName = "%s";
            String ___content = MethodLogInterceptor.afterInvoke(___methodName);
            try{
                java.nio.file.Files.write(java.nio.file.Paths.get(___fileName), ___content.getBytes(), java.nio.file.StandardOpenOption.APPEND);
            }catch(IOException e) {
            }
            """;
    private static String PRINT_METHOD = """
            System.out.println("%s");
            """;

    public static void main(String[] args) {
        System.out.println(PRINT_METHOD.formatted("AAAAAAA","Bbbbbbb"));
        System.out.println("AAAAAAA");

    }
}
