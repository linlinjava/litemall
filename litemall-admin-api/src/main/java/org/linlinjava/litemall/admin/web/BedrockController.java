package org.linlinjava.litemall.admin.web;
import org.linlinjava.litemall.admin.dto.Prompt;
import org.linlinjava.litemall.admin.service.BedrockService;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin/bedrock")
public class BedrockController {

    @Autowired
    private BedrockService bedrockService;

    @PostMapping(value = "/ask")
    public Object askAssistant(@RequestBody Prompt prompt) {

        List<String> response = bedrockService.invokeMistral7B(prompt.getPrompt());
        return ResponseUtil.okList(response);
    }

}