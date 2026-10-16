package org.linlinjava.litemall.core.storage;

import org.linlinjava.litemall.core.util.CharUtil;
import org.linlinjava.litemall.db.domain.LitemallStorage;
import org.linlinjava.litemall.db.service.LitemallStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * 商城文件存储入口，协调对象上传与 litemall_storage 元数据登记。
 * <p>先调用存储实现，再写入数据库，两者不构成跨系统原子事务；
 * 云实现可能仅记录上传异常，因此返回元数据不等于远端文件已经可用。
 */
public class StorageService {
    private String active;
    private Storage storage;
    @Autowired
    private LitemallStorageService litemallStorageService;

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    /**
     * 生成随机对象键、上传文件，再登记文件元数据。
     * <p>调用方必须提供包含扩展名的原始文件名；底层上传和数据库写入不是原子操作。
     * @param inputStream 文件内容输入流
     * @param contentLength 文件字节数，登记时转换为 int，调用方应限制上传大小
     * @param contentType 文件 MIME 类型
     * @param fileName 含扩展名的原始文件名，用于显示及提取后缀
     * @return 已写入数据库的文件元数据，不代表云端上传已经确认成功
     * @throws StringIndexOutOfBoundsException 文件名不包含点号，无法提取后缀时
     */
    public LitemallStorage store(InputStream inputStream, long contentLength, String contentType, String fileName) {
        String key = generateKey(fileName);
        storage.store(inputStream, contentLength, contentType, key);

        String url = generateUrl(key);
        LitemallStorage storageInfo = new LitemallStorage();
        storageInfo.setName(fileName);
        storageInfo.setSize((int) contentLength);
        storageInfo.setType(contentType);
        storageInfo.setKey(key);
        storageInfo.setUrl(url);
        litemallStorageService.add(storageInfo);

        return storageInfo;
    }

    /**
     * 保留文件后缀并生成随机键，避免不同用户的同名文件互相覆盖。
     * <p>查库重试降低重复风险，但不构成并发唯一性保证。
     * @param originalFilename 含点号及后缀的原始文件名
     * @return 20 位随机字符串与原始后缀组成的对象键
     * @throws StringIndexOutOfBoundsException 文件名不含点号时
     */
    private String generateKey(String originalFilename) {
        int index = originalFilename.lastIndexOf('.');
        String suffix = originalFilename.substring(index);

        String key = null;
        LitemallStorage storageInfo = null;

        do {
            key = CharUtil.getRandomString(20) + suffix;
            storageInfo = litemallStorageService.findByKey(key);
        }
        while (storageInfo != null);

        return key;
    }

    /**
     * 委托当前存储实现枚举对象。
     * @return 本地路径流（使用后应关闭）；云实现返回 null
     */
    public Stream<Path> loadAll() {
        return storage.loadAll();
    }

    /**
     * 委托当前存储实现定位对象。
     * @param keyName 存储对象键
     * @return 本地路径；云实现返回 null
     */
    public Path load(String keyName) {
        return storage.load(keyName);
    }

    /**
     * 将对象键转换为可读取的资源，供文件下载使用。
     * @param keyName 存储对象键
     * @return 可用资源；地址无效或不可访问时可能返回 null
     */
    public Resource loadAsResource(String keyName) {
        return storage.loadAsResource(keyName);
    }

    /**
     * 删除底层存储对象，不负责删除数据库中的元数据记录。
     * @param keyName 待删除的对象键
     */
    public void delete(String keyName) {
        storage.delete(keyName);
    }

    /**
     * 根据存储配置拼接访问地址，不检查对象存在性或生成签名凭证。
     * @param keyName 存储对象键
     * @return 对象访问地址
     */
    private String generateUrl(String keyName) {
        return storage.generateUrl(keyName);
    }
}
