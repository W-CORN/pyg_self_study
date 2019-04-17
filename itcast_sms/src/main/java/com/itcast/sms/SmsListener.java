package com.itcast.sms;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author corn
 * @version V1.0
 * @Package com.itcast.sms
 * @date 2019/4/3 18:30
 * @Copyright 无
 */
@Component
public class SmsListener {
    @Autowired
    private SmsUtil smsUtil;

    @JmsListener(destination = "sms")
    public void sendSms(Map<String,String> map){
        try {
            SendSmsResponse response = smsUtil.sendSms(
                    map.get("mobile"),
                    map.get("template_code"),
                    map.get("sign_name"),
                    map.get("param"));
            System.out.println("Code=" + response.getCode());
            System.out.println("Message=" + response.getMessage());
            System.out.println("RequestId=" + response.getRequestId());
            System.out.println("BizId=" + response.getBizId());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
