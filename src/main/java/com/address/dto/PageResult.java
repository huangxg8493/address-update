package com.address.dto;

import java.util.List;

public class PageResult<T> {
    private String clientNo;
    private int pageNum;
    private int pageSize;
    private long total;
    private int totalPages;
    private List<T> list;

    public PageResult() {}

    public PageResult(String clientNo, int pageNum, int pageSize, long total, List<T> list) {
        this.clientNo = clientNo;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
        this.list = list;
    }

    public String getClientNo() { return clientNo; }
    public int getPageNum() { return pageNum; }
    public int getPageSize() { return pageSize; }
    public long getTotal() { return total; }
    public int getTotalPages() { return totalPages; }
    public List<T> getList() { return list; }
}
