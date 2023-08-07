package cn.oneforce.javaagent;

/**
 * @author caoyanfei
 * @Classname StackTreeFormat
 * @Date 2022/7/1 09:59
 */
public interface TreeFormatter<T> {
    String format(StackTree<T> tree);
}
