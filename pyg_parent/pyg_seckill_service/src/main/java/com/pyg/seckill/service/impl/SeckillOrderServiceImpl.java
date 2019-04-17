package com.pyg.seckill.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.common.IdWorker;
import com.pyg.dao.TbSeckillGoodsMapper;
import com.pyg.dao.TbSeckillOrderMapper;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.pojo.TbSeckillOrderExample;
import com.pyg.pojo.TbSeckillOrderExample.Criteria;
import com.pyg.seckill.service.SeckillOrderService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper tbSeckillOrderMapper;
	@Autowired
	private TbSeckillGoodsMapper tbSeckillGoodsMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private IdWorker idWorker;

	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return tbSeckillOrderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) tbSeckillOrderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		tbSeckillOrderMapper.insert(seckillOrder);
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		tbSeckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return tbSeckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			tbSeckillOrderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
		
		Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)tbSeckillOrderMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void submitOrder(Long secikillId, String userId) {
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(secikillId);
		if (seckillGoods==null){
			throw new RuntimeException("商品不存在");
		}
		if (seckillGoods.getStockCount()<=0){
			throw new RuntimeException("商品已抢购一空");
		}
		seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
		redisTemplate.boundHashOps("seckillGoods").put(secikillId,seckillGoods);
		if (seckillGoods.getStockCount()==0){
			tbSeckillGoodsMapper.updateByPrimaryKey(seckillGoods);
			redisTemplate.boundHashOps("seckillGoods").delete(secikillId);
		}
		TbSeckillOrder tbSeckillOrder = new TbSeckillOrder();
		long id = idWorker.nextId();
		tbSeckillOrder.setId(id);//主键
		tbSeckillOrder.setCreateTime(new Date());//创建时间
		tbSeckillOrder.setSeckillId(secikillId);//秒杀商品ID
		tbSeckillOrder.setUserId(userId);//用户id
		tbSeckillOrder.setStatus("0");//状态
		tbSeckillOrder.setSellerId(seckillGoods.getSellerId());//商家id
		tbSeckillOrder.setMoney(seckillGoods.getCostPrice());
		redisTemplate.boundHashOps("seckillOrder").put(userId,tbSeckillOrder);

	}

	@Override
	public TbSeckillOrder searchOrderFromRedisByUserId(String userId) {
		return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
	}

	@Override
	public void SeckillOrderService(String userId, Long orderId, String transactionId) {
		TbSeckillOrder tbSeckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		if (tbSeckillOrder==null){
			throw new RuntimeException("订单不存在");
		}

		if (tbSeckillOrder.getId().longValue()!=orderId.longValue()){
			throw new RuntimeException("订单不相符");
		}
		tbSeckillOrder.setTransactionId(transactionId);//交易流水
		tbSeckillOrder.setPayTime(new Date());//交易时间
		tbSeckillOrder.setStatus("1");//支付类型
		tbSeckillOrderMapper.insert(tbSeckillOrder);//存储到数据
		redisTemplate.boundHashOps("seckillOrder").delete(tbSeckillOrder);
	}

	@Override
	public void deleteOrderFromRedis(String userId, Long orderId) {
		TbSeckillOrder tbSeckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		if (tbSeckillOrder!=null&&tbSeckillOrder.getId().longValue()==orderId.longValue()){
			redisTemplate.boundHashOps("seckillOrder").delete(userId);//清除缓存
			TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(tbSeckillOrder.getSeckillId());
			if (tbSeckillGoods!=null){
				tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount()+1);//如果存在则将库存数据+1
				redisTemplate.boundHashOps("seckillGoods").put(tbSeckillOrder.getSeckillId(),tbSeckillGoods);
			}else{
				TbSeckillGoods tbSeckillGoods1 = tbSeckillGoodsMapper.selectByPrimaryKey(tbSeckillOrder.getSeckillId());
				tbSeckillGoods.setId(tbSeckillGoods1.getId());
				tbSeckillGoods.setGoodsId(tbSeckillGoods1.getGoodsId());//spu ID
				tbSeckillGoods.setSellerId(tbSeckillGoods1.getSellerId());//SKU ID
				tbSeckillGoods.setTitle(tbSeckillGoods1.getTitle());//商品标题
				tbSeckillGoods.setSmallPic(tbSeckillGoods1.getSmallPic());//图片
				tbSeckillGoods.setCreateTime(tbSeckillGoods1.getCreateTime());//添加日期
				tbSeckillGoods.setCheckTime(tbSeckillGoods1.getCheckTime());//审核日期
				tbSeckillGoods.setStatus(tbSeckillGoods1.getStatus());//审核状态
				tbSeckillGoods.setStartTime(tbSeckillGoods1.getStartTime());//开始时间
				tbSeckillGoods.setEndTime(tbSeckillGoods1.getEndTime());//结束时间
				tbSeckillGoods.setNum(tbSeckillGoods1.getNum());//秒杀库存数
				tbSeckillGoods.setStockCount(1);//剩余库存数
				tbSeckillGoods.setIntroduction(tbSeckillGoods1.getIntroduction());//描述
				redisTemplate.boundHashOps("seckillGoods").put(tbSeckillOrder.getSeckillId(),tbSeckillGoods);
				tbSeckillGoodsMapper.updateByPrimaryKey(tbSeckillGoods);
			}

		}
	}

}
