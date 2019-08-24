package com.itheima.travel.service;

import com.itheima.travel.dao.IUserDao;
import com.itheima.travel.exception.UserNameNotFindException;
import com.itheima.travel.exception.UserNameNotNullException;
import com.itheima.travel.exception.UserPasswordErrorException;
import com.itheima.travel.exception.UserRehisterFailException;
import com.itheima.travel.factory.BeansFactory;
import com.itheima.travel.model.User;
import com.itheima.travel.util.Md5Util;
import com.itheima.travel.util.UuidUtil;

public class UserService implements IUserService{
    IUserDao userDao= (IUserDao) BeansFactory.createBeans("UserDao");
    @Override
    public boolean register(User user, String path) throws Exception {
        String userName = user.getUsername().trim();
        if (userName==null||"".equals(userName)){
            throw new UserNameNotNullException("用户名不能为空");
        }
        //根据用户输入的用户名去查找数据库对应的用户
        User userRegister = userDao.register(userName);
        if (userRegister!=null){
            throw new UserNameNotNullException("该用户已存在");
        }
        //设置激活状态为激活状态
        user.setStatus("N");
        //激活码,用于激活
        user.setCode(UuidUtil.getUuid());
        //对密码加密(MD5加密，消息摘要第五版加密算法,不可逆的加密算法)
        user.setPassword(Md5Util.encodeByMd5(user.getPassword()));
        //进行用户注册
         boolean flag=userDao.register(user);
        //发送激活邮件,根据用户提供的注册邮箱发送激活邮件
         if (flag){
            // MailUtil.sendMail(user.getEmail(), "<a href='http://localhost:8080"+path+"/user?action=active&code="+user.getCode()+"'>用户激活<a>");
         }else {
             throw new UserRehisterFailException("注册失败");
         }
        return flag;
    }
    @Override
    public User loginUsername(String username ,String password) throws Exception {
        User user=userDao.loginUserName(username);
        if (username==null||"".equals(username)){
            throw new UserNameNotNullException("用户名不能为空");
        }
        if (user==null){
            throw new UserNameNotFindException("用户不存在");
        }
        if (password==""||!password.equals(user.getPassword())){
            throw new UserPasswordErrorException("密码错误");
        }
        return user;
    }

}
