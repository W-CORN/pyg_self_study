package com.pyg.sellergoods.service;



import entity.PageResult;
import com.pyg.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    /**
     * 返回全部列表
     * @return
     */
    List<TbBrand> findAll();

    /**
     * 返回分页列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult findPage(int pageNum,int pageSize);

    /**
     * 增加数据
     * @param brand
     */
    void saveAddOne(TbBrand brand);

    /**
     * 查询一条数据
     * @param id
     * @return
     */
    TbBrand findOne(Long id);
    /**
     * 修改数据
     */
    void saveUpdateOne(TbBrand brand);

    /**
     * 删除一条数据
     * @param id
     */
    void delectById(Long id);

    /**
     * 根据条件查询
     * @param brand
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult findPage(TbBrand brand,int pageNum,int pageSize);

    List<Map> selectOptionList();
}
