package com.elastic.elasticsearchdemo.search;

import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.json.JsonData;
import com.elastic.elasticsearchdemo.bean.Class;
import com.elastic.elasticsearchdemo.repository.ClassRepository;
import com.elastic.elasticsearchdemo.response.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.MoreLikeThisQuery;
import org.springframework.web.bind.annotation.*;
import com.elastic.elasticsearchdemo.bean.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/document/class")
public class DocumentController {
    //查询文档
    @Autowired
    ElasticsearchTemplate elasticsearch;
    //使用继承的接口来CURD文档
    @Autowired
    ClassRepository classRepository;

    /**
     * 新增索引库文档
     * @return
     */
    @PostMapping("/add")
    public Object add(@RequestBody Class clazz){
        Class save = classRepository.save(clazz);
        return save;
    }
    /**
     * 指定查询索引库文档
     * @return
     */
    //@GetMapping("/query/{id}")
    //public ResponseBean query(@PathVariable Long id){
    //    Class data = classRepository.findById(id).get();
    //    ResponseBean responseBean = new ResponseBean();
    //    responseBean.setData(data);
    //    return responseBean;
    //}
    /**
     * 全文检索
     * @return
     */
    @GetMapping("/query/{text}")
    public ResponseBean queryFullText(@PathVariable String text){
        //根据用户输入进行指定字段检索
        //指定字段模糊查询
        //NativeQueryBuilder query = new NativeQueryBuilder().withQuery(QueryBuilders.match().field("name").query(text).build()._toQuery());
        //SearchHits<Class> search = elasticsearch.search(query.build(), Class.class);

        //多字段模糊查询检索
        //NativeQueryBuilder query = new NativeQueryBuilder().withQuery(QueryBuilders.multiMatch().fields("name","content").query(text).build()._toQuery());
        //SearchHits<Class> search = elasticsearch.search(query.build(), Class.class);

        //精确匹配查询，最好查询不分词的字段并且查询的文本最好也是不分词的字段，如果输入的文本是能分词的，那么可能会按照分词的词语来查询检索
        //NativeQueryBuilder query = new NativeQueryBuilder().withQuery(QueryBuilders.term().field("content").value(text).build()._toQuery());
        //SearchHits<Class> search = elasticsearch.search(query.build(), Class.class);

        //范围查询
        //NativeQueryBuilder query = new NativeQueryBuilder().withQuery(QueryBuilders.range().field("id").gt(JsonData.of(1)).build()._toQuery());
        //SearchHits<Class> search = elasticsearch.search(query.build(), Class.class);

        //根据ids查询
        NativeQueryBuilder query = new NativeQueryBuilder().withQuery(QueryBuilders.ids().values("1","2").build()._toQuery());
        SearchHits<Class> search = elasticsearch.search(query.build(), Class.class);
        ResponseBean bean=new ResponseBean();
        bean.setData(search.getSearchHits());
        return bean;
    }
}
