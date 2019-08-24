package com.itheima.travel.service;

import com.itheima.travel.model.PageBean;
import com.itheima.travel.model.Route;
import com.itheima.travel.model.User;

public interface IFavoriteService {

    boolean isFavorite(String rid, int uid);

    void addFavorite(Route route, User user) throws Exception;

    PageBean getPageBean(User user,String curPage) throws Exception;

    PageBean findFavorite(String curPageStr,String routeName,String minPrice,String maxPrice);
}
