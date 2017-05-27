package xyz.miffle.returnhome.server.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by dsm on 2017-04-28.
 */
public class Data {

    private static final String TAG = "DATA";

    public static HashMap<String, Object> jsonToHashmap(String json) {
        if (json != null) {
            return jsonToHashmap(json);
        }

        return null;
    }

    public static HashMap<String, String> jsonToStrHashmap(JSONObject json) {

        if (json != null) {
            HashMap<String, String> result = new HashMap<>();

            Iterator<String> keySet = json.keySet().iterator();

            while(keySet.hasNext()) {
                String key = keySet.next();
                result.put(key, json.get(key).toString());
            }

            return result;
        }

        return null;
    }

    public static HashMap<String, Object> jsonToHashmap(JSONObject json) {

        if (json != null) {
            HashMap<String, Object> result = new HashMap<>();

            Iterator<String> keySet = json.keySet().iterator();

            while(keySet.hasNext()) {
                String key = keySet.next();
                result.put(key, json.get(key));
            }

            return result;
        }

        return null;
    }

    public static boolean hasCorrectKeys(HashMap<String, String> map, String[] keys) {

        if (keys == null || map == null) {
            return false;
        }

        boolean failed = false;

        for(String key : keys) {
            if(map.get(key) == null) {
                System.out.println("Null!");
                failed = true;
            }
        }

        return !failed;
    }

    public static ArrayList<HashMap<String, String>> resultSetToListHashMap(ResultSet result) {

        if (result == null) {
            return null;
        }

        ResultSetMetaData metaData = null;
        try {
            metaData = result.getMetaData();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        int sizeOfColumn = 0;

        try {
            sizeOfColumn = metaData.getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            while (result.next()) {
                HashMap<String, String> hashMap = new HashMap<String, String>();

                for (int i = 0; i < sizeOfColumn; i++) {
                    String column = metaData.getColumnName(i + 1);

                    hashMap.put(column, result.getString(column));
                }
                list.add(hashMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static JSONObject resultSetToJson(ResultSet result, String keyName) {

        if (result == null || keyName == null) {
            return null;
        }

        ResultSetMetaData metaData = null;
        try {
            metaData = result.getMetaData();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        int sizeOfColumn = 0;

        try {
            sizeOfColumn = metaData.getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        JSONArray array = new JSONArray();
        try {
            while (result.next()) {
                JSONObject jsonObject = new JSONObject();

                for (int i = 0; i < sizeOfColumn; i++) {
                    String column = metaData.getColumnName(i + 1);
                    jsonObject.put(column, result.getString(column));
                }
                array.add(jsonObject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(keyName, array);

        return jsonObject;
    }
}