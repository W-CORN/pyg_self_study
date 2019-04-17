package com.pyg.solr.util;

import com.alibaba.fastjson.JSON;
import com.pyg.dao.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @ClassName SolrItemMap
 * @Author corn
 * @Date 2019/3/25 18:25
 **/
@Component
public class SolrItemMap {
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private SolrTemplate solrTemplate;
    public void importItemData(){
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = tbItemMapper.selectByExample(example);
        System.out.println("开始");
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem.getId()+"   "+tbItem.getTitle());
            Map map = JSON.parseObject(tbItem.getSpec(),Map.class);
            tbItem.setSolrMap(map);
        }

        Scanner scanner = new Scanner(System.in);
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
        System.out.println("结束");
    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrItemMap solrItemMap = (SolrItemMap) context.getBean("solrItemMap");
        solrItemMap.testDeleteAll();
    }

    public void testDeleteAll(){
        Query query=new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

}
