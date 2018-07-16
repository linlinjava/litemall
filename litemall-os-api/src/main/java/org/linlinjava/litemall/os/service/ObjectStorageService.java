package org.linlinjava.litemall.os.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * 对象存储接口
 */
public interface ObjectStorageService {

    /**
     * 存储一个文件对象
     * @param file      SpringBoot MultipartFile文件对象
     * @param keyName   文件索引名
     */
    void store(MultipartFile file, String keyName);

    Stream<Path> loadAll();

    Path load(String keyName);

    Resource loadAsResource(String keyName);

    void delete(String keyName);

    String generateUrl(String keyName);
}