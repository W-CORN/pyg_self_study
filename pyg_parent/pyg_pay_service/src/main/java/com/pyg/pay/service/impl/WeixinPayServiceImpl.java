package com.pyg.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pyg.common.HttpClient;
import com.pyg.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author corn
 * @version V1.0
 * @Package com.pyg.pay.service.impl
 * @date 2019/4/9 18:24
 * @Copyright 无
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {
    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        try {
            //1.参数封装
            Map param=new HashMap();
            param.put("appid",appid);//公众账号ID
            param.put("mch_id",partner);//商户号
            param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            param.put("body","CORN");//商品描述
            param.put("out_trade_no",out_trade_no);//商品订单号
            param.put("total_fee",total_fee);//标记金额
            param.put("spbill_create_ip","127.0.0.1");//终端ip
            param.put("notify_url","http://www.itcast.cn");//通知地址
            param.put("trade_type","NATIVE");//交易类型
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("请求的参数为:"+xmlParam);
            //2.发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();
            //3.获取结果
            String xmlResult = httpClient.getContent();
            Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);
            System.out.println("返回的结果:"+mapResult);
            Map map=new HashMap();
            map.put("code_url",mapResult.get("code_url"));//生成支付二维码的链接
            map.put("out_trade_no", out_trade_no);
            map.put("total_fee", total_fee);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        try {
            //1.参数封装
            Map param=new HashMap();
            param.put("appid", appid);//公众账号 ID
            param.put("mch_id", partner);//商户号
            param.put("out_trade_no", out_trade_no);//订单号
            param.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符串
            String url="https://api.mch.weixin.qq.com/pay/orderquery";
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            //2.发送请求
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();
            //3.获取结果
            String xmlResult = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(xmlResult);
            System.out.println(map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 关闭支付
     * @param out_trade_no
     * @return
     */
    public Map closePay(String out_trade_no){
        try {
            //1.参数封装
            Map param=new HashMap();
            param.put("appid", appid);//公众账号 ID
            param.put("mch_id", partner);//商户号
            param.put("out_trade_no", out_trade_no);//订单号
            param.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符串
            String url="https://api.mch.weixin.qq.com/pay/closeorder";
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            //2.发送请求
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();
            //3.获取结果
            String xmlResult = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(xmlResult);
            System.out.println(map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
