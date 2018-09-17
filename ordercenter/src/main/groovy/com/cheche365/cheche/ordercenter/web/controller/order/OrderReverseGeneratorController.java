package com.cheche365.cheche.ordercenter.web.controller.order;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.AreaRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import com.cheche365.cheche.core.service.OrderOperationInfoService;
import com.cheche365.cheche.core.service.UserService;
import com.cheche365.cheche.manage.common.service.reverse.InsuranceReverseProcess;
import com.cheche365.cheche.manage.common.service.reverse.OrderReverse;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.ordercenter.service.order.OrderReverseGeneratorService;
import com.cheche365.cheche.ordercenter.service.order.OrderTransmissionStatusHandler;
import com.cheche365.cheche.ordercenter.web.model.order.OrderInsuranceViewModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * Created by wangfei on 2015/8/18.
 */
@RestController
@RequestMapping("/orderCenter/order")
public class OrderReverseGeneratorController {
    private Logger logger = LoggerFactory.getLogger(OrderReverseGeneratorController.class);

    @Autowired
    private OrderReverseGeneratorService orderReverseGeneratorService;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderOperationInfoService orderOperationInfoService;


    @Autowired
    private OrderTransmissionStatusHandler orderTransmissionStatusHandler;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private InsuranceReverseProcess insuranceReverseProcess;

    @RequestMapping(value = "/insurances", method = RequestMethod.GET)
    @VisitorPermission("or0105")
    public DataTablePageViewModel<OrderInsuranceViewModel> list(PublicQuery query) throws Exception {
        logger.debug("list insurance orders, currentPage:{},pageSize:{},keyword:{}", query.getCurrentPage(), query.getPageSize(), query.getKeyword());

        if (query.getCurrentPage() == null || query.getCurrentPage() < 1) {
            logger.debug("list insurance orders, currentPage can not be null or less than 1");
            return null;
        }

        if (query.getPageSize() == null || query.getPageSize() < 1) {
            logger.debug("list insurance orders, pageSize can not be null or less than 1");
            return null;
        }

        return orderReverseGeneratorService.listInsurances(query);
    }

    @RequestMapping(value = "/{orderNo}/insurance", method = RequestMethod.GET)
    public OrderInsuranceViewModel findOne(@PathVariable String orderNo) {
        logger.debug("find insurance info by orderNo -> {}", orderNo);
        return orderReverseGeneratorService.findModelByOrderNo(orderNo);
    }

    @RequestMapping(value = "/insurance", method = RequestMethod.POST)
    public ResultModel add(@Valid OrderReverse model, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return new ResultModel(false, "请将信息填写完整");

        String policyNo = orderReverseGeneratorService.validRepetitivePolicyNo(model.getCommercialPolicyNo(), model.getCompulsoryPolicyNo());
        if (StringUtils.isNotBlank(policyNo))
            return new ResultModel(false, "保单号" + policyNo + "已经存在，请重新输入");
        List<Long> enableAreaIds = areaRepository.findAllIdByActive(true);

        if (enableAreaIds.indexOf(model.getArea()) > -1 && model.getInstitution() == null) {
            return new ResultModel(false, "所选城市的出单机构不能为空");
        }

        if (!StringUtils.isEmpty(model.getLicensePlateNo()) && !orderReverseGeneratorService.validAutoArea(model)) {
            return new ResultModel(false, "车牌号与所投保区域不匹配");
        }

        if (StringUtils.isNoneBlank(model.getMobile())) {
            User user = userService.getUserByMobile(model.getMobile());
            if (user == null)
                return new ResultModel(false, "没有该手机号对应的用户！");
        } else {
            if (!orderReverseGeneratorService.validAgent(model.getRecommender())) {
                return new ResultModel(false, "该推荐人在微信端没有绑定手机号");
            }
            if (!orderReverseGeneratorService.validAgentRebate(model)) {
                return new ResultModel(false, "该推荐人在指定的投保时间、城市和保险公司内未找到相应的费率，请添加费率");
            }
        }
        insuranceReverseProcess.doService(model);
        return new ResultModel();
    }

