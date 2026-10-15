package org.linlinjava.litemall.core.storage;

import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * 对象存储契约，使上传调用方不依赖本地磁盘或具体云厂商。
 * <p>对象键用于定位文件，不等同于原始文件名；资源可用性、异常处理和路径支持
 * 取决于实现。三个云实现的 loadAll/load 当前返回 null。
 */
public interface Storage {

    /**
     * 保存文件内容，以对象键而非用户原始文件名定位对象。
     * @param inputStream 文件内容输入流
     * @param contentLength 文件字节数
     * @param contentType 文件 MIME 类型
     * @param keyName 由调用方生成的可信对象键
     */
    void store(InputStream inputStream, long contentLength, String contentType, String keyName);

    /**
     * 枚举存储对象的相对路径，仅本地实现提供支持。
     * @return 路径流，调用方负责关闭；不支持时返回 null
     */
    Stream<Path> loadAll();

    /**
     * 将对象键映射为本地路径，不检查存在性。
     * @param keyName 存储对象键
     * @return 本地路径；云实现返回 null
     */
    Path load(String keyName);

    /**
     * 将对象键转换为可读取的资源，供文件下载使用。
     * @param keyName 存储对象键
     * @return 可用资源；地址无效或不可访问时可能返回 null
     */
    Resource loadAsResource(String keyName);

    /**
     * 删除底层存储对象，不负责删除数据库中的元数据记录。
     * @param keyName 待删除的对象键
     */
    void delete(String keyName);

    /**
     * 根据存储配置拼接访问地址，不检查对象存在性或生成签名凭证。
     * @param keyName 存储对象键
     * @return 对象访问地址
     */
    String generateUrl(String keyName);
}