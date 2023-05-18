package com.yolo.multi.datasource.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yolo.multi.datasource.domain.User;

/**
 * 数据服务层
 */
public interface UserService extends IService<User> {

    /**
     * 添加 User
     *
     * @param user 用户
     */
    void addUser(User user);
}