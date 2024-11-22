package com.elastic.elasticsearchdemo.web;

import cn.hutool.core.util.StrUtil;
import com.elastic.elasticsearchdemo.bean.FoodDoc;
import com.elastic.elasticsearchdemo.request.RequestParam;
import com.elastic.elasticsearchdemo.response.ResponseBean;
import com.elastic.elasticsearchdemo.util.EsUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("search")
public class SearchController {
    @Autowired
    EsUtils es;

    /**
     * 搜索功能
     * @param request
     * @return
     */
    @PostMapping("list")
    public ResponseBean list(@RequestBody RequestParam request){
        List<FoodDoc> foodDocs = es.boolQuery("food", FoodDoc.class, searchRequest -> {

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            if (StrUtil.isEmpty(request.getFoodName())) {
                boolQueryBuilder.must(QueryBuilders.matchAllQuery());
            } else {
                boolQueryBuilder.must(QueryBuilders.matchQuery("foodName", request.getFoodName()));
            }
            if (!StrUtil.isEmpty(request.getFoodBrand())) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("foodBrand", request.getFoodBrand()));
            }
            if (!StrUtil.isEmpty(request.getFoodTaste())) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("foodTaste", request.getFoodTaste()));
            }
            searchRequest.source().highlighter(SearchSourceBuilder.highlight().field("foodName"));
            searchRequest.source().query(boolQueryBuilder)
                    .from((request.getPage() - 1) * request.getSize()).size(request.getSize());
        },true);
        return ResponseBean.success(foodDocs);
    }

    @PostMapping("suggest")
    public ResponseBean suggest(@RequestBody RequestParam request)throws Exception{
        List<String> suggest = es.suggest("suggest", "mySuggest", request.getFoodName(), "foodName");
        return ResponseBean.success(suggest);
    }
}
