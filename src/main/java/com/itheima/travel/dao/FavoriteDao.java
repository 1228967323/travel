package com.itheima.travel.dao;

import com.itheima.travel.model.Favorite;
import com.itheima.travel.model.Route;
import com.itheima.travel.model.User;
import com.itheima.travel.util.JdbcUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoriteDao implements IFavoriteDao {
    JdbcTemplate jdbcTemplate=new JdbcTemplate(JdbcUtils.getDataSource());
    @Override
    public Favorite isFavorite(String rid, int uid) {
        String sql="select * from tab_favorite where rid=? and uid=?";
        Object[] Object={rid,uid};
        Favorite favorite = null;
        try {
            favorite = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Favorite.class), Object);
        } catch (DataAccessException e) {

        }
        return favorite;
    }

    @Override
    public int addFavorite(JdbcTemplate jdbcTemplate, Favorite favorite) {
        String sql="insert into tab_favorite values(?,?,?)";
        int update = jdbcTemplate.update(sql, favorite.getRoute().getRid(), favorite.getDate(), favorite.getUser().getUid());
        return update;
    }

    @Override
    public int updateRouteCount(JdbcTemplate jdbcTemplate, int rid) {
        String sql="update tab_route set count=count+1 where rid=?";
        int update = jdbcTemplate.update(sql, rid);
        return update;
    }

    @Override
    public int getCount(User user) {
            String sql="select count(*) from tab_favorite where uid=?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class,user.getUid());
            return count;
    }

    @Override
    public List<Map<String, Object>> findRoute(User user, int curPage, int pageSize) {
        String sql="select *from tab_route r,tab_favorite f,tab_user u where r.rid=f.rid and f.uid=? limit ?,?";
        int begin=(curPage-1)*pageSize;
        int end=pageSize;
        List<Map<String, Object>> list = null;
        try {
            list = jdbcTemplate.queryForList(sql, user.getUid(), begin, end);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int getCount(String routeName,String minPrice,String maxPrice) {
        StringBuilder stringBuilder=new StringBuilder("select count(*) from tab_route where rflag=1");
        List<Object> list=new ArrayList<>();
        if (routeName!=null&&!"".equals(routeName.trim())&&!"null".equals(routeName)){
            stringBuilder.append(" and rname like ?");
            list.add("%"+routeName+"%");
        }
        if (minPrice!=null&&!"".equals(minPrice.trim())&&!"null".equals(minPrice)){
            stringBuilder.append(" and price >= ?");
            list.add(minPrice);
        }
        if (maxPrice!=null&&!"".equals(maxPrice.trim())&&!"null".equals(maxPrice)){
            stringBuilder.append(" and price <= ?");
            list.add(maxPrice);
        }
        Object[] params = list.toArray();

        Integer count = jdbcTemplate.queryForObject(stringBuilder.toString(),params ,Integer.class);
        return count;
    }

    @Override
    public List<Route> findFavorite(int pageSize, int curPage, String routeName, String minPrice, String maxPrice) {
        List<Route> list = null;
        StringBuilder stringBuilder=new StringBuilder("select * from tab_route where rflag=1");
        List<Object> paramsList=new ArrayList<>();
        if (routeName!=null&&!"".equals(routeName.trim())&&!"null".equals(routeName)){
            stringBuilder.append(" and rname like ?");
            paramsList.add("%"+routeName+"%");
        }
        if (minPrice!=null&&!"".equals(minPrice.trim())&&!"null".equals(minPrice)){
            stringBuilder.append(" and price >= ?");
            paramsList.add(minPrice);
        }
        if (maxPrice!=null&&!"".equals(maxPrice.trim())&&!"null".equals(maxPrice)){
            stringBuilder.append(" and price <= ?");
            paramsList.add(maxPrice);
        }
        stringBuilder.append(" order by count desc limit ?,?");
        int start=(curPage-1)*pageSize;
        int end=pageSize;
        paramsList.add(start);
        paramsList.add(end);
        Object[] params = paramsList.toArray();
        try {
            list = jdbcTemplate.query(stringBuilder.toString(), new BeanPropertyRowMapper<>(Route.class),params);
        } catch (DataAccessException e) {

        }
        return list;
    }
}
