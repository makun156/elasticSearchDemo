package com.elastic.elasticsearchdemo.bean;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "index")
public class Index {
    @Field(type = FieldType.Keyword)
    private Long id;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String name;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String content;
}
