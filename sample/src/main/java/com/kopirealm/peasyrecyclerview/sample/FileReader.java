package com.kopirealm.peasyrecyclerview.sample;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileReader {

    public static JSONObject readRawJsonObject(Context context, int id) {
        try {
            return new JSONObject(readRawFile(context, id).trim());
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONArray readRawJsonArray(Context context, int id) {
        try {
            return new JSONArray(readRawFile(context, id).trim());
        } catch (Exception e) {
            return null;
        }
    }

    public static String readRawFile(Context context, int id) {
        final InputStream inputStream = context.getResources().openRawResource(id);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteArrayOutputStream.toString();
    }
}
