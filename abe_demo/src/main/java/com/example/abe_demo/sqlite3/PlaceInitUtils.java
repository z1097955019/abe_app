package com.example.abe_demo.sqlite3;

import android.content.Context;

import com.example.abe_demo.sqlite3.bean.City;
import com.example.abe_demo.sqlite3.bean.Place;
import com.example.abe_demo.sqlite3.bean.Province;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceInitUtils {

    private Context context;

    public PlaceInitUtils(Context context) {
        this.context =context;
    }

    public List<Province> getProvinceListFromFile(int id) {
        List<Province> list = new LinkedList<>();
        InputStream inputStream = context.getResources().openRawResource(id);
        String text = replaceSpecialStr(getStringFromInputStream(inputStream));
        String[] textGroup = text.split("-");
        for (String singleText : textGroup) {
            String[] blockText = singleText.split("\\|");
            for (String textMap : blockText) {
                String[] map = textMap.split(",");
                list.add(new Province(map[0], Integer.parseInt(map[1])));
            }
        }
        System.out.println("log015: " + text);
        System.out.println("log015: " + list);
        return list;
    }

    public List<City> getCityListFromFile(int id) {
        List<City> list = new LinkedList<>();
        InputStream inputStream = context.getResources().openRawResource(id);
        String text = replaceSpecialStr(getStringFromInputStream(inputStream));
        String[] textGroup = text.split("-");
        for (String singleText : textGroup) {
            String[] blockText = singleText.split("\\|");
            for (String textMap : blockText) {
                String[] map = textMap.split(",");
                list.add(new City(map[0], Integer.parseInt(map[1]), Integer.parseInt(map[1].substring(0, 2))));
            }
        }
        System.out.println("log015: " + list);
        return list;
    }

    public List<Place> getPlaceCityListFromFile(int id) {
        List<Place> list = new LinkedList<>();
        InputStream inputStream = context.getResources().openRawResource(id);
        String text = replaceSpecialStr(getStringFromInputStream(inputStream));
        String[] textGroup = text.split("-");
        for (String singleText : textGroup) {
            String[] blockText = singleText.split("\\|");
            for (String textMap : blockText) {
                String[] map = textMap.split(",");
                list.add(new Place(map[0], Integer.parseInt(map[1]), Integer.parseInt(map[1].substring(0, 4))));
            }
        }
        System.out.println("log015: " + list);
        return list;
    }

    private String getStringFromInputStream(InputStream inputStream) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while (true) {
            try {
                if ((length = inputStream.read(buffer)) == -1) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            result.write(buffer, 0, length);
        }
        try {
            return result.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 去除字符串中的空格、回车、换行符、制表符等
     *
     * @param str
     * @return
     */
    private static String replaceSpecialStr(String str) {
        String repl = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\n");
            Matcher m = p.matcher(str);
            repl = m.replaceAll("");
        }
        return repl;
    }
}
