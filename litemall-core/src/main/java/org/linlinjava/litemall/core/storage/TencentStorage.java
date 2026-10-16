package org.linlinjava.litemall.core.storage;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
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
 * 腾讯云 COS 存储适配器，以地域、存储桶和对象键生成访问地址。
 * <p>客户端采用无同步的延迟初始化，首次并发调用可能重复创建客户端；
 * 上传与删除异常仅记录日志，不提供本地路径和目录枚举。
 */
public class TencentStorage implements Storage {

    private final Log logger = LogFactory.getLog(TencentStorage.class);

    private String secretId;
    private String secretKey;
    private String region;
    private String bucketName;

    private COSClient cosClient;

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    private COSClient getCOSClient() {
        if (cosClient == null) {
            // 使用部署凭据创建客户端，避免每次文件操作都重复初始化。
            COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
            // 地域必须与目标存储桶一致，否则请求无法定位正确的桶。
            ClientConfig clientConfig = new ClientConfig(new Region(region));
            cosClient = new COSClient(cred, clientConfig);
        }

        return cosClient;
    }

    private String getBaseUrl() {
        return "https://" + bucketName + ".cos." + region + ".myqcloud.com/";
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
            // 对象键（Key）是对象在存储桶中的唯一标识。例如，在对象的访问域名 `bucket1-1250000000.cos.ap-guangzhou.myqcloud.com/doc1/pic1.jpg`
            // 中，对象键为 doc1/pic1.jpg, 详情参考 [对象键](https://cloud.tencent.com/document/product/436/13324)
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata);
            getCOSClient().putObject(putObjectRequest);
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
            }
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 删除底层存储对象，不负责删除数据库中的元数据记录。
     * @param keyName 待删除的对象键
     */
    @Override
    public void delete(String keyName) {
        try {
            getCOSClient().deleteObject(bucketName, keyName);
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
