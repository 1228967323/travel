package com.itheima.travel.dao;

import com.itheima.travel.model.User;
import com.itheima.travel.util.JdbcUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class UserDao implements IUserDao {
    JdbcTemplate jdbcTemplate=new JdbcTemplate(JdbcUtils.getDataSource());
    @Override
    public boolean register(User user) {
        String sql="insert into tab_user values(?,?,?,?,?,?,?,?,?,?)";
        int update = 0;
        boolean flag=false;
        try {
            update = jdbcTemplate.update(sql, user.getUid(), user.getUsername(), user.getPassword(),
                    user.getName(), user.getBirthday(), user.getSex(), user.getTelephone(),
                    user.getEmail(), user.getStatus(), user.getCode());
        } catch (DataAccessException e) {
        }
        if (update>=1){
            flag=true;
        }
        return flag;
    }

    @Override
    public User register(String userName) {
        String sql="select *from tab_user where username=?";
        User user = null;
        try {
            user = jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<>(User.class), userName);
        } catch (DataAccessException e) {

        }
        return user;
    }

    @Override
    public User loginUserName(String userName) {
        String sql="select *from tab_user where username=?";
        User user=null;
        try {
            user = jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<>(User.class), userName);
        } catch (DataAccessException e) {

        }
        return user;
    }

}
