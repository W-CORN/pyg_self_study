package com.pyg.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pyg.pojo.TbItem;
import com.pyg.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

/**
 * @author corn
 * @version V1.0
 * @Package com.pyg.search.service.impl
 * @date 2019/3/31 21:44
 * @Copyright 无
 */
@Component
public class ItemSearchAddListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage=(TextMessage)message;
        try {
            String text = textMessage.getText();
            System.out.println("监听到消息:"+text);
            List<TbItem> tbItems = JSON.parseArray(text, TbItem.class);
            itemSearchService.importList(tbItems);
            System.out.println("导入solr索引库");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
