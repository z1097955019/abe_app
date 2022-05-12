package com.example.abe_demo.abe_tools;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AccessTree {
    // 访问树的全部子节点，根节点
    Node[] nodes;
    Node mainNode;
    // pairing对象
    Pairing bp;
    // 存储此棵访问树的权限节点
    List<Node> levelControlNodes = new LinkedList<>();
    // 存储访问树的等级数目
    int levelSum = 0;

    // 构造函数赋初值
    public AccessTree(Node[] nodes, Pairing bp) {
        this.nodes = nodes;
        this.bp = bp;
        this.mainNode = nodes[0];
        for (Node node : nodes) {
            if (node.level >= 0) {
                levelControlNodes.add(node);
                this.levelSum++;
            }
        }
    }

    // 根据次数和常数项生成随机多项式的函数
    public static Element[] randomP(int Rank, Element ConstantTerm, Pairing bp) {
        Element[] conf = new Element[Rank];
        conf[0] = ConstantTerm;
        for (int index = 1; index < Rank; index++) {
            conf[index] = bp.getZr().newRandomElement().getImmutable();
        }
        return conf;
    }

    // 计算多项式在某x坐标处值的函数
    public static Element countNum(Element id, Element[] conf, Pairing bp) {
        System.out.println(id + Arrays.toString(conf));
        Element res = conf[0].getImmutable();
        for (int index = 1; index < conf.length; index++) {
            Element exp = bp.getZr().newElement(index).getImmutable();
            res = res.add(conf[index].mul(id.duplicate().powZn(exp)));
        }
        return res;
    }

    // 计算拉格朗日因子的函数
    public static Element lagrangeIndex(int i, int[] s, int x, Pairing bp) {
        Element res = bp.getZr().newOneElement().getImmutable();
        Element iElement = bp.getZr().newElement(i).getImmutable();
        Element xElement = bp.getZr().newElement(x).getImmutable();
        for (int j : s) {
            if (i != j) {
                Element numerator = xElement.sub(bp.getZr().newElement(j));
                Element denominator = iElement.sub(bp.getZr().newElement(j));
                res = res.mul(numerator.div(denominator));
            }
        }
        return res;
    }

    // 实现秘密
    public static boolean nodeRecover(Node[] nodes, Node n, String[] attrs, Pairing bp) {
        if (!n.isLeaf()) {
            List<Integer> validChildrenList = new ArrayList<Integer>();
            int[] validChildren;

            for (int index = 0; index < n.children.length; index++) {
                Node childNode = nodes[n.children[index]];
                if (nodeRecover(nodes, childNode, attrs, bp)) {
                    validChildrenList.add(n.children[index]);
                    // 如果节点个数满足门限要求，
                    if (validChildrenList.size() == n.gate[0]) {
                        System.out.println("节点个数满足门限要求");
                        // 从列表里取出节点序号
                        validChildren = validChildrenList.stream().mapToInt(i -> i).toArray();
                        // 利用拉格朗日差值恢复秘密
                        // 注意，此处是在指数因子上做拉格朗日差值
                        Element secret = bp.getGT().newOneElement().getImmutable();
                        for (int i : validChildren) {
                            Element delta = lagrangeIndex(i, validChildren, 0, bp);  //计算拉个朗日插值因子
                            secret = secret.mul(nodes[i].secretShare.duplicate().powZn(delta)); //基于拉格朗日因子进行指数运算，然后连乘
                        }
                        n.secretShare = secret;
                        n.valid = true;
                        break;
                    }
                }
            }
        } else {
            if (Arrays.asList(attrs).contains(n.attribute)) {
                n.valid = true;
            }
        }
        return n.valid;
    }

    // sha1编码
    public static byte[] sha1(String content) throws NoSuchAlgorithmException {
        MessageDigest instance = MessageDigest.getInstance("SHA-1");
        instance.update(content.getBytes());
        return instance.digest();
    }

    public boolean setDoorNull() {

        for (Node node : this.nodes) {
            if (!node.isLeaf()) {
                node.secretShare = null;
            }
        }
        return true;
    }

    // 实现秘密分享的函数
    public void nodeShare(Node[] nodes, Node n, Pairing bp) {
        // 非门限节点不需要秘密分享
        if (!n.isLeaf()) {
            // 生成随机多项式
            Element[] config = randomP(n.gate[0], n.secretShare, bp);
            // 遍历子节点，并根据随机产生的多项式计算每个节点处的值
            for (int index = 0; index < n.children.length; index++) {
                // 这个应该是浅拷贝
                Node childNode = nodes[n.children[index]];
                // 根据根节点的值，计算子节点的值
                childNode.secretShare = countNum(bp.getZr().newElement(n.children[index]), config, bp);
                // 如果子节点仍有子节点，递归遍历继续计算填充访问树的数据
                this.nodeShare(nodes, childNode, bp);
            }
        }
//        this.setDoorNull();
    }

    public void nodeShare(Pairing bp) {
        this.nodeShare(this.nodes, this.mainNode, bp);
    }

    public void nodeShare() {
        this.nodeShare(this.nodes, this.mainNode, this.bp);
    }

    private List<Node> addChildNode(Node node){
        List<Node> childNodes = new ArrayList<>();
        childNodes.add(node);
        for (int i : node.children){
            if (this.nodes[i].isLeaf()){
                childNodes.add(this.nodes[i]);
            }
            else {
                List<Node> smallerNodes = addChildNode(this.nodes[i]);
                childNodes.addAll(smallerNodes);
            }
        }
        return childNodes;
    }

    public Map<Integer, AccessTree> cutAccessTree(){

        Map<Integer, AccessTree> levelAccessTrees=new HashMap<>();
        for(Node node :levelControlNodes){
            //            childNodes.add(node);
            List<Node> childNodes = new LinkedList<>(addChildNode(node));
            levelAccessTrees.put(node.level,new AccessTree(childNodes.toArray(new Node[0]),this.bp));

        }
        return levelAccessTrees;
    }

}
