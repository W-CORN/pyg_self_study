package com.pyg.seckill.service;

import com.pyg.pojogroup.Cart;

import java.util.List;

/**
 * @author corn
 * @version V1.0
 * @Package com.pyg.seckill.service
 * @date 2019/4/6 19:10
 * 购物车服务接口
 */
public interface CartService {
    public List<Cart> addGroupToCartList(List<Cart> cartList,Long itemId,Integer num);

    public List<Cart> findCartListFromRedis(String username);
    public void saveCartListToRedis(String username,List<Cart> cartList);
    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
}
