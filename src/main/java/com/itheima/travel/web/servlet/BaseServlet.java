package com.itheima.travel.web.servlet;

import com.google.gson.Gson;
import com.itheima.travel.model.ResultInfo;
import com.sun.media.jfxmedia.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
/**
 * 继承此类可以做到一个Servlet处理请求,只要客户端携带了action参数
 * 子类可以将该参数当成一个方法名.通过反射机制调用该方法.
 */
public class BaseServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取从客户端发送过来的action参数
        String action = request.getParameter("action");
        Class clazz = this.getClass();
        try {
            //根据参数值获取用户需要访问的方法
            Method declaredMethod = clazz.getDeclaredMethod(action, HttpServletRequest.class, HttpServletResponse.class);
            //暴力反射可以调用私有方法
            declaredMethod.setAccessible(true);
            //优化ResultInfo携带到客户端的方法
            ResultInfo resultInfo = (ResultInfo) declaredMethod.invoke(this, request, response);
            if (resultInfo!=null){
                WriteJson(response,resultInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void WriteJson(HttpServletResponse response, ResultInfo resultInfo) throws IOException {
        response.getWriter().write(new Gson().toJson(resultInfo));
    }
}
