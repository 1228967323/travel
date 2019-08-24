package com.itheima.travel.dao;

import com.itheima.travel.model.Category;
import com.itheima.travel.model.User;

import java.util.List;

public interface IUserDao {
    boolean register(User user);
    User register(String userName);
    User loginUserName(String username);
}
