package com.example.abe_demo.abe_tools.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CodeConvert {
    //
    public static String stringToAscii(String value, boolean needSeparator) {
        StringBuilder sbu = new StringBuilder();
        sbu.append("99999");
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sbu.append(completeNum(String.valueOf((int) chars[i]))).append((i != chars.length - 1) && (needSeparator) ? "," : "");
        }
        return sbu.toString();
    }

    public static String stringToAscii(String value) {
        StringBuilder sbu = new StringBuilder();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sbu.append(completeNum(String.valueOf((int) chars[i])));
        }
        return sbu.toString();
    }

    public static String asciiToString(String value) {
        StringBuilder sbu = new StringBuilder();
        String[] chars = value.split(",");
        for (String aChar : chars) {
            sbu.append((char) Integer.parseInt(aChar));
        }
        return sbu.toString();
    }

    //
    public static String completeNum(String characterNum) {
        StringBuilder sbu = new StringBuilder();
        if (characterNum.length() <= 4) {
            sbu.append("0").append(characterNum);
            return completeNum(sbu.toString());
        } else {
//            System.out.println(characterNum);
            return characterNum;
        }
    }

    //
    public static String addSeparator(String num) {
        StringBuilder sbu = new StringBuilder();
        for (int i = 0; i <= (num.length() / 5); i += 5) {
            sbu.append(num, i, i + 5).append((i != (num.length() / 5) - 1) ? "," : "");
        }
        return sbu.toString();
    }

    //
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

    //
    public static List<String> group(String mes, int groupSize) {
        List<String> mesGroup = new LinkedList<>();
        System.out.println(mes.length());
        for (int i = 0; i <= (mes.length() / groupSize); i++) {
            mesGroup.add((i * groupSize + groupSize < mes.length() ? mes.substring(i * groupSize, (i + 1) * groupSize) : mes.substring(i * groupSize)));
        }
        return mesGroup;
    }

    //
    public static List<String> mesToBigNumGroup(String mes, int groupSize, boolean need9999) {
        //
        List<String> mesGroup = group(mes, groupSize);

        //
        List<String> mesValueGroup = new LinkedList<>();
        for (String singleMes : mesGroup) {
            System.out.println(singleMes.length());
            if (singleMes.length() != 0) {
                if (need9999) {
                    mesValueGroup.add(stringToAscii(singleMes, false));
                } else {
                    mesValueGroup.add(stringToAscii(singleMes));

                }
            }
        }

        //
        return mesValueGroup;
    }

    // 消息转单个大数字
    public static String mesToBigNum(String mes, int groupSize) {
        // 原始信息分组
        List<String> mesGroup = group(mes, groupSize);

        // 转为大数字组
        List<String> mesValueGroup = new LinkedList<>();
        for (String singleMes : mesGroup) {
            if (singleMes.length() != 0) {
                mesValueGroup.add(stringToAscii(singleMes, false));
            }
        }

        // 输出大数字组
        return mesValueGroup.get(0);
    }

    public static String mesToBigNum(String mes) {
        return mesToBigNum(mes, 29);
    }


    //
    public static List<String> mesToBigNumGroup(String mes) {
        return mesToBigNumGroup(mes, 29, true);
    }

    public static String BigNumGroupToMes(List<String> numGroup) {
        String mesNum = addSeparator(numGroup);

        //
        return asciiToString(mesNum);
    }

    public static String BigNumGroupToMes(String mes) {
        //
        String[] mesStrings = mes.substring(1, mes.length() - 1).split(", ");
        List<String> mesNumGroup = Arrays.stream(mesStrings).collect(Collectors.toList());
        return BigNumGroupToMes(mesNumGroup);
    }

    // 把最初的文本进行前置处理
    public static Map<String, DeliveryMessage> initData(Map<String, DeliveryMessage> dataBefore) {
        for (Map.Entry<String, DeliveryMessage> entry : dataBefore.entrySet()) {
            entry.getValue().setPhoneNumber("8686" + entry.getValue().getPhoneNumber());
            entry.getValue().setAheadAddress("8866" + entry.getValue().getAheadAddress());
        }
        return dataBefore;
    }

    // 把前置处理过的文本分别变为大数字字符串
    public static Map<String, DeliveryMessage> structuralDataToStructuralBigNumString(Map<String, DeliveryMessage> dataBefore) {
        for (Map.Entry<String, DeliveryMessage> entry : dataBefore.entrySet()) {
            entry.getValue().setPersonName(mesToBigNum(entry.getValue().getPersonName()));
            entry.getValue().setBehindAddress(mesToBigNum(entry.getValue().getBehindAddress()));
        }
        return dataBefore;
    }

    // 把转为大数字的结构化文本拼接成三个等级的大数字
    public static Map<Integer, String> StructuralBigNumStringToBigNumGroup(Map<String, DeliveryMessage> dataBefore) {
        Map<Integer, String> structuralClearText = new HashMap<>();

        try {
            StringBuilder senderSb = new StringBuilder();
            DeliveryMessage sender = dataBefore.get("sender");
            if (sender != null) {
                senderSb.append(sender.getAheadAddress()).append(sender.getPhoneNumber())
                        .append(sender.getPersonName()).append(sender.getBehindAddress());
            }
            structuralClearText.put(1, senderSb.toString());
        } catch (Exception e) {
            System.out.println("error log: " + e);
        }

        try {
            StringBuilder receiverSb1 = new StringBuilder();
            StringBuilder receiverSb2 = new StringBuilder();
            DeliveryMessage receiver = dataBefore.get("receiver");
            if (receiver != null) {
                receiverSb1.append(receiver.getAheadAddress()).append(receiver.getBehindAddress());
                receiverSb2.append(receiver.getPhoneNumber()).append(receiver.getPersonName());
            }
            structuralClearText.put(2, receiverSb1.toString());
            structuralClearText.put(3, receiverSb2.toString());
        } catch (Exception e) {
            System.out.println("error log: " + e);
        }

        return structuralClearText;
    }


    public static void main(String[] args) {

    }


}
