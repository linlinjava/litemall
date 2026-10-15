package org.linlinjava.litemall.core.storage;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

/**
 * 以服务器磁盘保存商品图片等文件，并通过配置的 HTTP 地址生成访问链接。
 * <p>路径由 storagePath 指定，URL 由 address 指定，两者需对应同一资源映射。
 * 本类不检查路径穿越；调用方必须传入可信对象键。
 */
public class LocalStorage implements Storage {


    private final Log logger = LogFactory.getLog(LocalStorage.class);

    private String storagePath;
    private String address;

    private Path rootLocation;

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;

        this.rootLocation = Paths.get(storagePath);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 保存文件内容，以对象键而非用户原始文件名定位对象。
     * <p>同名目标会被覆盖；contentLength 和 contentType 不参与磁盘复制。
     * @param inputStream 文件内容输入流
     * @param contentLength 文件字节数
     * @param contentType 文件 MIME 类型
     * @param keyName 由调用方生成的可信对象键
     * @throws RuntimeException 文件复制发生 I/O 错误时
     */
    @Override
    public void store(InputStream inputStream, long contentLength, String contentType, String keyName) {
        try {
            Files.copy(inputStream, rootLocation.resolve(keyName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + keyName, e);
        }
    }

    /**
     * 列出存储根目录下一层的相对路径。
     * @return 路径流，调用方使用后应关闭；云实现可能返回 null
     */
    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(rootLocation, 1)
                    .filter(path -> !path.equals(rootLocation))
                    .map(path -> rootLocation.relativize(path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read stored files", e);
        }

    }

    /**
     * 按对象键定位文件；本方法不验证文件是否存在。
     * @param filename 存储对象键
     * @return 本地路径；云实现可能返回 null
     */
    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    /**
     * 将对象键转换为可读取的资源，供文件下载使用。
     * @param filename 存储对象键
     * @return 可用资源；地址无效或不可访问时可能返回 null
     */
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
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 删除底层存储对象，不负责删除数据库中的元数据记录。
     * @param filename 待删除的对象键
     */
    @Override
    public void delete(String filename) {
        Path file = load(filename);
        try {
            Files.delete(file);
        } catch (IOException e) {
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

        return address + keyName;
    }
}
