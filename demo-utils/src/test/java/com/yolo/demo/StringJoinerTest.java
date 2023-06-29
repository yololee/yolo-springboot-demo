package com.yolo.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.StringJoiner;

@Slf4j
public class StringJoinerTest extends DemoUtilsApplicationTests{


    @Test
    public void test1(){
        StringJoiner sj = new StringJoiner(",");
        sj.add("aa").add("bb").add("cc");
        System.out.println(sj);
    }

    @Test
    public void test2(){
        StringJoiner sj = new StringJoiner(",","(",")");
        sj.add("aa").add("bb").add("cc");
        System.out.println(sj);
    }

    @Test
    public void test3(){
        StringJoiner sj1 = new StringJoiner(",","(",")");
        sj1.add("aa").add("bb").add("cc");
        StringJoiner sj2= new StringJoiner(",");
        sj2.add("11").add("22");

        StringJoiner merge = sj2.merge(sj1);
        System.out.println(merge);
    }

    @Test
    public void test4(){
        StringJoiner sjObj = new StringJoiner(",", "{", "}");
        //Add Element
        sjObj.add("AA").add("BB").add("CC").add("DD").add("EE");
        String output = sjObj.toString();
        System.out.println(output);
        //Create another StringJoiner
        StringJoiner otherSj = new StringJoiner(":", "(", ")");
        otherSj.add("10").add("20").add("30");
        System.out.println(otherSj);
        //Use StringJoiner.merge(StringJoiner o)
        StringJoiner finalSj = sjObj.merge(otherSj);
        System.out.println(finalSj);
        //get length using StringJoiner.length()
        System.out.println("Length of Final String:"+finalSj.length());
    }
}
