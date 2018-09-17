package com.cheche365.cheche.ordercenter.service.quote;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.AutoRepository;
import com.cheche365.cheche.core.repository.ChannelRepository;
import com.cheche365.cheche.core.repository.QuotePhoneRepository;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.core.service.UserService;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.common.util.MobileUtil;
import com.cheche365.cheche.manage.common.web.model.ModelAndViewResult;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import com.cheche365.cheche.ordercenter.web.model.quote.QuotePhoneViewModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * Created by wangfei on 2015/10/15.
 */
@Service
@Transactional
public class QuotePhoneService extends BaseService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private QuotePhoneRepository quotePhoneRepository;

    @Autowired
    private AutoRepository autoRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private IInternalUserManageService internalUserManageService;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ResourceService resourceService;

    public List<QuotePhone> findExistentQuotesByLicensePlateNo(String licensePlateNo) {
        return quotePhoneRepository.findByLicensePlateNo(licensePlateNo);
    }

    public List<QuotePhoneViewModel> createPhoneQuoteViewModel(List<QuotePhone> quotePhoneList) {
        if (null == quotePhoneList) {
            return null;
        }

        List<QuotePhoneViewModel> modelList = new ArrayList<>();
        quotePhoneList.forEach(quotePhone -> {
            QuotePhoneViewModel model = createViewData(quotePhone);
            modelList.add(model);
        });

        return modelList;
    }

    public QuotePhoneViewModel findExistentAutoByLicensePlateNo(String licensePlateNo) {
        QuotePhoneViewModel model = new QuotePhoneViewModel();
        Auto auto = autoRepository.findFirstByLicensePlateNoAndDisableOrderByUpdateTimeDesc(licensePlateNo, false);
        if (null != auto) {
            QuotePhone quotePhone = new QuotePhone();
            String[] contains = new String[]{"licensePlateNo", "owner", "identity", "vinNo", "engineNo", "enrollDate"};
            BeanUtil.copyPropertiesContain(auto, quotePhone, contains);
            quotePhone.setInsuredName(auto.getOwner());
            quotePhone.setInsuredIdNo(auto.getIdentity());
            quotePhone.setModel(auto.getAutoType() == null ? "" : auto.getAutoType().getModel());
            quotePhone.setCode(auto.getAutoType() == null ? "" : auto.getAutoType().getCode());
            quotePhone.setCreateTime(Calendar.getInstance().getTime());
            quotePhone.setUpdateTime(Calendar.getInstance().getTime());
            quotePhone.setVisited(false);
            return createViewData(quotePhone);
        }

        return model;
    }

    public QuotePhone savePhoneQuoteAuto(QuotePhoneViewModel quotePhoneViewModel) {
        Channel channel = channelRepository.findById(quotePhoneViewModel.getSourceChannel());
        QuotePhone quotePhone = new QuotePhone();
        if (quotePhoneViewModel.getId() != null && quotePhoneViewModel.getId() > 0) {
            quotePhone = quotePhoneRepository.findOne(quotePhoneViewModel.getId());
        } else {
            //责任人只在新建报价时保存
            quotePhone.setResponsible(internalUserManageService.getCurrentInternalUser());
        }
        String[] contains = new String[]{"licensePlateNo", "owner", "identity", "insuredName", "insuredIdNo",
                "vinNo", "engineNo", "model", "code", "sourceChannel"};
        BeanUtil.copyPropertiesContain(quotePhoneViewModel, quotePhone, contains);
        quotePhone.setEnrollDate(StringUtils.isNotBlank(quotePhoneViewModel.getEnrollDate()) ?
                DateUtils.getDate(quotePhoneViewModel.getEnrollDate(), DateUtils.DATE_SHORTDATE_PATTERN) : null);
        quotePhone.setExpireDate(StringUtils.isNotBlank(quotePhoneViewModel.getExpireDate()) ?
                DateUtils.getDate(quotePhoneViewModel.getExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN) : null);
        //用户不存在创建新用户，用户存在非绑定设置绑定状态
        quotePhone.setUser(userService.findOrCreateUser(quotePhoneViewModel.getMobile(), channel, UserSource.Enum.QUOTE_PHONE));
        quotePhone.setVisited(false);
        quotePhone.setOperator(internalUserManageService.getCurrentInternalUser());
        quotePhone.setCreateTime(Calendar.getInstance().getTime());
        quotePhone.setUpdateTime(Calendar.getInstance().getTime());
        quotePhone.setSourceChannel(channel);
        quotePhone.setIdentityType(quotePhoneViewModel.getIdentityType());
        quotePhone.setInsuredIdType(quotePhoneViewModel.getInsuredIdType());
        quotePhone.setTransferDate(DateUtils.getDate(quotePhoneViewModel.getTransferDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        return quotePhoneRepository.save(quotePhone);
    }

    public Page<QuotePhone> getPhoneQuoteByPage(PublicQuery query) {
        return findBySpecAndPaginate(super.buildPageable(query.getCurrentPage(), query.getPageSize(),
                Sort.Direction.DESC, SORT_CREATE_TIME), query);
    }

    private Page<QuotePhone> findBySpecAndPaginate(Pageable pageable, PublicQuery publicQuery) {
        return quotePhoneRepository.findAll(new Specification<QuotePhone>() {
            @Override
            public Predicate toPredicate(Root<QuotePhone> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<QuotePhone> criteriaQuery = cb.createQuery(QuotePhone.class);
                List<Predicate> predicateList = new ArrayList<>();

                if (!StringUtils.isEmpty(publicQuery.getMobile())) {
                    Path<String> mobilePath = root.get("user").get("mobile");
                    predicateList.add(cb.like(mobilePath, publicQuery.getMobile() + "%"));
                }
                if (!StringUtils.isEmpty(publicQuery.getLicensePlateNo())) {
                    Path<String> licensePlateNoPath = root.get("licensePlateNo");
                    predicateList.add(cb.like(licensePlateNoPath, publicQuery.getLicensePlateNo() + "%"));
                }

                if (null != publicQuery.getVisited()) {
                    Path<Boolean> visitedPath = root.get("visited");
                    predicateList.add(cb.equal(visitedPath, publicQuery.getVisited()));
                }
                if (null != publicQuery.getChannel()) {
                    Path<Integer> channelPath = root.get("sourceChannel");
                    predicateList.add(cb.equal(channelPath, publicQuery.getChannel()));
                }

                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    public ResultModel changeVisited(Long id, Integer visited) {
        ResultModel result = new ResultModel();
        try {
            QuotePhone quotePhone = quotePhoneRepository.findOne(id);
            quotePhone.setVisited(visited == 1 ? true : false);
            quotePhone.setOperator(internalUserManageService.getCurrentInternalUser());
            quotePhoneRepository.save(quotePhone);
            result.setPass(true);
        } catch (Exception e) {
            result.setPass(false);
            result.setMessage("操作失败，请稍后再试");
        }
        return result;
    }

    public QuotePhoneViewModel findById(Long id) {
        QuotePhone quotePhone = quotePhoneRepository.findOne(id);
        QuotePhoneViewModel viewModel = createViewData(quotePhone);
        return viewModel;
    }

    public QuotePhone getById(Long id) {
        return quotePhoneRepository.findOne(id);
    }

    public QuotePhoneViewModel createViewData(QuotePhone quotePhone) {
        QuotePhoneViewModel viewModel = new QuotePhoneViewModel();
        String[] contains = new String[]{"id", "licensePlateNo", "owner", "identityType", "identity", "insuredName", "insuredIdType", "insuredIdNo",
                "vinNo", "engineNo", "model", "code", "visited", "comment"};
        BeanUtil.copyPropertiesContain(quotePhone, viewModel, contains);
        viewModel.setUserId(quotePhone.getUser() == null ? null : quotePhone.getUser().getId());
        String mobile = quotePhone.getUser() != null ? quotePhone.getUser().getMobile() : "";
        viewModel.setMobile(mobile);
        viewModel.setEncyptMobile(MobileUtil.getEncyptMobile(mobile));

        viewModel.setEnrollDate(DateUtils.getDateString(quotePhone.getEnrollDate(), DateUtils.DATE_SHORTDATE_PATTERN));//初登时间
        viewModel.setExpireDate(DateUtils.getDateString(quotePhone.getExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));//保险到期时间
        viewModel.setCreateTime(DateUtils.getDateString(quotePhone.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));//创建时间
        viewModel.setUpdateTime(DateUtils.getDateString(quotePhone.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));//修改时间
        viewModel.setSourceChannel(quotePhone.getSourceChannel() == null ? null : quotePhone.getSourceChannel().getId());//来源
        viewModel.setTransferDate(DateUtils.getDateString(quotePhone.getTransferDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        if (quotePhone.getSourceChannel() != null && !StringUtils.isEmpty(quotePhone.getSourceChannel().getIcon())) {
            viewModel.setChannelIcon(resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getChannelPath()),
                    quotePhone.getSourceChannel().getIcon()));
        }
        return viewModel;
    }

    public ModelAndViewResult setComment(Long id, String comment) {
        ModelAndViewResult result = new ModelAndViewResult();

        try {
            QuotePhone phone = quotePhoneRepository.findOne(id);
            if (phone == null) {
                return null;
            }
            String newComment = comment.replace("\n", "\\r\\n");
            InternalUser internalUser = internalUserManageService.getCurrentInternalUser();
            logger.debug("修改电话信息备注，电话信息ID:{}，操作人:{}，原备注:{}，新备注:{}",
                    phone.getId(), internalUser.getName(), phone.getComment(), newComment);
            phone.setComment(newComment);
            phone.setOperator(internalUser);
            phone.setUpdateTime(Calendar.getInstance().getTime());
            quotePhoneRepository.save(phone);
            result.setResult(ModelAndViewResult.RESULT_SUCCESS);
        } catch (Exception e) {
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setMessage("操作失败，请稍后再试");
        }
        return result;
    }

    public QuotePhone updateByAuto(Long sourceId, Auto auto, Integer flowType) {
        if (null == auto) {
            return null;
        }

        boolean isModify = false;
        QuotePhone quotePhone = quotePhoneRepository.findOne(sourceId);
        AssertUtil.notNull(quotePhone, "can not find quotePhone by id -> " + sourceId);
        if (StringUtils.isNotBlank(auto.getLicensePlateNo()) && !auto.getLicensePlateNo().equals(quotePhone.getLicensePlateNo())) {
            isModify = true;
            quotePhone.setLicensePlateNo(auto.getLicensePlateNo());
        }
        if (StringUtils.isNotBlank(auto.getInsuredIdNo()) && !auto.getInsuredIdNo().equals(quotePhone.getInsuredIdNo())) {
            isModify = true;
            quotePhone.setInsuredIdNo(auto.getInsuredIdNo());
        }
        if (StringUtils.isNotBlank(auto.getIdentity()) && !auto.getIdentity().equals(quotePhone.getIdentity())) {
            isModify = true;
            quotePhone.setIdentity(auto.getIdentity());
        }
        if (StringUtils.isNotBlank(auto.getVinNo()) && !auto.getVinNo().equals(quotePhone.getVinNo())) {
            isModify = true;
            quotePhone.setVinNo(auto.getVinNo());
        }
        if (StringUtils.isNotBlank(auto.getEngineNo()) && !auto.getEngineNo().equals(quotePhone.getEngineNo())) {
            isModify = true;
            quotePhone.setEngineNo(auto.getEngineNo());
        }
        if (StringUtils.isNotBlank(auto.getOwner()) && !auto.getOwner().equals(quotePhone.getOwner())) {
            isModify = true;
            quotePhone.setOwner(auto.getOwner());
        }
        if (null != auto.getEnrollDate()) {
            if (null == quotePhone.getEnrollDate() || auto.getEnrollDate().compareTo(quotePhone.getEnrollDate()) != 0) {
                isModify = true;
                quotePhone.setEnrollDate(auto.getEnrollDate());
            }
        }
        if (null != auto.getAutoType() && StringUtils.isNotBlank(auto.getAutoType().getCode())
                && !auto.getAutoType().getCode().equals(quotePhone.getCode())) {
            isModify = true;
            quotePhone.setCode(auto.getAutoType().getCode());
        }
        if (isModify) {
            if (logger.isDebugEnabled()) {
                logger.debug("some auto info have been modified, need to update quotePhone.");
            }
            quotePhone.setUpdateTime(new Date());
            quotePhone.setOperator(internalUserManageService.getCurrentInternalUser());
            return quotePhoneRepository.save(quotePhone);
        }
        return null;
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
