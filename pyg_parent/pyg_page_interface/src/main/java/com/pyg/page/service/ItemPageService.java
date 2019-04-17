package com.pyg.page.service;

/**
 * @author corn
 * @version V1.0
 * @Package PACKAGE_NAME
 * @date 2019/3/30 19:00
 * @Copyright 无
 */
public interface ItemPageService {

    boolean genItemHtml(Long goodsId);
    public boolean deleteItemHtml(Long[] goodids);
}
