package com.itheima.travel.service;

import com.itheima.travel.model.PageBean;
import com.itheima.travel.model.Route;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface IRouteService {
    Map<String,List<Route>>  routeCareChoose() throws Exception;

    PageBean getPageBean(String cid, int curPage,String rname)throws Exception;

    Route findViewDetails(String rid) throws Exception;

    List<Route> findAllRoute();

}
