package com.itheima.travel.web.filter;
import com.itheima.travel.factory.BeansFactory;
import com.itheima.travel.model.User;
import com.itheima.travel.service.IUserService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
@WebFilter("/*")
public class AutoLoginFilter implements Filter {
    IUserService userService= (IUserService) BeansFactory.createBeans("UserService");
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user==null){
            Cookie[] cookies = request.getCookies();
            if (cookies!=null&&cookies.length>0){
                for (Cookie cookie : cookies) {
                    if ("user".equals(cookie.getName())){
                        String value = cookie.getValue();
                        String[] split = value.split("#");
                        String username = split[0];
                        String password = split[1];
                        try {
                            User user1 = userService.loginUsername(username, password);
                            session.setAttribute("user", user1);
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
