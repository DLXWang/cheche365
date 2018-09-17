package com.cheche365.cheche.admin.service.user;

import com.cheche365.cheche.admin.constants.KeyTypeEnum;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.admin.web.model.auto.AutoViewModel;
import com.cheche365.cheche.admin.web.model.user.UserViewModel;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by guoweifu on 2015/9/8.
 */
@Service
public class UserManagementService extends BaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRepository userRepository;


    public PageViewModel findAllUserInfos(Integer currentPage, Integer pageSize, String keyword, Integer keyType) throws UnsupportedEncodingException {
        PageViewModel model = new PageViewModel<UserViewModel>();
        PageInfo pageInfo = new PageInfo();

        List<Object[]> userInfoWithAutos;
        Long totalElements;
        int startIndex = (currentPage - 1) * pageSize;
        if (StringUtils.isNotBlank(keyword) && keyType == KeyTypeEnum.LICENSE_PLATE_NO.getIndex()) {//根据车牌号查询
            userInfoWithAutos = userRepository.findUserInfosByLicensePlateNo(keyword, startIndex, pageSize);
            totalElements = userRepository.countByLicensePlateno(keyword);
        } else {//查询全部或根据手机号查询
            boolean isMobileNull = true;
            if (StringUtils.isNotBlank(keyword) && keyType == KeyTypeEnum.MOBILE.getIndex()) {
                isMobileNull = false;
            }
            userInfoWithAutos = userRepository.findUserInfoAllOrByMobile(isMobileNull, keyword, startIndex, pageSize);
            totalElements = userRepository.countAllOrByMobile(isMobileNull, keyword);
        }

        pageInfo.setTotalElements(totalElements);
        pageInfo.setTotalPage(totalElements % pageSize == 0 ? totalElements / pageSize : (totalElements / pageSize + 1));
        model.setPageInfo(pageInfo);

        List<UserAutoModel> userAutoModelList = this.convertToUserAutoModel(userInfoWithAutos);

        List<UserViewModel> userViewModelList = this.convertToUserViewModel(userAutoModelList);
        this.sortDescending(userViewModelList, u -> u.getId());
        model.setViewList(userViewModelList);

        return model;
    }

    private List<UserViewModel> convertToUserViewModel(List<UserAutoModel> userAutoModelList) {
        List<UserViewModel> userViewModelList = new ArrayList<>();
        Map<Long, List<UserAutoModel>> userAutoListMap = userAutoModelList.stream().collect(Collectors.groupingBy(UserAutoModel::getId));
        for (List<UserAutoModel> userAutoModels : userAutoListMap.values()) {
            UserViewModel userViewModel = new UserViewModel();
            UserAutoModel firstUserAutoModel = userAutoModels.get(0);

            userViewModel.setId(firstUserAutoModel.getId());
            userViewModel.setLastLoginTime(firstUserAutoModel.getLastLoginTime());
            userViewModel.setBinding("".equals(firstUserAutoModel.getBinding()) ? "" : "微信：" + firstUserAutoModel.getBinding());
            userViewModel.setMobile(firstUserAutoModel.getMobile());
            userViewModel.setRegChannel(firstUserAutoModel.getRegChannel());
            userViewModel.setRegIp(firstUserAutoModel.getRegIp());
            userViewModel.setRegtime(firstUserAutoModel.getRegtime());

            userViewModel.setAutos(this.createAutoViewModel(userAutoModels));

            userViewModelList.add(userViewModel);
        }
        return userViewModelList;
    }

    private List<AutoViewModel> createAutoViewModel(List<UserAutoModel> userAutoModels) {
        List<AutoViewModel> autoViewModelList = autoViewModelList = new ArrayList<>();
        for (UserAutoModel userAutoModel : userAutoModels) {
            AutoViewModel autoViewModel = new AutoViewModel();
            if (userAutoModel.getAutoId().equals(0L)) {
                continue;
            }

            autoViewModel.setId(userAutoModel.getAutoId());
            autoViewModel.setLicensePlateNo(userAutoModel.getLicensePlateNo());

            autoViewModelList.add(autoViewModel);
        }
        return autoViewModelList;
    }

    private List<UserAutoModel> convertToUserAutoModel(List<Object[]> userInfoWithAutos) throws UnsupportedEncodingException {
        List<UserAutoModel> userAutoModelList = new ArrayList<>();
        UserAutoModel userAutoModel;
        for (Object[] objects : userInfoWithAutos) {
            userAutoModel = new UserAutoModel();
            userAutoModel.setId(NumberUtils.toLong(objects[0].toString()));
            userAutoModel.setMobile(objects[1] == null ? "" : (String) objects[1]);
            userAutoModel.setBinding(objects[2] == null ? "" : (String) objects[2]);
            userAutoModel.setRegtime(DateUtils.getDateString((Date) objects[3], DateUtils.DATE_LONGTIME24_HOUR_PATTERN));
            userAutoModel.setRegChannel(objects[4] == null ? "" : (String) objects[4]);
            userAutoModel.setRegIp(objects[5] == null ? "" : (String) objects[5]);
            userAutoModel.setLastLoginTime(DateUtils.getDateString((Date) objects[6], DateUtils.DATE_LONGTIME24_HOUR_PATTERN));
            userAutoModel.setAutoId(objects[7] == null ? 0 : ((BigInteger) objects[7]).longValue());
            userAutoModel.setLicensePlateNo(objects[8] == null ? "" : (String) objects[8]);
            userAutoModelList.add(userAutoModel);
        }
        return userAutoModelList;
    }


    public static <T, R extends Comparable<? super R>> void sortDescending(List<T> data, Function<T, R> func) {
        Comparator<T> comparator = Comparator.comparing(func).reversed();
        data.sort(comparator);
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
