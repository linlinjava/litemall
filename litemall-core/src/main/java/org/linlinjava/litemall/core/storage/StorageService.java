package org.linlinjava.litemall.core.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

public class StorageService {
    private String active;
    private Storage storage;
    private Map<String, Storage> supportedStorage;

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
        this.storage = this.supportedStorage.get(active);
    }

    public Map<String, Storage> getSupportedStorage() {
        return supportedStorage;
    }

    public void setSupportedStorage(Map<String, Storage> supportedStorage) {
        this.supportedStorage = supportedStorage;
    }

    /**
     * 存储一个文件对象
     * @param file      SpringBoot MultipartFile文件对象
     * @param keyName   文件索引名
     */
    public void store(MultipartFile file, String keyName) {
        storage.store(file, keyName);
    }

    public Stream<Path> loadAll() {
        return storage.loadAll();
    }

    public Path load(String keyName) {
        return storage.load(keyName);
    }

    public Resource loadAsResource(String keyName){
        return storage.loadAsResource(keyName);
    }

    public void delete(String keyName){
        storage.delete(keyName);
    }

    public String generateUrl(String keyName) {
        return storage.generateUrl(keyName);
    }
}
