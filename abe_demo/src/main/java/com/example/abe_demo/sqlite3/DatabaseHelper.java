package com.example.abe_demo.sqlite3;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.abe_demo.sqlite3.bean.City;
import com.example.abe_demo.sqlite3.bean.Place;
import com.example.abe_demo.sqlite3.bean.Province;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Data.db";
    private static final String TABLE_PROVINCE = "ProvinceTable";
    private static final String TABLE_CITY = "CityTable";
    private static final String TABLE_PLACE = "PlaceTable";

    private final Context context;

    private String ConstructSQLCode(String tableName, Map<String, String> dataStructure) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table").append(" ").append(tableName).append(" ").append("(");
        for (Map.Entry<String, String> entry : dataStructure.entrySet()) {
            sb.append(entry.getKey()).append(" ").append(entry.getValue()).append(",");
        }
//        sb.d
        sb.delete(sb.length() - 1, sb.length());
        sb.append(");");
        System.out.println("log015: " + sb);
        return sb.toString();
    }

    public DatabaseHelper(@Nullable Context context, int version) {
        super(context, DATABASE_NAME, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Map<String, String> ProvinceTable = new HashMap<>();
        ProvinceTable.put("id", "integer primary key autoincrement");
        ProvinceTable.put("ProvinceName", "text");
        ProvinceTable.put("ProvinceCode", "integer");
        sqLiteDatabase.execSQL(ConstructSQLCode(TABLE_PROVINCE, ProvinceTable));

        Map<String, String> CityTable = new HashMap<>();
        CityTable.put("id", "integer primary key autoincrement");
        CityTable.put("CityName", "text");
        CityTable.put("CityCode", "integer");
        CityTable.put("ProvinceId", "integer");
        sqLiteDatabase.execSQL(ConstructSQLCode(TABLE_CITY, CityTable));

        Map<String, String> PlaceTable = new HashMap<>();
        PlaceTable.put("id", "integer primary key autoincrement");
        PlaceTable.put("PlaceName", "text");
        PlaceTable.put("PlaceCode", "integer");
        PlaceTable.put("CityId", "integer");
        sqLiteDatabase.execSQL(ConstructSQLCode(TABLE_PLACE, PlaceTable));
        System.out.println("log015: ?????????????");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean insertProvinceList(List<Province> mes) {
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            for (Province province : mes) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("ProvinceName", province.getName());
                contentValues.put("ProvinceCode", province.getCode());
                writableDatabase.insert(TABLE_PROVINCE, null, contentValues);
            }
            System.out.println("log015:insertProvinceList");
            return true;
        } catch (Exception e) {
            System.out.println("log015: " + e);
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @SuppressLint("Range")
    public List<String> retrieveAllProvinceNames() {
        List<String> allProvinceNameList = new LinkedList<>();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        @SuppressLint("Recycle") Cursor query = readableDatabase.query(TABLE_PROVINCE, new String[]{"ProvinceName"}, null, null, null, null, null);
        if (query != null) {
            while (query.moveToNext()) {
                String provinceName = query.getString(query.getColumnIndex("ProvinceName"));
                allProvinceNameList.add(provinceName);
            }
        }
        return allProvinceNameList;
    }

    public boolean insertCityList(List<City> mes) {
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            for (City city : mes) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("CityName", city.getName());
                contentValues.put("CityCode", city.getCode());
                contentValues.put("ProvinceId", city.getUpper_id());
                writableDatabase.insert(TABLE_CITY, null, contentValues);
            }
            System.out.println("log015:insertCityList");
            return true;
        } catch (Exception e) {
            System.out.println("log015: " + e);
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // 查找所有的省份
    public boolean insertPlaceList(List<Place> mes) {
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            for (Place place : mes) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("PlaceName", place.getName());
                contentValues.put("PlaceCode", place.getCode());
                contentValues.put("CityId", place.getUpper_id());
                writableDatabase.insert(TABLE_PLACE, null, contentValues);
            }
            System.out.println("log015:insertPlaceList");
            return true;
        } catch (Exception e) {
            System.out.println("log015: " + e);
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // 根据省份代号查城市
    @SuppressLint("Range")
    public List<String> retrieveNeededCity(String ProvinceName) {
        System.out.println("log015: ？？？？？？？？？？？？？？？？？？" + ProvinceName);
        List<String> allProvinceNameList = new LinkedList<>();
        try {
            SQLiteDatabase readableDatabase = getReadableDatabase();
            String provinceCode = "12";
            @SuppressLint("Recycle") Cursor provinceQuery = readableDatabase.query(TABLE_PROVINCE, new String[]{"ProvinceCode"}, "ProvinceName like ?", new String[]{ProvinceName}, null, null, null);
            if (provinceQuery != null) {
                provinceQuery.moveToNext();
                provinceCode = provinceQuery.getString(provinceQuery.getColumnIndex("ProvinceCode"));
                System.out.println("log015 ProvinceCode: " + provinceCode);
            }
            @SuppressLint("Recycle") Cursor query = readableDatabase.query(TABLE_CITY, new String[]{"CityName"}, "ProvinceId like ?", new String[]{provinceCode}, null, null, null);
            if (query != null) {
                while (query.moveToNext()) {
                    String provinceName = query.getString(query.getColumnIndex("CityName"));
                    allProvinceNameList.add(provinceName);
                }
                System.out.println("log015: " + allProvinceNameList);
            }
        } catch (Exception e) {
            System.out.println("log015: " + e);
        }


        return allProvinceNameList;
    }

    // 根据城市代号查地区
    @SuppressLint("Range")
    public List<String> retrieveNeededPlace(String CityName) {
        System.out.println("log015: ？？？？？？？？？？？？？？？？？？" + CityName);
        List<String> allPlaceNameList = new LinkedList<>();
        try {
            SQLiteDatabase readableDatabase = getReadableDatabase();
            String CityCode = "12";
            @SuppressLint("Recycle") Cursor cityQuery = readableDatabase.query(TABLE_CITY, new String[]{"CityCode"}, "CityName like ?", new String[]{CityName}, null, null, null);
            if (cityQuery != null) {
                cityQuery.moveToNext();
                CityCode = cityQuery.getString(cityQuery.getColumnIndex("CityCode"));
                System.out.println("log015 CityCode: " + CityCode);
            }
            @SuppressLint("Recycle") Cursor query = readableDatabase.query(TABLE_PLACE, new String[]{"PlaceName"}, "CityId like ?", new String[]{CityCode}, null, null, null);
            if (query != null) {
                while (query.moveToNext()) {
                    String placeName = query.getString(query.getColumnIndex("PlaceName"));
                    allPlaceNameList.add(placeName);
                }
                System.out.println("log015: " + allPlaceNameList);
            }
        } catch (Exception e) {
            System.out.println("log015: " + e);
        }
        return allPlaceNameList;
    }

    @SuppressLint({"Recycle", "Range"})
    public String retrieveSinglePlace(String PlaceName) {
        String PlaceCode = "";
        try {
            SQLiteDatabase readableDatabase = getReadableDatabase();
            Cursor query = readableDatabase.query(TABLE_PLACE, new String[]{"PlaceCode"}, "PlaceName like ?", new String[]{PlaceName}, null, null, null);
            if (query != null) {
                query.moveToNext();
                PlaceCode = query.getString(query.getColumnIndex("PlaceCode"));
            }

        } catch (Exception e) {
            System.out.println("log015: " + e);
        }
        return PlaceCode;
    }

}
