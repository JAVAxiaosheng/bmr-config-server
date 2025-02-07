package com.ant.bmr.config.core.minio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import cn.hutool.core.io.IoUtil;
import com.ant.bmr.config.common.context.FileContext;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class MinioService {

    @Resource
    private MinioClient minioClient;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    // 默认过期时间12H
    private static final Integer DEFAULT_EXPIRES_TIME = 12;

    /**
     * 创建bucket
     */
    public void createBucket(String bucketName) throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 上传文件
     */
    @Transactional(rollbackFor = Exception.class)
    public String uploadFile(String bucketName, MultipartFile file, String uploadFilePath) {
        try {
            //判断文件是否为空
            if (Objects.isNull(file) || file.getSize() == 0) {
                return null;
            }
            //判断存储桶是否存在  不存在则创建
            createBucket(bucketName);
            //原文件名
            String originalFilename = file.getOriginalFilename();
            assert originalFilename != null;

            //上传文件
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(uploadFilePath)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            // 返回上传路径
            return uploadFilePath;
        } catch (Exception e) {
            log.error("upload file error,file path:{}", uploadFilePath, e);
            throw new RuntimeException("upload file error", e);
        }
    }

    /**
     * 获取文件内容
     */
    public String getFileContext(String bucketName, String uploadFilePath) {
        InputStream inputStream = null;
        try {
            inputStream = getObject(bucketName, uploadFilePath);
            return IoUtil.read(inputStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("get file context error,file path:{}", uploadFilePath, e);
            throw new RuntimeException("upload file error", e);
        } finally {
            IoUtil.close(inputStream);
        }
    }

    /**
     * 获取全部bucket
     *
     * @return
     */
    public List<Bucket> getAllBuckets() throws Exception {
        return minioClient.listBuckets();
    }

    /**
     * 根据bucketName获取信息
     *
     * @param bucketName bucket名称
     */
    public Optional<Bucket> getBucket(String bucketName)
        throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException,
        InvalidResponseException, InternalException,
        ServerException, ErrorResponseException, XmlParserException {
        return minioClient.listBuckets().stream().filter(b -> b.name().equals(bucketName)).findFirst();
    }

    /**
     * 根据bucketName删除信息
     *
     * @param bucketName bucket名称
     */
    public void removeBucket(String bucketName) throws Exception {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * 获取⽂件的下载URL
     *
     * @param bucketName     bucket名称
     * @param uploadFilePath ⽂件名称
     * @param expires        过期时间
     * @param timeUnit       单位
     * @return url
     */
    @Transactional(rollbackFor = Exception.class)
    public String getDownloadFileURL(String bucketName, String uploadFilePath,
        Integer expires, TimeUnit timeUnit) {
        try {
            if (Objects.isNull(expires)) {
                expires = DEFAULT_EXPIRES_TIME;
                timeUnit = TimeUnit.HOURS;
            }
            String redisKey = bucketName + FileContext.FILA_PATH_SEPARATOR + uploadFilePath;
            String downloadUrl = redisTemplate.opsForValue().get(redisKey);
            if (Objects.isNull(downloadUrl)) {
                downloadUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(uploadFilePath)
                        .expiry(expires, timeUnit)
                        .build());
                redisTemplate.opsForValue().set(redisKey, downloadUrl, expires, timeUnit);
            }
            return downloadUrl;
        } catch (Exception e) {
            log.error("get file download URL error,file path:{}", uploadFilePath, e);
            throw new RuntimeException("get file download URL error", e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public String getDownloadFileURL(String bucketName, String uploadFilePath) {
        try {
            String redisKey = bucketName + FileContext.FILA_PATH_SEPARATOR + uploadFilePath;
            String downloadUrl = redisTemplate.opsForValue().get(redisKey);
            if (Objects.isNull(downloadUrl)) {
                downloadUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(uploadFilePath)
                        .expiry(DEFAULT_EXPIRES_TIME, TimeUnit.HOURS)
                        .build());
                redisTemplate.opsForValue().set(redisKey, downloadUrl, DEFAULT_EXPIRES_TIME, TimeUnit.HOURS);
            }
            return downloadUrl;
        } catch (Exception e) {
            log.error("get file download URL error,file path:{}", uploadFilePath, e);
            throw new RuntimeException("get file download URL error", e);
        }
    }

    /**
     * 获取⽂件
     *
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @return ⼆进制流
     */
    public InputStream getObject(String bucketName, String objectName) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * 上传⽂件
     *
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @param stream     ⽂件流
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
     */
    public void putObject(String bucketName, String objectName, InputStream stream) throws
        Exception {
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(stream,
            stream.available(), -1).contentType(objectName.substring(objectName.lastIndexOf("."))).build());
    }

    /**
     * 上传⽂件
     *
     * @param bucketName  bucket名称
     * @param objectName  ⽂件名称
     * @param stream      ⽂件流
     * @param size        ⼤⼩
     * @param contextType 类型
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
     */
    public void putObject(String bucketName, String objectName, InputStream stream, long
        size, String contextType) throws Exception {
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(stream, size, -1)
            .contentType(contextType).build());
    }

    /**
     * 获取⽂件信息
     *
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#statObject
     */
    public StatObjectResponse getObjectInfo(String bucketName, String objectName) throws Exception {
        return minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * 删除⽂件
     *
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @throws Exception https://docs.minio.io/cn/java-client-apireference.html#removeObject
     */
    public void removeObject(String bucketName, String objectName) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }
}