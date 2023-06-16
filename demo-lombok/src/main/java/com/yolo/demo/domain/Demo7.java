package com.yolo.demo.domain;

import lombok.SneakyThrows;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Demo7 {

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        Integer data = getData(list);
        System.out.println(data);
    }

    @SneakyThrows(IndexOutOfBoundsException.class)
    public static Integer getData(List<Integer> list) {
        return list.get(2);
    }
}
