package com.example.demo;

import cn.hutool.core.collection.ListUtil;
import com.example.demo.dto.PersonDto;
import com.example.demo.entity.Person;
import com.example.demo.mapstruct.PersonConvertMapper;
import org.junit.Test;

public class PersonTest extends DemoMapstructApplicationTests{

    @Test
    public void test1(){
        PersonDto personDto = new PersonDto();
        personDto.setPhones("1,2,3,4");
        Person person = PersonConvertMapper.INSTANCE.dtoToEntity(personDto,",");
        System.out.println(person);
    }

    @Test
    public void test2(){
        Person person = new Person();
        person.setPhoneList(ListUtil.of("1,2,3"));
        PersonDto personDto = PersonConvertMapper.INSTANCE.entityToDto(person);
        System.out.println(personDto);
    }

    @Test
    public void test3(){

    }
}
