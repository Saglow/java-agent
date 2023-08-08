package cn.oneforce.javaagent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class MyMethodTree<T,Y,U>{

    private T value;
    private Y startTime;
    private U endTime;
    private List<MyMethodTree<T,Y,U>> children;
    private MyMethodTree<T,Y,U> parent;
    public MyMethodTree(T value, Y startTime){
        this.setValue(value);
        this.setStartTime(startTime);
    }

    public Boolean isParent() {
        if(this.children==null){
            return false;
        }
        return this.children.size() != 0;
    }
    public Boolean isRoot() {
        return this.parent == null;
    }
    public Boolean contains(MyMethodTree<T,Y,U> tree){
        if (this.equals(tree)) {
            return true;
        }
        for (MyMethodTree<T,Y,U> tree1 : this.children) {
            if(tree1.contains(tree)) {
                return true;
            }
        }
        return false;
    }
    public Integer getDepth() {
        if (this.isRoot()) {
            return 0;
        } else {
            return this.parent.getDepth() + 1;
        }
    }

    public void addChild(MyMethodTree<T,Y,U> tree) {
        this.children.add(tree);
        tree.setParent(this);
    }

    public static void print(List<MyMethodTree<String,Long,Long>> list){
        for(MyMethodTree<String,Long,Long> tree : list){
            for(int i = 0 ; i < tree.getDepth() ; i++){
                System.out.print("+ ");
            }
            long time = tree.endTime- tree.startTime;
            System.out.println(tree.value + " " + time + " ms ");
            if(tree.isParent()){
                print(tree.children);
            }
        }
    }



    public static void main(String[] args) {

    }

}
