package cn.oneforce.javaagent;

import javassist.*;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;

/**
 * Created by caoyanfei on 16/10/27.
 */
public class TraceConstructorParamsTransformer5 implements ClassFileTransformer{

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
                            temp.insertBefore(preConstructFormat.formatted(temp.getName(),temp.getName()));
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
                System.out.println("1");
                MyMethodTree<String,Long,Long> currentNode = cn.oneforce.javaagent.TreeStorage.getTree();
                if (currentNode.isRoot()) {
                    currentNode.setValue("%s");
                    currentNode.setStartTime(System.currentTimeMillis());
                    cn.oneforce.javaagent.TreeStorage.setTree(currentNode);
                } else {
                    MyMethodTree<String,Long,Long> newNode = new MyMethodTree<>("%s",System.currentTimeMillis());
                    currentNode.addChild(newNode);
                    cn.oneforce.javaagent.TreeStorage.setTree(newNode);
                }
            }
            """;
    private static String afterConstructFormat = """
            {
                 MyMethodTree<String,Long,Long> currentNode = cn.oneforce.javaagent.TreeStorage.getTree();
                 currentNode.setEndTime(System.currentTimeMillis());
                 if (currentNode.isRoot()) {
                     List<MyMethodTree<String,Long,Long>> list = new ArrayList<>();
                     list.add(currentNode);
                     MyMethodTree.print(list);
                 } else {
                     cn.oneforce.javaagent.TreeStorage.setTree(currentNode.getParent());
                 }
            }
            """;



    public static class A extends Object {

    }


}
