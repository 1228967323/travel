package com.itheima.travel.dao;

import com.itheima.travel.model.Route;
import com.itheima.travel.model.RouteImg;

import java.util.List;
import java.util.Map;

public interface IRouteDao {
    List<Route> getPopularityListRoute();
    List<Route> getNewestListRoute();
    List<Route> getThemeListRoute();
    int getCount(String cid,String rname);
    List<Map<String,Object>> getRoutesByPage(String cid, int curPage, int pageSize,String rname);

    Map<String, Object> findViewDetails(String rid);

    List<RouteImg> findRouteImg(String rid);
}
