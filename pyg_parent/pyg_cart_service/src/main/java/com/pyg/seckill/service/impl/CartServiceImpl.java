package com.pyg.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.seckill.service.CartService;
import com.pyg.dao.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbOrderItem;
import com.pyg.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author corn
 * @version V1.0
 * @Package com.pyg.seckill.service.impl
 * @date 2019/4/6 19:12
 * @Copyright 无
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<Cart> addGroupToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据商品 SKU ID 查询 SKU 商品信息
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        if (tbItem==null){
            throw  new RuntimeException("商品不存在");
        }
        if(!tbItem.getStatus().equals("1")){
            throw new RuntimeException("商品状态不合法");
        }
        //2.获取商家 ID
        String sellerId = tbItem.getSellerId();
        //3.根据商家 ID 判断购物车列表中是否存在该商家的购物车
        Cart cart = searchCartBySellerId(cartList, sellerId);
        if (cart==null) {//4.如果购物车列表中不存在该商家的购物车
            //4.1 新建购物车对象
            cart=new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(tbItem.getSeller());
            List<TbOrderItem> orderItemList=new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(tbItem, num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            //4.2 将新建的购物车对象添加到购物车列表
            cartList.add(cart);
        }else { //5.如果购物车列表中存在该商家的购物车
            // 查询购物车明细列表中是否存在该商品
            TbOrderItem tbOrderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            if (tbOrderItem==null) {//5.1. 如果没有，新增购物车明细
                TbOrderItem orderItem = createOrderItem(tbItem, num);
                cart.getOrderItemList().add(orderItem);
            }else {
                //5.2. 如果有，在原购物车明细上添加数量，更改金额
                tbOrderItem.setNum(tbOrderItem.getNum()+num);
                tbOrderItem.setTotalFee(new BigDecimal(tbOrderItem.getPrice().doubleValue()*tbOrderItem.getNum()));
                if (tbOrderItem.getNum()<=0){
                    cart.getOrderItemList().remove(tbOrderItem);
                }
                if (cart.getOrderItemList().size()==0){
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    /**
     * 从redis中查询数据
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从 redis 中提取购物车数据....."+username);
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if(cartList==null){
            cartList=new ArrayList();
        }
        return cartList;
    }

    /**
     * 向redis中添加数据
     * @param username
     * @param cartList
     */
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("向 redis 存入购物车数据....."+username);
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    /**
     * 合并cookie和redis中的数据
     * @param cartList1
     * @param cartList2
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        System.out.println("合并cookie数据");
        for (Cart cart : cartList2) {
            for (TbOrderItem tborderItem:cart.getOrderItemList()){
                cartList1=addGroupToCartList(cartList1,tborderItem.getItemId(),tborderItem.getNum());
            }
        }
        return cartList1;
    }

    //根据商家ID在购物车集合中查询对应的购物车对象
    private Cart searchCartBySellerId(List<Cart> cartList,String sellerId){
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }
    //根据skuID在购物车明细列表中查询购物车明细对象
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId){
        for (TbOrderItem tbOrderItem : orderItemList) {
            if(tbOrderItem.getItemId().longValue()==itemId.longValue()){
                return tbOrderItem;
            }
        }
        return null;
    }
    //创建购物车明细对象
    private TbOrderItem createOrderItem(TbItem item,Integer num){
        TbOrderItem tbOrderItem = new TbOrderItem();
        tbOrderItem.setNum(num);
        tbOrderItem.setItemId(item.getId());
        tbOrderItem.setGoodsId(item.getGoodsId());
        tbOrderItem.setPicPath(item.getImage());
        tbOrderItem.setPrice(item.getPrice());
        tbOrderItem.setTitle(item.getTitle());

        tbOrderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return tbOrderItem;
    }
}
