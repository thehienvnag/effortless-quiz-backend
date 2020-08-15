package com.example.springdemo.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.Optional;

public class PageableWrapper {
    private Integer page = 0;
    private Integer size = 5;
    private String sortBy = "id";

    public PageableWrapper() {
    }

    public Pageable getPageable(){
        return PageRequest.of(page, size, Sort.Direction.ASC, sortBy);
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
