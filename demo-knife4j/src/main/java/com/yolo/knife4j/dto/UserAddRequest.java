package com.yolo.knife4j.dto;



import com.yolo.knife4j.vo.StudentVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@ApiModel("添加用户")
public class UserAddRequest {
    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    private String userName;
 
    @ApiModelProperty("昵称")
    private String nickName;
 
    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("学生集合")
    private List<StudentVO> studentVOS;
}