package cn.oneforce.javaagent;

import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * Created by caoyanfei on 16/10/27.
 */
public class MyAgent {
    public static void premain(String agentOps, Instrumentation inst){
        System.out.println("Run the trace agent");
        ClassPool pool = ClassPool.getDefault();

//        try {
//            // 获取其他类的CtClass对象
//            CtClass myClass = pool.get("com.example.OtherClass");
//
//            // 创建一个新的CtClass对象
//            CtClass ctClass = pool.makeClass("com.example.MyModifiedClass");
//
//            // 导入其他类的包
//            ctClass.importPackage("com.example");
//
//            // 添加一个字段，类型为OtherClass
//            CtField field = new CtField(myClass, "otherInstance", ctClass);
//            ctClass.addField(field);
//
//            // 添加一个方法，使用OtherClass类型
//            CtMethod method = CtNewMethod.make("public void doSomething() { "
//                    + "otherInstance.doSomethingElse(); }", ctClass);
//            ctClass.addMethod(method);
//
//            // 将修改后的类加载到 JVM 中
//            Class<?> modifiedClass = ctClass.toClass();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        inst.addTransformer(new TraceConstructorParamsTransformer5());

    }

//    public static void main(String[] args) {
//    }
//
//    public class A {
//        private String a;
//
//        public A(String a) {
//            this.a = a;
//        }
//
//        public void test() {
//
//        }
//    }
//    public class B extends A {
//        private String b;
//        public B(String a) {
////            b = a + a;
////            System.out.println(b);
//            super(a);
//        }
//
//        @Override
//        public void test() {
//            var a = b;
//            super.test();
//            b = a;
//        }
//    }
}
