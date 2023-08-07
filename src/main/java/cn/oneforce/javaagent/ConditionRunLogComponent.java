package cn.oneforce.javaagent;

import java.lang.reflect.Constructor;

/**
 * @author caoyanfei
 * @Classname RunLogComponent
 * @Date 2022/6/27 17:34
 */
public interface ConditionRunLogComponent extends RunLogComponent {
    Boolean doLogTrace(Class clazz, Constructor constructor);
}
