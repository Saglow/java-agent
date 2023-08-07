package cn.oneforce.javaagent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by caoyanfei on 16/10/27.
 */
public class TraceTransformer implements ClassFileTransformer{
    private static Stack<String> beans = new Stack<>();
    public static Queue<String> classMethods = new ArrayBlockingQueue<>(10000);

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
//        Stack a;
//        Vector v;
//        Queue q;
//        q.po

        return classfileBuffer;
    }
}
