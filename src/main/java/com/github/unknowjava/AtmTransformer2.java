package com.github.unknowjava;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author caoyanfei
 * @Classname AtmTransformer
 * @Date 2022/6/16 10:26
 */
public class AtmTransformer2 implements ClassFileTransformer {

    private static final String WITHDRAW_MONEY_METHOD = "withdrawMoney";
    private static Integer LEVEL = 0;

    /** The internal form class name of the class to transform */
    private String targetClassName;
    /** The class loader of the class we want to transform */
    private ClassLoader targetClassLoader;

    public AtmTransformer2(String targetClassName, ClassLoader targetClassLoader) {
        this.targetClassName = targetClassName;
        this.targetClassLoader = targetClassLoader;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        byte[] byteCode = classfileBuffer;

        String finalTargetClassName = this.targetClassName.replaceAll("\\.", "/"); //replace . with /
        if (!className.equals(finalTargetClassName)) {
            return byteCode;
        }

        if (className.equals(finalTargetClassName) && loader.equals(targetClassLoader)) {
            try {
                ClassPool cp = ClassPool.getDefault();
                CtClass cc = cp.get(targetClassName);
                CtMethod m = cc.getDeclaredMethod(WITHDRAW_MONEY_METHOD);
                m.addLocalVariable("startTime", CtClass.longType);
                m.insertBefore("startTime = System.currentTimeMillis();");


                StringBuilder endBlock = new StringBuilder();

                m.addLocalVariable("endTime", CtClass.longType);
                m.addLocalVariable("opTime", CtClass.longType);
                endBlock.append("endTime = System.currentTimeMillis();");
                endBlock.append("opTime = (endTime-startTime)/1000;");

                endBlock.append("LOGGER.info(\"[Application] Withdrawal operation completed in:\" + opTime + \" seconds!\");");

                m.insertAfter(endBlock.toString());

                byteCode = cc.toBytecode();
                cc.detach();
            } catch (NotFoundException | CannotCompileException | IOException e) {
//                LOGGER.error("Exception", e);
            }
        }
        return byteCode;
    }

    private String printLog = """
            String methodName = "{}";
            String prefix = "{}";
            String fileName = "{}";
            
            
            """;

    public static void main(String[] args) {
        System.out.println(
        Collections.nCopies(8, " 1").stream().collect(Collectors.joining()));
        try {
            Files.write(Path.of(""), "".getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
        }
    }

}
