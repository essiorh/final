package com.teach.firstapp.javacode.simpleclass;

public class BaseClass implements IHuman {

    int age; // private, protected, public
    String name;
    boolean isAlive;
    PaymentCalculator calculator;

    public BaseClass(int age, String name) {
        this.age = age;
        this.name = name;
        isAlive = true;

        calculator = new PaymentCalculator() {
            @Override
            public int getPayment() {
                return 100 + 200 + 300;
            }
        };
    }

    public int getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setName(int personalCode) {
//        some code
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }
}
