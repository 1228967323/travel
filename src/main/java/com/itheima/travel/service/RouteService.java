package com.itheima.travel.service;

import com.itheima.travel.dao.IRouteDao;
import com.itheima.travel.factory.BeansFactory;
import com.itheima.travel.model.*;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteService implements IRouteService {
    IRouteDao routeDao= (IRouteDao) BeansFactory.createBeans("RouteDao");
    @Override
    public Map<String, List<Route>> routeCareChoose() throws Exception {
        HashMap<String,List<Route>> hm = new HashMap();
        List<Route> popularityListRoute = routeDao.getPopularityListRoute();
        hm.put("popularity", popularityListRoute);
        List<Route> newestListRoute = routeDao.getNewestListRoute();
        hm.put("newest", newestListRoute);
        List<Route> themeListRoute = routeDao.getThemeListRoute();
        hm.put("theme",themeListRoute);
        return hm;
    }
    @Override
    public PageBean getPageBean(String cid, int curPage,String rname)throws Exception{
        PageBean pageBean = new PageBean();
        //当前页
        pageBean.setCurPage(curPage);
        //每页大小,每页3条数据
        int pageSize = 3;
        pageBean.setPageSize(pageSize);
        //根据分类查询当前页数据列表---转换为动态获取数据库数据
        List<Map<String,Object>> mapList = routeDao.getRoutesByPage(cid,curPage,pageSize,rname);
        //将List<Map<String,Object>>类型转换为List<Route>数据
        List<Route> routeList = convertMapListToList(mapList);
        pageBean.setData(routeList);
        //获取当前分类总记录数---转换为动态获取数据库数据
        int count = routeDao.getCount(cid,rname);
        pageBean.setCount(count);
        //返回静态封装好的pageBean
        return pageBean;
    }
    //查看详情中
    @Override
    public Route findViewDetails(String rid) throws Exception {
        //多表联查得到一条数据所以需要放回Map<String,Object>集合
        Map<String, Object> viewDetailsMap = routeDao.findViewDetails(rid);
        //将得到的map集合对Route对象进行封装
        Route route=new Route();
        Seller seller=new Seller();
        Category category=new Category();
        BeanUtils.populate(route, viewDetailsMap);
        BeanUtils.populate(seller, viewDetailsMap);
        BeanUtils.populate(category, viewDetailsMap);
        //根据rid查询出该路线所有的详情图片
        List<RouteImg> routeImgList=routeDao.findRouteImg(rid);
        route.setCategory(category);
        route.setSeller(seller);
        route.setRouteImgList(routeImgList);
        return route ;
    }

    /**
     * 将List<Map<String,Object>>类型转换为List<Route>数据
     * @param mapList
     * @return List<Route>
     * @throws Exception
     */
    private List<Route> convertMapListToList(List<Map<String,Object>> mapList)throws Exception{
        List<Route> routeList = null;
        if(mapList!=null && mapList.size()>0){
            //实例routeList
            routeList = new ArrayList<Route>();
            //此处包含多表相同的数据,因此可以把他们的属性分别设置到对应的封装类对象
            for (Map<String,Object> map:mapList) {
                //实例Route,Category
                Route route = new Route();
                Category category = new Category();
                //封装数据
                BeanUtils.populate(route,map);
                BeanUtils.populate(category,map);
                //将category封装到route中
                route.setCategory(category);
                //将封装完成的route追加到routeList中
                routeList.add(route);
            }
        }
        return routeList;
    }
}
