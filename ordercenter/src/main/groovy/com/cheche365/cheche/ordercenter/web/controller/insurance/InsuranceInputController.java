package com.cheche365.cheche.ordercenter.web.controller.insurance;

import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.manage.common.service.reverse.OrderReverse;
import com.cheche365.cheche.ordercenter.service.insurance.InputInsuranceService;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.web.model.OrderReAssignViewModel;
import com.cheche365.cheche.ordercenter.web.model.order.OrderInsuranceViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

/**
 * 录入商业险保单控制器
 * Created by sunhuazhong on 2015/2/28.
 */
@RestController
public class InsuranceInputController {

    private Logger logger = LoggerFactory.getLogger(InsuranceInputController.class);

    @Autowired
    private InputInsuranceService inputInsuranceService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    /**
     * 保单保存
     *
     * @param orderReverse
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/orderCenter/insurance/save", method = RequestMethod.GET)
    public ResultModel insurance(@Valid OrderReverse orderReverse, BindingResult bindingResult) {
        try {
           inputInsuranceService.saveInsurance(orderReverse);
           return new ResultModel(true,"保存成功！");
        } catch (Exception e) {
            logger.error("save client insurance input error.", e);
            return new ResultModel(false,"保存失败");
        }
    }

    /**
     * 根据身份证号和车牌号获取信息
     * @return ModelAndViewResult
     */
    @RequestMapping(value = "/orderCenter/insurance/init",method = RequestMethod.GET)
    public OrderInsuranceViewModel getOrderInfo(@RequestParam(value = "id",required = true) Long id){
        try {
            return inputInsuranceService.getPaidPurchaseOrderById(id);
        }catch(Exception ex){
            logger.error("find orderInfo by order id error.", ex);
            return null;
        }
    }

    /**
     * 校验总保费是否与原订单一致
     * @param id
     * @param premium
     * @param compulsoryPremium
     * @return
     */
    @RequestMapping(value = "/orderCenter/insurance/checkPremium",method = RequestMethod.GET)
    public ResultModel checkPremium(@RequestParam(value = "id",required = true) Long id,
                                    @RequestParam(value = "premium",required = true) double premium,
                                    @RequestParam(value = "compulsoryPremium",required = true) double compulsoryPremium,
                                    @RequestParam(value = "autoTax",required = true) double autoTax){
        try {
            PurchaseOrder purchaseOrder =purchaseOrderService.findById(id);
            boolean checkFlag = inputInsuranceService.checkPremium(premium, compulsoryPremium, autoTax, purchaseOrder);
            return new ResultModel(checkFlag,"");
        }catch(Exception ex){
            return new ResultModel(false,"");
        }
    }
}

