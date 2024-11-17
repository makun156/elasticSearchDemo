package com.elastic.elasticsearchdemo.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName t_food
 */
@TableName(value ="t_food")
@Data
public class Food implements Serializable {
    /**
     * 
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 菜品名称
     */
    @TableField(value = "food_name")
    private String foodName;

    /**
     * 菜系
     */
    @TableField(value = "food_brand")
    private String foodBrand;

    /**
     * 菜品口味 0：不辣 1：微辣  2：特辣
     */
    @TableField(value = "food_taste")
    private Integer foodTaste;

    /**
     * 菜品描述
     */
    @TableField(value = "food_description")
    private String foodDescription;

    /**
     * 菜品食材
     */
    @TableField(value = "food_ingredients")
    private String foodIngredients;

    /**
     * 菜品图片
     */
    @TableField(value = "food_images")
    private String foodImages;

    /**
     * 烹饪时长
     */
    @TableField(value = "food_time")
    private String foodTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 创建用户
     */
    @TableField(value = "create_user")
    private String createTser;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 修改用户
     */
    @TableField(value = "update_user")
    private Date updateUser;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", foodName='" + foodName + '\'' +
                ", foodBrand='" + foodBrand + '\'' +
                ", foodTaste=" + foodTaste +
                ", foodDescription='" + foodDescription + '\'' +
                ", foodIngredients='" + foodIngredients + '\'' +
                ", foodImages='" + foodImages + '\'' +
                ", foodTime='" + foodTime + '\'' +
                ", createTime=" + createTime +
                ", createTser='" + createTser + '\'' +
                ", updateTime=" + updateTime +
                ", updateUser=" + updateUser +
                '}';
    }
}