package cn.oneforce.javaagent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by caoyanfei on 16/10/27.
 */
public class TraceTransformerForSuper implements ClassFileTransformer{
    private static StringBuilder space = new StringBuilder(" |___");
    public static Queue<String> classMethods = new ArrayBlockingQueue<String>(10000);
    private static String METHOD_BEFORE_BODY = "" +
            "{" +
            "   String _method_before = \"%s.%s(%s)\"; " +
            "   cn.oneforce.javaagent.TraceTransformer.classMethods.add(_method_before); " +
            "   for(int _i = 0; _i < cn.oneforce.javaagent.TraceTransformer.classMethods.size(); _i++) {" +
            "       cn.oneforce.javaagent.TraceFileWriter.write(\"    \");" +
            "   }" +
            "   cn.oneforce.javaagent.TraceFileWriter.write(\" |---\"); " +
            "   cn.oneforce.javaagent.TraceFileWriter.write( _method_before +\" ---  start\\n\");" +
            "}";

//    {
//        this.client.setCheckSign(false);
//    }
    private static String METHOD_AFTER_BODY = "" +
            "{" +
            "   " +
            "   String _method_after = \"%s.%s(%s)\";" +
            "   for(int _i = 0; _i < cn.oneforce.javaagent.TraceTransformer.classMethods.size(); _i++) {" +
            "       cn.oneforce.javaagent.TraceFileWriter.write(\"    \");" +
            "   } " +
            "   cn.oneforce.javaagent.TraceFileWriter.write(\" |___\");" +
            "   cn.oneforce.javaagent.TraceTransformer.classMethods.poll(); " +
            "   cn.oneforce.javaagent.TraceFileWriter.write( _method_after +\"  ---  end\\n\");" +
            "}";
    private static String CONSTRUCTOR_BEFORE_BODY = "" +
            "{" +
            "   String _constructor_before = \"%s()   %s\"; " +
            "   cn.oneforce.javaagent.TraceTransformer.classMethods.add(_constructor_before); " +
            "   for(int _i = 0; _i < cn.oneforce.javaagent.TraceTransformer.classMethods.size(); _i++) {" +
            "       cn.oneforce.javaagent.TraceFileWriter.write(\"    \");" +
            "   }" +
            "   cn.oneforce.javaagent.TraceFileWriter.write(\" |--- CONSTRUCTOR : \"); " +
            "   cn.oneforce.javaagent.TraceFileWriter.write( _constructor_before +\"  --->  start\\n\");" +
            "}";

    private static String CONSTRUCTOR_AFTER_BODY = "" +
            "{" +
            "   " +
            "   String _constructor_after = \"%s()   %s\";" +
            "   for(int _i = 0; _i < cn.oneforce.javaagent.TraceTransformer.classMethods.size(); _i++) {" +
            "       cn.oneforce.javaagent.TraceFileWriter.write(\"    \");" +
            "   } " +
            "   cn.oneforce.javaagent.TraceFileWriter.write(\" |___ CONSTRUCTOR : \");" +
            "   cn.oneforce.javaagent.TraceTransformer.classMethods.poll(); " +
            "   cn.oneforce.javaagent.TraceFileWriter.write( _constructor_after +\"  <---  end\\n\");" +
            "}";
    private static String ONE_LINE_CONSTRUCTOR = "" +
            "{" +
            "   String _oneline_constructor = \"%s()   (%s->%s)\";" +
            "   for(int _i = 0; _i < cn.oneforce.javaagent.TraceTransformer.classMethods.size() + 1; _i++) {" +
            "       cn.oneforce.javaagent.TraceFileWriter.write(\"    \");" +
            "   } " +
            "   cn.oneforce.javaagent.TraceFileWriter.write(\" |___ CONSTRUCTOR : \");" +
            "   cn.oneforce.javaagent.TraceFileWriter.write( _oneline_constructor +\"\\n\");" +
            "}";


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
            for(String tracePackageName : tracePackageNames) {
                if (!className.startsWith(tracePackageName)) {
                    return classfileBuffer;
                }
            }
            System.out.println(11111);
            ClassPool cp = null;
            try {
                 cp = ClassPool.getDefault();
            } catch (Throwable e) {
                e.printStackTrace(System.out);
            }

            System.out.println(2);
            CtClass ct = cp.makeClass(new ByteArrayInputStream(classfileBuffer));
            System.out.println("===========1" + ct.isInterface());
            if(ct.isInterface()) {
                // TODO
                return classfileBuffer;
            } else {
                CtMethod[] methods = ct.getDeclaredMethods();
                CtConstructor[] constructors = ct.getDeclaredConstructors();

                if(constructors != null) {
                    for (CtConstructor constructor : constructors) {
                        final CodeAttribute code = constructor.getMethodInfo().getCodeAttribute();;
                        code.iterator().skipSuperConstructor();
//                        constructor.insertBeforeBody();
//                        if (constructor == null || constructor.isEmpty()) {
//                            return classfileBuffer;
//                        } else {
                        int constructorStartLineNumber = constructor.getMethodInfo2().getLineNumber(0);
//                        System.out.println(ct.getName() + "--->start" + constructorStartLineNumber);
                        int constructorEndLineNumber = constructor.getMethodInfo2().getLineNumber(Integer.MAX_VALUE);
//                        System.out.println(ct.getName() + "---> end" + constructorEndLineNumber);
                        if(constructorStartLineNumber + 1>= constructorEndLineNumber) {
                            constructor.insertAfter(String.format(ONE_LINE_CONSTRUCTOR, ct.getName(), constructorStartLineNumber, constructorEndLineNumber), false);
//                            System.out.print(String.format(ONE_LINE_CONSTRUCTOR, ct.getName(), constructorStartLineNumber, constructorEndLineNumber));
                        } else{
                            constructor.insertBefore(String.format(CONSTRUCTOR_BEFORE_BODY, ct.getName(), constructorStartLineNumber));
                            constructor.insertAfter(String.format(CONSTRUCTOR_AFTER_BODY, ct.getName(), constructorEndLineNumber), true);
                        }
//                        }
                    }
                }


                for (CtMethod method : methods) {
                    if(method.isEmpty()) {
                        // TODO
                        return classfileBuffer;
                    } else {
                        int methodStart = method.getMethodInfo2().getLineNumber(0);
                        int methodEnd = method.getMethodInfo2().getLineNumber(Integer.MAX_VALUE);
                        method.insertBefore(String.format(METHOD_BEFORE_BODY, ct.getName(), method.getName(), methodStart));
                        method.insertAfter(String.format(METHOD_AFTER_BODY,ct.getName(),method.getName(),methodEnd), true);
                    }
                }
                System.out.println("===========");
                ct.writeFile();
                return ct.toBytecode();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return classfileBuffer;
    }
}
