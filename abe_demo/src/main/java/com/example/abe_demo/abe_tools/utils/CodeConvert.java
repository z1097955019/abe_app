package com.example.abe_demo.abe_tools.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.unisa.dia.gas.jpbc.Element;

/**
 * The type Code convert.
 */
public class CodeConvert {
    public CodeConvert() {
    }

    /**
     * String to ascii string.
     *
     * @param value         the value
     * @param needSeparator the need separator
     * @return the string
     */
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

    /**
     * String to ascii string.
     *
     * @param value the value
     * @return the string
     */
    public static String stringToAscii(String value) {
        StringBuilder sbu = new StringBuilder();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sbu.append(completeNum(String.valueOf((int) chars[i])));
        }
        return sbu.toString();
    }

    /**
     * Ascii to string string.
     *
     * @param value the value
     * @return the string
     */
    public static String asciiToString(String value) {
        StringBuilder sbu = new StringBuilder();
        String[] chars = value.split(",");
        for (String aChar : chars) {
            sbu.append((char) Integer.parseInt(aChar));
        }
        return sbu.toString();
    }

    /**
     * Complete num string.
     *
     * @param characterNum the character num
     * @return the string
     */
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

    /**
     * Add separator string.
     *
     * @param num the num
     * @return the string
     */
//
    public static String addSeparator(String num) {
        StringBuilder sbu = new StringBuilder();
        for (int i = 0; i <= (num.length() / 5); i += 5) {
            sbu.append(num, i, i + 5).append((i != (num.length() / 5) - 1) ? "," : "");
        }
        return sbu.toString();
    }

    /**
     * Add separator string.
     *
     * @param numGroup the num group
     * @return the string
     */
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

    /**
     * Group list.
     *
     * @param mes       the mes
     * @param groupSize the group size
     * @return the list
     */
//
    public static List<String> group(String mes, int groupSize) {
        List<String> mesGroup = new LinkedList<>();
        System.out.println(mes.length());
        for (int i = 0; i <= (mes.length() / groupSize); i++) {
            mesGroup.add((i * groupSize + groupSize < mes.length() ? mes.substring(i * groupSize, (i + 1) * groupSize) : mes.substring(i * groupSize)));
        }
        return mesGroup;
    }

    /**
     * Mes to big num group list.
     *
     * @param mes       the mes
     * @param groupSize the group size
     * @param need9999  the need 9999
     * @return the list
     */
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

    /**
     * Mes to big num string.
     *
     * @param mes       the mes
     * @param groupSize the group size
     * @return the string
     */
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

    /**
     * Mes to big num string.
     *
     * @param mes the mes
     * @return the string
     */
    public static String mesToBigNum(String mes) {
        return mesToBigNum(mes, 29);
    }


    /**
     * Mes to big num group list.
     *
     * @param mes the mes
     * @return the list
     */
//
    public static List<String> mesToBigNumGroup(String mes) {
        return mesToBigNumGroup(mes, 29, true);
    }

    /**
     * Big num group to mes string.
     *
     * @param numGroup the num group
     * @return the string
     */
    public static String BigNumGroupToMes(List<String> numGroup) {
        String mesNum = addSeparator(numGroup);

        //
        return asciiToString(mesNum);
    }

    /**
     * Big num group to mes string.
     *
     * @param mes the mes
     * @return the string
     */
    public static String BigNumGroupToMes(String mes) {
        //
        String[] mesStrings = mes.substring(1, mes.length() - 1).split(", ");
        List<String> mesNumGroup = Arrays.stream(mesStrings).collect(Collectors.toList());
        return BigNumGroupToMes(mesNumGroup);
    }

    /**
     * Init data map.
     *
     * @param dataBefore the data before
     * @return the map
     */
