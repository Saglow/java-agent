package cn.oneforce.javaagent;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.MethodParametersAttribute;

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
import java.util.stream.Stream;

/**
 * Created by caoyanfei on 16/10/27.
 */
public class TraceConstructorParamsTransformer implements ClassFileTransformer{
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
            "   %s" +
            "}";
    private static String CONSTRUCTOR_BEFORE_BODY_INNER = "   for(int _i = 0; _i < cn.oneforce.javaagent.TraceTransformer.classMethods.size() + 2; _i++) {" +
            "       cn.oneforce.javaagent.TraceFileWriter.write(\"    \");" +
            "   }" +
            "   cn.oneforce.javaagent.TraceFileWriter.write(%s); " +
            "   cn.oneforce.javaagent.TraceFileWriter.write(\"\\n\"); ";

    private static String CONSTRUCTOR_AFTER_BODY = "" +
            "{" +
            "   " +
            "   String _constructor_after = \"%s()   %s\";" +
            "   for(int _i = 0; _i < cn.oneforce.javaagent.TraceTransformer.classMethods.size(); _i++) {" +
            "       cn.oneforce.javaagent.TraceFileWriter.write(\"    \");" +
            "   } " +
            "   cn.oneforce.javaagent.TraceFileWriter.write(\" |___ CONSTRUCTOR : \");" +
            "   cn.oneforce.javaagent.TraceFileWriter.write( _constructor_after +\"  <---  end\\n\");" +
            "   cn.oneforce.javaagent.TraceTransformer.classMethods.poll(); " +
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
//            System.out.println(tracePackageNameEnv);
//            System.out.println("========================");
//            System.out.println(tracePackageNameEnv);
//            System.out.println("========================1");
//            System.out.println("========================");
//            System.out.println("========================");
//            System.out.println("========================");
//            System.out.println("========================");
//            System.out.println("========================");
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
//            System.out.println(className);
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

