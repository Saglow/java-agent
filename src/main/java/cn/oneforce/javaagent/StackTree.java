package cn.oneforce.javaagent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author caoyanfei
 * @Classname StackTree
 * @Date 2022/6/28 10:02
 */

/**
 * stack tree 的设计主要是使用stack的pop和push
 * push: 将一个node, 加入到tree中. 如果有节点是打开的, 那么久把node加入对应的node下的child
 * pop: 关闭一个已开通的tree
 * @param <T>
 */
public class StackTree<T> {
    private T content;
    private Integer depth;
    private StackTree<T> parent;
    private List<StackTree<T>> children;
    private Boolean isOpen;
    public T getNode() {
        return  content;
    }
    public Boolean isRoot() {
        return parent == null;
    }
    public Integer getDepth() {
        return depth;
    }
    public StackTree(T content) {
        this.content = content;
        this.children = new ArrayList<>();
        this.isOpen = true;
        this.parent =  null;
        this.depth = 0;
    }
    public StackTree(StackTree<T> parent, T content) {
        this.parent = parent;
        this.content = content;
        this.children = new ArrayList<>();
        this.isOpen = true;
        this.parent =  parent;
        this.depth = parent.depth+1;
    }

    public StackTree<T> push(T t) {
        StackTree<T> result = _pushChild(this,t, true);
        if(result ==  null){
            throw new IllegalArgumentException("当前对象已经关闭了");
        }
        return result;
    }

    public StackTree<T> pushAndPop(T t) {
        StackTree<T> result = _pushChild(this,t, false);
        if(result ==  null){
            throw new IllegalArgumentException("当前对象已经关闭了");
        }
        return result;
    }

    public Optional<StackTree<T>> pop(){
        return Optional.ofNullable(_pop(this));
    }
    private StackTree<T> _pop(StackTree<T> currentNode) {
        if(currentNode.isOpen) {
            // todo
            Optional<StackTree<T>> result = currentNode.children.stream()
                    .map(node -> _pop(node))
                    .filter(Objects::nonNull)
                    .findFirst();
            if(result.isPresent()) {
                return result.get();
            } else {
                currentNode.isOpen = false;
                return currentNode;
            }
        } else {
            return null;
        }
    }
    private StackTree<T> _pushChild(StackTree<T> currentNode, T t, Boolean isOpen) {
         if(currentNode.isOpen) {
             Optional<StackTree<T>> newChild = currentNode.children.stream()
                     .map(node -> _pushChild(node, t, isOpen))
                     .filter(Objects::nonNull)
                     .findFirst();
             if(newChild.isEmpty()) {
                 StackTree<T> child = new StackTree<>(currentNode,  t);
                 child.isOpen = isOpen;
                 currentNode.children.add(child);
                 return child;

             } else {
                 return newChild.get();
             }
         } else {
             return null;
         }
    }

    @Override
    public String toString() {

        return "StackTree{" +
                "content=" + content +
                ", depth=" + depth +
                ", children=" + children +
                ", isOpen=" + isOpen +
                '}';

    }
}
