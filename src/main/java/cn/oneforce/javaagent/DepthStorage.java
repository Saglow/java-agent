package cn.oneforce.javaagent;

/**
 * Description:
 * Author: HW
 * Date: 2023/8/6
 */
public class DepthStorage {
    private static ThreadLocal<Integer> tl = new ThreadLocal<>();

    public static void setDepth(int depth) {
        tl.set(depth);
    }

    public static int getDepth() {
        return tl.get()==null?0:tl.get();
    }
    public static void remove() {
        tl.remove();
    }
}
