package org.linlinjava.litemall.os.web;

import org.linlinjava.litemall.db.domain.LitemallStorage;
import org.linlinjava.litemall.db.service.LitemallStorageService;
import org.linlinjava.litemall.db.util.CharUtil;
import org.linlinjava.litemall.db.util.ResponseUtil;
import org.linlinjava.litemall.os.service.StorageService;
import org.linlinjava.litemall.os.config.ObjectStorageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/storage/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;
    @Autowired
    private LitemallStorageService zmallStorageService;

    @Autowired
    private ObjectStorageConfig osConfig;

    private String generateUrl(String key){
        return "http://" + osConfig.getAddress() + ":" + osConfig.getPort() + "/storage/storage/fetch?key=" + key;
    }

    private final String generateKey(){
        String key = null;
        LitemallStorage storageInfo = null;

        do{
            key = CharUtil.getRandomString(20);
            storageInfo = zmallStorageService.findByKey(key);
        }
        while(storageInfo != null);

        return key;
    }

    @GetMapping("/list")
    public Object list(String key, String name,
                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                       String sort, String order){
        List<LitemallStorage> storageList = zmallStorageService.querySelective(key, name, page, limit, sort, order);
        int total = zmallStorageService.countSelective(key, name, page, limit, sort, order);
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
        String key = generateKey();
        storageService.store(inputStream, key);

        String url = generateUrl(key);
        LitemallStorage storageInfo = new LitemallStorage();
        storageInfo.setName(originalFilename);
        storageInfo.setSize((int)file.getSize());
        storageInfo.setType(file.getContentType());
        storageInfo.setModified(LocalDate.now());
        storageInfo.setKey(key);
        storageInfo.setUrl(url);
        zmallStorageService.add(storageInfo);
        return ResponseUtil.ok(storageInfo);
    }

    @PostMapping("/read")
    public Object read(Integer id) {
        if(id == null){
            return ResponseUtil.badArgument();
        }
        LitemallStorage storageInfo = zmallStorageService.findById(id);
        if(storageInfo == null){
            return ResponseUtil.badArgumentValue();
        }
        return ResponseUtil.ok(storageInfo);
    }

    @PostMapping("/update")
    public Object update(@RequestBody LitemallStorage litemallStorage) {

        zmallStorageService.update(litemallStorage);
        return ResponseUtil.ok(litemallStorage);
    }

    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallStorage litemallStorage) {
        zmallStorageService.deleteByKey(litemallStorage.getKey());
        storageService.delete(litemallStorage.getKey());
        return ResponseUtil.ok();
    }

    @GetMapping("/fetch")
    public ResponseEntity<Resource> fetch(String key) {

        Resource file = storageService.loadAsResource(key);

        if(file == null) {
            ResponseEntity.notFound();
        }
        return ResponseEntity.ok().body(file);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> download(String key) {

        Resource file = storageService.loadAsResource(key);
        if(file == null) {
            ResponseEntity.notFound();
        }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

}
