package com.teach.firstapp.javacode;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BaseSample {
    private static final String TAG = "BaseSample";

    public static void main(String... args) {

        int i = 0;
        int taskCounter = 0;
        boolean isReady = false;

        String name = new String("user");
        String userName = new String("user");

        Integer span = null;
        span = 3;
        span = new Integer(3);
        span = Integer.valueOf("3");
        span = Integer.valueOf(3);

        int[] numberArray = new int[4];
        String[] nameArray = new String[3];

        ArrayList<Integer> list = new ArrayList<Integer>(); // size?
        HashSet<Integer> set = new HashSet<Integer>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        SparseArray<Object> sparseArray = new SparseArray<Object>();

        System.out.println(TAG + " " + "equals: " + (i == taskCounter)); // right
        System.out.println(TAG + " " + "equals: " + (name == userName)); // WRONG!!!
        System.out.println(TAG + " " + "equals: " + (name.equals(userName))); // right, but...


        for (int j = 0; j < numberArray.length; j++) {
            System.out.println(j + " : " + numberArray[j]);
        }

        for (int number : numberArray) {
            System.out.println(number);
        }

        for(Integer integer : list){
            System.out.println(integer);
        }

    }

}
