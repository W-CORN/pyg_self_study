package com.pyg.search.service.impl;

import com.pyg.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

/**
 * @author corn
 * @version V1.0
 * @Package com.pyg.search.service.impl
 * @date 2019/4/2 13:42
 * @Copyright 无
 */
@Component
public class ItemSearchDeleteListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage=(ObjectMessage)message;
        try {
            Long[] goodsIds = (Long[]) objectMessage.getObject();
            System.out.println("获取到消息:"+goodsIds);
            itemSearchService.deleteList(Arrays.asList(goodsIds));
            System.out.println("删除索引库数据");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
