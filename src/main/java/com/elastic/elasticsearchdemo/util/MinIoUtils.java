package com.elastic.elasticsearchdemo.util;

import com.elastic.elasticsearchdemo.constant.CommonConstant;
import io.minio.BucketExistsArgs;
import io.minio.DownloadObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.elastic.elasticsearchdemo.constant.CommonConstant.DEFAULT_EXPIRE_TIME;

@Component
public class MinIoUtils {
    @Autowired
    MinioClient minio;

    /**
     * 获取对象地址
     * @param bucketName
     * @param objectName
     * @param expires
     * @param timeUnit
     * @return
     * @throws Exception
     */
    public String getObjectUrl(String bucketName, String objectName, Integer expires, TimeUnit timeUnit) throws Exception{
        boolean bucketExists = minio.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!bucketExists) {
            return "存储桶不存在";
        }
        GetPresignedObjectUrlArgs build = GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .expiry(expires != null ? expires : DEFAULT_EXPIRE_TIME, timeUnit)
                .build();
        return minio.getPresignedObjectUrl(build);
    }

    /**
     * 获取存储对象地址
     * @param bucketName
     * @param objectName
     * @return
     * @throws Exception
     */
    public String getObjectUrl(String bucketName, String objectName) throws Exception{
        boolean bucketExists = minio.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!bucketExists) {
            return "存储桶不存在";
        }
        GetPresignedObjectUrlArgs build = GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(objectName)
                .build();
        return minio.getPresignedObjectUrl(build);
    }
}
