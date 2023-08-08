package cn.oneforce.javaagent;

/**
 * Description:
 * Author: HW
 * Date: 2023/8/6
 */
public class TreeStorage {
    private static ThreadLocal<MyMethodTree> tl = new ThreadLocal<>();

    public static void setTree(MyMethodTree tree) {
        tl.set(tree);
    }

    public static MyMethodTree getTree() {
        return tl.get();
    }
    public static void remove() {
        tl.remove();
    }
}
