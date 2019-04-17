package com.pyg.sellergoods.service.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.dao.TbSellerMapper;
import com.pyg.pojo.TbSeller;
import com.pyg.pojo.TbSellerExample;
import com.pyg.pojo.TbSellerExample.Criteria;
import com.pyg.sellergoods.service.SellerService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SellerServiceImpl implements SellerService {

	@Autowired
	private TbSellerMapper sellerMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeller> findAll() {

		List<TbSeller> tbSellers = sellerMapper.selectByExample(null);
		for (int i=0;i<tbSellers.size();i++) {
			TbSeller tbSeller = tbSellers.get(i);
			if (tbSeller.getStatus().equals("0")){
				tbSeller.setStatus("未审核");
			}else if (tbSeller.getStatus().equals("1")){
				tbSeller.setStatus("审核通过");
			}else if (tbSeller.getStatus().equals("2")){
				tbSeller.setStatus("审核未通过");
			}else if (tbSeller.getStatus().equals("3")){
				tbSeller.setStatus("关闭店铺");
			}
			tbSellers.set(i,tbSeller);
		}
		return tbSellers;
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeller> page=   (Page<TbSeller>) sellerMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeller seller) {
		seller.setStatus("0");
		seller.setCreateTime(new Date());
		sellerMapper.insert(seller);
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeller seller){
		sellerMapper.updateByPrimaryKey(seller);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeller findOne(String id){
		return sellerMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(String[] ids) {
		for(String id:ids){
			sellerMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeller seller, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSellerExample example=new TbSellerExample();
		Criteria criteria = example.createCriteria();

		
		if(seller!=null){			
						if(seller.getSellerId()!=null && seller.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seller.getSellerId()+"%");
			}
			if(seller.getName()!=null && seller.getName().length()>0){
				criteria.andNameLike("%"+seller.getName()+"%");
			}
			if(seller.getNickName()!=null && seller.getNickName().length()>0){
				criteria.andNickNameLike("%"+seller.getNickName()+"%");
			}
			if(seller.getPassword()!=null && seller.getPassword().length()>0){
				criteria.andPasswordLike("%"+seller.getPassword()+"%");
			}
			if(seller.getEmail()!=null && seller.getEmail().length()>0){
				criteria.andEmailLike("%"+seller.getEmail()+"%");
			}
			if(seller.getMobile()!=null && seller.getMobile().length()>0){
				criteria.andMobileLike("%"+seller.getMobile()+"%");
			}
			if(seller.getTelephone()!=null && seller.getTelephone().length()>0){
				criteria.andTelephoneLike("%"+seller.getTelephone()+"%");
			}
			if(seller.getStatus()!=null && seller.getStatus().length()>0){
				criteria.andStatusEqualTo(seller.getStatus());
			}
			if(seller.getAddressDetail()!=null && seller.getAddressDetail().length()>0){
				criteria.andAddressDetailLike("%"+seller.getAddressDetail()+"%");
			}
			if(seller.getLinkmanName()!=null && seller.getLinkmanName().length()>0){
				criteria.andLinkmanNameLike("%"+seller.getLinkmanName()+"%");
			}
			if(seller.getLinkmanQq()!=null && seller.getLinkmanQq().length()>0){
				criteria.andLinkmanQqLike("%"+seller.getLinkmanQq()+"%");
			}
			if(seller.getLinkmanMobile()!=null && seller.getLinkmanMobile().length()>0){
				criteria.andLinkmanMobileLike("%"+seller.getLinkmanMobile()+"%");
			}
			if(seller.getLinkmanEmail()!=null && seller.getLinkmanEmail().length()>0){
				criteria.andLinkmanEmailLike("%"+seller.getLinkmanEmail()+"%");
			}
			if(seller.getLicenseNumber()!=null && seller.getLicenseNumber().length()>0){
				criteria.andLicenseNumberLike("%"+seller.getLicenseNumber()+"%");
			}
			if(seller.getTaxNumber()!=null && seller.getTaxNumber().length()>0){
				criteria.andTaxNumberLike("%"+seller.getTaxNumber()+"%");
			}
			if(seller.getOrgNumber()!=null && seller.getOrgNumber().length()>0){
				criteria.andOrgNumberLike("%"+seller.getOrgNumber()+"%");
			}
			if(seller.getLogoPic()!=null && seller.getLogoPic().length()>0){
				criteria.andLogoPicLike("%"+seller.getLogoPic()+"%");
			}
			if(seller.getBrief()!=null && seller.getBrief().length()>0){
				criteria.andBriefLike("%"+seller.getBrief()+"%");
			}
			if(seller.getLegalPerson()!=null && seller.getLegalPerson().length()>0){
				criteria.andLegalPersonLike("%"+seller.getLegalPerson()+"%");
			}
			if(seller.getLegalPersonCardId()!=null && seller.getLegalPersonCardId().length()>0){
				criteria.andLegalPersonCardIdLike("%"+seller.getLegalPersonCardId()+"%");
			}
			if(seller.getBankUser()!=null && seller.getBankUser().length()>0){
				criteria.andBankUserLike("%"+seller.getBankUser()+"%");
			}
			if(seller.getBankName()!=null && seller.getBankName().length()>0){
				criteria.andBankNameLike("%"+seller.getBankName()+"%");
			}
	
		}
		
		Page<TbSeller> page= (Page<TbSeller>)sellerMapper.selectByExample(example);
			List<TbSeller> result = page.getResult();
			for (int i=0;i<result.size();i++) {
				TbSeller tbSeller = result.get(i);
				if (tbSeller.getStatus().equals("0")){
					tbSeller.setStatus("未审核");
				}else if (tbSeller.getStatus().equals("1")){
					tbSeller.setStatus("审核通过");
				}else if (tbSeller.getStatus().equals("2")){
					tbSeller.setStatus("审核未通过");
				}else if (tbSeller.getStatus().equals("3")){
					tbSeller.setStatus("关闭店铺");
				}
				result.set(i,tbSeller);
			}
			return new PageResult(page.getTotal(), result);
	}

	@Override
	public void updateStatus(String sellerId, String status) {
		TbSeller tbSeller = sellerMapper.selectByPrimaryKey(sellerId);
		tbSeller.setStatus(status);
		 sellerMapper.updateByPrimaryKey(tbSeller);
	}

	@Override
	public List<TbSeller> findByIds(String status) {
		TbSellerExample example=new TbSellerExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(status);
		List<TbSeller> tbSellers = sellerMapper.selectByExample(example);
		for (int i=0;i<tbSellers.size();i++) {
			TbSeller tbSeller = tbSellers.get(i);
			if (tbSeller.getStatus().equals("0")){
				tbSeller.setStatus("未审核");
			}else if (tbSeller.getStatus().equals("1")){
				tbSeller.setStatus("审核通过");
			}else if (tbSeller.getStatus().equals("2")){
				tbSeller.setStatus("审核未通过");
			}else if (tbSeller.getStatus().equals("3")){
				tbSeller.setStatus("关闭店铺");
			}
			tbSellers.set(i,tbSeller);
		}

		return tbSellers;
	}

}
