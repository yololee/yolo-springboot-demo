package com.yolo.jackson.domain;

import com.fasterxml.jackson.annotation.*;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@JsonPropertyOrder(value={"name", "age"})
public class Model {
    @JsonInclude(value= JsonInclude.Include.NON_NULL)
    private Integer id;
    @JsonIgnore
    private int age;
    @JsonProperty("myName")
    private String name;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private Date createTime;
}


