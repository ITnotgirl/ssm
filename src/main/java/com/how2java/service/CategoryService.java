package com.how2java.service;

import java.util.List;

import com.how2java.pojo.Category;
import com.how2java.util.Page;

/**
 * @author zhou
 */
public interface CategoryService {

    /**
     * 创建category的list
     * @return
     */
	List<Category> list();
}
