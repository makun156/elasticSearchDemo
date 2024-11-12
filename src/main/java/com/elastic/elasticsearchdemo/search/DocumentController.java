package com.elastic.elasticsearchdemo.search;


import cn.hutool.json.JSONUtil;
import com.elastic.elasticsearchdemo.bean.ClassDoc;
import com.elastic.elasticsearchdemo.response.ResponseBean;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RestController
@RequestMapping("/document/class")
public class DocumentController {
    @Autowired
    RestHighLevelClient es;

    @GetMapping("search/{text}")
    public ResponseBean search(@PathVariable String text) throws Exception {
        ResponseBean bean = new ResponseBean();
        //ResponseBean bean = matchQuery(text);
        //ResponseBean bean = multiMatchQuery(text);
        //boolQuery(text, bean);

        return bean;
    }

    /**
     * 根据多条字段进行Boolean查询
     * @param text
     * @param bean
     * @throws IOException
     */
    private void boolQuery(String text, ResponseBean bean) throws IOException {
        SearchRequest request = new SearchRequest("class");
        request.source().query(
                QueryBuilders.boolQuery()
                        //必须匹配
                        .must(QueryBuilders.matchQuery("content", text))
                        //不能匹配
                        .mustNot(QueryBuilders.matchQuery("name", "里斯"))
                        //过滤，必须匹配，不参与算分
                        .filter(QueryBuilders.matchQuery("name", "李四"))
        );
        SearchResponse search = es.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
        ArrayList<ClassDoc> list = new ArrayList<>();
        for (SearchHit hit : hits) {
            list.add(JSONUtil.toBean(hit.getSourceAsString(), ClassDoc.class));
        }
        bean.setData(list);
    }

    private ResponseBean multiMatchQuery(String text) throws IOException {
        SearchRequest request = new SearchRequest("class");
        request.source().query(
                QueryBuilders.multiMatchQuery(text, "name", "content")
        );
        SearchResponse search = es.search(request, RequestOptions.DEFAULT);
        ArrayList<ClassDoc> list = new ArrayList<>();
        for (SearchHit hit : search.getHits().getHits()) {
            list.add(JSONUtil.toBean(hit.getSourceAsString(), ClassDoc.class));
        }
        ResponseBean bean = new ResponseBean();
        bean.setData(list);
        return bean;
    }

    /**
     * 单个匹配查询
     */
    private ResponseBean matchQuery(String text) throws IOException {
        //1.构建查询请求，查哪个索引库
        SearchRequest request = new SearchRequest("class");
        //2.构建查询条件
        request.source().query(
                QueryBuilders.matchQuery("content", text)
        );
        //3.查询
        SearchResponse search = es.search(request, RequestOptions.DEFAULT);
        //4.获取查询结果集
        SearchHit[] hits = search.getHits().getHits();
        //5.遍历封装
        ArrayList<ClassDoc> list = new ArrayList<>();
        for (SearchHit documentFields : hits) {
            list.add(JSONUtil.toBean(documentFields.getSourceAsString(), ClassDoc.class));
        }
        ResponseBean bean = new ResponseBean();
        bean.setData(list);
        return bean;
    }


    ////查询文档
    //@Autowired
    //ElasticsearchTemplate elasticsearch;
    ////使用继承的接口来CURD文档
    //@Autowired
    //ClassRepository classRepository;

    ///**
    // * 新增索引库文档
    // * @return
    // */
    //@PostMapping("/add")
    //public Object add(@RequestBody Class clazz){
    //    Class save = classRepository.save(clazz);
    //    return save;
    //}
    ///**
    // * 指定查询索引库文档
    // * @return
    // */
    ////@GetMapping("/query/{id}")
    ////public ResponseBean query(@PathVariable Long id){
    ////    Class data = classRepository.findById(id).get();
    ////    ResponseBean responseBean = new ResponseBean();
    ////    responseBean.setData(data);
    ////    return responseBean;
    ////}
    ///**
    // * 全文检索
    // * @return
    // */
    //@GetMapping("/query/{text}")
    //public ResponseBean queryFullText(@PathVariable String text){
    //    //根据用户输入进行指定字段检索
    //    //指定字段模糊查询
    //    //NativeQueryBuilder query = new NativeQueryBuilder().withQuery(QueryBuilders.match().field("name").query(text).build()._toQuery());
    //    //SearchHits<Class> search = elasticsearch.search(query.build(), Class.class);
    //
    //    //多字段模糊查询检索
    //    //NativeQueryBuilder query = new NativeQueryBuilder().withQuery(QueryBuilders.multiMatch().fields("name","content").query(text).build()._toQuery());
    //    //SearchHits<Class> search = elasticsearch.search(query.build(), Class.class);
    //
    //    //精确匹配查询，最好查询不分词的字段并且查询的文本最好也是不分词的字段，如果输入的文本是能分词的，那么可能会按照分词的词语来查询检索
    //    //NativeQueryBuilder query = new NativeQueryBuilder().withQuery(QueryBuilders.term().field("content").value(text).build()._toQuery());
    //    //SearchHits<Class> search = elasticsearch.search(query.build(), Class.class);
    //
    //    //范围查询
    //    //NativeQueryBuilder query = new NativeQueryBuilder().withQuery(QueryBuilders.range().field("id").gt(JsonData.of(1)).build()._toQuery());
    //    //SearchHits<Class> search = elasticsearch.search(query.build(), Class.class);
    //
    //    //根据ids查询
    //    //NativeQueryBuilder query = new NativeQueryBuilder().withQuery(QueryBuilders.ids().values("1","2").build()._toQuery());
    //    //SearchHits<Class> search = elasticsearch.search(query.build(), Class.class);
    //
    //    Query content = QueryBuilders.match().field("content").query(text).build()._toQuery();
    //    Query toQuery = QueryBuilders.matchNone().queryName("name").build()._toQuery();
    //    Query query = QueryBuilders.bool().must(content,toQuery).build()._toQuery();
    //    QueryBuilders.
    //    NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder().withQuery(query).withFilter();
    //    SearchHits<Class> search = elasticsearch.search(nativeQueryBuilder.build(), Class.class);
    //    ResponseBean bean=new ResponseBean();
    //    bean.setData(search.getSearchHits());
    //    return bean;
    //}
}
