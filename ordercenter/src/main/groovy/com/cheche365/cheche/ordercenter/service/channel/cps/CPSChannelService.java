package com.cheche365.cheche.ordercenter.service.channel.cps;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.CPSChannel;
import com.cheche365.cheche.core.repository.CPSChannelRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.ordercenter.service.channel.IChannelService;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.ordercenter.web.model.channel.ChannelViewData;
import com.cheche365.cheche.web.util.UrlUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wangfei on 2015/5/22.
 */
@Service("cpsChannelService")
public class CPSChannelService extends BaseService<CPSChannel, ChannelViewData> implements IChannelService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IInternalUserManageService internalUserManageService;

    @Autowired
    private CPSChannelRepository cpsChannelRepository;

    /**
     * add new cpsChannel
     * @param viewData
     * @return
     */
    @Override
    public boolean addChannel(ChannelViewData viewData) {
        try {
            cpsChannelRepository.save(this.createChannel(viewData));
            return true;
        } catch (Exception e) {
            logger.error("add new cpsChannel has error", e);
        }

        return false;
    }

    /**
     * update cpsChannel
     * @param viewData
     * @return
     */
    @Override
    public boolean updateChannel(ChannelViewData viewData) {
        return this.addChannel(viewData);
    }

    /**
     * delete cpsChannel by id
     * @param id
     * @return
     */
    @Override
    public boolean deleteChannel(Long id) {
        try {
            logger.info("delete cpsChannel by id start...");
            cpsChannelRepository.delete(id);
            logger.info("delete cpsChannel by id completely...");
            return true;
        } catch (Exception e) {
            logger.error("delete cpsChannel by id has error", e);
        }

        return false;
    }

    /**
     * list cpsChannel by page
     * @param currentPage
     * @param pageSize
     * @param keyword
     * @return
     */
    @Override
    public PageViewModel<ChannelViewData> listChannels(Integer currentPage, Integer pageSize, String keyword) {
        try {
            Page<CPSChannel> page = this.findBySpecAndPaginate(keyword,
                this.buildPageable(currentPage, pageSize));

            return super.createResult(page);
        } catch (Exception e) {
            logger.error("list cpsChannel has error", e);
        }

        return null;
    }

    /**
     * find channel by id
     * @param id
     * @return
     */
    @Override
    public ChannelViewData findById(Long id) {
        try {
            CPSChannel cpsChannel = cpsChannelRepository.findOne(id);
            return this.createViewDate(cpsChannel);
        } catch (Exception e) {
            logger.error("find cpsChannel by id has error", e);
        }

        return null;
    }

    /**
     * create return view body list
     * @param tList
     * @return
     * @throws Exception
     */
    @Override
    public List<ChannelViewData> createList(List<CPSChannel> tList) throws Exception {
        List<ChannelViewData> viewDataList = new ArrayList<>();
        for (CPSChannel cpsChannel  : tList) {
            viewDataList.add(this.createViewDate(cpsChannel));
        }
        return viewDataList;
    }

    /**
     * create cpsChannel view model
     * @param cpsChannel
     * @return
     * @throws Exception
     */
    public ChannelViewData createViewDate(CPSChannel cpsChannel) throws Exception {
        ChannelViewData viewData = new ChannelViewData();
        viewData.setId(cpsChannel.getId());
        viewData.setName(cpsChannel.getName());
        viewData.setRebate(cpsChannel.getRebate());
        viewData.setChannelNo(cpsChannel.getChannelNo());
        viewData.setWapUrl(UrlUtil.toFullUrl(cpsChannel.getWapUrl()));
        viewData.setStartDate(DateUtils.getDateString(cpsChannel.getStartDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        viewData.setEndDate(DateUtils.getDateString(cpsChannel.getEndDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        viewData.setLinkMan(cpsChannel.getLinkMan());
        viewData.setMobile(cpsChannel.getMobile());
        viewData.setEmail(cpsChannel.getEmail());
        viewData.setFrequency(cpsChannel.getFrequency());
        viewData.setEnable(cpsChannel.isEnable());
        viewData.setDisplay(cpsChannel.isDisplay());
        viewData.setCreateTime(DateUtils.getDateString(cpsChannel.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewData.setUpdateTime(DateUtils.getDateString(cpsChannel.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewData.setOperator(cpsChannel.getOperator().getName());

        return viewData;
    }

    /**
     * select by keyword and page
     * @param keyword
     * @param pageable
     * @return
     */
    private Page<CPSChannel> findBySpecAndPaginate(String keyword, Pageable pageable) throws Exception {
        return cpsChannelRepository.findAll(new Specification<CPSChannel>() {
            @Override
            public Predicate toPredicate(Root<CPSChannel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<CPSChannel> criteriaQuery = cb.createQuery(CPSChannel.class);



                //条件构造
                List<Predicate> predicateList = new ArrayList<>();
                if (StringUtils.isNotBlank(keyword)) {
                    //获取实体属性
                    Path<String> namePath = root.get("name");
                    Path<String> channelNoPath = root.get("channelNo");
                    predicateList.add(cb.or(
                        cb.like(namePath, keyword + "%"),
                        cb.like(channelNoPath, keyword + "%")
                    ));
                }

                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    /**
     * build pageable by page
     * @param currentPage
     * @param pageSize
     * @return
     */
    private Pageable buildPageable(int currentPage, int pageSize) throws Exception {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        return new PageRequest(currentPage-1, pageSize, sort);
    }

    /**
     * create cpsChannel db model
     * @param viewData
     * @return
     * @throws Exception
     */
    public CPSChannel createChannel(ChannelViewData viewData) throws Exception {
        CPSChannel channel =  new CPSChannel();
        if (viewData.getId() != null && viewData.getId() > 0)
            channel = cpsChannelRepository.findOne(viewData.getId());

        channel.setName(viewData.getName());
        channel.setRebate(viewData.getRebate());
        channel.setChannelNo(viewData.getChannelNo());
        channel.setWapUrl(viewData.getWapUrl());
        channel.setStartDate(DateUtils.getDate(viewData.getStartDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        channel.setEndDate(DateUtils.getDate(viewData.getEndDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        channel.setLinkMan(viewData.getLinkMan());
        channel.setMobile(viewData.getMobile());
        channel.setEmail(viewData.getEmail());
        channel.setFrequency(viewData.getFrequency());
        channel.setEnable(viewData.isEnable());
        channel.setDisplay(viewData.isDisplay());
        channel.setCreateTime(viewData.getId() == null ? new Date() : channel.getCreateTime());
        channel.setUpdateTime(new Date());
        channel.setOperator(internalUserManageService.getCurrentInternalUser());

        return channel;
    }

}
