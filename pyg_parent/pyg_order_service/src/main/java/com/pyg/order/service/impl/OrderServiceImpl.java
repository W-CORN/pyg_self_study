package com.pyg.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.common.IdWorker;
import com.pyg.dao.TbOrderItemMapper;
import com.pyg.dao.TbOrderMapper;
import com.pyg.dao.TbPayLogMapper;
import com.pyg.order.service.OrderService;
import com.pyg.pojo.TbOrder;
import com.pyg.pojo.TbOrderExample;
import com.pyg.pojo.TbOrderExample.Criteria;
import com.pyg.pojo.TbOrderItem;
import com.pyg.pojo.TbPayLog;
import com.pyg.pojogroup.Cart;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper tbOrderMapper;
    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;
    @Autowired
    private TbPayLogMapper tbPayLogMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;


    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return tbOrderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page = (Page<TbOrder>) tbOrderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbOrder order) {
        //从redis中取出购物车信息
        List<Cart> cartList= (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
        List<String> orderIdList=new ArrayList<>();//订单列表集合
        double total_money=0;//总金额 （元）
        for (Cart cart : cartList) {
            TbOrder tbOrder = new TbOrder();
            long orderId = idWorker.nextId();//获取orderId
            tbOrder.setOrderId(orderId);
            tbOrder.setPaymentType(order.getPaymentType());//支付类型
            tbOrder.setStatus("1");//未付款
            tbOrder.setCreateTime(new Date());//下单时间
            tbOrder.setUpdateTime(new Date());//更新时间
            tbOrder.setUserId(order.getUserId());
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());//收货人地址
            tbOrder.setReceiverMobile(order.getReceiverMobile());//收货人电话
            tbOrder.setReceiver(order.getReceiver());//收货人
            tbOrder.setSourceType(order.getSourceType());//订单来源
            tbOrder.setSellerId(cart.getSellerId());//商家ID
            double money=0;//金额总和
            for (TbOrderItem tbOrderItem : cart.getOrderItemList()) {
                tbOrderItem.setId(idWorker.nextId());
                tbOrderItem.setOrderId(orderId);
                tbOrderItem.setSellerId(cart.getSellerId());
                tbOrderItemMapper.insert(tbOrderItem);
                money+=tbOrderItem.getPrice().doubleValue();
            }
            tbOrder.setPayment(new BigDecimal(money));//合计
            tbOrderMapper.insert(tbOrder);
            orderIdList.add(orderId+"");
            total_money+=money;
        }
        if ("1".equals(order.getPaymentType())){//如果是微信支付
            TbPayLog tbPayLog=new TbPayLog();
            String outTradeNo = idWorker.nextId()+"";
            tbPayLog.setOutTradeNo(outTradeNo);//支付的订单号
            tbPayLog.setCreateTime(new Date());//创建时间
            String ids=orderIdList.toString().replace("[","").replace("]","").replace(" ","");
            tbPayLog.setOrderList(ids);//订单号列表，逗号分隔
            tbPayLog.setTotalFee( (long)( total_money*100)   );//金额（分）
            tbPayLog.setTradeState("0");//交易状态
            tbPayLog.setPayType("1");//微信
            tbPayLogMapper.insert(tbPayLog);
            redisTemplate.boundHashOps("payLog").put(order.getUserId(), tbPayLog);//放入缓存
        }
        //清空缓存
        redisTemplate.boundHashOps("cartLsit").delete(order.getUserId());
    }


    /**
     * 修改
     */
    @Override
    public void update(TbOrder order) {
        tbOrderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbOrder findOne(Long id) {
        return tbOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            tbOrderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();

        if (order != null) {
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
            }
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andPostFeeLike("%" + order.getPostFee() + "%");
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {
                criteria.andStatusLike("%" + order.getStatus() + "%");
            }
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andShippingNameLike("%" + order.getShippingName() + "%");
            }
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
            }
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + order.getUserId() + "%");
            }
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
            }
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
            }
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
            }
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
            }
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
            }
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {
                criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
            }
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + order.getSellerId() + "%");
            }

        }

        Page<TbOrder> page = (Page<TbOrder>) tbOrderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public TbPayLog searchPayLogFromRedis(String userId) {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        TbPayLog tbPayLog = tbPayLogMapper.selectByPrimaryKey(out_trade_no);
        tbPayLog.setPayTime(new Date());//修改时间
        tbPayLog.setTradeState("1");//已支付
        tbPayLog.setTransactionId(transaction_id);//交易号
        tbPayLogMapper.updateByPrimaryKey(tbPayLog);//修改
        String orderList = tbPayLog.getOrderList();//获取订单号
        String[] split = orderList.split(",");//获取订单号数组
        for (String s : split) {
            TbOrder tbOrder = tbOrderMapper.selectByPrimaryKey(Long.parseLong(s));
            if (tbOrder!=null){
                tbOrder.setStatus("2");//已付款
                tbOrderMapper.updateByPrimaryKey(tbOrder);
            }
        }
        redisTemplate.boundHashOps("payLog").delete(tbPayLog.getUserId());
    }

}
