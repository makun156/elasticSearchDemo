package com.elastic.elasticsearchdemo.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
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
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * elasticsearch工具类
 */
@Component
@Slf4j
public class EsUtils {
    @Autowired
    RestHighLevelClient es;

    public <T> List<T> boolQuery(String index, Class<T> clazz, Consumer<SearchRequest> consumer){
        if (verifyEmptyIndex(index)) return null;
        GetIndexRequest getIndexRequest = new GetIndexRequest(index);
        try {
            if (existIndex(getIndexRequest)) return null;
            SearchRequest searchRequest = new SearchRequest(index);
            consumer.accept(searchRequest);
            SearchResponse search = es.search(searchRequest, RequestOptions.DEFAULT);
            return parseSearchHit(search,clazz);
        } catch (IOException e) {
            log.error("elasticSearch boolQuery出现异常,报错原因：{}",e.getMessage());
        }
        return null;
    }

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
        if (verifyEmptyIndex(index)) return null;
        GetIndexRequest getIndexRequest = new GetIndexRequest(index);
        try {
            if (existIndex(getIndexRequest)) return null;
            SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.source().query(
                    QueryBuilders.matchQuery(field, value)
            );
            SearchResponse searchResponse = es.search(searchRequest, RequestOptions.DEFAULT);
            return parseSearchHit(searchResponse,clazz);
        } catch (IOException e) {
            log.error("elasticSearch出现异常,{}",e.getMessage());
        }
        return null;
    }


    /**
     * 多字段全文检索
     * @param index
     * @param field
     * @param value
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> multiMatchQuery(Class<T> clazz,String index,String value,String... field) {
        if (verifyEmptyIndex(index)) return null;
        GetIndexRequest getIndexRequest = new GetIndexRequest(index);
        try {
            if (existIndex(getIndexRequest)) return null;
            SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.source().query(
                    QueryBuilders.multiMatchQuery(value, field)
            );
            SearchResponse searchResponse = es.search(searchRequest, RequestOptions.DEFAULT);
            return parseSearchHit(searchResponse,clazz);
        } catch (IOException e) {
            log.error("elasticSearch出现异常,{}",e.getMessage());
        }
        return null;
    }

    /**
     * 解析查询结果为集合
     * @param searchResponse
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> List<T> parseSearchHit(SearchResponse searchResponse,Class<T> clazz) {
        SearchHit[] hits = searchResponse.getHits().getHits();
        ArrayList<T> objects = new ArrayList<>();
        for (SearchHit hit : hits) {
            objects.add(JSONUtil.toBean(hit.getSourceAsString(), clazz));
        }
        return objects;
    }
    /**
     * 判断索引库是否存在
     * @param getIndexRequest
     * @return
     * @throws IOException
     */
    private boolean existIndex(GetIndexRequest getIndexRequest) throws IOException {
        boolean isExistIndex = es.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        if (!isExistIndex) {
            log.error("索引不存在,请检查!");
            return true;
        }
        return false;
    }

    /**
     * 索引库名不能为空
     * @param index
     * @return
     */
    private boolean verifyEmptyIndex(String index) {
        if (StrUtil.isEmpty(index)) {
            log.info("索引名不能为空!");
            return true;
        }
        return false;
    }

}
