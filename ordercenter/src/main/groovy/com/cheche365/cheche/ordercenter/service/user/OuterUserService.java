package com.cheche365.cheche.ordercenter.service.user;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.AutoRepository;
import com.cheche365.cheche.core.repository.OrderOperationInfoRepository;
import com.cheche365.cheche.core.repository.OrderProcessHistoryRepository;
import com.cheche365.cheche.core.repository.UserRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.ordercenter.web.model.user.UserViewModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by wangshaobin on 2017/3/30.
 */
@Service
public class OuterUserService extends BaseService {

    private static final Integer MOBILE = 1;//是否按照手机号进行筛选

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;

    @Autowired
    private OrderProcessHistoryRepository orderProcessHistoryRepository;

    @Autowired
    private AutoRepository autoRepository;

    public DataTablePageViewModel<UserViewModel> findAllUserInfos(Integer currentPage, Integer pageSize, String keyword, Integer keyType, Integer draw) throws UnsupportedEncodingException {

        List<Object[]> userInfoWithAutos;
        Long totalElements;
        int startIndex = (currentPage - 1) * pageSize;
        boolean isMobileNull = true;
        if (StringUtils.isNotBlank(keyword) && keyType == MOBILE) {
            isMobileNull = false;
        }
        userInfoWithAutos = userRepository.findUserInfoByMobile(isMobileNull, keyword, startIndex, pageSize);
        totalElements = userRepository.countAllOrByMobile(isMobileNull, keyword);

        List<UserAutoModel> userAutoModelList = this.convertToUserAutoModel(userInfoWithAutos);
        List<UserViewModel> userViewModelList = this.convertToUserViewModel(userAutoModelList);
        this.sortDescending(userViewModelList, u -> u.getId());

        return new DataTablePageViewModel<>(totalElements, totalElements, draw, userViewModelList);
    }

    public Page<OrderOperationInfo> findOrderInfoByUserId(Long userId, Integer currentPage, Integer pageSize){
        return findInfoBySpecAndPaginate(buildPageable(currentPage, pageSize, Sort.Direction.DESC, "purchaseOrder"), userId);
    }

    public List<OrderProcessHistory> getOrderHistories(Long purchaseOrderId) {
        return orderProcessHistoryRepository.findByOrderIdOrderByCreateTime(purchaseOrderId);
    }

