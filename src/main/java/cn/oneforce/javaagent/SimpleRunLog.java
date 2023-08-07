package cn.oneforce.javaagent;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @author caoyanfei
 * @Classname SimpleRunLog
 * @Date 2022/6/27 17:33
 */
public class SimpleRunLog implements RunLogComponent{
//    @Override
//    public StackTree<TraceInfo> startConstruct(Class clazz, String constructName, Object[] args) {
//        return null;
//    }
//
//    @Override
//    public void endConstruct(StackTree<TraceInfo> traceInfoStackTree) {
//
//    }

    private SimpleRunLog() {}
    private static SimpleRunLog instance = new SimpleRunLog();
    public static SimpleRunLog getInstance() {
        return instance;
    }

    private Stack<String> classStack = new Stack<>();

//    @Override
    public void startConstruct(Class clazz, Constructor constructor) {
        Integer depth = classStack.size();

        classStack.push(clazz.getCanonicalName());
    }

//    @Override
    public void endConstruct(Class clazz, Constructor constructor) {
        classStack.pop();
    }



    public class SimpleTreeFormatter implements TreeFormatter<TraceInfo> {
        private String startPrefix;
        private String endPrefix;
        private String treeLevelKey = "\t";
        private Boolean isDisplayParam;
        private Boolean isDisplayLineNumber;
        private Boolean isDisplayPackageName;
        private Boolean isShortPackageName;

        @Override
        public String format(StackTree<TraceInfo> tree) {
            String result = "";
            if(!tree.isRoot()) {
                result += startPrefix;
            }

            String treeLevelPrefix = Collections.nCopies(tree.getDepth(), this.treeLevelKey).stream()
                    .collect(Collectors.joining());
            result += treeLevelPrefix;
            TraceInfo traceInfo = tree.getNode();
            if(isDisplayPackageName) {
                Package p = traceInfo.getClazz().getPackage();
                if(isShortPackageName) {
                    result += Arrays.stream(p.getName().split("\\.")).map(s -> s.substring(0,1)).collect(Collectors.joining("."));
                } else {
                    result += p.getName();
                }
            }
            result += "." + traceInfo.getMethodName();
            if(isDisplayLineNumber) {
                result +="(" + traceInfo.getStartLineNumber() + ")";
            }


            // todo

            return null;
        }
    }
}
