package com.yolo.demo.dict;

import cn.hutool.core.lang.Dict;

public class DictDemo {
    public static void main(String[] args) {
        Dict dict = new Dict();
        dict.put("zhangsan",20);
        dict.put("lisi",21);
        dict.put("wangwu",22);
        System.out.println(dict);
    }
}
