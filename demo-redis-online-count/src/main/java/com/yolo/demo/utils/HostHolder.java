package com.yolo.demo.utils;


import com.yolo.demo.domain.User;
import org.springframework.stereotype.Component;

/**
 * 线程隔离，用于替代session
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }


}

