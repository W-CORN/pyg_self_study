package com.pyg.sellergoods.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pyg.dao.TbBrandMapper;
import com.pyg.dao.TbSpecificationOptionMapper;
import com.pyg.dao.TbTypeTemplateMapper;
import com.pyg.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.dao.TbTypeTemplateMapper;
import com.pyg.pojo.TbTypeTemplateExample.Criteria;
import com.pyg.sellergoods.service.TypeTemplateService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbBrandMapper tbBrandMapper;
    /**
     * 查询全部
     */
    @Override
    public List<TbTypeTemplate> findAll() {
        return typeTemplateMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.insert(typeTemplate);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKey(typeTemplate);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbTypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbTypeTemplateExample example = new TbTypeTemplateExample();
        Criteria criteria = example.createCriteria();

        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
            if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
                criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
            }
            if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
                criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
            }
            if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
                criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
            }

        }

        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(example);
        saveToRedis();
        return new PageResult(page.getTotal(), page.getResult());
    }

    private void saveToRedis() {
        List<TbTypeTemplate> list = findAll();
        for (TbTypeTemplate tbTypeTemplate : list) {
            List<Map> maps = JSON.parseArray(tbTypeTemplate.getBrandIds(), Map.class);
            for (int i = 0; i < maps.size(); i++) {
                Map map = maps.get(i);
                Integer id = (Integer) map.get("id");
                long l = id.longValue();
                TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(l);
                String url = tbBrand.getUrl();
                map.put("url",url);
                maps.remove(i);
                maps.add(i,map);
            }
            redisTemplate.boundHashOps("brandList").put(tbTypeTemplate.getId(), maps);
            List<Map> specList = findSpecList(tbTypeTemplate.getId());
            redisTemplate.boundHashOps("specList").put(tbTypeTemplate.getId(), specList);
        }
        System.out.println("缓存品牌");
    }

    @Override
    public List<Map> findSpecList(Long id) {
        //查询模板
        TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(id);//模板
        //将JSON集合转换为List数组
        List<Map> list = JSON.parseArray(tbTypeTemplate.getSpecIds(), Map.class);
        for (Map map : list) {
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(new Long((Integer) map.get("id")));
            List<TbSpecificationOption> tbSpecificationOptions = specificationOptionMapper.selectByExample(example);
            map.put("options", tbSpecificationOptions);
        }
        return list;
    }


}
