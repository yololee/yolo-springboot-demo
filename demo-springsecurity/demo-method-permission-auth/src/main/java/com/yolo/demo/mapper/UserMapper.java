package com.yolo.demo.mapper;

import com.yolo.demo.domain.User;
import org.apache.ibatis.annotations.Mapper;


/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper {
   //根据id删除
    int deleteByPrimaryKey(Integer uid);

   //新增用户
    int insert(User record);

    //根据id查询用户
    User selectByPrimaryKey(Integer uid);

    //根据用户名查询
    User selectBYUserName(String username);


}