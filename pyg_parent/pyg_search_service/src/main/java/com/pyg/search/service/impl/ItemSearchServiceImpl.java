package com.pyg.search.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.pyg.pojo.TbItem;
import com.pyg.search.service.ItemSearchService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ItemSearchServiceImpl
 * @Author corn
 * @Date 2019/3/25 19:30
 **/
@Service(timeout=5000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map<String,Object> search(Map searchMap){
        Map map = new HashMap();
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));
        //查询所有信息
        map.putAll(searchList(searchMap));
        //查询分组信息
        List<String> list = searchCategoryList(searchMap);
        map.put("categoryList",list);
        if (list.size()>0){
            map.putAll(searchBrandAndSpecList(list.get(0)));
        }
        return map;
    }

    /**
     * 审核通过修改solr数据
     * @param list
     */
    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteList(List list) {
        Query query = new SimpleQuery();
        Criteria item_goodsid = new Criteria("item_goodsid").in(list);
        query.addCriteria(item_goodsid);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    private Map<String, Object> searchList(Map searchMap) {
        Map map = new HashMap();
        SimpleHighlightQuery highlightQuery = new SimpleHighlightQuery();
        //设置高亮区域
        HighlightOptions item_title = new HighlightOptions().addField("item_title");
        //设置高亮区域的前后属性
        item_title.setSimplePrefix("<em style='color:red'>");
        item_title.setSimplePostfix("</em>");
        highlightQuery.setHighlightOptions(item_title);
        //分页查询数据
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        highlightQuery.addCriteria(criteria);
        //1. 按商品分类过滤
        if (!StringUtils.isEmpty((String) searchMap.get("category"))){
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria criteria1 = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(criteria1);
            highlightQuery.addFilterQuery(filterQuery);
        }
        //2. 按照商品品牌过滤
        if (!StringUtils.isEmpty((String) searchMap.get("brand"))){
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria criteria1 = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(criteria1);
            highlightQuery.addFilterQuery(filterQuery);
        }
        //3. 按照规格过滤
        if (searchMap.get("spec")!=null){
            Map<String,String> spec = (Map<String, String>) searchMap.get("spec");
            for (String key:spec.keySet()){
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria criteria1 = new Criteria("item_spec_"+key).is(spec.get(key));
                filterQuery.addCriteria(criteria1);
                highlightQuery.addFilterQuery(filterQuery);
            }
        }
        //4. 价钱查询
        if (!StringUtils.isEmpty((String) searchMap.get("price"))){
            String[] prices = ((String) searchMap.get("price")).split("-");
            if (!prices[0].equals("0")){//如果最低不等于0
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria criteria1 = new Criteria("item_price").greaterThanEqual(prices[0]);
                filterQuery.addCriteria(criteria1);
                highlightQuery.addFilterQuery(filterQuery);
            }
            if (!prices[1].equals("*")){//最高不等于*
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria criteria1 = new Criteria("item_price").lessThanEqual(prices[1]);
                filterQuery.addCriteria(criteria1);
                highlightQuery.addFilterQuery(filterQuery);
            }
        }

        //5. 分页查询
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo==null){
            pageNo=1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize==null){
            pageSize=40;
        }
        highlightQuery.setOffset((pageNo-1)*pageSize );
        highlightQuery.setRows(pageSize);
        //6. 排序查询
        String sortValue= (String) searchMap.get("sort");//获取排序方式
        String sortField= (String) searchMap.get("sortField");//获取排序字段
        if (!sortValue.equals("")&&!sortField.equals("")){
            if (sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                highlightQuery.addSort(sort);
            }
            if (sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                highlightQuery.addSort(sort);
            }
        }
        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(highlightQuery, TbItem.class);
        List<HighlightEntry<TbItem>> highlighted = tbItems.getHighlighted();
        for (HighlightEntry<TbItem> tbItem: highlighted) {
            //获取高亮列表
            List<HighlightEntry.Highlight> h = tbItem.getHighlights();
            if (h.size()>0&&h.get(0).getSnipplets().size()>0){
                //获取原始的集合内容
                TbItem entity = tbItem.getEntity();
                //获去高亮内容并进行修改
                entity.setTitle(h.get(0).getSnipplets().get(0));
            }
        }
        map.put("rows",tbItems.getContent());
        map.put("totalPages",tbItems.getTotalPages());//总页数
        map.put("total",tbItems.getTotalElements());//总记录数
        return map;
    }

    private List<String> searchCategoryList(Map searchMap){
        List list = new ArrayList();
        SimpleQuery query = new SimpleQuery();
        //增加条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //获取分组选项
        GroupOptions item_category = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(item_category);
        //获取分组页
        GroupPage<TbItem> tbItems = solrTemplate.queryForGroupPage(query, TbItem.class);
        //获取分页结果
        GroupResult<TbItem> groupResult = tbItems.getGroupResult("item_category");
        //获取分页入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //获取分页结果数据
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
            list.add(tbItemGroupEntry.getGroupValue());
        }
        return list;
    }

    private Map searchBrandAndSpecList(String category){
        Map map = new HashMap();
        Long itemCat = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (itemCat!=null){
            List<Map> brandList = (List) redisTemplate.boundHashOps("brandList").get(itemCat);
            map.put("brandList",brandList);
            List specList = (List) redisTemplate.boundHashOps("specList").get(itemCat);
            map.put("specList",specList);
        }
        return map;
    }

}
