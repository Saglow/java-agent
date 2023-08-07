package cn.oneforce.javaagent;

/**
 * @author caoyanfei
 * @Classname SimpleTreeFormatter
 * @Date 2022/7/1 10:02
 */
public class SimpleTreeFormatter<T> implements TreeFormatter<T> {
    private String startPrefix;
    private String endPrefix;
    private String treeLevelKey = "\t";
    private Boolean isDisplayParam;
    private Boolean isDisplayLineNumber;
    private Boolean isDisplayPackageName;
    private Boolean isShortPackageName;

    @Override
    public String format(StackTree<T> tree) {
        return null;
    }
}
