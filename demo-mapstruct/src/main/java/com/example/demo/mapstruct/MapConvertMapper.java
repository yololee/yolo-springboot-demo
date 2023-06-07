package com.example.demo.mapstruct;

import org.mapstruct.MapMapping;
import org.mapstruct.Mapper;

import java.util.Date;
import java.util.Map;

@Mapper(componentModel = "spring", uses = {ConverterUtil.class})
public interface MapConvertMapper {

    @MapMapping(keyQualifiedByName = "getValue", valueDateFormat = "dd.MM.yyyy")
    Map<String, String> longDateMapToStringStringMap(Map<Long, Date> source);
}
