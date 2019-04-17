package com.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pyg.pojo.TbGoods;
import com.pyg.pojo.TbItem;
import com.pyg.pojogroup.Goods;
import com.pyg.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination queueTextDestination;
    @Autowired
    private Destination queueTextDeleteDestination;
    @Reference
    private GoodsService goodsService;
    @Autowired
    private Destination topicPageDestination;
    @Autowired
    private Destination topicPageDeleteDestination;
    //@Reference(timeout=100000)
    //private ItemSearchService itemSearchService;
    //@Reference(timeout=40000)
    //private ItemPageService itemPageService;

    /**
     * 返回全部列表
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

    /**
     * 返回全部列表
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 增加
     * @param goods
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            goodsService.add(goods);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            goodsService.update(goods);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findOne(id);
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(final Long[] ids) {
        try {
            goodsService.delete(ids);
            //searchService.deleteList(Arrays.asList(ids));
            jmsTemplate.send(queueTextDeleteDestination, (session)-> {
                    return session.createObjectMessage(ids);
            });
            jmsTemplate.send(topicPageDeleteDestination,(session)->{
                    return session.createObjectMessage(ids);
            });
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        return goodsService.findPage(goods, page, rows);
    }

    /**
     * 审核修改
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids, status);
            if (status.equals("1")){
                List<TbItem> itemListByGoodsIdandStatus = goodsService.findItemListByGoodsIdandStatus(ids, status);
                if (itemListByGoodsIdandStatus.size()>0){
                    //searchService.importList(itemListByGoodsIdandStatus);
                    final String s = JSON.toJSONString(itemListByGoodsIdandStatus);
                    jmsTemplate.send(queueTextDestination, (session)-> {
                            return session.createTextMessage(s);
                    });
                }else {
                    System.out.println("无明显数据");
                }
                //静态页面生成
                for (Long goodsId : ids) {
                    jmsTemplate.send(topicPageDestination,(session)->{
                        return session.createTextMessage(goodsId+"");
                    });
                    //itemPageService.genItemHtml(goodsId);
                }
            }
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

   /*
   @RequestMapping("/genHtml")
    public void genHtml(Long goodsId){
        itemPageService.genItemHtml(goodsId);
    }
    */
}
