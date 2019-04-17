package com.pyg.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.dao.TbBrandMapper;
import com.pyg.pojo.TbBrand;
import com.pyg.pojo.TbBrandExample;
import com.pyg.pojo.TbBrandExample.Criteria;
import com.pyg.sellergoods.service.BrandService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper brandMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbBrand> findAll() {
		return brandMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbBrand> page=   (Page<TbBrand>) brandMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void saveAddOne(TbBrand brand) {
		brandMapper.insert(brand);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void saveUpdateOne(TbBrand brand){
		brandMapper.updateByPrimaryKey(brand);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbBrand findOne(Long id){
		return brandMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delectById(Long ids) {
			brandMapper.deleteByPrimaryKey(ids);

	}
	
	
		@Override
	public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbBrandExample example=new TbBrandExample();
		Criteria criteria = example.createCriteria();
		
		if(brand!=null){			
						if(brand.getName()!=null && brand.getName().length()>0){
				criteria.andNameLike("%"+brand.getName()+"%");
			}
			if(brand.getFirstChar()!=null && brand.getFirstChar().length()>0){
				criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
			}
			if(brand.getUrl()!=null && brand.getUrl().length()>0){
				criteria.andUrlLike("%"+brand.getUrl()+"%");
			}
	
		}
		
		Page<TbBrand> page= (Page<TbBrand>)brandMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	@Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
