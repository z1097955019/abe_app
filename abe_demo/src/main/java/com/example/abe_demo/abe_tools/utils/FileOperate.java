package com.example.abe_demo.abe_tools.utils;

import android.content.Context;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

public class FileOperate {
    public static void storePropToFile(Properties prop, FileOutputStream out) {
        try {
            prop.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ������д���ļ�
    public static void storePropToFile(Properties prop, String fileName) {
        try (FileOutputStream out = new FileOutputStream(fileName)) {
            storePropToFile(prop, out);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(fileName + " save failed!");
            System.exit(-1);
        }
    }

    public static Properties loadPropFromFile(FileInputStream in) {
        Properties prop = new Properties();
        try {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    // ��ȡ�����ļ�������������
    public static Properties loadPropFromFile(String fileName) {
        Properties prop = new Properties();
        try (FileInputStream in = new FileInputStream(fileName)) {
            loadPropFromFile(in);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(fileName + " load failed!");
            System.exit(-1);
        }
        return prop;
    }

    // �������ļ��ж�ȡ����
    public static Element loadElementFromPropFile(String elementName, String fileName, Field field) {
        Properties Prop = FileOperate.loadPropFromFile(fileName);
        String gString = Prop.getProperty(elementName);
        return field.newElementFromBytes(Base64.getDecoder().decode(gString)).getImmutable();
    }

    public static Element loadElementFromProp(String elementName, Properties prop, Field field) {
        String gString = prop.getProperty(elementName);
        System.out.println("log002 gString: "+gString);
        return field.newElementFromBytes(Base64.getDecoder().decode(gString)).getImmutable();
    }




}
