package org.linlinjava.litemall.wx.web;

import org.linlinjava.litemall.core.storage.StorageService;
import org.linlinjava.litemall.core.util.CharUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallStorage;
import org.linlinjava.litemall.db.service.LitemallStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx/storage")
public class WxStorageController {

    @Autowired
    private StorageService storageService;
    @Autowired
    private LitemallStorageService litemallStorageService;

    private String generateKey(String originalFilename){
        int index = originalFilename.lastIndexOf('.');
        String suffix = originalFilename.substring(index);

        String key = null;
        LitemallStorage storageInfo = null;

        do{
            key = CharUtil.getRandomString(20) + suffix;
            storageInfo = litemallStorageService.findByKey(key);
        }
        while(storageInfo != null);

        return key;
    }

    @PostMapping("/upload")
    public Object upload(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseUtil.badArgumentValue();
        }
        String key = generateKey(originalFilename);
        storageService.store(file, key);

        String url = storageService.generateUrl(key);
        LitemallStorage storageInfo = new LitemallStorage();
        storageInfo.setName(originalFilename);
        storageInfo.setSize((int)file.getSize());
        storageInfo.setType(file.getContentType());
        storageInfo.setAddTime(LocalDateTime.now());
        storageInfo.setModified(LocalDateTime.now());
        storageInfo.setKey(key);
        storageInfo.setUrl(url);
        litemallStorageService.add(storageInfo);
        return ResponseUtil.ok(storageInfo);
    }

    @GetMapping("/fetch/{key:.+}")
    public ResponseEntity<Resource> fetch(@PathVariable String key) {
        LitemallStorage litemallStorage = litemallStorageService.findByKey(key);
        if(key == null){
            ResponseEntity.notFound();
        }
        String type = litemallStorage.getType();
        MediaType mediaType = MediaType.parseMediaType(type);

        Resource file = storageService.loadAsResource(key);
        if(file == null) {
            ResponseEntity.notFound();
        }
        return ResponseEntity.ok().contentType(mediaType).body(file);
    }

    @GetMapping("/download/{key:.+}")
    public ResponseEntity<Resource> download(@PathVariable String key) {
        LitemallStorage litemallStorage = litemallStorageService.findByKey(key);
        if(key == null){
            ResponseEntity.notFound();
        }
        String type = litemallStorage.getType();
        MediaType mediaType = MediaType.parseMediaType(type);

        Resource file = storageService.loadAsResource(key);
        if(file == null) {
            ResponseEntity.notFound();
        }
        return ResponseEntity.ok().contentType(mediaType).header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

}
