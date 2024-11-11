package com.elastic.elasticsearchdemo;

import org.apache.http.HttpHost;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ElasticSearchDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticSearchDemoApplication.class, args);
    }

    @Bean
    public RestHighLevelClient elasticsearchClient() {
        //return new RestHighLevelClient(RestClient.builder("149.88.71.145:9200"));
        return new RestHighLevelClient(RestClient.builder(new HttpHost("149.88.71.145", 9200, "http")));
    }
}
