package com.elastic.elasticsearchdemo.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * 菜品文档
 */
@Data
public class FoodDoc {
    /**
     *
     */
    private Long id;

    /**
     * 菜品名称
     */
    private String foodName;

    /**
     * 菜系
     */
    private String foodBrand;

    /**
     * 菜品口味 0：不辣 1：微辣  2：特辣
     */
    private Integer foodTaste;

    /**
     * 菜品描述
     */
    private String foodDescription;

    /**
     * 菜品食材
     */
    private String foodIngredients;
}
