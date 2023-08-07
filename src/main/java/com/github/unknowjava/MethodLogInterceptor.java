package com.github.unknowjava;

import java.util.Collections;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @author caoyanfei
 * @Classname MethodLogInterceptor
 * @Date 2022/6/16 12:08
 */
public class MethodLogInterceptor {
    private static Stack<String> methodStack = new Stack<>();
    public static String beforeInvoke(String methodName) {
        methodStack.push(methodName);
        return "->" + Collections.nCopies(methodStack.size(), "  ").stream().collect(Collectors.joining()) + "\n";
    }
    public static String afterInvoke(String methodName) {
        String result = "<-" + Collections.nCopies(methodStack.size(), "  ").stream().collect(Collectors.joining()) + "\n";
        methodStack.pop();
        return result;
    }
}
