package com.itheima.travel.dao;

import com.itheima.travel.model.Favorite;
import com.itheima.travel.model.Route;
import com.itheima.travel.model.User;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public interface IFavoriteDao {
    Favorite isFavorite(String rid, int uid);

    int addFavorite(JdbcTemplate jdbcTemplate, Favorite favorite);

    int updateRouteCount(JdbcTemplate jdbcTemplate, int rid);

    int getCount(User user);

    List<Map<String, Object>> findRoute(User user, int curPage, int PageSize);

    int getCount(String routeName, String minPrice, String maxPrice);

    List<Route> findFavorite(int pageSize, int curPage, String routeName, String minPrice, String maxPrice);
}