// 把最初的文本进行前置处理
    public static Map<String, DeliveryMessage> initData(Map<String, DeliveryMessage> dataBefore) {
        for (Map.Entry<String, DeliveryMessage> entry : dataBefore.entrySet()) {
            entry.getValue().setPhoneNumber("8686" + entry.getValue().getPhoneNumber());
            entry.getValue().setAheadAddress("8866" + entry.getValue().getAheadAddress());
        }
        return dataBefore;
    }

    /**
     * Structural data to structural big num string map.
     *
     * @param dataBefore the data before
     * @return the map
     */
// 把前置处理过的文本分别变为大数字字符串
    public static Map<String, DeliveryMessage> structuralDataToStructuralBigNumString(Map<String, DeliveryMessage> dataBefore) {
        for (Map.Entry<String, DeliveryMessage> entry : dataBefore.entrySet()) {
            entry.getValue().setPersonName(mesToBigNum(entry.getValue().getPersonName()));
            entry.getValue().setBehindAddress(mesToBigNum(entry.getValue().getBehindAddress()));
        }
        return dataBefore;
    }

    /**
     * Structural big num string to big num group map.
     *
     * @param dataBefore the data before
     * @return the map
     */
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


    /**
     * From data to big num group map.
     *
     * @param dataBefore the data before
     * @return the map
     */
    public static Map<Integer, String> fromDataToBigNumGroup(Map<String, DeliveryMessage> dataBefore) {
        return StructuralBigNumStringToBigNumGroup(structuralDataToStructuralBigNumString(initData(dataBefore)));
    }

    /**
     * The type Abe ct decoder.
     * 解码工具类
     */
    public class ABECtDecoder {

        // 类内部存储解密得到的密文元素的变量
        private final List<Element> CtElement;

        /**
         * Instantiates a new Abe ct decoder.
         * 构造函数赋值
         *
         * @param CtElement the ct element
         */
        public ABECtDecoder(List<Element> CtElement) {
            this.CtElement = CtElement;
        }


        /**
         * Decode to string string.
         * 生成单个字符串的解码函数
         *
         * @return the string
         */
        public String decodeToString() {
            return BigNumGroupToMes(CtElement);
        }

        /**
         * Decode to structural string list.
         * 生成字符串列表的解码函数
         *
         * @return the list
         */
        public List<String> decodeToStructuralString() {
            return BigNumGroupToStructuralMes(CtElement);
        }

        /**
         * Ascii to string string.
         * 把含有“，”的字符串通过ASCII码编码为正常的符号
         *
         * @param value the value
         * @return the string
         */
        public String asciiToString(String value) {
            StringBuilder sbu = new StringBuilder();
            String[] chars = value.split(",");
            for (String aChar : chars) {
                sbu.append((char) Integer.parseInt(aChar));
            }
            return sbu.toString();
        }

        /**
         * Add separator string.
         * 为单个jpbc元素添加分隔符
         *
         * @param num the num
         * @return the string
         */
        public String addSeparator(Element num) {
            String num_str = ElementToElement_str(num);
            StringBuilder sbu = new StringBuilder();
            for (int i = 5; i < num_str.length(); i += 5) {
                sbu.append(num_str, i, i + 5).append(",");
            }
            return sbu.substring(0, sbu.toString().length() - 1);
        }

        /**
         * Add separator string.
         * 为一组jpbc元素添加分隔符
         *
         * @param numGroup the num group
         * @return the string
         */
        public String addSeparator(List<Element> numGroup) {
            StringBuilder sbu = new StringBuilder();
            for (Element num : numGroup) {
                String num_str = ElementToElement_str(num);
                for (int i = 5; i < num_str.length(); i += 5) {
                    sbu.append(num_str, i, i + 5).append(",");
                }
            }
            return sbu.substring(0, sbu.toString().length() - 1);
        }

        /**
         * Big num group to mes string.
         * 将一组元素转为一个合并的解码后的明文
         *
         * @param numGroup the num group
         * @return the string
         */
        public String BigNumGroupToMes(List<Element> numGroup) {
            String mesNum = addSeparator(numGroup);
            return asciiToString(mesNum);
        }

        public String BigNumToMes(String num_str) {
            String mesNum = addSeparator(num_str);
            return asciiToString(mesNum);
        }

        private String addSeparator(String num_str) {
            StringBuilder sbu = new StringBuilder();
            for (int i = 5; i < num_str.length(); i += 5) {
                sbu.append(num_str, i, i + 5).append(",");
            }
            return sbu.substring(0, sbu.toString().length() - 1);
        }

        /**
         * Big num group to structural mes list.
         * 将一组元素转化成一个解码后的明文列表
         *
         * @param numGroup the num group
         * @return the list
         */
        public List<String> BigNumGroupToStructuralMes(List<Element> numGroup) {
            List<String> structuralMes = new LinkedList<>();
            for (Element num : numGroup) {
                structuralMes.add(asciiToString(addSeparator(num)));
            }
            return structuralMes;
        }

        /**
         * Element to string string.
         * 把元素转化为对应的大数字字符串
         *
         * @param num the num
         * @return the string
         */
        public String ElementToElement_str(Element num) {
            return (num.toString().substring(1, num.toString().length() - 1).split(",")[0].substring(2));
        }

        /**
         * Element group to element str group list.
         * 把元素列表转化为对应的大数字字符串列表
         *
         * @param numGroup the num group
         * @return the list
         */
        public List<String> ElementGroupToElement_strGroup(List<Element> numGroup) {
            List<String> element_strGroup = new LinkedList<>();
            for (Element num : numGroup) {
                element_strGroup.add(ElementToElement_str(num));
            }
            return element_strGroup;
        }

        public String DeliveryDecodeToString(){
            StringBuilder sb =new StringBuilder();
            Map<String, DeliveryMessage> dm = DeliveryElementGroupToStructMes(this.CtElement);
            for(Map.Entry<String, DeliveryMessage> singleDM: dm.entrySet()){
                sb.append(singleDM.getKey()).append(singleDM.getValue());
            }
            return sb.toString();
        }

        /**
         * Delivery element group to struct mes map.
         * 从元素列表解析出所有快递数据
         *
         * @param BigNumGroup the big num group
         * @return the map
         */
        public Map<String, DeliveryMessage> DeliveryElementGroupToStructMes(List<Element> BigNumGroup) {
            Map<String, DeliveryMessage> structMes = new HashMap<>();
            DeliveryMessage senderMessage = new DeliveryMessage();
            DeliveryMessage receiverMessage = new DeliveryMessage();
            List<String> element_strGroup = ElementGroupToElement_strGroup(BigNumGroup);
            FirstLevelNumDealer(receiverMessage, element_strGroup.get(0));
            SecondLevelNumDealer(receiverMessage, element_strGroup.get(1));
            ThirdLevelNumDealer(senderMessage, element_strGroup.get(2));
            structMes.put("sender", senderMessage);
            structMes.put("receiver", receiverMessage);
            return structMes;
        }

        private void FirstLevelNumDealer(DeliveryMessage receiverMessage, String element_str) {
            receiverMessage.setAheadAddress(element_str.substring(5, 10));
            receiverMessage.setBehindAddress(BigNumToMes(element_str.substring(11)));
        }

        private void SecondLevelNumDealer(DeliveryMessage receiverMessage, String element_str) {
            receiverMessage.setPhoneNumber(element_str.substring(5, 15));
            receiverMessage.setPersonName(BigNumToMes(element_str.substring(16)));
        }

        private void ThirdLevelNumDealer(DeliveryMessage senderMessage, String element_str) {
            senderMessage.setAheadAddress(element_str.substring(5, 10));
            senderMessage.setPhoneNumber(element_str.substring(15, 25));
            senderMessage.setPersonName(BigNumToMes(element_str.substring(26)));
        }

    }


    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {

    }


}
