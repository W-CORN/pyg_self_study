package com.pyg.pojogroup;

import com.pyg.pojo.TbGoods;
import com.pyg.pojo.TbGoodsDesc;
import com.pyg.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName Goods
 * @Author corn
 * @Date 2019/3/18 18:50
 **/
public class Goods implements Serializable{

    private TbGoods tbGoods;
    private TbGoodsDesc tbGoodsDesc;
    private List<TbItem> ItemList;

    public TbGoods getTbGoods() {
        return tbGoods;
    }

    public void setTbGoods(TbGoods tbGoods) {
        this.tbGoods = tbGoods;
    }

    public TbGoodsDesc getTbGoodsDesc() {
        return tbGoodsDesc;
    }

    public void setTbGoodsDesc(TbGoodsDesc tbGoodsDesc) {
        this.tbGoodsDesc = tbGoodsDesc;
    }

    public List<TbItem> getItemList() {
        return ItemList;
    }

    public void setItemList(List<TbItem> ItemList) {
        this.ItemList = ItemList;
    }
}
