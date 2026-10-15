package org.linlinjava.litemall.core.storage;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
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
 * 七牛云对象存储适配器，上传凭证由桶名及访问密钥生成。
 * <p>访问地址使用配置的 endpoint；SDK 管理器无同步地延迟初始化，
 * 首次并发调用可能重复创建对象，配置应在服务使用前完成。
 */
public class QiniuStorage implements Storage {

    private final Log logger = LogFactory.getLog(QiniuStorage.class);

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private Auth auth;
    private UploadManager uploadManager;
    private BucketManager bucketManager;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
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
        if (uploadManager == null) {
            if (auth == null) {
                auth = Auth.create(accessKey, secretKey);
            }
            uploadManager = new UploadManager(new Configuration());
        }

        try {
            String upToken = auth.uploadToken(bucketName);
            uploadManager.put(inputStream, keyName, upToken, null, contentType);
        } catch (QiniuException ex) {
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
            URL url = new URL(generateUrl(keyName));
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
        if (bucketManager == null) {
            if (auth == null) {
                auth = Auth.create(accessKey, secretKey);
            }
            bucketManager = new BucketManager(auth, new Configuration());
        }
        try {
            bucketManager.delete(bucketName, keyName);
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
        return endpoint + "/" + keyName;
    }
}
