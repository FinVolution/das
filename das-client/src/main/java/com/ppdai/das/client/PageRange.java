package com.ppdai.das.client;

import java.sql.SQLException;

/**
 * A value object that represent page range
 * @author hejiehui
 *
 */
public class PageRange {
    private int pageSize;
    private int pageNo;
    private Object[] orders;
    
    /**
     * @param pageNo page index, start from 1
     * @param pageSize how many records in a page
     * @param orders
     */
    public PageRange(int pageNo, int pageSize, Object...orders){
        if(pageNo < 1 || pageSize < 1)
            throw new IllegalArgumentException("pageNo or pageSize is invalid");
        
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.orders = orders;
    }
    
    /**
     * @param pageNo page index, start from 1
     * @param pageSize how many records in a page
     * @param orders
     * @return
     */
    public static PageRange atPage(int pageNo, int pageSize, Object...orders){
        return new PageRange(pageNo, pageSize, orders);
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public boolean hasOrders() {
        return orders != null && orders.length > 0;
    }
    
    public Object[] getOrders() {
        return orders;
    }
}
