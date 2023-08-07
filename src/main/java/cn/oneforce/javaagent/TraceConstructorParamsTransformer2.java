package cn.oneforce.javaagent;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by caoyanfei on 16/10/27.
 */
public class TraceConstructorParamsTransformer2 implements ClassFileTransformer{

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        try {
            String tracePackageNameEnv = System.getProperty("tracePackageName");

            if(tracePackageNameEnv == null || tracePackageNameEnv.trim().length() == 0) {
                return classfileBuffer;
            }
            String[] tracePackageNames = tracePackageNameEnv.split(",");
//            System.out.println(tracePackageNameEnv);
//            System.out.println(tracePackageNames.length);
            if (Arrays.stream(tracePackageNames).filter(packageName -> className.startsWith(packageName))
                    .findAny().isEmpty()) {
                return classfileBuffer;
            }
            ClassPool cp = null;
            try {
                 cp = ClassPool.getDefault();
            } catch (Throwable e) {
                e.printStackTrace(System.out);
            }

            CtClass ct = cp.makeClass(new ByteArrayInputStream(classfileBuffer));
            if(ct.isInterface() || ct.getModifiers() == Modifier.ABSTRACT) {
                // TODO
                return classfileBuffer;
            } else {
                CtConstructor[] constructors = ct.getDeclaredConstructors();
                CtMethod[] ctMethods = ct.getMethods();
                for(int i = 0; i < ctMethods.length; i++) {
                    CtMethod temp = ctMethods[i];
                    try {
                        if (!Modifier.isAbstract(temp.getModifiers())
                                && !Modifier.isFinal(temp.getModifiers())
                                && !Modifier.isNative(temp.getModifiers())
                                && !Modifier.isStatic(temp.getModifiers())) {
                            temp.insertBefore(preConstructFormat.formatted(temp.getName()));
                        }
                    } catch (Exception e) {
                        System.out.println(temp);
                        System.out.println(temp.getName());
                        throw new RuntimeException(e);
                    }
                }
                ct.writeFile();
                return ct.toBytecode();
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return classfileBuffer;
    }

    private static String preConstructFormat = """
            {
                System.out.println("invoke %s");
            }
            """;


    public static class A extends Object {

    }


}
