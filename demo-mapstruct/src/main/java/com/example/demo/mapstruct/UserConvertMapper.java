package com.example.demo.mapstruct;

import com.example.demo.anno.MappingIgnore;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.BasicEntity;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;



@Mapper
public interface UserConvertMapper {

    UserConvertMapper INSTANCE = Mappers.getMapper(UserConvertMapper.class);

//    @MappingIgnore
    @Mapping(target = "age", ignore = true) // 忽略id，不进行映射
//    @Mapping(target = "address",source = "address",defaultValue = "武汉")
//    @Mapping(target = "createTime",expression = "java(new java.util.Date())")
    User convert(UserDto userDto);


//    @Mapping(target = "address",source = "address",defaultValue = "武汉")
//    @Mapping(target = "createTime",expression = "java(new java.util.Date())")
//    void dto2Entity(UserDto userDto, @MappingTarget User user);


    @Mapping(target = "address",source = "address",defaultValue = "武汉")
    @Mapping(target = "createTime",source = "createTime",dateFormat = "yyyy-MM-dd")
    void dto2Entity2(UserDto userDto, @MappingTarget User user);


    @Mapping(target = "createTime",source = "entity.createTime",dateFormat = "yyyy-MM-dd")
    User dtoToEntity2(UserDto userDto, BasicEntity entity);

    @Mapping(target = "address",source = "address",defaultValue = "武汉")
    @Mapping(target = "createTime",source = "createTime",dateFormat = "yyyy-MM-dd")
    @Mapping(target = "personDto",source = "personDto")
    User dtoToEntity3(UserDto userDto);

}
