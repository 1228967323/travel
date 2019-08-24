package com.itheima.travel.dao;

import com.itheima.travel.model.Route;
import com.itheima.travel.model.RouteImg;
import com.itheima.travel.util.JdbcUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RouteDao implements IRouteDao {
    JdbcTemplate jdbcTemplate=new JdbcTemplate(JdbcUtils.getDataSource());
    @Override
    //最受欢迎的路线
    public List<Route> getPopularityListRoute() {
        String sql="select *from tab_route where rflag=1 order by count desc limit 0,4";
        List<Route> PopularityListRouteList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Route>(Route.class));
        return PopularityListRouteList;
    }
    //最新路线
    @Override
    public List<Route> getNewestListRoute() {
        String sql="select *from tab_route where rflag=1 order by rdate desc limit 0,4";
        List<Route> NewestListRouteList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Route>(Route.class));
        return NewestListRouteList;
    }
    //主题路线
    @Override
    public List<Route> getThemeListRoute() {
        String sql="select *from tab_route where rflag=1 order by isThemeTour desc limit 0,4";
        List<Route> ThemeListRouteList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Route>(Route.class));
        return ThemeListRouteList;
    }
    @Override
    public int getCount(String cid,String rname) {
        StringBuilder builder=new StringBuilder("select count(*) from tab_route where rflag=1");
        ArrayList<Object> paramsList = new ArrayList();
        if (cid!=null&&!"".equals(cid.trim())){
            builder.append(" and cid=?");
            paramsList.add(cid);
        }
        if (rname!=null&&!"".equals(rname.trim())&&!"null".equals(rname)){
            builder.append(" and rname like ?");
            try {
                paramsList.add("%"+URLDecoder.decode(rname, "utf-8")+"%");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Object[] params = paramsList.toArray();
        Integer count = jdbcTemplate.queryForObject(builder.toString(), params, Integer.class);
        return count;
    }
    @Override
    public List<Map<String, Object>> getRoutesByPage(String cid, int curPage, int pageSize,String rname) {
        //查询所有相匹配的数据.当用户名点击的导航id查询符合的路线图
        //此处涉及到多表查询,所以返回一个List<Map<String,Object>>
        String sql="SELECT * FROM tab_route r,tab_category c WHERE c.cid=r.cid AND r.cid=? AND r.rflag='1' LIMIT ?,?";
        StringBuilder builder=new StringBuilder("select * from tab_route r,tab_category c where c.cid=r.cid ");
        ArrayList<Object> paramsList = new ArrayList();
        if (cid!=null&&!"".equals(cid.trim())){
            builder.append(" and r.cid=? and r.rflag=1");
            paramsList.add(cid);
        }
        if (rname!=null&&!"".equals(rname.trim())&&!"null".equals(rname)){
            builder.append(" and rname like ?");
            try {
                paramsList.add("%"+URLDecoder.decode(rname, "utf-8")+"%");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        builder.append(" limit ?,?");
        int start = (curPage-1)*pageSize;//查询开始索引
        paramsList.add(start);
        int length=pageSize;//每页多少条记录
        paramsList.add(length);
        Object[] parmas = paramsList.toArray();
        //执行sql语句返回结果
        List<Map<String,Object>> mapList = jdbcTemplate.queryForList(builder.toString(),parmas);
        return  mapList;
    }
    //多表查询一条数据,所以返回的是一个Map<String,Object>集合对象
    @Override
    public Map<String, Object> findViewDetails(String rid) {
        String sql="select * from tab_route r,tab_category c,tab_seller s where r.cid=c.cid and r.sid=s.sid and rid=?";
        Map<String, Object> routeMap = jdbcTemplate.queryForMap(sql,rid);
        return routeMap;
    }
    //根据rid查询出该表中的所有图片
    @Override
    public List<RouteImg> findRouteImg(String rid) {
        String sql="select *from tab_route_img where rid=?";
        List<RouteImg> RouteImgList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RouteImg.class), rid);
        return RouteImgList;
    }
}
