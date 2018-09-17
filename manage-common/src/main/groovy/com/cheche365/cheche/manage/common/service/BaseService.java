package com.cheche365.cheche.manage.common.service;

import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wangfei on 2015/5/23.
 */
@Component
public class BaseService<T, U> {
    public static final String SORT_ID = "id";
    public static final String SORT_CREATE_TIME = "createTime";
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * create pageInfo
     *
     * @param page
     * @return
     * @throws Exception
     */
    public PageInfo createPageInfo(Page page) {
        PageInfo pageInfo = new PageInfo();

        pageInfo.setTotalElements(page.getTotalElements());
        pageInfo.setTotalPage(page.getTotalPages());

        return pageInfo;
    }

    /**
     * create return model
     *
     * @param page
     * @return
     * @throws Exception
     */
    public PageViewModel<U> createResult(Page<T> page) throws Exception {
        PageViewModel model = new PageViewModel<U>();

        model.setPageInfo(this.createPageInfo(page));
        model.setViewList(this.createList(page.getContent()));

        return model;
    }

    /**
     * create return model
     *
     * @param page
     * @return
     * @throws Exception
     */
    public DataTablePageViewModel<U> createResult(Page<T> page, Integer draw) throws Exception {
        DataTablePageViewModel model = new DataTablePageViewModel<U>();
        model.setiTotalRecords(page.getTotalElements());
        model.setiTotalDisplayRecords(page.getTotalElements());
        model.setDraw(draw);
        model.setAaData(this.createList(page.getContent()));
        return model;
    }

    /**
     * create return list
     *
     * @param tList
     * @return
     * @throws Exception
     */
    public List<? extends U> createList(List<T> tList) throws Exception {
        return null;
    }

    /**
     * build pageable for select
     *
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @param column      字段名
     * @return
     * @throws Exception
     */
    public Pageable buildPageable(Integer currentPage, Integer pageSize, Sort.Direction direction, String column) {
        Sort sort = new Sort(direction, column);
        return new PageRequest(currentPage - 1, pageSize, sort);
    }

    /**
     * find all
     *
     * @param repository
     * @return
     */
    public List<T> getAll(PagingAndSortingRepository repository) {
        List<T> tList = new ArrayList<>();
        try {
            Iterable<T> brandIterable = repository.findAll();
            Iterator<T> brandIterator = brandIterable.iterator();
            while (brandIterator.hasNext()) {
                tList.add(brandIterator.next());
            }

            return tList;
        } catch (Exception ex) {
            logger.error("list entity has error", ex);
        }

        return null;
    }
}
