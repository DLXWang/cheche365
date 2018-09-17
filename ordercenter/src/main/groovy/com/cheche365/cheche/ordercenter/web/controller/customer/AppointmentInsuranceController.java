package com.cheche365.cheche.ordercenter.web.controller.customer;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.AppointmentInsurance;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.ResultModel;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.ordercenter.service.customer.AppointmentInsuranceManageService;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.ordercenter.web.model.customer.AppointmentInsuranceData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/7/24.
 */
@RestController
@RequestMapping("/orderCenter/customer")
public class AppointmentInsuranceController {
    @Autowired
    private AppointmentInsuranceManageService appointmentInsuranceManageService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private BaseService baseService;

    @RequestMapping(value = "/appointment", method = RequestMethod.GET)
    @VisitorPermission("or0201")
    public DataTablePageViewModel list(PublicQuery query) {
        Page<AppointmentInsurance> page = appointmentInsuranceManageService.getAppointmentInsuranceByPage(query);
        List<AppointmentInsuranceData> dataList = new ArrayList<>();
        page.getContent().forEach(appointmentInsurance -> dataList.add(createViewModel(appointmentInsurance)));
        PageInfo pageInfo = baseService.createPageInfo(page);
        return new DataTablePageViewModel<>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), query.getDraw(), dataList);
    }

    @RequestMapping(value = "channels", method = RequestMethod.GET)
    public List<Channel> getChannels() {
        List<Channel> channels = this.appointmentInsuranceManageService.getChannels();
        return channels;
    }

    /**
     * 修改客户预约状态
     *
     * @param status
     * @param appointmentInsuranceId
     * @return
     */
    @RequestMapping(value = "/status", method = RequestMethod.PUT)
    public ResultModel changeStatus(@RequestParam(value = "status", required = true) Integer status,
                                    @RequestParam(value = "appointmentInsuranceId", required = true) Long appointmentInsuranceId) {
        if (appointmentInsuranceId == null || appointmentInsuranceId < 1) {
            throw new FieldValidtorException("change status, id can not be null or less than 1");
        }
        boolean isSuccess = appointmentInsuranceManageService.changeStatus(status, appointmentInsuranceId);
        if (isSuccess) {
            return new ResultModel();
        } else {
            return new ResultModel(false, "修改客户预约处理状态失败！");
        }
    }

    /**
     * 保存备注
     *
     * @param comment
     * @param appointmentInsuranceId
     * @return
     */
    @RequestMapping(value = "/comment", method = RequestMethod.PUT)
    public ResultModel updateComment(@RequestParam(value = "comment", required = true) String comment,
                                     @RequestParam(value = "appointmentInsuranceId", required = true) Long appointmentInsuranceId) {
        if (appointmentInsuranceId == null || appointmentInsuranceId < 1) {
            throw new FieldValidtorException("change comment, id can not be null or less than 1");
        }
        boolean isSuccess = appointmentInsuranceManageService.updateComment(comment, appointmentInsuranceId);
        if (isSuccess) {
            return new ResultModel();
        } else {
            return new ResultModel(false, "修改客户预约备注失败！");
        }
    }

    private AppointmentInsuranceData createViewModel(AppointmentInsurance appointmentInsurance) {
        AppointmentInsuranceData viewData = new AppointmentInsuranceData();
        viewData.setId(appointmentInsurance.getId());
        viewData.setSourceChannel(Integer.parseInt(String.valueOf(appointmentInsurance.getSourceChannel().getId())));
        viewData.setContact(appointmentInsurance.getContact());
        viewData.setLicensePlateNo(appointmentInsurance.getLicensePlateNo());
        viewData.setMobile(appointmentInsurance.getUser().getEncyptMobile());
        viewData.setExpireBefore(DateUtils.getDateString(
            appointmentInsurance.getExpireBefore(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewData.setCreateTime(DateUtils.getDateString(
            appointmentInsurance.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewData.setStatus(appointmentInsurance.getStatus());
        viewData.setComment(appointmentInsurance.getComment());
        if (!StringUtils.isEmpty(appointmentInsurance.getSourceChannel().getIcon())) {
            viewData.setChannelIcon(resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getChannelPath()),
                appointmentInsurance.getSourceChannel().getIcon()));
        }
        return viewData;
    }
}
