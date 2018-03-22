package org.linlinjava.litemall.os.service;

import org.springframework.core.io.Resource;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void store(InputStream inputStream, String filename);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void delete(String filename);
}