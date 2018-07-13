package org.linlinjava.litemall.os.web;

import org.linlinjava.litemall.core.util.CharUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallStorage;
import org.linlinjava.litemall.db.service.LitemallStorageService;
import org.linlinjava.litemall.os.service.StorageService;
import org.linlinjava.litemall.os.config.ObjectStorageConfig;
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
@RequestMapping("/os/storage")
public class OsStorageController {

    @Autowired
    private StorageService storageService;
    @Autowired
    private LitemallStorageService litemallStorageService;

    @Autowired
    private ObjectStorageConfig osConfig;

    private String generateUrl(String key){
        String url = osConfig.getAddress() + ":" + osConfig.getPort() + "/os/storage/fetch/" + key;
        if(!url.startsWith("http")){
            url = "http://" + url;
        }
        return url;
    }

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

    @GetMapping("/list")
    public Object list(String key, String name,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                       String sort, String order){
        List<LitemallStorage> storageList = litemallStorageService.querySelective(key, name, page, limit, sort, order);
        int total = litemallStorageService.countSelective(key, name, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", storageList);

        return ResponseUtil.ok(data);
    }

    @PostMapping("/create")
    public Object create(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseUtil.badArgumentValue();
        }
        String key = generateKey(originalFilename);
        storageService.store(inputStream, key);

        String url = generateUrl(key);
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

    @PostMapping("/read")
    public Object read(Integer id) {
        if(id == null){
            return ResponseUtil.badArgument();
        }
        LitemallStorage storageInfo = litemallStorageService.findById(id);
        if(storageInfo == null){
            return ResponseUtil.badArgumentValue();
        }
        return ResponseUtil.ok(storageInfo);
    }

    @PostMapping("/update")
    public Object update(@RequestBody LitemallStorage litemallStorage) {

        litemallStorageService.update(litemallStorage);
        return ResponseUtil.ok(litemallStorage);
    }

    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallStorage litemallStorage) {
        litemallStorageService.deleteByKey(litemallStorage.getKey());
        storageService.delete(litemallStorage.getKey());
        return ResponseUtil.ok();
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
