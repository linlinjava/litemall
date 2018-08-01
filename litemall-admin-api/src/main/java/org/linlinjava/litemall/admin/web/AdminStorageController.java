package org.linlinjava.litemall.admin.web;

import org.linlinjava.litemall.admin.annotation.LoginAdmin;
import org.linlinjava.litemall.core.storage.StorageService;
import org.linlinjava.litemall.core.util.CharUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallStorage;
import org.linlinjava.litemall.db.service.LitemallStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/storage")
@Validated
public class AdminStorageController {

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

    @GetMapping("/list")
    public Object list(@LoginAdmin Integer adminId,
                       String key, String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order){
        List<LitemallStorage> storageList = litemallStorageService.querySelective(key, name, page, limit, sort, order);
        int total = litemallStorageService.countSelective(key, name, page, limit, sort, order);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", storageList);

        return ResponseUtil.ok(data);
    }

    @PostMapping("/create")
    public Object create(@LoginAdmin Integer adminId, @RequestParam("file") MultipartFile file) {
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
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

    @PostMapping("/read")
    public Object read(@LoginAdmin Integer adminId, @NotNull Integer id) {
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        LitemallStorage storageInfo = litemallStorageService.findById(id);
        if(storageInfo == null){
            return ResponseUtil.badArgumentValue();
        }
        return ResponseUtil.ok(storageInfo);
    }

    @PostMapping("/update")
    public Object update(@LoginAdmin Integer adminId, @RequestBody LitemallStorage litemallStorage) {
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        litemallStorageService.update(litemallStorage);
        return ResponseUtil.ok(litemallStorage);
    }

    @PostMapping("/delete")
    public Object delete(@LoginAdmin Integer adminId, @RequestBody LitemallStorage litemallStorage) {
        if(adminId == null){
            return ResponseUtil.unlogin();
        }
        litemallStorageService.deleteByKey(litemallStorage.getKey());
        storageService.delete(litemallStorage.getKey());
        return ResponseUtil.ok();
    }
}
