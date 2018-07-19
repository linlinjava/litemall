package org.linlinjava.litemall.os.service;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * @author Yogeek
 * @date 2018/7/16 16:10
 * @decrpt 阿里云对象存储服务
 */
@PropertySource(value = "classpath:aliyun.properties")
@Service("aos")
public class AliyunOsService implements ObjectStorageService {

    @Value("${aliyun.os.ENDPOINT}")
    private  String ENDPOINT;
    @Value("${aliyun.os.ACCESS_KEY_ID}")
    private  String ACCESS_KEY_ID;
    @Value("${aliyun.os.ACCESS_KEY_SECRET}")
    private  String ACCESS_KEY_SECRET;
    @Value("${aliyun.os.BUCKET_NAME}")
    private  String BUCKET_NAME;
//    @Value("${aliyun.os.FOLDER}")
//    private  String FOLDER;

    /**
     * 获取阿里云OSS客户端对象
     *
     * @return ossClient
     */
    private OSSClient getOSSClient(){
        return new OSSClient(ENDPOINT,ACCESS_KEY_ID, ACCESS_KEY_SECRET);
    }

    private String getBaseUrl() {
        return "https://" + BUCKET_NAME + "." +  ENDPOINT + "/" ;
    }

    /**
     * 阿里云OSS对象存储简单上传实现
     */
    @Override
    public void store(MultipartFile file, String keyName) {
        try {
            // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20M以下的文件使用该接口
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());
            // 对象键（Key）是对象在存储桶中的唯一标识。
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, keyName, file.getInputStream(), objectMetadata);
            PutObjectResult putObjectResult = getOSSClient().putObject(putObjectRequest);
        } catch (Exception ex) {
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
            URL url = new URL(getBaseUrl() + keyName);
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
        try {
            getOSSClient().deleteObject(BUCKET_NAME, keyName);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public String generateUrl(String keyName) {
        return getBaseUrl() + keyName;
    }
}
