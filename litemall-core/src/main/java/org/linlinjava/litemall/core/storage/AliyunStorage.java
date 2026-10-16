package org.linlinjava.litemall.core.storage;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * 阿里云 OSS 存储适配器，以 bucket 和对象键定位商城文件。
 * <p>上传和删除异常仅写入日志，不向调用方报告失败；不提供本地路径与目录枚举。
 * @author Yogeek
 */
public class AliyunStorage implements Storage {

    private final Log logger = LogFactory.getLog(AliyunStorage.class);

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 获取阿里云OSS客户端对象
     *
     * @return ossClient
     */
    private OSSClient getOSSClient() {
        return new OSSClient(endpoint, accessKeyId, accessKeySecret);
    }

    private String getBaseUrl() {
        return "https://" + bucketName + "." + endpoint + "/";
    }

    /**
     * 保存文件内容，以对象键而非用户原始文件名定位对象。
     * <p>当前实现捕获上传异常并记录日志，正常返回不能作为上传成功凭据。
     * @param inputStream 文件内容输入流
     * @param contentLength 文件字节数
     * @param contentType 文件 MIME 类型
     * @param keyName 由调用方生成的可信对象键
     */
    @Override
    public void store(InputStream inputStream, long contentLength, String contentType, String keyName) {
        try {
            // 设置长度和 MIME 类型，供对象存储识别内容及下游浏览器展示。
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(contentLength);
            objectMetadata.setContentType(contentType);
            // 对象键（Key）是对象在存储桶中的唯一标识。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata);
            PutObjectResult putObjectResult = getOSSClient().putObject(putObjectRequest);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

    }

    /**
     * 当前云适配器不实现目录枚举。
     * @return null
     */
    @Override
    public Stream<Path> loadAll() {
        return null;
    }

    /**
     * 当前云适配器不提供本地文件路径。
     * @param keyName 对象键
     * @return null
     */
    @Override
    public Path load(String keyName) {
        return null;
    }

    /**
     * 将对象键转换为可读取的资源，供文件下载使用。
     * @param keyName 存储对象键
     * @return 可用资源；地址无效或不可访问时可能返回 null
     */
    @Override
    public Resource loadAsResource(String keyName) {
        try {
            URL url = new URL(getBaseUrl() + keyName);
            Resource resource = new UrlResource(url);
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 删除底层存储对象，不负责删除数据库中的元数据记录。
     * @param keyName 待删除的对象键
     */
    @Override
    public void delete(String keyName) {
        try {
            getOSSClient().deleteObject(bucketName, keyName);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * 根据存储配置拼接访问地址，不检查对象存在性或生成签名凭证。
     * @param keyName 存储对象键
     * @return 对象访问地址
     */
    @Override
    public String generateUrl(String keyName) {
        return getBaseUrl() + keyName;
    }
}
