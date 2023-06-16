package com.yolo.demo.domain;

public class Person {

    public void personMsg() {
        System.out.println("Person.personMsg");
    }

    public String printName(String name) {
        return name;
    }

    private Integer printAge(Integer age) {
        return age;
    }

    public static void printOther() {
        System.out.println("Person.printOther");
    }
}
