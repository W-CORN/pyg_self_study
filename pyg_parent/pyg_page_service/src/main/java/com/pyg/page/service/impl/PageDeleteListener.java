package com.pyg.page.service.impl;

import com.pyg.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * @author corn
 * @version V1.0
 * @Package com.pyg.page.service.impl
 * @date 2019/4/2 18:32
 * @Copyright 无
 */
@Component
public class PageDeleteListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage=(ObjectMessage)message;
        try {
            Long[] goodsIds = (Long[]) objectMessage.getObject();
            System.out.println("获取到消息:"+goodsIds);
            boolean b = itemPageService.deleteItemHtml(goodsIds);
            System.out.println("生成:"+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
