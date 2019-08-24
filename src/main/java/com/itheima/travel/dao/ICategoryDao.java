package com.itheima.travel.dao;

import com.itheima.travel.model.Category;

import java.util.List;

public interface ICategoryDao {
    List<Category> findAllCategory();
}
