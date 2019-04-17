package com.pyg.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.dao.TbGoodsDescMapper;
import com.pyg.dao.TbGoodsMapper;
import com.pyg.dao.TbItemCatMapper;
import com.pyg.dao.TbItemMapper;
import com.pyg.page.service.ItemPageService;
import com.pyg.pojo.TbGoods;
import com.pyg.pojo.TbGoodsDesc;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author corn
 * @version V1.0
 * @Package com.pyg.page.service.impl
 * @date 2019/3/30 19:03
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Value("${pagedir}")
    private String pagedir;
    @Autowired
    private TbGoodsMapper tbGoodsMapper;
    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;
    @Autowired
    private TbItemCatMapper tbItemCatMapper;
    @Autowired
    private TbItemMapper tbItemMapper;
    @Override
    public boolean genItemHtml(Long goodsId) {
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        try {
            Template template = configuration.getTemplate("item.ftl");
            Map map = new HashMap<>();
            TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);
            map.put("tbGoods",tbGoods);
            TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);
            map.put("tbGoodsDesc",tbGoodsDesc);
            //面包屑
            //3.商品分类获取
            String tbItemCat1Name = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String tbItemCat2Name = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String tbItemCat3Name = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
            map.put("tbItemCat1Name",tbItemCat1Name);
            map.put("tbItemCat2Name",tbItemCat2Name);
            map.put("tbItemCat3Name",tbItemCat3Name);
            //4.sku获取
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");
            criteria.andGoodsIdEqualTo(goodsId);
            example.setOrderByClause("is_default desc");
            List<TbItem> tbItems = tbItemMapper.selectByExample(example);
            map.put("itemList",tbItems);
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pagedir + goodsId + ".html"), "UTF-8"));
            template.process(map,out);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteItemHtml(Long[] goodids){
        try {
            for (Long goodId : goodids) {
                new File(pagedir + goodId + ".html").delete();
            }
            return true;
        }catch (Exception e){
            return false;
        }

    }
}
