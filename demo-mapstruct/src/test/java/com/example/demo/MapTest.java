package com.example.demo;

import com.example.demo.mapstruct.MapConvertMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MapTest extends DemoMapstructApplicationTests{


    @Autowired
    private MapConvertMapper mapConvertMapper;

    @Test
    public void test1(){
        Map<Long, Date> map = new HashMap<>();
        map.put(1685548800000L,new Date());

        Map<String, String> map1 = mapConvertMapper.longDateMapToStringStringMap(map);
        System.out.println(map1);

    }
}
