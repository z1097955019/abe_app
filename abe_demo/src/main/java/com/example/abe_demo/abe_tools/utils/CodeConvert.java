package com.example.abe_demo.abe_tools.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CodeConvert {
    // ���� -> ���֣���һ���ַ���ת��Ϊһ�����Ϊ��������ַ���
    public static String stringToAscii(String value, boolean needSeparator) {
        StringBuilder sbu = new StringBuilder();
        sbu.append("99999");
//        System.out.println((char) Integer.parseInt("106400"));
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sbu.append(completeNum(String.valueOf((int) chars[i]))).append((i != chars.length - 1) && (needSeparator) ? "," : "");
        }
        return sbu.toString();
    }

    /**
     * Asciiת��Ϊ�ַ���
     */
    public static String asciiToString(String value) {
        StringBuilder sbu = new StringBuilder();
        String[] chars = value.split(",");
        for (String aChar : chars) {
//            System.out.println(aChar);
            sbu.append((char) Integer.parseInt(aChar));
        }
        return sbu.toString();
    }

    // ���� -> ���֣�ʵ�ֲ�ȫascII��Ϊ��λ���ֵĺ���
    public static String completeNum(String characterNum) {
        StringBuilder sbu = new StringBuilder();
//        !!!!!!!!!!!
        if (characterNum.length() <= 4) {
            sbu.append("0").append(characterNum);
            return completeNum(sbu.toString());
        } else {
//            System.out.println(characterNum);
            return characterNum;
        }
    }

    // ���� -> ���֣� �������ַָ��Ϊ5��һ��
    public static String addSeparator(String num) {
        StringBuilder sbu = new StringBuilder();
        for (int i = 0; i <= (num.length() / 5); i += 5) {
            sbu.append(num, i, i + 5).append((i != (num.length() / 5) - 1) ? "," : "");
        }
        return sbu.toString();
    }

    // ���� -> ���֣� �������ַָ��Ϊ5��һ��
    public static String addSeparator(List<String> numGroup) {
        StringBuilder sbu = new StringBuilder();
        for (String num : numGroup) {
            System.out.println(num.length());
            System.out.println((num.length() / 5) - 1);
            for (int i = 5; i < num.length(); i += 5) {
                sbu.append(num, i, i + 5).append(",");
            }
        }
        System.out.println(sbu);
        return sbu.substring(0, sbu.toString().length() - 1);
    }

    // ���� -> ���֣������ַ���
    public static List<String> group(String mes, int groupSize) {
        List<String> mesGroup = new LinkedList<>();
        System.out.println(mes.length());
        for (int i = 0; i <= (mes.length() / groupSize); i++) {
            mesGroup.add((i * groupSize + groupSize < mes.length() ? mes.substring(i * groupSize, (i + 1) * groupSize) : mes.substring(i * groupSize)));
        }
        return mesGroup;
    }

    // ���� -> ���֣���װ�ĺ���
    public static List<String> mesToBigNumGroup(String mes, int groupSize) {
        // ԭʼ��Ϣ����
        List<String> mesGroup = group(mes, groupSize);

        // תΪ��������
        List<String> mesValueGroup = new LinkedList<>();
        for (String singleMes : mesGroup) {
            System.out.println(singleMes.length());
            if(singleMes.length()!=0){
                mesValueGroup.add(stringToAscii(singleMes, false));
            }
        }

        // �����������
        return mesValueGroup;
    }

    // 消息转单个大数字
    public static String mesToBigNum(String mes, int groupSize) {
        // 原始信息分组
        List<String> mesGroup = group(mes, groupSize);

        // 转为大数字组
        List<String> mesValueGroup = new LinkedList<>();
        for (String singleMes : mesGroup) {
//            System.out.println(singleMes.length());
            if(singleMes.length()!=0){
                mesValueGroup.add(stringToAscii(singleMes, false));
            }
        }

        // 输出大数字组
        return mesValueGroup.get(0);
    }


    // ���� -> ���֣���װ�ĺ���
    public static List<String> mesToBigNumGroup(String mes) {
        return mesToBigNumGroup(mes, 29);
    }

    public static String BigNumGroupToMes(List<String> numGroup) {
        String mesNum = addSeparator(numGroup);

        // ���ת����ַ���
        return asciiToString(mesNum);
    }

    public static String BigNumGroupToMes(String mes) {
        // ԭʼ��Ϣ����
        String[] mesStrings = mes.substring(1, mes.length() - 1).split(", ");
        List<String> mesNumGroup = Arrays.stream(mesStrings).collect(Collectors.toList());
        return BigNumGroupToMes(mesNumGroup);
    }


    public static void main(String[] args) {
//        // ԭʼ��Ϣ��
//        List<String> mesGroup = group("������Ҫ�趨java����������Ĭ��ֵ,���ְ������������еķ����в�ͨ,java���ƺ�ֻ��ͨ��������������ʵ��", 30);
//
//        // ��������Ϣ��
//        List<String> mesValueGroup = new LinkedList<>();
//        List<String> mesValueWithSeparatorGroup = new LinkedList<>();
//        List<String> mesAfterValueGroup = new LinkedList<>();
//        for (String singleMes : mesGroup) {
//            mesValueGroup.add(stringToAscii(singleMes, false));
//            mesValueWithSeparatorGroup.add(stringToAscii(singleMes, true));
//        }
//        System.out.println(mesValueGroup);
//        System.out.println(mesValueWithSeparatorGroup);
//        System.out.println(mesValueWithSeparatorGroup.size());
//        String mesNumGroup = addSeparator(mesValueGroup);
//        System.out.println(mesNumGroup);
//        for (String singleMes : mesValueWithSeparatorGroup) {
//            mesAfterValueGroup.add(asciiToString(singleMes));
//        }
//        System.out.println(mesAfterValueGroup);
//        System.out.println(asciiToString(mesNumGroup));

        List<String> BigNumGroup = mesToBigNumGroup("��ʼ��2021��2��21�� C����ͨ������main()����������������,�����û��Լ�����,�������ǹ̶��ġ���һ������������,�ڶ���������һ��ָ���ַ���ָ�������ָ���һ����������Ϊ�ַ���...");
        System.out.println(BigNumGroup);
        System.out.println(BigNumGroupToMes(BigNumGroup.toString()));
    }


}
