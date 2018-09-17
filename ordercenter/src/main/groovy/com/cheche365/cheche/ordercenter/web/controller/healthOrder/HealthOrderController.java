package com.cheche365.cheche.ordercenter.web.controller.healthOrder;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.OrderOperationInfo;
import com.cheche365.cheche.core.model.abao.InsurancePolicy;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.ordercenter.service.healthOrder.HealthOrderService;
import com.cheche365.cheche.ordercenter.web.model.healthOrder.InsurancePolicyViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Created by cxy on 2016/12/23.
 */
@RestController
@RequestMapping(value = "/orderCenter/healthOrder")
public class HealthOrderController {
    @Autowired
    private HealthOrderService heathOrderService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @VisitorPermission("or10011")
    public DataTablePageViewModel getOrders(PublicQuery query) {
        Page<OrderOperationInfo> page = heathOrderService.getOrdersByPage(query);
        List<OrderOperationInfo> operationInfos = page.getContent();
        return new DataTablePageViewModel<>(page.getTotalElements(), page.getTotalElements(), query.getDraw(), heathOrderService.createViewModelList(operationInfos));
    }

    @RequestMapping(value = "/{insurancePolicyId}", method = RequestMethod.GET)
    public InsurancePolicyViewModel getInsurancePolicyInfo(@PathVariable Long insurancePolicyId) {
        InsurancePolicy insurancePolicy = heathOrderService.getById(insurancePolicyId);
        AssertUtil.notNull(insurancePolicy, "can not find insurancePolicy by id: " + insurancePolicyId);
        return heathOrderService.createViewModel(insurancePolicy);
    }

    @RequestMapping(value = "/paymentInfoDetail", method = RequestMethod.POST)
    public DataTablePageViewModel getPaymentInfoDetail(@RequestParam Long insurancePolicyId) {
        InsurancePolicy insurancePolicy = heathOrderService.getById(insurancePolicyId);
        AssertUtil.notNull(insurancePolicy, "can not find insurancePolicy by id: " + insurancePolicyId);
        return new DataTablePageViewModel(heathOrderService.getPaymentInfoDetail(insurancePolicy.getPurchaseOrder().getId()));
    }
}
