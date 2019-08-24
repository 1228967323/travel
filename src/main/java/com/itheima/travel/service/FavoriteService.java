package com.itheima.travel.service;

import com.itheima.travel.dao.IFavoriteDao;
import com.itheima.travel.factory.BeansFactory;
import com.itheima.travel.model.*;
import com.itheima.travel.util.JdbcUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FavoriteService implements IFavoriteService {
    IFavoriteDao favoriteDao = (IFavoriteDao) BeansFactory.createBeans("FavoriteDao");

    @Override
    public boolean isFavorite(String rid, int uid) {
        Favorite favorite = favoriteDao.isFavorite(rid, uid);
        if (favorite == null) {
            return false;
        }
        return true;
    }

    /**
     * 用户添加收藏 ,需要分别对两张表进行操作
     * 一 对收藏表进行添加操作,收藏表中分别有 route对象中的 rid属性 .date对象的string 格式的日期
     * 对应用户User对象 uid属性.Favorite对象具体他的共同属性,因此可以封装一个Favorite对象传入要添加数据的
     * 方法中
     *
     * @param route
     * @param user
     * @throws Exception
     */
    @Override
    public void addFavorite(Route route, User user) throws Exception {
        //封装一个Favorite对象,此对象中包含用户收藏需要的属性
        //favorite中包含着route,date,user,因此封装好此对象可以对tab_favorite进行插入操作
        Favorite favorite = new Favorite();
        favorite.setRoute(route);
        favorite.setUser(user);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        favorite.setDate(format);
        //获取连接池对象
        DataSource dataSource = JdbcUtils.getDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        TransactionSynchronizationManager.initSynchronization();
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            //关闭事物自动提交
            connection.setAutoCommit(false);
            //保存当前的用户需要收藏的数据,将favorite传入需要插入的数据
            favoriteDao.addFavorite(jdbcTemplate, favorite);
            //当用户点击收藏需要对当前route中的count属性进行更新操作
            int count = favoriteDao.updateRouteCount(jdbcTemplate, route.getRid());
        } catch (Exception e) {
            //出现异常,对事物进行回滚
            connection.rollback();
            e.printStackTrace();
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
            connection.setAutoCommit(true);
        }
    }

    /**
     * PageBean对象包含着需要分页展示的所有数据,因此需要封装一个PageBean传入到客户端
     * 页面展示的数据有route表中的大部分信息,但我们的数据只有user信息,因此可以进行联查操作:
     * 根据传入的user对象中的rid对应的route对象中的rid可以获取到route表中的数据
     * 联查操作没有对应的封装属性,因此可以返回一个List<Map<String,Object>>集合,
     * 然后遍历List<Map<String,Object>>集合得到每一个Map<String,Object>.将遍历的每一个Map
     * 集合通过BeanUtils进行封装.
     */
    @Override
    public PageBean getPageBean(User user, String curPageStr) throws Exception {
        //对PageBean进行封装 创建一个PageBean对象
        PageBean<Favorite> pageBean = new PageBean<>();
        //curPage是从客户端带入,是String类型,而数据库中的curPage是Int或long类型,因此需要对curPage进行强制转换
        //设置PageBean当前页属性
        int curPage = 1;
        if(curPageStr!=null && !curPageStr.trim().equals("")){
            curPage = Integer.parseInt(curPageStr);
        }
        pageBean.setCurPage(curPage);
        //设置每页最大条数为4条
        int pageSize = 4;
        pageBean.setPageSize(pageSize);
        //从数据库中查询当前用户收藏的最大行数
        int count = favoriteDao.getCount(user);
        //设置PageBean最大条数
        pageBean.setCount(count);
        //查询当前用户点击的curPage获取展示数据,
        List<Map<String, Object>> route = favoriteDao.findRoute(user, curPage, pageSize);
        //将List<Map<String,Object>>对象封装成想要的List<T>集合
        List<Favorite> favoriteList = convertMapListToList(route);
        //设置PageBean中的data数据.完成对PageBean的封装
        pageBean.setData(favoriteList);
        return pageBean;
    }
    /**
     * 查询最受欢迎的路线
     *
     */
    @Override
    public PageBean findFavorite(String curPageStr,String routeName,String minPrice,String maxPrice) {
        PageBean<Route> pageBean=new PageBean<>();
        int curPage=1;
        if (curPageStr!=null&&!"".equals(curPageStr)){
            curPage=Integer.parseInt(curPageStr);
        }
        pageBean.setCurPage(curPage);
        int pageSize=8;
        pageBean.setPageSize(pageSize);
        int count=favoriteDao.getCount(routeName,minPrice,maxPrice);
        pageBean.setCount(count);
        List<Route> list=favoriteDao.findFavorite(pageSize,curPage,routeName,minPrice,maxPrice);
        pageBean.setData(list);
        return pageBean;
    }

    /**
     * 间接将List<Map<String,Object>>对象封装成想要的List<T>集合
     * 通过遍历得到每一个Map<String,Object>集合,
     * 然后用BeanUtils类将map集合中的数据给需要封装的对象进行封装
     * Favorite类中包含有Route,String date,User
     * Route对象包含着大量的数据
     */
    private List<Favorite> convertMapListToList(List<Map<String, Object>> mapList) throws Exception {
        List<Favorite> favoriteList = null;
        if (mapList != null && mapList.size() > 0) {
            //实例routeList
            favoriteList = new ArrayList<Favorite>();
            //此处包含多表相同的数据,因此可以把他们的属性分别设置到对应的封装类对象
            for (Map<String, Object> map : mapList) {
                Favorite favorite=new Favorite();
                Route route=new Route();
                User user=new User();
                BeanUtils.populate(favorite, map);
                BeanUtils.populate(route, map);
                BeanUtils.populate(user, map);
                favorite.setRoute(route);
                favorite.setUser(user);
                favoriteList.add(favorite);
            }
        }
        return favoriteList;
    }
}