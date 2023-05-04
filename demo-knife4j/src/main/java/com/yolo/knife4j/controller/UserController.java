package com.yolo.knife4j.controller;


import cn.hutool.core.collection.ListUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;

import com.yolo.knife4j.base.ResultVo;
import com.yolo.knife4j.dto.UserAddRequest;
import com.yolo.knife4j.dto.UserDTO;
import com.yolo.knife4j.vo.StudentVO;
import com.yolo.knife4j.vo.UserVO;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@ApiResponses(value = {
        @ApiResponse(code = 200, message = "接口返回成功状态"),
        @ApiResponse(code = 500, message = "接口返回未知错误，请联系开发人员调试")
})
@Api(tags = "用户")
@ApiSupport(author = "yolo-controller")
@RestController
@RequestMapping("/user")
public class UserController {


//    @PostMapping(value = "/saveUser")
//    @ApiOperation("新增用户信息-表单")
//    @ApiOperationSupport(author = "yolo",ignoreParameters = {"id","userAddressDTO.id"})
//    public String saveUser(UserDTO userDTO){
//        System.out.println("前端传递的用户信息："+ userDTO);
//        return "save success";
//    }
//    @PostMapping(value = "/updateUser")
//    @ApiOperation("编辑用户信息")
//    @ApiOperationSupport(author = "yolo")
//    public String updateUser( UserDTO userDTO){
//        System.out.println("前端传递的用户信息："+ userDTO);
//        return "edit success";
//    }


    @PostMapping(value = "/saveUser")
    @ApiOperation("新增用户信息")
    @ApiOperationSupport(author = "yolo",ignoreParameters = {"userDTO.id","userDTO.userAddressDTO.id"})
    public String saveUser(@RequestBody UserDTO userDTO){
        System.out.println("前端传递的用户信息："+ userDTO);
        return "save success";
    }
    @PostMapping(value = "/updateUser")
    @ApiOperation("编辑用户信息")
    @ApiOperationSupport(author = "yolo")
    public String updateUser(@RequestBody UserDTO userDTO){
        System.out.println("前端传递的用户信息："+ userDTO);
        return "save success";
    }


    @ApiOperationSupport(author = "yolo-test")
    @ApiOperation(value = "保存用户", notes = "简单传参")
    @PostMapping("/add")
    public ResultVo<Object> add(@RequestBody @Valid UserAddRequest userAddRequest) {
        return ResultVo.builder().build().setCode(200).setSuccess(true)
                .setTime(new Date()).setMsg("保存用户成功").setData(userAddRequest);
    }

    @ApiOperation(value = "保存用户2", notes = "复杂传参")
    @PostMapping("/add2")
    public ResultVo<Object> add2(@RequestBody @Valid UserAddRequest userAddRequest) {
        return ResultVo.builder().build().setCode(200).setSuccess(true)
                .setTime(new Date()).setMsg("保存用户成功").setData(userAddRequest);
    }


    @GetMapping("/list")
    @ApiOperation(value = "查找用户列表", notes = "根据id查找单个用户")
    public ResultVo<Object> list(@RequestParam @ApiParam(value = "当前页", defaultValue = "1") Integer pageNum,
                          @RequestParam @ApiParam(value = "页大小", defaultValue = "10") Integer pageSize) {

        UserVO vo = new UserVO();
        StudentVO studentVO = StudentVO.builder().build().setAddress("wuhan").setCode(2);
        vo.setStudentVOS(ListUtil.of(studentVO));

        return ResultVo.builder().build().setCode(200).setSuccess(true)
                .setTime(new Date()).setMsg("查找用户列表").setData(vo);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除用户", notes = "删除后无法恢复")
    @ApiImplicitParam(name = "id", value = "用户ID", defaultValue = "1")
    public ResultVo<Object> delete(@PathVariable(name = "id") Long id) {
        return ResultVo.builder().build().setCode(200).setSuccess(true)
                .setTime(new Date()).setMsg("删除用户");
    }

    @DeleteMapping()
    @ApiOperation(value = "批量删除用户", notes = "删除后无法恢复")
    public ResultVo<Object> delete2(@RequestBody List<Integer> ids) {
        return ResultVo.builder().build().setCode(200).setSuccess(true)
                .setTime(new Date()).setMsg("删除用户");
    }
}