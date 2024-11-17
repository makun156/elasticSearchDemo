//package com.elastic.elasticsearchdemo.config;
//
//import io.minio.MinioClient;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class MinIoConfig implements InitializingBean{
//    @Value(value = "${minio.bucket}")
//    private String bucket;
//
//    @Value(value = "${minio.host}")
//    private String host;
//
//    @Value(value = "${minio.url}")
//    private String url;
//
//    @Value(value = "${minio.access-key}")
//    private String accessKey;
//
//    @Value(value = "${minio.secret-key}")
//    private String secretKey;
//
//    MinioClient minioClient;
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        this.minioClient = MinioClient.builder().endpoint(host).build();
//    }
//
//}
