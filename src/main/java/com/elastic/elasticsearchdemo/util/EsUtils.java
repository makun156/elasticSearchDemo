package com.elastic.elasticsearchdemo.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * elasticsearch工具类
 */
@Component
public class EsUtils {
    @Autowired
    RestHighLevelClient es;

    /**
     * 单字段匹配查询
     * @param index
     * @param field
     * @param value
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> matchQuery(String index,String field,String value,Class<T> clazz) {
        if (StrUtil.isEmpty(index)) {
            return null;
        }
        GetIndexRequest getIndexRequest = new GetIndexRequest(index);
        try {
            boolean isExistIndex = es.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
            if (!isExistIndex) {
                return null;
            }
            SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.source().query(
                    QueryBuilders.matchQuery(field, value)
            );
            SearchResponse searchResponse = es.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = searchResponse.getHits().getHits();
            ArrayList<T> objects = new ArrayList<>();
            for (SearchHit hit : hits) {
                objects.add(JSONUtil.toBean(hit.getSourceAsString(), clazz));
            }
            return objects;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
