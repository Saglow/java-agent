package cn.oneforce.javaagent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.util.HotSwapper;

import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author caoyanfei
 * @Classname TestJavassist
 * @Date 2022/6/21 13:30
 */
public class TestJavassist {
    public static void main(String[] args) throws Exception {

//        ClassPool cp = ClassPool.getDefault();
//        CtClass ctClass = cp.get("cn.oneforce.javaagent.TestJavassist$TestClass");
//
//        for (CtConstructor temp : ctClass.getDeclaredConstructors()) {
//            System.out.println(temp);
//            temp.insertBefore("{System.out.println(\"hello world\");}");
//        }
//
//        ctClass.writeFile();
//        new TestClass("test","18");

        Logger logger = Logger.getLogger(TestJavassist.class.getName());
//        logger.addHandler(new ConsoleHandler());
        logger.log(Level.INFO,"hello wolrd");
        logger.log(Level.INFO,"hello wolrd");
        logger.log(Level.INFO,"hello wolrd");
        logger.log(Level.INFO,"hello wolrd");
        logger.log(Level.INFO,"hello wolrd");
        logger.log(Level.INFO,"hello wolrd");
    }

    public void printConstructBefore(Objects[] $args) {
        {
            Objects[] temp = $args;
            for(Object o : temp) {
                System.out.println(o);
            }
        }
    }

    static class TestClass {
        public TestClass(String name, String age) {
            System.out.println(name);
        }
    }



}
