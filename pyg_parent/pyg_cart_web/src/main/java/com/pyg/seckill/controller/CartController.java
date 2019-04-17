package com.pyg.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pyg.seckill.service.CartService;
import com.pyg.common.CookieUtil;
import com.pyg.pojogroup.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author corn
 * @version V1.0
 * @Package com.pyg.seckill.controller
 * @date 2019/4/6 19:38
 * @Copyright 无
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9105")
    public Result addGoodsToCartList(Long itemId, Integer num) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        try {
            List<Cart> cartList = findCartList();
            cartList = cartService.addGroupToCartList(cartList, itemId, num);
            if (username.equals("anonymousUser")) {//未登陆
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "UTF-8");
                System.out.println("向 cookie 存入数据");
            } else {//已登陆,则需要向redis中存储数据
                cartService.saveCartListToRedis(username,cartList);
            }
            return new Result(true, "存入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "存入购物车失败");
        }
    }

    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
            String cartList = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
            if (cartList == null || cartList.equals("")) {
                cartList = "[]";
            }
            List<Cart> cartList_cookie = JSON.parseArray(cartList, Cart.class);
        if (username.equals("anonymousUser")) {
            return cartList_cookie;
        } else {//已登录,从redis中读取数据
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            if (cartList_cookie.size()>0){
                List<Cart> carts = cartService.mergeCartList(cartList_cookie, cartList_redis);
                cartService.saveCartListToRedis(username,carts);
                CookieUtil.deleteCookie(request,response,"cartList");
                System.out.println("执行了合并购物车的逻辑");
                return carts;
            }
            return cartList_redis;
        }
    }
}
