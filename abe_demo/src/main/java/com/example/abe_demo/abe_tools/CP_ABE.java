package com.example.abe_demo.abe_tools;

import android.util.Log;

import com.example.abe_demo.abe_tools.utils.CodeConvert;
import com.example.abe_demo.abe_tools.utils.FileOperate;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;

public class CP_ABE {

    public static Map<Integer, Element> beforeEncrypt(Map<Integer, String> structMes, Pairing bp) {
        Map<Integer, Element> structElementMes = new HashMap<>();
        for (Map.Entry<Integer, String> entry : structMes.entrySet()) {
            int mesLevel = entry.getKey();
            String mes = entry.getValue();
            structElementMes.put(mesLevel, bp.getGT().newElement(new BigInteger(mes)));
        }
        return structElementMes;
    }

    public static boolean isValid(AccessTree accessTree, String[] userAttList, Pairing bp, Element D0, Properties ctProp1, Properties ctProp2, Properties skProp) {
        System.out.println(Arrays.toString(accessTree.nodes));
        // 核对用户属性是否满足解密需求
        for (Node node : accessTree.nodes) {
            if (node.isLeaf()) {
                // 如果叶子节点的属性值属于用户属性列表，则将属性对应的密文组件和秘钥组件配对的结果作为秘密值
                if (Arrays.asList(userAttList).contains(node.attribute) && node.index <= 5) {
                    // 从密文文件中恢复所有叶子节点的C1， C2值
                    Element C1 = FileOperate.loadElementFromProp("C1-" + node.attribute, ctProp1, bp.getG1());
                    Element C2 = FileOperate.loadElementFromProp("C2-" + node.attribute, ctProp1, bp.getG1());

                    // 从用户密钥文件中恢复用户属性对应的 Datt 值
                    System.out.println("node.attribute" + node.attribute);
                    System.out.println(" skProp" + skProp);
                    Element Datt = FileOperate.loadElementFromProp("D" + node.attribute, skProp, bp.getG1());

                    // 拿到去除随机数r的秘密碎片值
                    node.secretShare = bp.pairing(C1, D0).mul(bp.pairing(C2, Datt)).getImmutable();
                } else if (Arrays.asList(userAttList).contains(node.attribute) && node.index > 5) {
                    // 从密文文件中恢复所有叶子节点的C1， C2值
                    Element C1 = FileOperate.loadElementFromProp("C1-" + node.attribute, ctProp2, bp.getG1());
                    Element C2 = FileOperate.loadElementFromProp("C2-" + node.attribute, ctProp2, bp.getG1());

                    // 从用户密钥文件中恢复用户属性对应的 Datt 值
                    System.out.println("node.attribute" + node.attribute);
                    System.out.println(" skProp" + skProp);
                    Element Datt = FileOperate.loadElementFromProp("D" + node.attribute, skProp, bp.getG1());

                    // 拿到去除随机数r的秘密碎片值
                    node.secretShare = bp.pairing(C1, D0).mul(bp.pairing(C2, Datt)).getImmutable();
                } else {
                    node.secretShare = null;
                }
            }
        }
        System.out.println("++++++++++++++++++++" + " in " + "++++++++++++++++++++");
        return AccessTree.nodeRecover(accessTree.nodes, accessTree.mainNode, userAttList, bp);
    }


    public static Map<String, Properties> setup(String pairingParametersFileName) {
        Pairing bp = PairingFactory.getPairing(pairingParametersFileName);
        return setup(bp);
    }

    public static Map<String, Properties> setup(Pairing bp) {
        // 生成群生成元
        Element g = bp.getG1().newRandomElement().getImmutable();

        // 生成两个随机整数alpha，beta，也是私钥的一部分
        Element alpha = bp.getZr().newRandomElement().getImmutable();
        Element beta = bp.getZr().newRandomElement().getImmutable();

        // 将随机整数映射到群元素，生成公钥
        Element g_alpha = g.powZn(alpha).getImmutable();
        Element g_beta = g.powZn(beta).getImmutable();
        Element egg_alpha = bp.pairing(g, g).powZn(alpha).getImmutable();

        // 新建私钥配置文件并写入
        Properties mskProp = new Properties();
        mskProp.setProperty("g_alpha", Base64.getEncoder().withoutPadding().encodeToString(g_alpha.toBytes()));

        // 新建公钥文件并写入
        Properties pkProp = new Properties();
        pkProp.setProperty("g", Base64.getEncoder().withoutPadding().encodeToString(g.toBytes()));
        pkProp.setProperty("g_beta", Base64.getEncoder().withoutPadding().encodeToString(g_beta.toBytes()));
        pkProp.setProperty("egg_alpha", Base64.getEncoder().withoutPadding().encodeToString(egg_alpha.toBytes()));

        Map<String, Properties> res = new HashMap<>();
        res.put("msk.properties", mskProp);
        res.put("pk.properties", pkProp);

        return res;
//        FileOperate.storePropToFile(mskProp, mskFileName);
//        FileOperate.storePropToFile(pkProp, pkFileName);
    }

