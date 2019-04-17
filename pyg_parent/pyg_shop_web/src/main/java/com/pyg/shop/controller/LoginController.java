package com.pyg.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName LoginController
 * @Author corn
 * @Date 2019/3/18 9:09
 **/
@RestController
@RequestMapping("/login")
public class LoginController {
    @RequestMapping("/name")
    public Map name(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, String> map = new HashMap<String, String>();
        map.put("loginName",name);
        return map;
    }
}
