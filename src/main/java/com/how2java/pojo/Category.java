package com.how2java.pojo;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhou
 */
public class Category {
	private int id;
	private String name;
	private String date1;


    /**
     * 设置id
     * @param id
     */
	public void setId(int id) {
		this.id = id;
	}
    public int getId() {
        return id;
    }
    /**定义与get,set方法命名要一致*/
	public void setDate1(String date1) {
		this.date1=date1;
	}
	public String getDate1(){
		/**Date date=new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date1 = df.format(date);*/
		return this.date1;
	}
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
	@Override
	public String toString() {
		return "Category [id=" + id + ", name=" + name + ",date1=" + date1 + "]";
	}
	
	
	
}
