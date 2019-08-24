package com.itheima.travel.service;

import com.google.gson.Gson;
import com.itheima.travel.dao.ICategoryDao;
import com.itheima.travel.factory.BeansFactory;
import com.itheima.travel.model.Category;
import com.itheima.travel.util.JedisUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

public class CategoryService implements ICategoryService {
    ICategoryDao categoryDao= (ICategoryDao) BeansFactory.createBeans("CategoryDao");
    @Override
    public String findAllCategory() {
        Jedis jedis = JedisUtil.getJedis();
        String dataJson = jedis.get("categoryList");
        if (dataJson==null||"".equals(dataJson.trim())){
            List<Category> allCategory = categoryDao.findAllCategory();
            Gson gson = new Gson();
            String strJson = gson.toJson(allCategory);
            jedis.set("categoryList", strJson);
        }
        return dataJson;
    }
}