    public static Properties keygen(String pairingParametersFileName, String[] userAttList, String pkFileName, String mskFileName) throws NoSuchAlgorithmException {
        Pairing bp = PairingFactory.getPairing(pairingParametersFileName);
        return keygen(bp, userAttList, pkFileName, mskFileName);
    }

    public static Properties keygen(Pairing bp, String[] userAttList, String pkFileName, String mskFileName) throws NoSuchAlgorithmException {
        Properties pkProp = FileOperate.loadPropFromFile(pkFileName);
        Properties mskProp = FileOperate.loadPropFromFile(mskFileName);
        return keygen(bp, userAttList, pkProp, mskProp);

    }

    public static Properties keygen(Pairing bp, String[] userAttList, Properties pkProp, Properties mskProp) throws NoSuchAlgorithmException {
        // 从公钥文件中加载生成元g, 一个g_beta
        String gString = pkProp.getProperty("g");
        Element g = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(gString)).getImmutable();
        String g_betaString = pkProp.getProperty("g_beta");
        Element g_beta = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(g_betaString)).getImmutable();

        // 从私钥文件中加载私钥g_alpha
        String g_alphaString = mskProp.getProperty("g_alpha");
        Element g_alpha = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(g_alphaString)).getImmutable();

        // 新建用户密钥文件
        Properties skProp = new Properties();

        // 生成新随机数t， 并打包 D = g^alpha · g^beta*t 与 D0 = g^t
        Element t = bp.getZr().newRandomElement().getImmutable();
        Element D = g_alpha.mul(g_beta.powZn(t)).getImmutable();
        Element D0 = g.powZn(t);

        // 写入用户密钥文件
        skProp.setProperty("D", Base64.getEncoder().withoutPadding().encodeToString(D.toBytes()));
        skProp.setProperty("D0", Base64.getEncoder().withoutPadding().encodeToString(D0.toBytes()));

        // 处理用户拥有的属性组
        for (String attr : userAttList) {
            // 对属性值进行哈希处理
            byte[] idHash = AccessTree.sha1(attr);
            // 将哈希值映射到群元素 H
            Element H = bp.getG1().newElementFromHash(idHash, 0, idHash.length).getImmutable();
            // 计算 H^t 并写入用户密钥文件
            Element Datt = H.powZn(t).getImmutable();
            skProp.setProperty("D" + attr, Base64.getEncoder().withoutPadding().encodeToString(Datt.toBytes()));
        }

        // 将用户属性集合的明文也写入用户密钥文件
        skProp.setProperty("userAttList", Arrays.toString(userAttList));

        return skProp;

        // �洢�ļ�
