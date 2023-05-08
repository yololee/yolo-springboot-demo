package com.yolo.mybatis.mapper;


import com.yolo.mybatis.dto.UserDTO;
import com.yolo.mybatis.pojo.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User Mapper
 */
@Mapper
//实现实体和数据表的映射关系可以在Mapper类上添加@Mapper注解
//但是建议以后直接在SpringBoot启动类中加 @MapperScan("com.yolo.mybatis.mapper")注解，这样会比较方便，不需要对每个Mapper都添加@Mapper注解。
@Repository
public interface UserMapper {

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @Select("SELECT * FROM orm_user")
    List<User> selectAllUser();

    /**
     * 根据id查询用户
     *
     * @param id 主键id
     * @return 当前id的用户，不存在则是 {@code null}
     */
    @Select("SELECT * FROM orm_user WHERE id = #{id}")
    User selectUserById(@Param("id") Long id);


    /**
     * 根据id批量查询
     * @param ids 主键id集合
     * @return 用户列表
     */
    @Select("<script> SELECT * FROM orm_user " +
            " WHERE id in " +
            " <foreach collection='list' item='id' index='index' open='(' separator=',' close=')' >" +
            " #{id}" +
            " </foreach>" +
            " AND status != -1 " +
            " </script>")
    List<User> selectByIds(@Param("list") List<Integer> ids);

    /**
     * 多条件查询
     * @param userDTO 查询条件
     * @return 用户列表
     */
    @Select("<script> SELECT * from orm_user "  +
            "<where>" +
            "<if test= \" item.name != null and item.name != '' \"> name like '%" + "${item.name}" + "%'</if>" +
            "<if test= \" item.phoneNumber != null and item.phoneNumber != ''\"> phoneNumber =#{item.phoneNumber}</if>" +
            " AND status = 1 " +
            "</where>  ORDER BY id  DESC </script>")
    List<User> selectPage(@Param("item") UserDTO userDTO);

    /**
     * 保存用户
     * useGeneratedKeys属性表示使用自增主键
     * keyProperty属性是Java包装类对象的属性名
     * keyColumn属性是mysql表中的字段名
     * @param user 用户
     * @return 成功 - {@code 1} 失败 - {@code 0}
     */
    @Insert("insert into orm_user (name,password,salt,email,phone_number,status,create_time,last_login_time,last_update_time)" +
            " VALUES (#{user.name},#{user.password},#{user.salt},#{user.email},#{user.phoneNumber},#{user.status}," +
            " #{user.createTime},#{user.lastLoginTime},#{user.lastUpdateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id" , keyColumn = "id")
    int saveUser(@Param("user") User user);

    /**
     * 批量插入
     * @param userList
     */
    @Insert({
            "<script>",
            "INSERT INTO orm_user (name,password,salt,email,phone_number,status,create_time,last_login_time,last_update_time)  VALUES",
            "<foreach collection='list' item='item' index='index' separator=','>",
            "( #{item.name},#{item.password},#{item.salt},#{item.email},#{item.phoneNumber},#{item.status}," +
                    "#{item.createTime},#{item.lastLoginTime},#{item.lastUpdateTime})",
            "</foreach>",
            "</script>"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertList(@Param("list")List<User> userList);


    @Update({"<script> UPDATE orm_user SET status =#{status}"
            + " WHERE status !=-1 and id IN "
            + "<foreach  collection = 'ids' item = 'id' index = 'index' open = '(' separator= ',' close = ')' >"
            + "	#{id} "
            + "</foreach>"
            + "</script>"})
    int updateBatchStateById(@Param("ids") List<Integer> ids, @Param("status") Integer status);


    @Update("<script>" +
            "<foreach collection = 'list' item ='item' open='' close='' separator=';'>" +
            " update orm_user set " +
            "<if test= \" item.name != null and item.name != '' \"> name =#{item.name} ,</if>" +
            "<if test= \" item.email != null and item.email != '' \"> email =#{item.email} </if>" +
            " where id=#{item.id} " +
            "</foreach>" +
            "</script>")
    int updateBatch(@Param("list")List<User> userList);


    /**
     * 删除用户
     *
     * @param id 主键id
     * @return 成功 - {@code 1} 失败 - {@code 0}
     */
    @Delete("delete from orm_user where id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 批量删除
     * @param ids 主键id集合
     */
    @Delete({"<script> DELETE FROM orm_user  WHERE id IN "
            + "<foreach  collection = 'ids' item = 'id' index = 'index' open = '(' separator= ',' close = ')' >"
            + "	#{id} "
            + "</foreach>"
            + "</script>"})
    int batchDelete(@Param("ids") List<Integer> ids);


}