package com.elastic.elasticsearchdemo.search;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.elastic.elasticsearchdemo.bean.Food;
import com.elastic.elasticsearchdemo.bean.FoodDoc;
import com.elastic.elasticsearchdemo.constant.CommonConstant;
import com.elastic.elasticsearchdemo.enums.ResponseEnum;
import com.elastic.elasticsearchdemo.response.ResponseBean;
import com.elastic.elasticsearchdemo.service.FoodService;
import com.elastic.elasticsearchdemo.util.EsUtils;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("food")
public class FoodController {
    private static final String[] FOOD_NAMES = {"红烧肉", "宫保鸡丁", "鱼香肉丝", "麻婆豆腐", "清蒸鲈鱼"};
    private static final String[] FOOD_BRANDS = {"川菜", "粤菜", "鲁菜", "苏菜", "浙菜"};
    private static final String[] FOOD_INGREDIENTS = {"猪肉", "鸡肉", "牛肉", "鱼肉", "豆腐"};
    private static final String[] FOOD_DESCRIPTIONS = {"美味可口", "香气扑鼻", "口感独特", "色泽诱人", "营养丰富"};
    private static final String[] FOOD_TIMES = {"30分钟", "1小时", "1小时30分钟", "2小时"};

    public List<Food> generateFoodData(int count) {
        List<Food> foodList = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= count; i++) {
            Food food = new Food();
            food.setId((long) i + 1); // 假设ID是自增的
            food.setFoodName(FOOD_NAMES[random.nextInt(FOOD_NAMES.length)]);
            food.setFoodBrand(FOOD_BRANDS[random.nextInt(FOOD_BRANDS.length)]);
            food.setFoodTaste(random.nextInt(3)); // 0: 不辣, 1: 微辣, 2: 特辣
            food.setFoodDescription(FOOD_DESCRIPTIONS[random.nextInt(FOOD_DESCRIPTIONS.length)]);
            food.setFoodIngredients(FOOD_INGREDIENTS[random.nextInt(FOOD_INGREDIENTS.length)]);
            food.setFoodImages("image_" + i + ".jpg"); // 假设图片名称
            food.setFoodTime(FOOD_TIMES[random.nextInt(FOOD_TIMES.length)]);
            food.setCreateTime(new Date());
            food.setCreateTser("user_" + random.nextInt(10)); // 假设创建用户
            food.setUpdateTime(new Date());
            food.setUpdateUser(new Date()); // 注意：这里应该是用户ID，而不是Date类型

            foodList.add(food);
        }

        return foodList;
    }

    @Autowired
    FoodService foodService;
    @Autowired
    MinioClient minio;
    @Autowired
    EsUtils es;

    /**
     * 添加菜品
     *
     * @param requestFood
     * @return
     * @throws Exception
     */
    @PostMapping("add")
    public ResponseBean add(@RequestBody Food requestFood) throws Exception {
        boolean save = foodService.save(requestFood);
        if (!save) {
            return ResponseBean.fail(ResponseEnum.FAIL);
        }
        es.addDocument("food", BeanUtil.toBean(requestFood, FoodDoc.class));
        return ResponseBean.success(ResponseEnum.SUCCESS);
    }

    /**
     * 搜索菜品
     *
     * @param foodId
     * @return
     * @throws Exception
     */
    @GetMapping("search/{id}")
    public ResponseBean search(@RequestParam("${id}") String foodId) throws Exception {
        Food food = foodService.getById(foodId);
        return ResponseBean.success(food);
    }

    /**
     * 搜索全部菜品
     *
     * @return
     * @throws Exception
     */
    @GetMapping("search")
    public ResponseBean searchAll() throws Exception {
        List<Food> foodList = foodService.list();
        return ResponseBean.success(foodList);
    }

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("upload")
    public ResponseBean upload(@RequestParam("file") MultipartFile file) {
        try {
            boolean bucketExists = minio.bucketExists(BucketExistsArgs.builder().bucket(CommonConstant.INDEX_NAME).build());
            if (!bucketExists) {
                return ResponseBean.fail(ResponseEnum.FAIL);
            }
            String todayDate = DateUtil.today();
            String randomName = UUID.randomUUID().toString().replaceAll("-", "");
            String objectName = todayDate + "/" + randomName;
            PutObjectArgs build = PutObjectArgs.builder()
                    .bucket(CommonConstant.INDEX_NAME)
                    .object(objectName)
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(), file.getSize(), ObjectWriteArgs.MAX_PART_SIZE).build();
            minio.putObject(build);
            log.info("文件上传成功!");
            GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(CommonConstant.INDEX_NAME)
                    .object(objectName)
                    .build();
            String presignedObjectUrl = minio.getPresignedObjectUrl(args);
            return ResponseBean.success(randomName);
        } catch (Exception e) {
            log.error("上传文件发生错误,原因:{}",e.getMessage());
            return ResponseBean.fail(ResponseEnum.FAIL);
        }
    }
}
