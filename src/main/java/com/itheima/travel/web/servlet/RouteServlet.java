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
import java.io.IOException;
import java.util.List;
import java.util.Map;
@WebServlet("/route")
public class RouteServlet extends BaseServlet {
    IRouteService routeService= (IRouteService) BeansFactory.createBeans("RouteService");
    IFavoriteService favoriteService= (IFavoriteService) BeansFactory.createBeans("FavoriteService");
    public ResultInfo chooseCareRoute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultInfo resultInfo=null;
        try {
            Map<String, List<Route>> routeMap = routeService.routeCareChoose();
            resultInfo=new ResultInfo(true,routeMap,"");
        } catch(Exception e) {
           resultInfo=new ResultInfo(false);
        }
        return resultInfo;
    }
    public ResultInfo page(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResultInfo resultInfo=null;
        try {
            //当用户首次进入该页面时,设置当前页面的数据默认为1
            int curPage=1;
            String cid = request.getParameter("cid");
            //当用户点击该页面的分页条时获取用户点击的页数
            String curPageStr = request.getParameter("curPage");
            String rname = request.getParameter("rname");
            if (curPageStr!=null&&!"".equals(curPageStr)){
                //把用户点击的页数设置成为当前页
                curPage = Integer.parseInt(curPageStr);
            }
            //把获取的到cid和当前页数传入到getPageBean中,返回得到一个pageBean对象
            PageBean pageBean = routeService.getPageBean(cid, curPage,rname);
            resultInfo=new ResultInfo(true,pageBean,"");
        } catch(Exception e) {
            e.printStackTrace();
            resultInfo=new ResultInfo(false,"服务器异常");
        }
        return  resultInfo;
    }

    /**
     * 当用户点击查看详情时,需要将该详情路线的rid带入到服务器中
     *因为路线详情只是该路线的详情信息
     * @param request
     * @param response
     * @return
     */
    public ResultInfo viewDetails(HttpServletRequest request,HttpServletResponse response){
        ResultInfo resultInfo=null;
        String rid = request.getParameter("rid");
        Route route=new Route();
        try {
            //进行三表联查得到一条数据,并将该查询出来的结果封装到Route中
            route=routeService.findViewDetails(rid);
            resultInfo=new ResultInfo(true,route,"");
        } catch (Exception e) {
            e.printStackTrace();
            //
            resultInfo=new ResultInfo(false,"服务器异常");
        }
        return resultInfo;
    }
    public ResultInfo isFavorite(HttpServletRequest request,HttpServletResponse response){
        ResultInfo resultInfo=null;
        User user = null;
        try {
            user = (User) request.getSession().getAttribute("user");
            if (user==null) {
                resultInfo=new ResultInfo(true,false,"");
            }else {
                String rid = request.getParameter("rid");
                boolean isFavorite=favoriteService.isFavorite(rid,user.getUid());
                resultInfo=new ResultInfo(true,isFavorite,"");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo=new ResultInfo(true,"服务器异常啦");
        }
        return resultInfo;
    }
    public ResultInfo findAll(HttpServletRequest request,HttpServletResponse response){
        List<Route> allRoute = routeService.findAllRoute();
        return new ResultInfo(true,allRoute,"查询成功!");
    }
}
