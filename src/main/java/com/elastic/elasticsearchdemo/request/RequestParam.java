package com.elastic.elasticsearchdemo.request;

import lombok.Data;

@Data
public class RequestParam {
    //菜品名称
    private String foodName;
    //菜品分类
    private String foodBrand;
    //菜品口味
    private String foodTaste;
    //根据什么排序
    private String sortBy;
    //每页展示
    private Integer size=5;
    //页码
    private Integer page=1;
}
