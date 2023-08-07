package cn.oneforce.javaagent;

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
        inst.addTransformer(new TraceConstructorParamsTransformer3());

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
