package com.elastic.elasticsearchdemo;

import io.minio.MinioClient;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@MapperScan("com.elastic.elasticsearchdemo.mapper")
public class ElasticSearchDemoApplication {

    public static List<List<Integer>> batchList(List<Integer> inputList, int batchSize) {
        List<List<Integer>> batchedLists = new ArrayList<>();
        int listSize = inputList.size();

        for (int start = 0; start < listSize; start += batchSize) {
            // 计算每批的结束索引，不超过原始列表的大小
            int end = Math.min(start + batchSize, listSize);
            // 创建每批次的子列表
            batchedLists.add(inputList.subList(start, end));
        }

        return batchedLists;
    }


    public static void main(String[] args) {
        SpringApplication.run(ElasticSearchDemoApplication.class, args);
    }

    @Value("${elasticsearch.uris}")
    private String url;
    @Value("${elasticsearch.port}")
    private Integer port;

    @Bean
    public RestHighLevelClient elasticsearchClient() {
        //return new RestHighLevelClient(RestClient.builder("149.88.71.145:9200"));
        return new RestHighLevelClient(RestClient.builder(new HttpHost(url, port, "http")));
    }

    @Value(value = "${minio.host}")
    private String host;
    @Value(value = "${minio.access-key}")
    private String accessKey;
    @Value(value = "${minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() throws Exception {
        return MinioClient.builder().credentials(accessKey, secretKey).endpoint(host).build();
    }
}
