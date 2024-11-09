package com.elastic.elasticsearchdemo.search;

import com.elastic.elasticsearchdemo.bean.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("index")
public class IndexController {
    @Autowired
    ElasticsearchTemplate elasticsearch;
    @PostMapping("add")
    public String add(){
        IndexOperations indexOperations = elasticsearch.indexOps(Index.class);
        if (indexOperations.exists()) {
            return "index already exists!";
        }
        elasticsearch.indexOps(Index.class).createWithMapping();
        return "index add successful!";
    }
}
