package com.pyg.pay.service;

import java.util.Map;

/**
 * @author corn
 * @version V1.0
 * @Package com.pyg.pay.service
 * @date 2019/4/9 18:23
 * @Copyright 无
 */
public interface WeixinPayService {

    /**
     * 生成微信支付二维码
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    public Map createNative(String out_trade_no,String total_fee);

    /**
     * 查询支付状态
     * @param out_trade_no
     * @return
     */
    public Map queryPayStatus(String out_trade_no);
    /**
     * 关闭支付
     * @param out_trade_no
     * @return
     */
    public Map closePay(String out_trade_no);
}
