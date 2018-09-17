package com.cheche365.cheche.ordercenter.web.controller.nationwideOrder;

import com.cheche365.cheche.manage.common.service.reverse.OrderReverse;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.NationalWideInsuranceInputService;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.web.model.order.OrderInsuranceViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by taguangyao on 2015/11/23.
 */
@RestController
@RequestMapping("/orderCenter/nationwide")
public class NationalWideInsuranceInputController {
    private Logger logger = LoggerFactory.getLogger(NationalWideInsuranceInputController.class);

    @Autowired
    private NationalWideInsuranceInputService nationalWideInsuranceInputService;

    @RequestMapping(value = "/{orderNo}/orderInsuranceRecord", method = RequestMethod.GET)
    public OrderInsuranceViewModel findOneOrderInsuranceRecord(@PathVariable String orderNo) {
        logger.info("find order insurance record info by orderNo -> {}", orderNo);
        return nationalWideInsuranceInputService.findModelByOrderCooperationNo(orderNo);
    }

    @RequestMapping(value = "/{orderNo}/orderInsuranceRecord", method = RequestMethod.PUT)
    public ResultModel saveOrderInsuranceRecord(@PathVariable String orderNo, OrderReverse model, BindingResult bindingResult) {
        logger.info("update order insurance record info by  orderNo -> {}", orderNo);
        if (bindingResult.hasErrors()) {
            return new ResultModel(false, "请将信息填写完整");
        }
        ResultModel resultModel = nationalWideInsuranceInputService.validRepetitivePolicyNo(
            orderNo, model.getCommercialPolicyNo(), model.getCompulsoryPolicyNo());
        if (resultModel != null) {
            return resultModel;
        }
        resultModel = nationalWideInsuranceInputService.validPremium(model);
        if (resultModel != null) {
            return resultModel;
        }
        nationalWideInsuranceInputService.updateOrderInsuranceRecord(model);
        return new ResultModel();
    }

}
