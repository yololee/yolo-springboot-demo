package com.yolo.demo.dto;

import com.yolo.demo.entity.User;
import com.yolo.demo.mapstruct.StringToListString;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import lombok.Data;

@Data
@AutoMapper(target = User.class,uses = StringToListString.class,reverseConvertGenerate = false)
public class UserDto {
    private String username;
    private int age;
    private boolean young;

    @AutoMapping(target = "educationList")
    private String educations;
}