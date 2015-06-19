package com.teach.firstapp.javacode.stat;

public class StaticExample {

    public static final double PI = 3.14;

    static Object commonObject;

    int i;

    public static int helperMethod() {
        return 1;
    }

    public static class InnerClass {
        int i;

        public InnerClass(int i) {
            this.i = i;
        }
    }
}
