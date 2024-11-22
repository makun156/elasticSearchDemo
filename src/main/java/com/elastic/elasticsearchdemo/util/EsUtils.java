package com.elastic.elasticsearchdemo.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * elasticsearch工具类
 */
@Component
@Slf4j
public class EsUtils {
    @Autowired
    RestHighLevelClient es;

    public List<String> suggest(String index, String customSuggestName,String prefix, String suggestField) throws IOException {
        if (verifyEmptyIndex(index)) {
            log.error("索引库不存在!");
            return null;
        }
        SearchRequest suggest = new SearchRequest(index);
        suggest.source().suggest(
                new SuggestBuilder().addSuggestion(
                        customSuggestName,
                        SuggestBuilders.completionSuggestion(suggestField).skipDuplicates(true).prefix(prefix))
        ).size(10);
        SearchResponse response = es.search(suggest, RequestOptions.DEFAULT);
        Suggest suggestResult = response.getInternalResponse().suggest();
        List<? extends Suggest.Suggestion.Entry.Option> suggestList = suggestResult.getSuggestion(customSuggestName).getEntries().get(0).getOptions();
        List<String> list = new ArrayList<>();
        for (Suggest.Suggestion.Entry.Option option : suggestList) {
            String result = option.getText().toString();
            list.add(result);
        }
        return list;
    }

public <T> void addSuggest(String index, T data) throws Exception {
        if (StrUtil.isEmpty(index) || Objects.isNull(data)) return;
        if (existIndex(index)) return;
        IndexRequest indexRequest = new IndexRequest(index);
        String jsonData = JSONUtil.toJsonStr(data);
        indexRequest.source(jsonData, XContentType.JSON);
        es.index(indexRequest, RequestOptions.DEFAULT);
    }

    public <T> void addDocument(String index, T data) throws Exception {
        if (StrUtil.isEmpty(index) || Objects.isNull(data)) return;
        if (existIndex(index)) return;
        IndexRequest indexRequest = new IndexRequest(index);
        String jsonData = JSONUtil.toJsonStr(data);
        indexRequest.source(jsonData, XContentType.JSON);
        es.index(indexRequest, RequestOptions.DEFAULT);
    }

    /**
     * 布尔查询
     *
     * @param index
     * @param clazz
     * @param consumer
     * @param <T>
     * @return
     */
    public <T> List<T> boolQuery(String index, Class<T> clazz, Consumer<SearchRequest> consumer, boolean isHighlight) {
        if (verifyEmptyIndex(index)) return null;
        try {
            if (existIndex(index)) return null;
            SearchRequest searchRequest = new SearchRequest(index);
            consumer.accept(searchRequest);
            SearchResponse search = es.search(searchRequest, RequestOptions.DEFAULT);
            return parseSearchHit(search, clazz, isHighlight);
        } catch (IOException e) {
            log.error("elasticSearch boolQuery出现异常,报错原因：{}", e.getMessage());
        }
        return null;
    }

    /**
     * 单字段匹配查询
     *
     * @param index
     * @param field
     * @param value
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> matchQuery(String index, String field, String value, Class<T> clazz) {
        if (verifyEmptyIndex(index)) return null;
        try {
            if (existIndex(index)) return null;
            SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.source().query(
                    QueryBuilders.matchQuery(field, value)
            );
            SearchResponse searchResponse = es.search(searchRequest, RequestOptions.DEFAULT);
            return parseSearchHit(searchResponse, clazz, false);
        } catch (IOException e) {
            log.error("elasticSearch出现异常,{}", e.getMessage());
        }
        return null;
    }


    /**
     * 多字段全文检索
     *
     * @param index
     * @param queryField
     * @param value
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> multiMatchQuery(Class<T> clazz, String index, String value, String... queryField) {
        if (verifyEmptyIndex(index)) return null;
        try {
            if (existIndex(index)) return null;
            SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.source().query(
                    QueryBuilders.multiMatchQuery(value, queryField)
            );
            SearchResponse searchResponse = es.search(searchRequest, RequestOptions.DEFAULT);
            return parseSearchHit(searchResponse, clazz, false);
        } catch (IOException e) {
            log.error("elasticSearch出现异常,{}", e.getMessage());
        }
        return null;
    }

    /**
     * 解析查询结果为集合
     * @param searchResponse
     * @param clazz
     * @param isHighlight
     * @param <T>
     * @return
     */
    private <T> List<T> parseSearchHit(SearchResponse searchResponse, Class<T> clazz, boolean isHighlight) {
        SearchHit[] hits = searchResponse.getHits().getHits();
        ArrayList<T> objects = new ArrayList<>();
        for (SearchHit hit : hits) {
            T bean = JSONUtil.toBean(hit.getSourceAsString(), clazz);
            if (isHighlight) {
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                for (HighlightField highlightField : highlightFields.values()) {
                    try {
                        Class<?> cla = bean.getClass();
                        String fieldName = highlightField.getName();
                        String fieldValue = highlightField.getFragments()[0].string();
                        Method method = cla.getDeclaredMethod("set" + StrUtil.upperFirst(fieldName), cla.getField(fieldName).getType());
                        method.invoke(bean, fieldValue);
                    } catch (Exception e) {
                        log.error("找不到高亮字段映射方法错误!");
                    }
                }
            }
            objects.add(bean);
        }
        return objects;
    }

    /**
     * 判断索引库是否存在
     *
     * @param index
     * @return
     * @throws IOException
     */
    private boolean existIndex(String index) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(index);
        boolean isExistIndex = es.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        if (!isExistIndex) {
            log.error("索引不存在,请检查!");
            return true;
        }
        return false;
    }

    /**
     * 索引库名不能为空
     *
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
