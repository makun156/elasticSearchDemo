package com.elastic.elasticsearchdemo.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 菜品文档
 */
@Data
public class FoodDoc {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodBrand() {
        return foodBrand;
    }

    public void setFoodBrand(String foodBrand) {
        this.foodBrand = foodBrand;
    }

    public Integer getFoodTaste() {
        return foodTaste;
    }

    public void setFoodTaste(Integer foodTaste) {
        this.foodTaste = foodTaste;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public String getFoodIngredients() {
        return foodIngredients;
    }

    public void setFoodIngredients(String foodIngredients) {
        this.foodIngredients = foodIngredients;
    }

    public String getFoodImages() {
        return foodImages;
    }

    public void setFoodImages(String foodImages) {
        this.foodImages = foodImages;
    }

    /**
     *
     */
    private Long id;

    /**
     * 菜品名称
     */
    public String foodName;

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
    /**
     * 菜品图片
     */
    private String foodImages;
}
