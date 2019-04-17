package com.pyg.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pay.service.WeixinPayService;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author corn
 * @version V1.0
 * @Package com.pyg.seckill.controller
 * @date 2019/4/9 18:42
 * @Copyright 无
 */
@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private WeixinPayService weixinPayService;
    @Reference
    private SeckillOrderService seckillOrderService;
    @RequestMapping("/createNative")
    public Map createNative(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //2.提取支付日志（从缓存 ）
        TbSeckillOrder tbSeckillOrder = seckillOrderService.searchOrderFromRedisByUserId(username);
        if (tbSeckillOrder!=null){
            Map aNative = weixinPayService.createNative(tbSeckillOrder.getId() + "", (long) (tbSeckillOrder.getMoney().doubleValue() * 100) + "");
            aNative.put("timeOut",tbSeckillOrder.getCreateTime());
            return aNative;
        }else {
            return new HashMap<>();
        }
    }
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Result result=null;
        int x=0;
        while (true){
            Map map = weixinPayService.queryPayStatus(out_trade_no);
            if (map==null){
                result=new Result(false,"支付发送错误");
                break;
            }
            if (map.get("trade_state").equals("SUCCESS")){//支付成功
                result=new Result(true,"支付成功");
                seckillOrderService.SeckillOrderService(username,Long.valueOf(out_trade_no), (String) map.get("transaction_id"));
                break;
            }
            //每个3秒测试一下
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x++;
            if (x>=90){
                result=new Result(false,"二维码超时");
                Map<String,String> payresult = weixinPayService.closePay(out_trade_no);
                if (!"SUCCESS".equals(payresult.get("result_code"))) {
                    if ("ORDERPAID".equals(payresult.get("err_code"))) {
                        result = new Result(true, "支付成功");
                        seckillOrderService.SeckillOrderService(username, Long.valueOf(out_trade_no), (String) map.get("transaction_id"));
                    }
                }
                if (result.isSuccess()==false){
                    System.out.println("超时取消订单");
                    seckillOrderService.deleteOrderFromRedis(username,Long.valueOf(out_trade_no));
                }
                break;
            }
        }
        return result;
    }

}
