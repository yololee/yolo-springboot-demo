package com.yolo.mybatis.plus.page;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yolo.mybatis.plus.util.ServletUtils;
import com.yolo.mybatis.plus.util.SqlUtil;


/**
 * 分页工具类
 */
public class PageUtils{

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";


    /**
     * 设置请求分页数据
     */
    public static <E>  Page<E> startPage(Class<E> clazz) {
        PageDomain pageDomain = getPageDomain();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        String isAsc = pageDomain.getIsAsc();
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        Page<E> page = new Page<>(pageNum, pageSize);

        if (StrUtil.isNotBlank(orderBy) && StrUtil.isNotBlank(isAsc)){
            if ("desc".equals(isAsc)){
                page.addOrder(OrderItem.desc(orderBy));
            }
            if ("asc".equals(isAsc)){
                page.addOrder(OrderItem.asc(orderBy));
            }
        }
        return page;

    }


    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static PageResult getPageResult(Page<?> page) {
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setPageNum(page.getCurrent());
        pageResult.setData(page.getRecords());
        pageResult.setPageSize(page.getSize());
        return pageResult;
    }

    /**
     * 封装分页对象
     */
    private static PageDomain getPageDomain(){
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(Convert.toInt(ServletUtils.getParameter(PAGE_NUM),1));
        pageDomain.setPageSize(Convert.toInt(ServletUtils.getParameter(PAGE_SIZE), 10));
        pageDomain.setOrderByColumn(ServletUtils.getParameter(ORDER_BY_COLUMN));
        pageDomain.setIsAsc(ServletUtils.getParameter(IS_ASC));
        return pageDomain;
    }



}
