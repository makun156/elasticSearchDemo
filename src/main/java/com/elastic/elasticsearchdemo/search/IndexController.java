package com.elastic.elasticsearchdemo.search;

import com.elastic.elasticsearchdemo.bean.Class;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.web.bind.annotation.*;
import com.elastic.elasticsearchdemo.bean.*;
import java.util.Map;

@RestController
@RequestMapping("class")
public class IndexController {
    @Autowired
    ElasticsearchTemplate elasticsearch;

    /**
     * 添加索引库表
     * @return
     */
    @PostMapping("add")
    public String add(){
        IndexOperations indexOperations = elasticsearch.indexOps(Class.class);
        if (indexOperations.exists()) {
            return "index already exists!";
        }
        elasticsearch.indexOps(Class.class).createWithMapping();
        return "index add successful!";
    }

    /**
     * 查询索引库表结构
     * @return
     */
    @GetMapping("query")
    public Object query(){
        Map<String, Object> mapping = elasticsearch.indexOps(Class.class).getMapping();
        return mapping;
    }
    /**
     * 查询索引库表结构
     * @return
     */
    @DeleteMapping("delete")
    public String delete(){
        elasticsearch.indexOps(Class.class).delete();
        return "删除索引库表";
    }
}
