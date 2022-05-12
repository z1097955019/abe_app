package com.example.abe_demo.abe_tools;

import it.unisa.dia.gas.jpbc.Element;

import java.util.Arrays;


public class Node {
    // 顺序编号
    public int index = 0;
    // 门限及其子节点列表
    public int[] gate;
    public int[] children;

    // 属性值与秘密分片的秘密值
    public String attribute;
    public Element secretShare;

    // 存储节点等级（等级节点专有属性）
    public int level=-1;

    // 存储该节点是否能恢复的信息
    public boolean valid;

    // 门限的构造方法
    public Node(int index, int[] gate, int[] children) {
        this.index =index;
        this.gate = gate;
        this.children = children;
    }

    public Node(int index, int[] gate, int[] children, int level) {
        this.index =index;
        this.gate = gate;
        this.children = children;
        this.level = level;
    }

    // 叶子节点的构造方法
    public Node(int index, String attribute) {
        this.index = index;
        this.attribute = attribute;
    }

    // 标记是否为叶子节点
    public boolean isLeaf() {
        return this.children == null;
    }

    //重载string方法分别打印门限和叶子节点
    @Override
    public String toString() {
        if (this.isLeaf()) {
            return this.attribute;
        }
        else {
            return Arrays.toString(this.gate);
        }
    }
}
