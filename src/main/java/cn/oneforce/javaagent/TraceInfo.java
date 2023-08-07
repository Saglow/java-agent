package cn.oneforce.javaagent;

import java.util.List;
import java.util.Stack;

/**
 * @author caoyanfei
 * @Classname TraceInfo
 * @Date 2022/6/23 23:38
 */
public class TraceInfo {
    private Class clazz;
    private String methodName;
    private Object[] $args;
    private Integer startLineNumber;
    private Integer endLineNumber;

    public Class getClazz() {
        return clazz;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getParams() {
        return $args;
    }

    public Integer getStartLineNumber() {
        return startLineNumber;
    }

    public Integer getEndLineNumber() {
        return endLineNumber;
    }
    //    public static enum Type {
//        ONE_LINE_CONSTRUCT,
//        START_CONSTRUCT,
//        END_CONSTRUCT
//    }

    public static void main(String[] args) {
//        Stack
        System.out.println(TraceInfo.class.getPackage());
    }
}