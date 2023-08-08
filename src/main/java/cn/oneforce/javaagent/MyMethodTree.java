package cn.oneforce.javaagent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: HW
 * Date: 2023/7/31
 * @author HW
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyMethodTree{

    private String value;
    private long startTime;
    private long endTime;
    private List<MyMethodTree> children;
    private MyMethodTree parent;
    private int depth;
    public MyMethodTree(String value, long startTime){
        this.setValue(value);
        this.setStartTime(startTime);
        this.children = new ArrayList<>();
        this.depth = 0;
    }

    public boolean isParent() {
        if(this.children == null){
            return false;
        }
        return this.children.size() != 0;
    }
    public boolean isRoot() {
        return this.parent == null;
    }
    public boolean contains(MyMethodTree tree){
        if (this.equals(tree)) {
            return true;
        }
        for (MyMethodTree tree1 : this.children) {
            if(tree1.contains(tree)) {
                return true;
            }
        }
        return false;
    }


    public void addChild(MyMethodTree tree) {
        this.children.add(tree);
        tree.setParent(this);
        tree.setDepth(this.depth + 1);
    }
    public void setParent(MyMethodTree t){
        this.parent = t;
    }
    public void setDepth(int  i ){
        this.depth = i;
    }
    public void setValue(String s){
        this.value = s;
    }
    public void setStartTime(long l){
        this.startTime = l;
    }

    public static void print(List<MyMethodTree> list){
        for(MyMethodTree tree : list){
            for(int i = 0 ; i < tree.getDepth() ; i++){
                System.out.print("+ ");
            }
            long time = tree.endTime-tree.startTime;
            System.out.println(tree.value + " " + time + " ms ");
            if(tree.isParent()){
                print(tree.children);
            }
        }
    }



}
