package com.pyg.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.dao.*;
import com.pyg.pojo.*;
import com.pyg.pojo.TbGoodsExample.Criteria;
import com.pyg.pojogroup.Goods;
import com.pyg.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbSellerMapper sellerMapper;
    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        goods.getTbGoods().setAuditStatus("0");//未审核
        goods.getTbGoods().setIsMarketable("0");//上下架
        goodsMapper.insert(goods.getTbGoods());//插入基本信息
        goods.getTbGoodsDesc().setGoodsId(goods.getTbGoods().getId());//根据goods的id给tbGoodsDesc中个goodsid添加id
        goodsDescMapper.insert(goods.getTbGoodsDesc());//插入基本信息
        saveItemList(goods);
    }

    /**
     * 私有化向数据中添加数据
     *
     * @param goods
     */
    private void saveItemList(Goods goods) {
        if ("1".equals(goods.getTbGoods().getIsEnableSpec())) {
            for (TbItem tbItem : goods.getItemList()) {
                String title = goods.getTbGoods().getGoodsName();
                Map<String, Object> map = JSON.parseObject(tbItem.getSpec());
                for (String s : map.keySet()) {
                    title += "" + map.get(s);
                }
                tbItem.setTitle(title);
                setItemValues(goods, tbItem);
                itemMapper.insert(tbItem);
            }
        } else {
            TbItem item = new TbItem();
            item.setTitle(goods.getTbGoods().getGoodsName());//名称
            item.setPrice(goods.getTbGoods().getPrice());//价格
            item.setNum(9999);//数量
            item.setStatus("1");//状态
            item.setIsDefault("1");//默认
            item.setSpec("{}");//规格
            setItemValues(goods, item);
            itemMapper.insert(item);
        }
    }

    /**
     * 向item中添加页面上没有获取的值
     *
     * @param goods
     * @param item
     */
    private void setItemValues(Goods goods, TbItem item) {
        item.setCategoryid(goods.getTbGoods().getCategory3Id());//三级分类ID
        item.setCreateTime(new Date());//创建日期
        item.setUpdateTime(new Date());//更新日期
        item.setGoodsId(goods.getTbGoods().getId());//商品ID
        item.setSellerId(goods.getTbGoods().getSellerId());//商家id
        //分类名称
        TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
        item.setCategory(tbItemCat.getName());
        //品牌
        TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
        item.setBrand(tbBrand.getName());
        //商家名称
        TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
        item.setSellerId(tbSeller.getNickName());
        //图片
        List<Map> list = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(), Map.class);
        if (list.size() > 0) {
            item.setImage((String) list.get(0).get("url"));
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        //设置为未申请状态；如果是修改的商品需要重新申请
        goods.getTbGoods().setAuditStatus("0");
        goodsMapper.updateByPrimaryKey(goods.getTbGoods());
        goodsDescMapper.updateByPrimaryKey(goods.getTbGoodsDesc());
        //先清楚原有的SKU数据
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getTbGoods().getId());
        itemMapper.deleteByExample(example);
        //添加新的SKU数据
        saveItemList(goods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();
        //获取tbGoods
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setTbGoods(tbGoods);
        //获取tbGoodsDesc
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setTbGoodsDesc(tbGoodsDesc);
        //获取相关的tbItems
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        goods.setItemList(tbItems);
        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

    /**
     * 分页查询
     *
     * @param goods
     * @param pageNum  当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsDeleteIsNull();
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }
        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 审核修改
     *
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

    @Override
    public Result updateIsMarketable(Long[] ids, String isMarketable) {
        try {
            String frames="";
            for (Long id : ids) {
                TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
                //判断是否上下架
                if (isMarketable.equals("1")) {
                    //获取审核状态
                    String auditStatus = tbGoods.getAuditStatus();
                    //判断是否为已审核
                    if (auditStatus.equals("1")) {
                        //是的，则修改上架
                        tbGoods.setIsMarketable(isMarketable);
                        goodsMapper.updateByPrimaryKey(tbGoods);
                        frames="上架成功";
                    } else {
                        return new Result(false, "审核未通过,不允许上架");
                    }
                } else {
                    tbGoods.setIsMarketable(isMarketable);
                    goodsMapper.updateByPrimaryKey(tbGoods);
                    frames="下架成功";
                }
            }
            return new Result(true, frames);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上架出现异常");
        }
    }

    @Override
    public List<TbItem> findItemListByGoodsIdandStatus(Long[] ids, String status) {
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(ids));
        criteria.andStatusEqualTo(status);
        return itemMapper.selectByExample(example);
    }


}
