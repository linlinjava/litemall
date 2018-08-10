package org.linlinjava.litemall.core.storage;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.stream.Stream;

public class QiniuStorage implements Storage {

    private  String endpoint;
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
     * 七牛云OSS对象存储简单上传实现
     */
    @Override
    public void store(InputStream inputStream, long contentLength, String contentType, String keyName) {
        if(uploadManager == null){
            if(auth == null) {
                auth = Auth.create(accessKey, secretKey);
            }
            uploadManager = new UploadManager(new Configuration());
        }

        try {
            String upToken = auth.uploadToken(bucketName);
            Response response = uploadManager.put(inputStream, keyName, upToken, null, contentType);
        } catch (QiniuException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Stream<Path> loadAll() {
        return null;
    }

    @Override
    public Path load(String keyName) {
        return null;
    }

    @Override
    public Resource loadAsResource(String keyName) {
        try {
            URL url = new URL(generateUrl(keyName));
            Resource resource = new UrlResource(url);
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(String keyName) {
        if(bucketManager == null){
            if(auth == null) {
                auth = Auth.create(accessKey, secretKey);
            }
            bucketManager = new BucketManager(auth, new Configuration() );
        }

        try {
            bucketManager.delete(bucketName, keyName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String generateUrl(String keyName) {
        return endpoint + "/" + keyName;
    }
}
