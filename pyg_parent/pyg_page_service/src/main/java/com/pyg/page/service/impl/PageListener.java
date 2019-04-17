package com.pyg.page.service.impl;

import com.pyg.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @author corn
 * @version V1.0
 * @Package com.pyg.page.service.impl
 * @date 2019/4/2 18:28
 * @Copyright 生成页面
 */
@Component
public class PageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage=(TextMessage)message;
        try {
            String text = textMessage.getText();
            System.out.println("收到消息为:"+text);
            boolean b = itemPageService.genItemHtml(Long.parseLong(text));
            System.out.println("结果为:"+b);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
