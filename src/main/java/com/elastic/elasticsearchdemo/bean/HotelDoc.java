package com.elastic.elasticsearchdemo.bean;

import lombok.Data;

@Data
public class HotelDoc {
    //id
    public Long id;
    //酒店名称
    public String hotelName;
    //地址
    public String address;
    //描述
    public String description;
    //星级
    public Integer start;
    //价格
    public Integer price;
    // 构造函数
    public HotelDoc(Long id, String hotelName, String address, String description, Integer start, Integer price) {
        this.id = id;
        this.hotelName = hotelName;
        this.address = address;
        this.description = description;
        this.start = start;
        this.price = price;
    }
}