                if(constructors != null) {
                    for (CtConstructor constructor : constructors) {
                        System.out.println("处理 constructor: " + constructor);
                        int constructorStartLineNumber = constructor.getMethodInfo2().getLineNumber(0);
                        int constructorEndLineNumber = constructor.getMethodInfo2().getLineNumber(Integer.MAX_VALUE);
                        if(constructorStartLineNumber + 1>= constructorEndLineNumber) {
                            constructor.insertAfter(String.format(ONE_LINE_CONSTRUCTOR, ct.getName(), constructorStartLineNumber, constructorEndLineNumber), false);
                        } else{
//                            constructor.setModifiers(Modifier.PUBLIC);
                            String innerParam = buildParamString(getMethodVariableName2(constructor));
                            String inner = (innerParam == null || innerParam.trim().length() == 0 ) ? "" : String.format(CONSTRUCTOR_BEFORE_BODY_INNER, innerParam);
//                            System.out.println("the inner is " + inner);
                            String temp = String.format(CONSTRUCTOR_BEFORE_BODY, ct.getName(), constructorStartLineNumber, inner);
//                            System.out.println(temp);
                            constructor.insertBeforeBody(temp);
                            constructor.insertAfter(String.format(CONSTRUCTOR_AFTER_BODY, ct.getName(), constructorEndLineNumber), false);
                        }
//                        }
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

    public List<String> getMethodVariableName2(CtConstructor ctMethod) {
        try {
//            System.out.println("the method param size is " + ctMethod.getParameterTypes().length);
            return IntStream.range(1, ctMethod.getParameterTypes().length + 1)
                    .mapToObj( i -> "$" + i)
                    .toList();
        } catch (Exception e) {
//            System.out.println("invoke exception: " + e.getMessage());
            e.printStackTrace(System.out);
            return new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        System.out.println(IntStream.range(1, 2)
                .mapToObj( i -> "$" + i)
                .collect(Collectors.joining(","))
        );
    }
    public List<String> getMethodVariableName(CtConstructor ctMethod) {
        String s = "param[ctMethod]: " + ctMethod;
        MethodInfo methodInfo = ctMethod.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
//        codeAttribute.getAttributes().forEach(a -> {
//            if(a != null) {
//                System.out.printf("a: class %s, size %s, a: %s\n", a.getClass(), a.length(), a);
//            }
//        });
        List<String> result = new ArrayList<>();
        if(attr != null) {
            try {
//                int pos = Modifier.isStatic(ctMethod.getModifiers())  ? 0 : (Modifier.isPrivate(ctMethod.getDeclaringClass().getModifiers()) ? 0:1);
//
//                int startIndex =  0;
//                if( Modifier.isPrivate(ctMethod.getDeclaringClass().getModifiers()) ) {
//                    startIndex = 0;
//                } else {
//                    startIndex = 1;
//                }

                System.out.println(ctMethod.getLongName());
//                System.out.println(" startIndex =" + startIndex + ", pos = " + pos);
                for (int i = 0; i < ctMethod.getParameterTypes().length; i++) {
//                    System.out.println("ctMethod.getParameterTypes().length" + ctMethod.getParameterTypes().length);
//                    System.out.println("attr.get()" + attr.get());
//                    System.out.println("new String(attr.get())" + new String(attr.get()));
//                    System.out.println("attr.getName()" + attr.getName());
//                    try {
//                        for(int j = 0; j< ctMethod.getParameterTypes().length; j++) {
//                            System.out.println(attr.variableNameByIndex(j) + "->" + attr.variableName(j));
//                        }
//                    }catch (Throwable e1) {
//
//                    }
//                    System.out.println(" i =" + i + ", pos = " + pos);
                    // TODO
                    String variableName = attr.variableName(i);
                    result.add(variableName);
                }
            } catch (NotFoundException e) {
                System.out.println(" get param failed");
                e.printStackTrace(System.out);
            }
        }
        return result;
    }
    public static String buildParamString(List<String> params) {
        if(params.isEmpty()) {
            return "";
        }
        String result =  params.stream()
                .map( s -> """
                        "param[%s]: " + %s """.formatted(s,s))
                .collect(Collectors.joining(" + \",\" + "));
//        return params.stream().collect(Collectors.joining("+"));
        return result;

    }

//    public static void main(String[] args) {
//        String s = String.format(CONSTRUCTOR_BEFORE_BODY, "ct.getName()", 1, buildParamString(List.of()));
//        System.out.print(s);
//    }

    public void print(String a, String name, String age) {
//        {   String _constructor_before = "   ()   ";    cn.oneforce.javaagent.TraceTransformer.classMethods.add(_constructor_before);    for(int _i = 0; _i < cn.oneforce.javaagent.TraceTransformer.classMethods.size(); _i++) {       cn.oneforce.javaagent.TraceFileWriter.write("    ");   }   cn.oneforce.javaagent.TraceFileWriter.write(" |--- CONSTRUCTOR : ");    cn.oneforce.javaagent.TraceFileWriter.write( _constructor_before +"  --->  start\n");   for(int _i = 0; _i < cn.oneforce.javaagent.TraceTransformer.classMethods.size(); _i++) {       cn.oneforce.javaagent.TraceFileWriter.write("    ");   }   cn.oneforce.javaagent.TraceFileWriter.write("param[a]: " + a + "," + "param[name]: " + name + "," + "param[age]: " + age); }
    }

    public class A {
        public A(String size) {
//            {   String _constructor_before = "ct.getName()()   1";    cn.oneforce.javaagent.TraceTransformer.classMethods.add(_constructor_before);    for(int _i = 0; _i < cn.oneforce.javaagent.TraceTransformer.classMethods.size(); _i++) {       cn.oneforce.javaagent.TraceFileWriter.write("    ");   }   cn.oneforce.javaagent.TraceFileWriter.write(" |--- CONSTRUCTOR : ");    cn.oneforce.javaagent.TraceFileWriter.write( _constructor_before +"  --->  start\n");   for(int _i = 0; _i < cn.oneforce.javaagent.TraceTransformer.classMethods.size() + 2; _i++) {       cn.oneforce.javaagent.TraceFileWriter.write("    ");   }   cn.oneforce.javaagent.TraceFileWriter.write("param[size]: " + size);    cn.oneforce.javaagent.TraceFileWriter.write("\n"); }
        }
    }
}
