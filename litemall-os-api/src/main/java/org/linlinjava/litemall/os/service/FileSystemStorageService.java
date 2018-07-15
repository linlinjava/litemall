package org.linlinjava.litemall.os.service;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.stream.Stream;

import org.linlinjava.litemall.os.config.ObjectStorageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 服务器本地文件存储
 */
@Service("localStorage")
public class FileSystemStorageService implements StorageService {

    @Autowired
    private ObjectStorageConfig osConfig;
    private static final Path rootLocation = Paths.get("storage");

    static {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void store(MultipartFile file, String keyName) {
        try {
            Files.copy(file.getInputStream(), this.rootLocation.resolve(keyName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + keyName, e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(path -> this.rootLocation.relativize(path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
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
    public void delete(String filename) {
        Path file = load(filename);
        try {
            Files.delete(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String generateUrl(String keyName) {
        String url = osConfig.getAddress() + ":" + osConfig.getPort() + "/os/storage/fetch/" + keyName;
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        return url;
    }
}