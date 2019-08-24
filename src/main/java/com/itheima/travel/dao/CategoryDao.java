package com.itheima.travel.dao;

import com.itheima.travel.model.Category;
import com.itheima.travel.util.JdbcUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class CategoryDao implements ICategoryDao {
    JdbcTemplate jdbcTemplate=new JdbcTemplate(JdbcUtils.getDataSource());
    @Override
    public List<Category> findAllCategory() {
        String sql="select *from tab_category order by cid";
        List<Category> category=null;
        try {
            category = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Category.class));
        } catch (DataAccessException e) {

        }
        return category;
    }
}
