package cn.oneforce.javaagent;

/**
 * Description:
 * Author: HW
 * Date: 2023/8/6
 */
public class DepthStorage {
    private static ThreadLocal<Integer> tl = new ThreadLocal<>();

    public static void setDepth(Integer depth) {
        tl.set(depth);
    }

    public static Integer getDepth() {
        return tl.get();
    }
    public static void remove() {
        tl.remove();
    }
}
