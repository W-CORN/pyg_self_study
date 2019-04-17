package com.pyg.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.dao.TbSeckillGoodsMapper;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.pojo.TbSeckillGoodsExample;
import com.pyg.pojo.TbSeckillGoodsExample.Criteria;
import com.pyg.seckill.service.SeckillGoodsService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private TbSeckillGoodsMapper tbSeckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询全部
     */
    @Override
    public List<TbSeckillGoods> findAll() {
        return tbSeckillGoodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSeckillGoods> page = (Page<TbSeckillGoods>) tbSeckillGoodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbSeckillGoods seckillGoods) {
        tbSeckillGoodsMapper.insert(seckillGoods);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbSeckillGoods seckillGoods) {
        tbSeckillGoodsMapper.updateByPrimaryKey(seckillGoods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbSeckillGoods findOne(Long id) {
        return tbSeckillGoodsMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            tbSeckillGoodsMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        Criteria criteria = example.createCriteria();

        if (seckillGoods != null) {
            if (seckillGoods.getTitle() != null && seckillGoods.getTitle().length() > 0) {
                criteria.andTitleLike("%" + seckillGoods.getTitle() + "%");
            }
            if (seckillGoods.getSmallPic() != null && seckillGoods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + seckillGoods.getSmallPic() + "%");
            }
            if (seckillGoods.getSellerId() != null && seckillGoods.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + seckillGoods.getSellerId() + "%");
            }
            if (seckillGoods.getStatus() != null && seckillGoods.getStatus().length() > 0) {
                criteria.andStatusLike("%" + seckillGoods.getStatus() + "%");
            }
            if (seckillGoods.getIntroduction() != null && seckillGoods.getIntroduction().length() > 0) {
                criteria.andIntroductionLike("%" + seckillGoods.getIntroduction() + "%");
            }

        }

        Page<TbSeckillGoods> page = (Page<TbSeckillGoods>) tbSeckillGoodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<TbSeckillGoods> findList() {
        List<TbSeckillGoods> seckillGoods = redisTemplate.boundHashOps("seckillGoods").values();
        if (seckillGoods == null || seckillGoods.size() == 0) {
            TbSeckillGoodsExample example = new TbSeckillGoodsExample();
            Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");//审核通过
            criteria.andStockCountGreaterThan(0);//库存大于0
            criteria.andStartTimeLessThanOrEqualTo(new Date());//小于等于当前时间
            criteria.andEndTimeGreaterThan(new Date());//大于当前时间
            seckillGoods = tbSeckillGoodsMapper.selectByExample(example);
            System.out.println("从数据裤中查询数据");
            System.out.println("数据存入缓存中");
            for (TbSeckillGoods seckillGood : seckillGoods) {
                redisTemplate.boundHashOps("seckillGoods").put(seckillGood.getId(),seckillGood);
            }
        }else {
            System.out.println("从redis中读取数据");
        }
        return seckillGoods;
    }

    @Override
    public TbSeckillGoods findOneFromRedis(Long id) {
        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);
        return seckillGoods;
    }

}
