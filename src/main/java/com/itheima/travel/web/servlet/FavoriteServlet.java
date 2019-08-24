package com.itheima.travel.web.servlet;
import com.itheima.travel.factory.BeansFactory;
import com.itheima.travel.model.PageBean;
import com.itheima.travel.model.ResultInfo;
import com.itheima.travel.model.Route;
import com.itheima.travel.model.User;
import com.itheima.travel.service.IFavoriteService;
import com.itheima.travel.service.IRouteService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/favorite")
public class FavoriteServlet extends BaseServlet {
    IFavoriteService favoriteService= (IFavoriteService) BeansFactory.createBeans("FavoriteService");
    IRouteService routeService= (IRouteService) BeansFactory.createBeans("RouteService");

    /**
     * 可以从客户端带来的属性有:
     * route中的rid属性
     * 检验当前路线是否可以编辑操作.
     * 1.当用户没有登录时,可以编辑
     * 2.
     * @param request
     * @param response
     * @return
     */
    public ResultInfo addFavorite(HttpServletRequest request, HttpServletResponse response){
        ResultInfo resultInfo=null;
        try {
            //判断用户是否登录
            User user = (User) request.getSession().getAttribute("user");
            if (user==null){
                resultInfo=new ResultInfo(true,false,"");
            }else {
                //获取用户选择要收藏路线的id属性
                String rid = request.getParameter("rid");
                Route route=new Route();
                route.setRid(Integer.parseInt(rid));
                favoriteService.addFavorite(route,user);
                Route viewDetailsRoute = routeService.findViewDetails(rid);
                //从route表中中查看count值,并将该值带回到客户端中
                int count = viewDetailsRoute.getCount();
                resultInfo=new ResultInfo(true,count,"");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo=new ResultInfo(false,"服务器异常");
        }
        return resultInfo;
    }
    public ResultInfo getPageBean(HttpServletRequest request, HttpServletResponse response){
        ResultInfo resultInfo=null;
        try {
            //获取已经登录的用户的账号信息
            User user = (User) request.getSession().getAttribute("user");
            //获取收藏页中用户选择的当前页
            String curPage = request.getParameter("curPage");
            //将用户的收藏页信息封装成一个PageBean中
            PageBean pageBean=favoriteService.getPageBean(user,curPage);
            //将已经封装好用户收藏页的PageBean带入到客户端进行展示
            resultInfo=new ResultInfo(true,pageBean,"");
        } catch(Exception e) {
            e.printStackTrace();
            resultInfo=new ResultInfo(false,"服务器异常");
        }
        return resultInfo;
    }
    public ResultInfo findFavorite(HttpServletRequest request, HttpServletResponse response){
        ResultInfo resultInfo=null;
        String curPage = request.getParameter("curPage");
        String routeName = request.getParameter("routeName");
        String minPrice = request.getParameter("minPrice");
        String maxPrice = request.getParameter("MaxPrice");
        try {
            PageBean pageBean=favoriteService.findFavorite(curPage,routeName,minPrice,maxPrice);
            resultInfo=new ResultInfo(true,pageBean,"");
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo=new ResultInfo(false,"服务器异常");
        }
        return resultInfo;
    }
}
