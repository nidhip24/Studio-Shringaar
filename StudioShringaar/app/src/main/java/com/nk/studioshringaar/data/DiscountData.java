package com.nk.studioshringaar.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class DiscountData {

    private Context c;
    private String key = "discount";
    private String PREF = "discount";
    private static final String TAG = "DiscountData";

    public DiscountData(Context c) {
        this.c = c;
    }

    public void addDiscount(ArrayList<Discount> data){
        // save the task list to preference
        SharedPreferences prefs = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            String dddd = convertToString(data);
            editor.putString(key, dddd);
            editor.apply();
            Log.e(TAG, "String in disocunt " + dddd);
            Log.e(TAG, "arraylist saved succesfully");
        } catch (Exception e) {
            Log.e(TAG, "error");
            e.printStackTrace();
        }
        editor.commit();
    }

    public ArrayList<Discount> getDiscount() {
        ArrayList<Discount> temp = new ArrayList<Discount>();
        // load tasks from preference
        SharedPreferences prefs = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        try {
            String data = prefs.getString(key, null);
            Log.e(TAG, "retrived data " + data);
            if (data != null) {
                temp = convertToOriginal(data);
                Log.e(TAG, " listin discount " + temp);
                return temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return temp;
        }
        return temp;
    }

    private String convertToString(ArrayList<Discount> d){
        String convertedString = "";
        for (int i = 0; i<d.size(); i++) {
            Discount temp = d.get(i);
            if (i!=0){
                convertedString+="@";
            }
            convertedString += "id=" + temp.getId();
            convertedString += "~name=" + temp.getName();
            convertedString += "~cat=" + temp.getCat();
            convertedString += "~start=" + temp.getStartDate();
            convertedString += "~end=" + temp.getEndDate();
            convertedString += "~discount=" + temp.getDiscount();
            convertedString += "~maxdiscount=" + temp.getMaxDiscount();
        }
        return convertedString;
    }

    private ArrayList<Discount> convertToOriginal(String d) {
        ArrayList<Discount> temp = new ArrayList<Discount>();
        String[] str = d.split("@");
        Log.e(TAG, " array skzw " + str );
        for (int i=0; i < str.length; i++){
            String value[] = str[i].split("~");
//            Log.e(TAG, "i = "+ str[i] );
////            for (int ii=0; ii < value.length; ii++) {
////
////            }
            String[] id = value[0].split("=");
            String[] name = value[1].split("=");
            String[] cat = value[2].split("=");
            String[] start = value[3].split("=");
            String[] end = value[4].split("=");
            String[] discount = value[5].split("=");
            String[] maxdiscount = value[6].split("=");
            temp.add(new Discount(id[1], name[1], cat[1], start[1], end[1], Long.parseLong(discount[1]), Long.parseLong(maxdiscount[1])) );
        }
        return temp;
    }
}
