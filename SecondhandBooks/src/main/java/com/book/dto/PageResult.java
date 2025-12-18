package com.book.dto;

import java.util.List;

public class PageResult<T> {
    private List<T> list;
    private long totalCount;
    private int totalPages;
    private int currentPage;

    public PageResult(List<T> list, long totalCount, int totalPages, int currentPage) {
        this.list = list;
        this.totalCount = totalCount;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
