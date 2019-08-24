package com.itheima.travel.web.servlet;

import com.itheima.travel.exception.*;
import com.itheima.travel.factory.BeansFactory;
import com.itheima.travel.model.ResultInfo;
import com.itheima.travel.model.User;
import com.itheima.travel.service.ICategoryService;
import com.itheima.travel.service.IUserService;
import com.itheima.travel.util.Md5Util;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/concat")
public class UserServlet extends BaseServlet {
    IUserService userService = (IUserService) BeansFactory.createBeans("UserService");
    ICategoryService categoryService = (ICategoryService) BeansFactory.createBeans("CategoryService");

    public ResultInfo register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResultInfo resultInfo = null;
        //2.验证码验证
        //2.1获取用户输入的验证
        String userCheckCode = request.getParameter("check");
        //2.2服务器生成的验证码
        String serverCheckCode = (String) request.getSession().getAttribute("CHECKCODE_SERVER");
        //2.3校验
        if (serverCheckCode != null && !serverCheckCode.equalsIgnoreCase(userCheckCode)) {
            resultInfo = new ResultInfo(false, "验证码错误");
        } else {
            try {
                //3.获取数据并封装数据到User对象
                User user = new User();
                BeanUtils.populate(user, request.getParameterMap());
                //4.调用业务逻辑层注册用户
                boolean flag = userService.register(user, request.getContextPath());
                //5.获取注册结果
                if (flag) {
                    resultInfo = new ResultInfo(true);
                }
            } catch (UserNameNotNullException e) {
                resultInfo = new ResultInfo(false, e.getMessage());
            } catch (UserNameExistsException e) {
                resultInfo = new ResultInfo(false, e.getMessage());
            } catch (UserRehisterFailException e) {
                resultInfo = new ResultInfo(false, e.getMessage());
            } catch (Exception e) {
                //打印异常信息
                e.printStackTrace();
                //用户处理不了的异常，要去到友好页面
                throw new RuntimeException(e);
            }
        }
        //6.将resultInfo转换为json数据返回给客户端
        return resultInfo;
    }

    public ResultInfo login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResultInfo resultInfo = null;
        String code = request.getParameter("check");
        String serverCheckCode = (String) request.getSession().getAttribute("CHECKCODE_SERVER");
        if (serverCheckCode != null && !serverCheckCode.equalsIgnoreCase(code)) {
            resultInfo = new ResultInfo(false, "验证码错误");
        } else {
            try {
                String userName = request.getParameter("username");
                String password = request.getParameter("password");
                String powerLogin = request.getParameter("powerLogin");
                password = (Md5Util.encodeByMd5(password));
                User user = userService.loginUsername(userName, password);
                if (user != null) {
                    if ("on".equals(powerLogin)){
                        String username = user.getUsername();
                        String userPassword = user.getPassword();
                        Cookie cookie=new Cookie("user", username+"#"+userPassword);
                        cookie.setMaxAge(7*60*60*24);
                        cookie.setPath(request.getContextPath());
                        response.addCookie(cookie);
                    }
                    request.getSession().setAttribute("user", user);
                    resultInfo = new ResultInfo(true);
                }
            } catch (UserNameNotNullException e) {
                resultInfo = new ResultInfo(false, e.getMessage());
            } catch (UserNameNotFindException e) {
                resultInfo = new ResultInfo(false, e.getMessage());
            } catch (UserPasswordErrorException e) {
                resultInfo = new ResultInfo(false, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultInfo;
    }

    public ResultInfo headerInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResultInfo resultInfo = null;
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            resultInfo = new ResultInfo(false);
        } else {
            resultInfo = new ResultInfo(true, user, "");
        }
        return resultInfo;
    }

    public ResultInfo userExit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().invalidate();
        Cookie cookie=new Cookie("user","userExit");
        cookie.setPath(request.getContextPath());
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        response.sendRedirect(request.getContextPath() + "/login.html");
        return null;
    }

    public ResultInfo findAllCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResultInfo resultInfo = null;
        try {
            String dataJson = categoryService.findAllCategory();
            resultInfo = new ResultInfo(true, dataJson, "");
        } catch (Exception e) {
            resultInfo = new ResultInfo(false);
        }
        return resultInfo;
    }
}
