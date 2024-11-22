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

import java.util.Arrays;
import java.util.stream.Stream;

@SpringBootApplication
@MapperScan("com.elastic.elasticsearchdemo.mapper")
public class ElasticSearchDemoApplication {

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
        return MinioClient.builder().credentials(accessKey,secretKey).endpoint(host).build();
    }
}
