package com.itheima.travel.service;

import com.itheima.travel.model.User;

import java.util.List;

public interface IUserService {
    boolean register(User user, String path) throws Exception;

    User loginUsername(String username, String password) throws Exception;

}
