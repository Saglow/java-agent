package cn.oneforce.javaagent;

import javassist.*;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;

/**
 * Created by caoyanfei on 16/10/27.
 */
public class TraceConstructorParamsTransformer4 implements ClassFileTransformer{

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
                            temp.insertAfter(afterConstructFormat);
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
               int depth = cn.oneforce.javaagent.DepthStorage.getDepth();
                for(int i = 0 ; i < depth ; i++){
                    System.out.print("+ ");
               }
               System.out.println("%s");
               cn.oneforce.javaagent.DepthStorage.setDepth(depth+1);
            }
            """;
    private static String afterConstructFormat = """
            {
                int depth = cn.oneforce.javaagent.DepthStorage.getDepth();
                cn.oneforce.javaagent.DepthStorage.setDepth(depth-1);
            }
            """;



    public static class A extends Object {

    }


}
