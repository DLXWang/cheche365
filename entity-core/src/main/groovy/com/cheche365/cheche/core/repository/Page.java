package com.cheche365.cheche.core.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxiaozhe on 15-7-24.
 */
public class Page<T> {

    public static final int DEFAULT_PAGE_SIZE = 10;
    private int number = 0;
    @JsonIgnore
    private int firstIdx = 0;
    @JsonIgnore
    private int lastIdx;
    @JsonIgnore
    private List<T> allElements;

    private long totalElements;

    private boolean first;

    private boolean last;

    private int totalPages;

    private String sort = StringUtils.EMPTY;

    private List<T> content = new ArrayList<>();

    private int size = DEFAULT_PAGE_SIZE;


    public Page(int pageNo) {
        this(pageNo, DEFAULT_PAGE_SIZE);
    }


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }


    public Page(int pageNo, int pageSize) {
        this(pageNo, pageSize, 0);
    }

    public Page(int pageNo, int pageSize, long count) {
        this(pageNo, pageSize, count, new ArrayList<T>());
    }

    public Page(int pageNo, int pageSize, long count, List<T> list) {
        this.setTotalElements(count);
        this.setNumber(pageNo);
        this.size = pageSize;
        this.setAllElements(list);
    }

    public int getTotalPages() {
        totalPages = lastIdx + 1;
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isLast() {
        int pageSize = (this.size < 1) ? DEFAULT_PAGE_SIZE : this.size;
        lastIdx = (int) ((this.totalElements + pageSize - 1) / pageSize - 1 + firstIdx);
        if (this.number >= lastIdx) {
            this.last = true;
        }
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isFirst() {
        if (this.number <= 0) {
            this.number = firstIdx;
            this.first = true;
        }
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long count) {
        this.totalElements = count;
        if (size >= count) {
            number = 0;
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int pageSize) {
        this.size = pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize > 500 ? 500 : pageSize;
    }

    public List<T> getContent() {
        if (content != null && !content.isEmpty()) {
            return content;
        }

        if (allElements != null && !allElements.isEmpty() && number + 1 <= totalPages) {
            int fromIndex = (number <= lastIdx ? number : lastIdx) * size;
            long toIndex = (number + 1) * size > totalElements ? totalElements : (number + 1) * size;

            return allElements.subList(fromIndex, Integer.valueOf(String.valueOf(toIndex)));
        }
        return content;
    }

    public void setContent(List<T> list) {
        this.content = list;
    }

    public List<T> getAllElements() {
        return allElements;
    }

    public void setAllElements(List<T> allElements) {
        this.allElements = allElements;
    }

    public String getSort() {
        return sort;
    }

    /**
     * 设置查询排序 如:updatedate desc, name asc
     */
    public void setSort(String orderBy) {
        this.sort = orderBy;
    }

    @JsonIgnore
    public boolean isDisabled() {
        return this.size == -1;
    }

    @JsonIgnore
    public boolean isNotCount() {
        return this.totalElements == -1;
    }

    /**
     * 获取 Hibernate FirstResult
     */
    @JsonIgnore
    public int getFirstResult() {
        int firstResult = (getNumber()) * getSize();
        if (firstResult >= getTotalElements()) {
            firstResult = 0;
        }
        return firstResult;
    }

    /**
     * 获取 Hibernate MaxResults
     */
    @JsonIgnore
    public int getMaxResults() {
        return getSize();
    }

    @JsonIgnore
    public Pageable getSpringPage() {
        List<Order> orders = new ArrayList<Order>();
        if (sort != null) {
            for (String order : StringUtils.split(sort, ",")) {
                String[] o = StringUtils.split(order, " ");
                if (o.length == 1) {
                    orders.add(new Order(Direction.ASC, o[0]));
                } else if (o.length == 2) {
                    if ("DESC".equals(o[1].toUpperCase())) {
                        orders.add(new Order(Direction.DESC, o[0]));
                    } else {
                        orders.add(new Order(Direction.ASC, o[0]));
                    }
                }
            }
        }
        return new PageRequest(this.number, this.size, new Sort(orders));
    }

    @JsonIgnore
    public void setSpringPage(org.springframework.data.domain.Page<T> page) {
        this.number = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.content = page.getContent();
    }
}
