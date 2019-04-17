package com.pyg.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.order.service.OrderService;
import com.pyg.pay.service.WeixinPayService;
import com.pyg.pojo.TbPayLog;
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
    private OrderService orderService;
    @RequestMapping("/createNative")
    public Map createNative(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //2.提取支付日志（从缓存 ）
        TbPayLog payLog = orderService.searchPayLogFromRedis(username);
        if (payLog!=null){
            return weixinPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee()+"");
        }else {
            return new HashMap<>();
        }
    }
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
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
                orderService.updateOrderStatus(out_trade_no, (String) map.get("transaction_id"));
                break;
            }
            //每个3秒测试一下
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x++;
            if (x>=5){
                result=new Result(false,"二维码超时");
                break;
            }
        }
        return result;
    }
}