    public Page<Auto> findAutoInfoByUserId(Integer currentPage, Integer pageSize, Integer draw, Long userId) {
        return autoRepository.findAll(new Specification<Auto>() {
            @Override
            public Predicate toPredicate(Root<Auto> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<Auto> criteriaQuery = cb.createQuery(Auto.class);
                Root<UserAuto> userAutoRoot = query.from(UserAuto.class);
                List<Predicate> predicateList = new ArrayList<Predicate>();
                predicateList.add(cb.equal(root.get("id"), userAutoRoot.get("auto").get("id")));
                predicateList.add(cb.equal(userAutoRoot.get("user").get("id"), userId));
                predicateList.add(cb.equal(root.get("disable"), 0));
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, buildPageable(currentPage, pageSize, Sort.Direction.ASC, "id"));
    }

    private Page<OrderOperationInfo> findInfoBySpecAndPaginate(Pageable pageable, Long userId){
        return orderOperationInfoRepository.findAll(new Specification<OrderOperationInfo>() {
            @Override
            public Predicate toPredicate(Root<OrderOperationInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<OrderOperationInfo> criteriaQuery = cb.createQuery(OrderOperationInfo.class);
                Join<OrderOperationInfo, PurchaseOrder> orderJoin = root.join("purchaseOrder");
                List<Predicate> predicateList = new ArrayList<Predicate>();
                predicateList.add(cb.equal(orderJoin.get("applicant").get("id"), userId));
                predicateList.add(cb.notEqual(orderJoin.get("type").get("id"), OrderType.Enum.HEALTH.getId()));
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    public static <T, R extends Comparable<? super R>> void sortDescending(List<T> data, Function<T, R> func) {
        Comparator<T> comparator = Comparator.comparing(func).reversed();
        data.sort(comparator);
    }

    private List<UserViewModel> convertToUserViewModel(List<UserAutoModel> userAutoModelList) {
        List<UserViewModel> userViewModelList = new ArrayList<>();
        Map<Long, List<UserAutoModel>> userAutoListMap = userAutoModelList.stream().collect(Collectors.groupingBy(UserAutoModel::getId));
        for (List<UserAutoModel> userAutoModels : userAutoListMap.values()) {
            UserViewModel userViewModel = new UserViewModel();
            UserAutoModel firstUserAutoModel = userAutoModels.get(0);

            userViewModel.setId(firstUserAutoModel.getId());
            userViewModel.setLastLoginTime(firstUserAutoModel.getLastLoginTime());
            userViewModel.setMobile(firstUserAutoModel.getMobile());
            userViewModel.setRegChannel(firstUserAutoModel.getRegChannel());
            userViewModel.setRegIp(firstUserAutoModel.getRegIp());
            userViewModel.setRegtime(firstUserAutoModel.getRegtime());

            userViewModelList.add(userViewModel);
        }
        return userViewModelList;
    }

    private List<UserAutoModel> convertToUserAutoModel(List<Object[]> userInfoWithAutos) throws UnsupportedEncodingException {
        List<UserAutoModel> userAutoModelList = new ArrayList<>();
        UserAutoModel userAutoModel;
        for (Object[] objects : userInfoWithAutos) {
            userAutoModel = new UserAutoModel();
            userAutoModel.setId(NumberUtils.toLong(objects[0].toString()));
            String mobile = (objects[1] == null || !StringUtils.isNotBlank((String)objects[1]))?"" : new StringBuffer( ((String)objects[1]).substring(0,3)).append("****").append(((String)objects[1]).substring(7,11)).toString();
            userAutoModel.setMobile(mobile);
            userAutoModel.setRegtime(DateUtils.getDateString((Date) objects[2], DateUtils.DATE_LONGTIME24_PATTERN));
            userAutoModel.setRegChannel(objects[3] == null ? "" : (String) objects[3]);
            userAutoModel.setRegIp(objects[4] == null ? "" : (String) objects[4]);
            userAutoModel.setLastLoginTime(DateUtils.getDateString((Date) objects[5], DateUtils.DATE_LONGTIME24_PATTERN));
            userAutoModelList.add(userAutoModel);
        }
        return userAutoModelList;
    }

    class UserAutoModel {
        private Long id;
        private String mobile;//手机号
        private String binding;//第三方绑定
        private String regtime;//注册时间
        private String regChannel;//注册渠道
        private String regIp;//注册IP
        private String lastLoginTime;//最后登录时间
        private Long autoId;
        private String licensePlateNo; //车牌号

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getBinding() {
            return binding;
        }

        public void setBinding(String binding) {
            this.binding = binding;
        }

        public String getRegtime() {
            return regtime;
        }

        public void setRegtime(String regtime) {
            this.regtime = regtime;
        }

        public String getRegChannel() {
            return regChannel;
        }

        public void setRegChannel(String regChannel) {
            this.regChannel = regChannel;
        }

        public String getRegIp() {
            return regIp;
        }

        public void setRegIp(String regIp) {
            this.regIp = regIp;
        }

        public String getLastLoginTime() {
            return lastLoginTime;
        }

        public void setLastLoginTime(String lastLoginTime) {
            this.lastLoginTime = lastLoginTime;
        }

        public Long getAutoId() {
            return autoId;
        }

        public void setAutoId(Long autoId) {
            this.autoId = autoId;
        }

        public String getLicensePlateNo() {
            return licensePlateNo;
        }

        public void setLicensePlateNo(String licensePlateNo) {
            this.licensePlateNo = licensePlateNo;
        }
    }
}
