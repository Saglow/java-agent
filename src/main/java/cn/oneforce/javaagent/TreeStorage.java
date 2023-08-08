package cn.oneforce.javaagent;

/**
 * Description:
 * Author: HW
 * Date: 2023/8/6
 */
public class TreeStorage {
    private static ThreadLocal<MyMethodTree<String,Long,Long>> tl = new ThreadLocal<>();

    public static void setTree(MyMethodTree<String,Long,Long> tree) {
        tl.set(tree);
    }

    public static MyMethodTree<String,Long,Long> getTree() {
        return tl.get()==null?new MyMethodTree<>():tl.get();
    }
    public static void remove() {
        tl.remove();
    }
}
