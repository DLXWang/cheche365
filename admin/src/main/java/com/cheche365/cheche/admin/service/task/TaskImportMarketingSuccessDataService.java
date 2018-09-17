package com.cheche365.cheche.admin.service.task;

import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.admin.web.model.task.TaskImportMarketingSuccessDataViewModel;
import com.cheche365.cheche.core.model.QuotePhoto;
import com.cheche365.cheche.manage.common.model.TaskImportMarketingSuccessData;
import com.cheche365.cheche.manage.common.repository.TaskImportMarketingSuccessDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xu.yelong on 2016-03-25.
 */
@Service
public class TaskImportMarketingSuccessDataService {
    @Autowired
    private TaskImportMarketingSuccessDataRepository taskImportMarketingSuccessDataRepository;

    public void save(TaskImportMarketingSuccessData telMarketingCenterTaskManage){
        taskImportMarketingSuccessDataRepository.save(telMarketingCenterTaskManage);
    }

    public TaskImportMarketingSuccessData findByCacheKey(String cacheKey){
        return taskImportMarketingSuccessDataRepository.findByCacheKey(cacheKey);
    }

    public List<TaskImportMarketingSuccessData> findEnable(){
       return taskImportMarketingSuccessDataRepository.findByEnable(true);
    }

    public PageViewModel<TaskImportMarketingSuccessDataViewModel> findAll(Integer currentPage, Integer pageSize){
        Pageable pageable = this.buildPageable(currentPage, pageSize);
        Page<TaskImportMarketingSuccessData> taskImportMarketingSuccessDataPage = this.findBySpecAndPaginate(pageable);
        return this.createResult(taskImportMarketingSuccessDataPage);
    }

    public TaskImportMarketingSuccessData findOne(Long id){
        return taskImportMarketingSuccessDataRepository.findOne(id);
    }

    public Page<TaskImportMarketingSuccessData> findBySpecAndPaginate(Pageable pageable) {
        return taskImportMarketingSuccessDataRepository.findAll(new Specification<TaskImportMarketingSuccessData>() {
            @Override
            public Predicate toPredicate(Root<TaskImportMarketingSuccessData> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<QuotePhoto> criteriaQuery = cb.createQuery(QuotePhoto.class);
                List<Predicate> predicateList = new ArrayList<Predicate>();
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    private Pageable buildPageable(int currentPage, int pageSize) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        return new PageRequest(currentPage - 1, pageSize, sort);
    }

    public PageViewModel<TaskImportMarketingSuccessDataViewModel> createResult(Page page) {
        PageViewModel model = new PageViewModel<TaskImportMarketingSuccessDataViewModel>();
        PageInfo pageInfo = new PageInfo();
        pageInfo.setTotalElements(page.getTotalElements());
        pageInfo.setTotalPage(page.getTotalPages());
        model.setPageInfo(pageInfo);
        List<TaskImportMarketingSuccessDataViewModel> pageViewDataList = new ArrayList<TaskImportMarketingSuccessDataViewModel>();
        for (TaskImportMarketingSuccessData data : (List<TaskImportMarketingSuccessData>) page.getContent()) {
            TaskImportMarketingSuccessDataViewModel viewModel=new TaskImportMarketingSuccessDataViewModel(data);
            pageViewDataList.add(viewModel);
        }
        model.setViewList(pageViewDataList);
        return model;
    }

}
