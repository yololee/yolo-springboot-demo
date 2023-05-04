package com.yolo.knife4j.base;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@Accessors(chain = true)
@ApiModel("响应参数")
@AllArgsConstructor
@NoArgsConstructor
public class ResultVo<T> implements Serializable {

    private static final long serialVersionUID = -8054007511410819665L;

    @ApiModelProperty(value = "响应状态码", example = "1", dataType = "Integer")
    private int code;

    // 是否成功标识.true表示成功,false表示失败
    @ApiModelProperty("success标识,true表示成功,false表示失败")
    private boolean success;

    // 操作成功时需要响应给客户端的响应数据
    @ApiModelProperty("响应信息")
    private String msg;


    @ApiModelProperty("响应数据")
    private T data;

    @ApiModelProperty("当前时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;


}
