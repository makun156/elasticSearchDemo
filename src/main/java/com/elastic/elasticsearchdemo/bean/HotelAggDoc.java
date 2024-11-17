package com.elastic.elasticsearchdemo.bean;

import lombok.Data;

@Data
public class HotelAggDoc {
    //聚合分组名称
    public String aggName;
    //平均值
    public Double avg;
    //最大值
    public Double max;
    //最小值
    public Double min;
    //总数
    public Long count;
}
