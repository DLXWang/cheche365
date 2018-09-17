package com.cheche365.cheche.ordercenter.service.quote;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.QuotePhoto;
import com.cheche365.cheche.core.repository.AutoRepository;
import com.cheche365.cheche.core.repository.ChannelRepository;
import com.cheche365.cheche.core.repository.QuotePhotoRepository;
import com.cheche365.cheche.core.service.IResourceService;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.common.util.MobileUtil;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.manage.common.web.model.ModelAndViewResult;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.ordercenter.web.model.quote.QuotePhotoViewModel;
import com.cheche365.cheche.ordercenter.web.model.quote.QuoteViewModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * Created by xu.yelong on 2015/10/20.
 */
@Service
public class QuotePhotoService extends BaseService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private QuotePhotoRepository quotePhotoRepository;

    @Autowired
    private AutoRepository autoRepository;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    @Autowired
    private ChannelRepository channelRepository;

    public Page<QuotePhoto> getQuotePhotoByPage(PublicQuery query) {
        return findBySpecAndPaginate(super.buildPageable(query.getCurrentPage(), query.getPageSize(),
            Sort.Direction.DESC, SORT_CREATE_TIME), query);
    }

    public QuotePhoto findById(Long id) {
        QuotePhoto quotePhoto = quotePhotoRepository.findOne(id);
        AssertUtil.notNull(quotePhoto, "can not find QuotePhoto by id -> " + id);
        return quotePhoto;
    }

    public Page<QuotePhoto> findBySpecAndPaginate(Pageable pageable, PublicQuery publicQuery) {
        return quotePhotoRepository.findAll(new Specification<QuotePhoto>() {
            @Override
            public Predicate toPredicate(Root<QuotePhoto> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<QuotePhoto> criteriaQuery = cb.createQuery(QuotePhoto.class);
                List<Predicate> predicateList = new ArrayList<Predicate>();

                if (!StringUtils.isEmpty(publicQuery.getMobile())) {
                    Path<String> mobilePath = root.get("user").get("mobile");
                    predicateList.add(cb.like(mobilePath, publicQuery.getMobile() + "%"));
                }
                if (!StringUtils.isEmpty(publicQuery.getLicensePlateNo())) {
                    Path<String> licensePlateNoPath = root.get("licensePlateNo");
                    predicateList.add(cb.like(licensePlateNoPath, publicQuery.getLicensePlateNo() + "%"));
                }

                if (publicQuery.getVisited() != null) {
                    Path<Boolean> visitedPath = root.get("visited");
                    predicateList.add(cb.equal(visitedPath, publicQuery.getVisited()));
                }
                if (publicQuery.getDisable() != null) {
                    Path<Boolean> disablePath = root.get("disable");
                    //状态: 0:空;1:有效;2:有效+空;3:无效
                    switch (publicQuery.getDisable()) {
                        case 0:
                            predicateList.add(cb.isNull(disablePath));
                            break;
                        case 1:
                            predicateList.add(cb.equal(disablePath, 0));
                            break;
                        case 2:
                            predicateList.add(cb.or(cb.equal(disablePath, 0), cb.isNull(disablePath)));
                            break;
                        case 3:
                            predicateList.add(cb.equal(disablePath, 1));
                            break;
                    }
                }
                if (!publicQuery.getQuoteEntrance().trim().equals("")) {
                    Path<Long> quoteEntrancePath = root.get("userImg").get("quoteEntrance").get("id");
                    predicateList.add(cb.equal(quoteEntrancePath, publicQuery.getQuoteEntrance()));
                }
                if (publicQuery.getChannel() != null && publicQuery.getChannel() != 0) {
                    Path<Long> sourceChannelPath = root.get("userImg").get("sourceChannel").get("id");
                    predicateList.add(cb.equal(sourceChannelPath, publicQuery.getChannel()));
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    public QuotePhoto setQuotePhotoDisable(Long id, Integer disable) {
        QuotePhoto photo = findById(id);
        photo.setDisable(disable == 1);
        photo.setUpdateTime(new Date());
        InternalUser internalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        if (photo.getResponsible() == null) {
            photo.setResponsible(internalUser);
        }
        photo.setOperator(internalUser);
        quotePhotoRepository.save(photo);
        return photo;
    }

    public ModelAndViewResult setQuotePhotoVistited(Long id, Integer visited) {
        ModelAndViewResult result = new ModelAndViewResult();
        try {
            QuotePhoto photo = findById(id);
            photo.setVisited(visited == 0);
            photo.setUpdateTime(new Date());
            quotePhotoRepository.save(photo);
            result.setResult(ModelAndViewResult.RESULT_SUCCESS);
        } catch (Exception e) {
            logger.error("修改拍照信息回访状态失败");
            result.setResult(ModelAndViewResult.RESULT_FAIL);
        }
        return result;
    }

    public QuotePhotoViewModel update(QuotePhotoViewModel viewModel) {
        try {
            quotePhotoRepository.save(createQuotePhote(viewModel));
        } catch (Exception e) {
            logger.error("修改拍照详情失败");
        }
        return viewModel;
    }

    /**
     * 根据车牌号查询
     */
    public List<QuotePhotoViewModel> listByLicensePlateNo(String licensePlateNo) {
        List<QuotePhoto> quotePhotoList = quotePhotoRepository.listByLicensePlateNo(licensePlateNo);
        List<QuotePhotoViewModel> quotePhotoViewModelList = new ArrayList();
        for (QuotePhoto photo : quotePhotoList) {
            quotePhotoViewModelList.add(createViewModel(photo));
        }
        return quotePhotoViewModelList;
    }

    /**
     * 查询用户在报价表及车辆表中车牌号
     */
    public List<String> findLicensePlateNoByUser(Long userId) {
        List<QuotePhoto> quotePhotoList = quotePhotoRepository.listLicensePlateNoByUser(userId);
        List<Auto> autoList = autoRepository.listByUserAndPlateNoNotNull(userId);
        List result = new ArrayList();
        for (QuotePhoto photo : quotePhotoList) {
            if (StringUtils.isBlank(photo.getLicensePlateNo())) {
                continue;
            }
            result.add(new StringBuffer(photo.getId() + "").append(",").append(photo.getLicensePlateNo()));
        }
        for (Auto auto : autoList) {
            if (StringUtils.isBlank(auto.getLicensePlateNo())) {
                continue;
            }
            result.add(new StringBuffer("-").append(auto.getId()).append(",").append(auto.getLicensePlateNo()));
        }
        return result;
    }

    /**
     * 重新选择或新建车牌号
     */
    public QuotePhoto changeAuto(String licensePlateNo, Long id, Long currentId) {
        //判断历史报价ID，如果有，则查询历史报价记录
        QuotePhoto quotePhoto = null;
        QuotePhoto currentQuotePhoto = quotePhotoRepository.findOne(currentId);
        if (id > 0) {
            quotePhoto = quotePhotoRepository.findOne(id);
        }
        //将历史报价信息转为当前报价信息并保存
        if (quotePhoto != null) {
            currentQuotePhoto = cerateQuotePhoteByOld(quotePhoto, currentQuotePhoto);
        } else {
            id = Math.abs(id);
            Auto auto = null;
            if (id == 0) {//用户新建车牌号，查询Auto
                auto = autoRepository.findFirstByLicensePlateNoAndDisableOrderByUpdateTimeDesc(licensePlateNo, false);
            } else {
                auto = autoRepository.findOne(id);
            }
            currentQuotePhoto = createQuotePhotoByAuto(auto, currentQuotePhoto, licensePlateNo);
        }
        quotePhotoRepository.save(currentQuotePhoto);
        return currentQuotePhoto;
    }

    /**
     * 将历史报价记录转换为当前报价
     */
    private QuotePhoto cerateQuotePhoteByOld(QuotePhoto quotePhoto, QuotePhoto currentQuotePhoto) {
        String[] contains = new String[]{"licensePlateNo", "owner", "identity", "insuredName", "insuredIdNo",
            "vinNo", "engineNo", "enrollDate", "model", "code", "expireDate"};
        BeanUtil.copyPropertiesContain(quotePhoto, currentQuotePhoto, contains);
        currentQuotePhoto.setUpdateTime(new Date());
        return currentQuotePhoto;
    }

    /**
     * 将查询出的Auto表车辆相关信息，转换为拍照信息
     */
    private QuotePhoto createQuotePhotoByAuto(Auto auto, QuotePhoto quotePhoto, String licensePlateNo) {
        if (auto == null) {
            auto = new Auto();
            quotePhoto.setInsuredName(null);
            quotePhoto.setInsuredIdNo(null);
            quotePhoto.setExpireDate(null);
            quotePhoto.setEnrollDate(null);
        }
        quotePhoto.setOwner(auto.getOwner());
        quotePhoto.setIdentity(auto.getIdentity());
        quotePhoto.setInsuredName(auto.getOwner());
        quotePhoto.setInsuredIdNo(auto.getIdentity());
        quotePhoto.setLicensePlateNo(StringUtils.isEmpty(auto.getLicensePlateNo()) ?
            licensePlateNo : auto.getLicensePlateNo());
        quotePhoto.setVinNo(auto.getVinNo());
        quotePhoto.setEngineNo(auto.getEngineNo());
        quotePhoto.setEnrollDate(auto.getEnrollDate());
        quotePhoto.setModel(auto.getAutoType() == null ? "" : auto.getAutoType().getModel());
        quotePhoto.setCode(auto.getAutoType() == null ? "" : auto.getAutoType().getCode());
        quotePhoto.setUpdateTime(new Date());
        return quotePhoto;
    }

    /**
     * 封装展示层实体
     *
     * @param page 分页信息
     * @return PageViewModel<PageViewData>
     */
    public PageViewModel<QuotePhotoViewModel> createResult(Page page) {
        PageViewModel model = new PageViewModel<QuoteViewModel>();
        PageInfo pageInfo = new PageInfo();
        pageInfo.setTotalElements(page.getTotalElements());
        pageInfo.setTotalPage(page.getTotalPages());
        model.setPageInfo(pageInfo);
        List<QuotePhotoViewModel> pageViewDataList = new ArrayList<QuotePhotoViewModel>();
        for (QuotePhoto quote : (List<QuotePhoto>) page.getContent()) {
            QuotePhotoViewModel viewData = createViewModel(quote);
            pageViewDataList.add(viewData);
        }
        model.setViewList(pageViewDataList);
        return model;
    }

    public QuotePhotoViewModel createViewModel(QuotePhoto photo) {
        QuotePhotoViewModel model = new QuotePhotoViewModel();
        String[] contains = new String[]{"id", "licensePlateNo", "owner", "identityType", "identity", "insuredName", "insuredIdType", "insuredIdNo",
            "vinNo", "engineNo", "model", "code", "disable", "visited", "comment"};
        BeanUtil.copyPropertiesContain(photo, model, contains);
        model.setUserId(photo.getUser() != null ? photo.getUser().getId() : null);
        String mobile = StringUtil.convertNull(photo.getUser() != null ? photo.getUser().getMobile() : "");
        model.setMobile(mobile);
        model.setEncyptMobile(MobileUtil.getEncyptMobile(mobile));
        model.setEnrollDate(photo.getEnrollDate() != null ?
            DateUtils.getDateString(photo.getEnrollDate(), DateUtils.DATE_SHORTDATE_PATTERN) : "");
        model.setExpireDate(photo.getExpireDate() != null ?
            DateUtils.getDateString(photo.getExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN) : "");
        model.setCreateTime(DateUtils.getDateString(photo.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        model.setSourceChannel(Integer.parseInt(String.valueOf(photo.getUserImg().getSourceChannel().getId())));
        model.setTransferDate(DateUtils.getDateString(photo.getTransferDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        if (photo.getUserImg().getQuoteEntrance() != null) {
            model.setQuoteEntrance(photo.getUserImg().getQuoteEntrance().getDescription());
        } else {
            model.setQuoteEntrance("报价");
        }
        if (photo.getUserImg() != null) {
            String drivingLicensePath = photo.getUserImg().getDrivingLicensePath();
            String ownerIdentityPath = photo.getUserImg().getOwnerIdentityPath();
            int num = 0;
            if (!StringUtils.isEmpty(drivingLicensePath)) {
                num++;
                model.setDrivingLicensePath(getImagePath(drivingLicensePath));
            }
            if (!StringUtils.isEmpty(ownerIdentityPath)) {
                num++;
                model.setOwnerIdentityPath(getImagePath(ownerIdentityPath));
            }
            model.setUserImg(num);
        }
        if (null == photo.getUserImg() || null == photo.getUserImg().getSourceChannel()) {
            return null;
        }
        model.setSourceChannel(Integer.valueOf(photo.getUserImg().getSourceChannel().getId().toString()));
        if (!StringUtils.isEmpty(photo.getUserImg().getSourceChannel().getIcon())) {
            model.setChannelIcon(resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getChannelPath()),
                photo.getUserImg().getSourceChannel().getIcon()));
        }
        return model;
    }

    /**
     * 将第三方渠道转为自由渠道用于拍照报价 #10687
     * @param photo
     * @return
     */
    public QuotePhotoViewModel createViewModelWithSELF(QuotePhoto photo){
        QuotePhotoViewModel quotePhotoViewModel = createViewModel(photo);
        Channel channel = Channel.toOrderCenterChannel(Long.valueOf(quotePhotoViewModel.getSourceChannel()));
        quotePhotoViewModel.setSourceChannel(channel.getId().intValue());
        return quotePhotoViewModel;
    }

    private String getImagePath(String path) {
        //判断是否在didi图片目录内，否则默认双十一目录
        String didPath = resourceService.getProperties().getDidiPath();
        String doubleOnePath = resourceService.getProperties().getDoubleonePath();
        int index = 0;
        if ((index = path.indexOf(didPath)) > -1) {
            return resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(didPath),
                path.substring(index + didPath.length(), path.length()));
        }
        if ((index = path.indexOf(doubleOnePath)) > -1) {
            return resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(doubleOnePath),
                path.substring(index + doubleOnePath.length(), path.length()));
        }
        return null;
    }


    private QuotePhoto createQuotePhote(QuotePhotoViewModel viewModel) {
        if (viewModel == null)
            return null;
        QuotePhoto quotePhoto = new QuotePhoto();
        if (viewModel.getId() != null) {
            quotePhoto = findById(viewModel.getId());
            quotePhoto.setUpdateTime(new Date());
        }
        InternalUser internalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        quotePhoto.setOperator(internalUser);
        String[] contains = new String[]{"owner", "identityType", "identity", "insuredName","insuredIdType", "insuredIdNo", "licensePlateNo", "vinNo",
            "engineNo", "model", "code", "comment"};
        BeanUtil.copyPropertiesContain(viewModel, quotePhoto, contains);
        quotePhoto.setEnrollDate(DateUtils.getDate(viewModel.getEnrollDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        quotePhoto.setExpireDate(DateUtils.getDate(viewModel.getExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        quotePhoto.setTransferDate(DateUtils.getDate(viewModel.getTransferDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        return quotePhoto;
    }

    public QuotePhoto updateByAuto(Long sourceId, Auto auto, Integer flowType) {
        if (null == auto) {
            return null;
        }

        boolean isModify = false;
        QuotePhoto quotePhoto = quotePhotoRepository.findOne(sourceId);
        AssertUtil.notNull(quotePhoto, "can not find QuotePhoto by id -> " + sourceId);
        if (StringUtils.isNotBlank(auto.getLicensePlateNo()) && !auto.getLicensePlateNo().equals(quotePhoto.getLicensePlateNo())) {
            isModify = true;
            quotePhoto.setLicensePlateNo(auto.getLicensePlateNo());
        }
        if (StringUtils.isNotBlank(auto.getInsuredIdNo()) && !auto.getInsuredIdNo().equals(quotePhoto.getInsuredIdNo())) {
            isModify = true;
            quotePhoto.setInsuredIdNo(auto.getInsuredIdNo());
        }
        if (StringUtils.isNotBlank(auto.getIdentity()) && !auto.getIdentity().equals(quotePhoto.getIdentity())) {
            isModify = true;
            quotePhoto.setIdentity(auto.getIdentity());
        }

        if (StringUtils.isNotBlank(auto.getVinNo()) && !auto.getVinNo().equals(quotePhoto.getVinNo())) {
            isModify = true;
            quotePhoto.setVinNo(auto.getVinNo());
        }
        if (StringUtils.isNotBlank(auto.getEngineNo()) && !auto.getEngineNo().equals(quotePhoto.getEngineNo())) {
            isModify = true;
            quotePhoto.setEngineNo(auto.getEngineNo());
        }
        if (StringUtils.isNotBlank(auto.getOwner()) && !auto.getOwner().equals(quotePhoto.getOwner())) {
            isModify = true;
            quotePhoto.setOwner(auto.getOwner());
        }
        if (null != auto.getEnrollDate()) {
            if (null == quotePhoto.getEnrollDate() || auto.getEnrollDate().compareTo(quotePhoto.getEnrollDate()) != 0) {
                isModify = true;
                quotePhoto.setEnrollDate(auto.getEnrollDate());
            }
        }
        if (null != auto.getAutoType() && StringUtils.isNotBlank(auto.getAutoType().getCode())
            && !auto.getAutoType().getCode().equals(quotePhoto.getCode())) {
            isModify = true;
            quotePhoto.setCode(auto.getAutoType().getCode());
        }
        if (isModify) {
            if (logger.isDebugEnabled()) {
                logger.debug("some auto info have been modified, need to update QuotePhoto.");
            }
            quotePhoto.setUpdateTime(new Date());
            return quotePhotoRepository.save(quotePhoto);
        }
        return null;
    }

    public ModelAndViewResult setComment(Long id, String comment) {
        ModelAndViewResult result = new ModelAndViewResult();

        try {
            QuotePhoto photo = quotePhotoRepository.findOne(id);
            if (photo == null) {
                return null;
            }
            String newComment = comment.replace("\n", "\\r\\n");
            InternalUser internalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
            logger.debug("修改拍照信息备注，拍照信息ID:{}，操作人:{}，原备注:{}，新备注:{}",
                photo.getId(), internalUser.getName(), photo.getComment(), newComment);
            photo.setComment(newComment);
            photo.setOperator(internalUser);
            photo.setUpdateTime(Calendar.getInstance().getTime());
            quotePhotoRepository.save(photo);
            result.setResult(ModelAndViewResult.RESULT_SUCCESS);
        } catch (Exception e) {
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setMessage("操作失败，请稍后再试");
        }
        return result;
    }

    public List<Channel> getChannels() {
        Iterable<Channel> channels = this.channelRepository.findAll();
        Iterator<Channel> channelIterator = channels.iterator();
        List<Channel> channelList = new ArrayList<>();
        while (channelIterator.hasNext()) {
            channelList.add(channelIterator.next());
        }
        return channelList;
    }
}

