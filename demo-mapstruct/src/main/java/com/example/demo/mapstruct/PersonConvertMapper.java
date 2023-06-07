package com.example.demo.mapstruct;

import com.example.demo.dto.PersonDto;
import com.example.demo.entity.Person;
import org.mapstruct.MapMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.Map;

@Mapper(uses = ListUtil.class)
public interface PersonConvertMapper {

    PersonConvertMapper INSTANCE = Mappers.getMapper(PersonConvertMapper.class);


    @Mapping(target = "phoneList",expression = "java(ListUtil.stringToListString(personDto.getPhones(),separator))")
    Person dtoToEntity(PersonDto personDto,String separator);


    @Mapping(target = "phones",source = "phoneList",qualifiedByName = "listStringToString")
    PersonDto entityToDto(Person person);





}
