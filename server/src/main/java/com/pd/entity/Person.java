package com.pd.entity;


import java.util.ArrayList;

public class Person {
    public String name;
    private String pwd;
    public Integer age;

    public ArrayList<String> arrayList =new ArrayList<>();

    public Person() {

    }
    public Person(String name, String pwd, Integer age) {
        this.name = name;
        this.pwd = pwd;
        this.age = age;
    }



    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                ", age=" + age +
                '}';
    }
}
