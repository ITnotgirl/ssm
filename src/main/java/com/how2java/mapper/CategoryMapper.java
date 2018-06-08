package com.how2java.mapper;
 
import java.util.List;

import com.how2java.pojo.Category;
import com.how2java.util.Page;
 
/**
 * @author zhou
 */
public interface CategoryMapper {


    /**
     * 添加category
     * @param category
     * @return
     */
    public int add(Category category);


    /**
     * 删除category表中的id
     * @param id
     */
    public void delete(int id);


    /**
     * 获取category表中的id
     * @param id
     * @return
     */
    public Category get(int id);


    /**
     * 更新category表
     * @param category
     * @return
     */
    public int update(Category category);


    /**
     * 生成category的list
     * @return
     */
    public List<Category> list();
      
}