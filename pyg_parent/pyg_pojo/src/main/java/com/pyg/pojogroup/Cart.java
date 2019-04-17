package com.pyg.pojogroup;

import com.pyg.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * @author corn
 * @version V1.0
 * @Package com.pyg.pojogroup
 * @date 2019/4/6 18:59
 * @Copyright 无
 */
public class Cart implements Serializable {
    private String sellerId;//商家Id
    private  String sellerName;//商家名称
    private List<TbOrderItem> orderItemList;//购物车明细

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
