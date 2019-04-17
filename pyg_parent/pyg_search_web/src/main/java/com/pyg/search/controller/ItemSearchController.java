package com.pyg.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName ItemSearchController
 * @Author corn
 * @Date 2019/3/25 19:33
 **/
@RestController
@RequestMapping("/itemSearch")
public class ItemSearchController {
    @Reference
    private ItemSearchService itemSearchService;
    @RequestMapping("/search")
    public Map<String,Object> search(@RequestBody Map searchMap){
        return itemSearchService.search(searchMap);
    }
}