//        FileOperate.storePropToFile(skProp, skFileName);
    }

    public static Map<String, Properties> encrypt(String pairingParametersFileName, List<Element> messageGroup, AccessTree accessTree, String pkFileName) throws NoSuchAlgorithmException {
        Pairing bp = PairingFactory.getPairing(pairingParametersFileName);
        return encrypt(bp, messageGroup, accessTree, pkFileName);
    }

    public static Map<String, Properties> encrypt(Pairing bp, List<Element> messageGroup, AccessTree accessTree, String pkFileName) throws NoSuchAlgorithmException {
        Properties Prop = FileOperate.loadPropFromFile(pkFileName);
        return encrypt(bp, messageGroup, accessTree, Prop);
    }

    public static Map<String, Properties> encrypt(Pairing bp, List<Element> messageGroup, AccessTree accessTree, Properties pkProps) throws NoSuchAlgorithmException {
        // 从公钥文件中加载生成元g, 一个g_beta, 一个egg_alpha
        Element g = FileOperate.loadElementFromProp("g", pkProps, bp.getG1());
        Element g_beta = FileOperate.loadElementFromProp("g_beta", pkProps, bp.getG1());
        Element egg_alpha = FileOperate.loadElementFromProp("egg_alpha", pkProps, bp.getGT());

        // 生成密文文件
        Properties ctProp1 = new Properties();
        Properties ctProp2 = new Properties();

        // 生成随机整数s
        Element s = bp.getZr().newRandomElement().getImmutable();

        // 计算密文组件 C=M · e(g,g)^(alpha*s) (使用s与公钥的alpha部分对明文进行加密)； 计算密文组件 C0 = g^s
        int index = 0;
        for (Element message : messageGroup) {
            Element C = message.duplicate().mul(egg_alpha.powZn(s)).getImmutable();
            ctProp2.setProperty("C_" + index, Base64.getEncoder().withoutPadding().encodeToString(C.toBytes()));
            index++;
        }
        Element C0 = g.powZn(s).getImmutable();

        // 写入文件
        ctProp2.setProperty("C0", Base64.getEncoder().withoutPadding().encodeToString(C0.toBytes()));

        // 将s设置为访问树根节点秘密值并进行秘密共享，设lambda为秘密共享的碎片值
        accessTree.nodes[0].secretShare = s;
        accessTree.nodeShare(bp);

        // 将整个访问结构的秘密共享碎片写入文件
        for (Node node : accessTree.nodes) {
            if (node.isLeaf()) {
                // 生成一个新的随机数r
                Element r = bp.getZr().newRandomElement().getImmutable();

                // 对访问树的每个属性值进行哈希处理并映射到群元素
                byte[] idHash = AccessTree.sha1(node.attribute);
                Element Hi = bp.getG1().newElementFromHash(idHash, 0, idHash.length).getImmutable();

                // 生成密文部件 C1 = g^beta*lambda_i * H_i^(-r); C2 = g^r
//                System.out.println(Hi.powZn(r.negate()));
//                System.out.println(g_beta);
//                System.out.println(node.secretShare);
                Element C1 = g_beta.powZn(node.secretShare).mul(Hi.powZn(r.negate()));
                Element C2 = g.powZn(r);

                System.out.println(C1);

                // 写入密文文件
                ctProp1.setProperty("C1-" + node.attribute, Base64.getEncoder().withoutPadding().encodeToString(C1.toBytes()));
                ctProp1.setProperty("C2-" + node.attribute, Base64.getEncoder().withoutPadding().encodeToString(C2.toBytes()));
            }
        }

        // 存储文件
        Map<String, Properties> res = new HashMap<>();
        res.put("ct1.properties", ctProp1);
        res.put("ct2.properties", ctProp2);
        return res;
    }

    public static Map<String, Properties> encrypt(Pairing bp, Map<Integer, String> structMes, boolean isDeeplyStructural, AccessTree accessTree, Properties pkProps) throws NoSuchAlgorithmException {
        if (isDeeplyStructural){
            return encrypt(bp, structMes, accessTree, pkProps);
        } else{
            Map<Integer, String> structElementMes = new HashMap<>();
            for (Map.Entry<Integer, String> entry : structMes.entrySet()) {
                int mesLevel = entry.getKey();
                String mes = entry.getValue();

                String ElementStringMes = CodeConvert.mesToBigNum(mes, 29);
                structElementMes.put(mesLevel, ElementStringMes);
            }
            return encrypt(bp, structElementMes, accessTree, pkProps);
        }
    }

    public static Map<String, Properties> encrypt(Pairing bp, Map<Integer, String> structMes, AccessTree accessTree, Properties pkProps) throws NoSuchAlgorithmException {

        // 从公钥文件中加载生成元g, 一个g_beta, 一个egg_alpha
        Element g = FileOperate.loadElementFromProp("g", pkProps, bp.getG1());
        Element g_beta = FileOperate.loadElementFromProp("g_beta", pkProps, bp.getG1());
        Element egg_alpha = FileOperate.loadElementFromProp("egg_alpha", pkProps, bp.getGT());

        // 生成密文文件
        Properties ctProp1 = new Properties();
        Properties ctProp2 = new Properties();

        // 生成随机整数s
        Element s = bp.getZr().newRandomElement().getImmutable();
        // 将s设置为访问树根节点秘密值并进行秘密共享，设lambda为秘密共享的碎片值
        accessTree.nodes[0].secretShare = s;
        accessTree.nodeShare(bp);

        // 计算密文组件 C=M · e(g,g)^(alpha*s) (使用s与公钥的alpha部分对明文进行加密)； 计算密文组件 C0 = g^s
        Map<Integer, Element> structElementMes = beforeEncrypt(structMes, bp);
        System.out.println("structElementMes:" + structElementMes);
        System.out.println("accessTree.levelControlNodes" + accessTree.levelControlNodes);
        for (Map.Entry<Integer, Element> entry : structElementMes.entrySet()) {
            int mesLevel = entry.getKey();
            Element ElementMes = entry.getValue();
            Element C = ElementMes.duplicate().mul(egg_alpha.powZn(accessTree.levelControlNodes.get(mesLevel - 1).secretShare)).getImmutable();
            ctProp2.setProperty("C_" + mesLevel, Base64.getEncoder().withoutPadding().encodeToString(C.toBytes()));
        }

        Element C0 = g.powZn(s).getImmutable();

        // 写入文件
        ctProp2.setProperty("C0", Base64.getEncoder().withoutPadding().encodeToString(C0.toBytes()));

        // 将整个访问结构的秘密共享碎片写入文件
        for (Node node : accessTree.nodes) {
            if (node.isLeaf()) {
                // 生成一个新的随机数r
                Element r = bp.getZr().newRandomElement().getImmutable();

                // 对访问树的每个属性值进行哈希处理并映射到群元素
                byte[] idHash = AccessTree.sha1(node.attribute);
                Element Hi = bp.getG1().newElementFromHash(idHash, 0, idHash.length).getImmutable();

                Element C1 = g_beta.powZn(node.secretShare).mul(Hi.powZn(r.negate()));
                Element C2 = g.powZn(r);

                if (node.index <= 5) {
                    // 写入密文文件
                    ctProp1.setProperty("C1-" + node.attribute, Base64.getEncoder().withoutPadding().encodeToString(C1.toBytes()));
                    ctProp1.setProperty("C2-" + node.attribute, Base64.getEncoder().withoutPadding().encodeToString(C2.toBytes()));
                } else {
                    ctProp2.setProperty("C1-" + node.attribute, Base64.getEncoder().withoutPadding().encodeToString(C1.toBytes()));
                    ctProp2.setProperty("C2-" + node.attribute, Base64.getEncoder().withoutPadding().encodeToString(C2.toBytes()));

                }
            }
        }

        // 存储文件
        Map<String, Properties> res = new HashMap<>();
        res.put("ct1.properties", ctProp1);
        res.put("ct2.properties", ctProp2);
        return res;
    }


    public static List<Element> Decrypt(String pairingParametersFileName, AccessTree accessTree, String ctFileName1, String ctFileName2, String skFileName) {
        Pairing bp = PairingFactory.getPairing(pairingParametersFileName);
        return Decrypt(bp, accessTree, ctFileName1, ctFileName2, skFileName);
    }


    public static List<Element> Decrypt(Pairing bp, AccessTree accessTree, String ctFileName1, String ctFileName2, String skFileName) {
        // 读取密文文件
        Properties ctProp1 = FileOperate.loadPropFromFile(ctFileName1);
        Properties ctProp2 = FileOperate.loadPropFromFile(ctFileName2);
        // 读取用户密钥文件
        Properties skProp = FileOperate.loadPropFromFile(skFileName);
        return Decrypt(bp, accessTree, ctProp1, ctProp2, skProp);
    }

    public static List<Element> Decrypt(Pairing bp, AccessTree accessTree, Properties ctProp1, Properties ctProp2, Properties skProp) {

        // 从用户密钥文件中读取用户拥有的属性集
        String userAttListString = skProp.getProperty("userAttList");
        String[] userAttList = userAttListString.substring(1, userAttListString.length() - 1).split(", ");

        Element C0 = FileOperate.loadElementFromProp("C0", ctProp2, bp.getG1());
        // 从用户密钥文件中恢复存有alpha， beta， t属性的 D 与单独存储t属性的 D0
        Element D = FileOperate.loadElementFromProp("D", skProp, bp.getG1());
        Element D0 = FileOperate.loadElementFromProp("D0", skProp, bp.getG1());

        for (String attr : userAttList) {
            Log.v("log", "User attribute contains: " + attr);
        }

        List<Element> C = new LinkedList<>();
        // 从密文文件中恢复存有密文的C， 存有混淆密文整数值的C0
        for (int index = 0; ; index++) {
            try {
                String CString = ctProp2.getProperty("C_" + index);
                C.add(bp.getGT().newElementFromBytes(Base64.getDecoder().decode(CString)).getImmutable());
            } catch (Exception e) {
                break;
            }
        }

        // 核对用户属性是否满足解密需求
        for (Node node : accessTree.nodes) {
            if (node.isLeaf()) {
                // 如果叶子节点的属性值属于用户属性列表，则将属性对应的密文组件和秘钥组件配对的结果作为秘密值
                if (Arrays.asList(userAttList).contains(node.attribute)) {
                    // 从密文文件中恢复所有叶子节点的C1， C2值
                    Element C1 = FileOperate.loadElementFromProp("C1-" + node.attribute, ctProp1, bp.getG1());
                    Element C2 = FileOperate.loadElementFromProp("C2-" + node.attribute, ctProp1, bp.getG1());

                    // 从用户密钥文件中恢复用户属性对应的 Datt 值
                    Element Datt = FileOperate.loadElementFromProp("D" + node.attribute, skProp, bp.getG1());

                    // 拿到去除随机数r的秘密碎片值
                    node.secretShare = bp.pairing(C1, D0).mul(bp.pairing(C2, Datt)).getImmutable();
                } else {
                    node.secretShare = null;
                }
            }
        }


        // 进行秘密恢复
        boolean treeOK = AccessTree.nodeRecover(accessTree.nodes, accessTree.nodes[0], userAttList, bp);
        if (treeOK) {
            // 新建存放恢复C的变量
            List<Element> afterC = new LinkedList<>();
            Element egg_alphas = bp.pairing(C0, D).div(accessTree.nodes[0].secretShare);
            for (Element C_block : C) {
                afterC.add(C_block.div(egg_alphas));
            }
            return afterC;
        } else {
            Log.v("log", "The access tree is not satisfied.");
            return null;
        }
    }

    public static List<Element> Decrypt(Pairing bp, AccessTree accessTree, Properties ctProp1, Properties ctProp2, Properties skProp, boolean isNew) {

        // 从用户密钥文件中读取用户拥有的属性集
        String userAttListString = skProp.getProperty("userAttList");
        String[] userAttList = userAttListString.substring(1, userAttListString.length() - 1).split(", ");
        Element C0 = FileOperate.loadElementFromProp("C0", ctProp2, bp.getG1());
        // 从用户密钥文件中恢复存有alpha， beta， t属性的 D 与单独存储t属性的 D0
        Element D = FileOperate.loadElementFromProp("D", skProp, bp.getG1());
        Element D0 = FileOperate.loadElementFromProp("D0", skProp, bp.getG1());
        System.out.println("log001  D0_1: " + D0);
        D0 = FileOperate.loadElementFromProp("D0", skProp, bp.getG1());
        System.out.println("log001  D0_2: " + D0);
        D0 = FileOperate.loadElementFromProp("D0", skProp, bp.getG1());

        System.out.println("log001  C0: " + C0);
        System.out.println("log001  D: " + D);
        System.out.println("log001  D0: " + D0);
        System.out.println("log001" + "\n\n");


        Map<Integer, Element> secretShare = new HashMap<>();
        int userLevel = 0;

        for (Node levelNode : accessTree.levelControlNodes) {
            accessTree.mainNode = levelNode;

            if (isValid(accessTree, userAttList, bp, D0, ctProp1, ctProp2, skProp)) {
                System.out.println("log001:"+levelNode.level + " level is ok");
                secretShare.put(levelNode.level, accessTree.mainNode.secretShare);
                userLevel = Math.max(levelNode.level, userLevel);
            }
        }

        // 新建变量存储存有被混淆的密文C
        Map<Integer, Element> CWithLevel = new HashMap<>();

        // 从密文文件中恢复存有密文的C， 存有混淆密文整数值的C0
        for (int index = 0; ; index++) {
            try {
                String CString = ctProp2.getProperty("C_" + index);
                CWithLevel.put(index, bp.getGT().newElementFromBytes(Base64.getDecoder().decode(CString)).getImmutable());
            } catch (Exception e) {
                if (index == 0) {
                    continue;
                } else {
                    break;
                }
            }
        }

//
//        // 进行秘密恢复
//        boolean treeOK = AccessTree.nodeRecover(accessTree.nodes, accessTree.nodes[0], userAttList, bp);
        if (!secretShare.isEmpty()) {
            // 新建存放恢复C的变量
            List<Element> afterC = new LinkedList<>();


            for (Map.Entry<Integer, Element> singleSecretShare : secretShare.entrySet()) {
                // 获取已知的各个等级的秘密共享值
                int thisLevel = singleSecretShare.getKey();
                Element thisSecret = singleSecretShare.getValue();

                Element egg_alphas = bp.pairing(C0, D).div(thisSecret);
                System.out.println("log001: login_egg_alphas" + egg_alphas);

                afterC.add(CWithLevel.get(thisLevel).div(egg_alphas));
            }
            return afterC;
        } else {
            System.out.println("log001: No access tree is satisfied.");
            return null;
        }
    }


    public static void basicTest() throws Exception {
        int rBits = 160;
        int qBits = 512;
        TypeACurveGenerator pg = new TypeACurveGenerator(rBits, qBits);
        PairingParameters pp = pg.generate();
        Log.v("log", pp.toString());
        Pairing bp = PairingFactory.getPairing(pp);

//        Pairing bp = PairingFactory.getPairing("a.properties");

        // 文件存储路径
        String dir = "data/";
        String pairingParametersFileName = "a.properties";
        String pkFileName = dir + "pk.properties";
        String mskFileName = dir + "msk.properties";
        String skFileName = dir + "sk.properties";
        String ctFileName1 = dir + "ct1.properties";
        String ctFileName2 = dir + "ct2.properties";

        // ������Բ����Ⱥ
//        Pairing bp = PairingFactory.getPairing(pairingParametersFileName);

        // ������Ϣ
        String mes = "\tat it.unisa.dia.gas.plaf.jpbc.field.curve.ImmutableCurveElement.powZn(ImmutableCurveElement.java:10)";
        Log.v("log", "明文信息:" + mes);

        // �û�ӵ�е����Ա�
//        String[] userAttList = {"Hedgehog", "zshw@outlook.com", "13204163804"};
        String[] userAttList = {"121"};

        Node[] nodes = new Node[8];
        nodes[0] = new Node(0, new int[]{1, 2}, new int[]{1, 2});
        nodes[1] = new Node(1, new int[]{3, 4}, new int[]{3, 4, 5, 6});
        nodes[2] = new Node(2, new int[]{1, 1}, new int[]{7});
        nodes[3] = new Node(3, "user");
        nodes[4] = new Node(4, "Hedgehog");
        nodes[5] = new Node(5, "zshw@outlook.com");
        nodes[6] = new Node(6, "13204163804");
        nodes[7] = new Node(7, "121");

        AccessTree accessTree = new AccessTree(nodes, bp);

        // ��ʼ����������
        setup(bp);

        // �����û���Կ
        keygen(bp, userAttList, pkFileName, mskFileName);

        // ����������Ϣ
        List<String> messageStringGroup;
//        StringBuilder messageBigNumStringGroup = new StringBuilder();
        List<String> messageBigNumStringGroup = new LinkedList<>();
        List<Element> messageGroup = new LinkedList<>();
        messageStringGroup = CodeConvert.mesToBigNumGroup(mes);
        for (String mesBigNum : messageStringGroup) {
            messageGroup.add(PairingFactory.getPairing(pairingParametersFileName).getGT().newElement(new BigInteger(mesBigNum)));
        }
//        Element message = PairingFactory.getPairing(pairingParametersFileName).getGT().newElement(new BigInteger("010203040506070809101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778"));

        // ����������Ϣ
        encrypt(bp, messageGroup, accessTree, pkFileName);

        // ����������Ϣ����ֵ
        List<Element> res = Decrypt(bp, accessTree, ctFileName1, ctFileName2, skFileName);
        assert res != null;
        for (Element bigNum : res) {
//            messageBigNumStringGroup.add(bigNum.toString());
            messageBigNumStringGroup.add(bigNum.toString().substring(1, bigNum.toString().length() - 1).split(",")[0].substring(2));
        }
//        utils.util.BigNumGroupToMes()
        String resString = CodeConvert.BigNumGroupToMes(messageBigNumStringGroup);
        Log.v("log", messageBigNumStringGroup.toString());
        Log.v("log", "解密信息:" + resString);

        if (mes.equals(resString)) {
            Log.v("log", "解密成功");
        }
    }

    public static void main(String[] args) throws Exception {
        basicTest();
    }
}