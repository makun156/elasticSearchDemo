package com.elastic.elasticsearchdemo.util;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MinIoUtils {
    @Autowired
    MinioClient minio;
}