    @RequestMapping(value = "/{orderNo}/insurance", method = RequestMethod.PUT)
    public ResultModel update(@PathVariable String orderNo, OrderReverse model, BindingResult bindingResult) {
        logger.debug("update insurance info by orderNo -> {}", orderNo);
        if (bindingResult.hasErrors()) {
            return new ResultModel(false, "请将信息填写完整");
        }


        ResultModel resultModel = orderReverseGeneratorService.validRepetitivePolicyNo(
                orderNo, model.getCommercialPolicyNo(), model.getCompulsoryPolicyNo());
        if (resultModel != null) {
            return resultModel;
        }

        List<Long> enableAreaIds = areaRepository.findAllIdByActive(true);

        if (enableAreaIds.indexOf(model.getArea()) > -1 && model.getInstitution() == null) {
            return new ResultModel(false, "所选城市的出单机构不能为空");
        }

        if (!orderReverseGeneratorService.validAutoArea(model)) {
            return new ResultModel(false, "车牌号与所投保区域不匹配");
        }

        if (StringUtils.isNoneBlank(model.getMobile())) {
            User user = userService.getUserByMobile(model.getMobile());
            if (user == null)
                return new ResultModel(false, "没有该手机号对应的用户！");
        } else {
            if (!orderReverseGeneratorService.validAgent(model.getRecommender())) {
                return new ResultModel(false, "该推荐人在微信端没有绑定手机号");
            }
        }
        insuranceReverseProcess.doService(model);
        return new ResultModel();
    }

    @RequestMapping(value = "/licensePlateNo", method = RequestMethod.GET)
    public PageViewModel listByLicensePlateNo(@RequestParam(value = "licensePlateNo") String licensePlateNo) {
        try {
            PageViewModel pageViewModel = new PageViewModel();
            List<OrderInsuranceViewModel> orderOperationInfoList = orderReverseGeneratorService.listByLicensePlateNo(licensePlateNo);
            pageViewModel.setViewList(orderOperationInfoList);
            return pageViewModel;
        } catch (Exception e) {
            logger.debug("录入保单查询已支付订单错误,车牌号:{}", licensePlateNo, e);
        }
        return null;
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @VisitorPermission("or0110")
    public ResultModel getUserByMobile(@RequestParam(value = "mobile") String mobile){
        User user = userService.getUserByMobile(mobile);
        if (user == null)
            return new ResultModel(false, "没有该手机号对应的用户！");
        return new ResultModel(true, user.getId().toString());
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    @VisitorPermission("or0110")
    public ResultModel getStatusByOrderNo(@RequestParam(value = "orderNo") String orderNo) {
        /**
         * 判断订单状态是否是承保失败的，报价的保险公司为安心
         * **/
        PurchaseOrder order = purchaseOrderRepository.findFirstByOrderNo(orderNo);
        if (order == null)
            return new ResultModel(false, "在出单中心查询不到该订单");

        if (!OrderStatus.Enum.PAID_3.equals(order.getStatus()))
            return new ResultModel(false, "不是出单中状态！");

        QuoteRecord record = quoteRecordRepository.findOne(order.getObjId());
        if (!InsuranceCompany.Enum.ANSWERN_65000.equals(record.getInsuranceCompany()))
            return new ResultModel(false, "不是安心保险的订单！");

        //修改出单的状态为录单完成
        OrderOperationInfo info = orderOperationInfoService.findByOrderNo(orderNo);
        orderTransmissionStatusHandler.request(info, OrderTransmissionStatus.Enum.ORDER_INPUTED);
        //如果确认出单时间为空的话，修改确认出单时间为当前时间，因为如果不改的话，跳转到修改保安界面，出单机构取不出来
        if (info.getConfirmOrderDate() == null)
            info.setConfirmOrderDate(new Date());
        logger.debug("订单号为【{}】的订单确认出单时间从{}变更为{}", orderNo, DateUtils.getDateString(info.getConfirmOrderDate(), DateUtils.DATE_LONGTIME24_PATTERN), DateUtils.getDateString(new Date(), DateUtils.DATE_LONGTIME24_PATTERN));
        orderOperationInfoService.save(info);
        return new ResultModel(true, order.getId().toString());
    }

}
