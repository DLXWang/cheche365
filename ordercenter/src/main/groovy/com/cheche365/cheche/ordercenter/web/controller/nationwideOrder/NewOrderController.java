package com.cheche365.cheche.ordercenter.web.controller.nationwideOrder;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.service.InsuranceCompanyService;
import com.cheche365.cheche.core.service.InsurancePackageService;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.ordercenter.model.NationwideOrderQuery;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.InstitutionManageService;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.InstitutionQuoteRecordService;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.OrderCooperationInfoManageService;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.OrderCooperationStatusHandler;
import com.cheche365.cheche.ordercenter.service.order.OrderManageService;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.InstitutionQuoteRecordViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.OrderCooperationInfoViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Created by xu.yelong on 2015/11/17.
 */
@RestController
@RequestMapping("/orderCenter/nationwide/new")
public class NewOrderController extends NationalWideOrderController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderCooperationInfoManageService orderCooperationInfoManageService;

    @Autowired
    PurchaseOrderService purchaseOrderService;

    @Autowired
    InsurancePackageService insurancePackageService;

    @Autowired
    InsuranceCompanyService insuranceCompanyService;

    @Autowired
    InstitutionManageService institutionManageService;

    @Autowired
    OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    @Autowired
    InstitutionQuoteRecordService institutionQuoteRecordService;

    @Autowired
    private OrderCooperationStatusHandler orderCooperationStatusHandler;

    @Autowired
    private OrderManageService orderManageService;

    @Autowired
    private ResourceService resourceService;

    @RequestMapping(value = "",method = RequestMethod.GET)
    public PageViewModel<OrderCooperationInfoViewModel> find(@RequestParam(value = "currentPage",required = true) Integer currentPage,
                                                             @RequestParam(value = "pageSize",required = true) Integer pageSize,
                                                             NationwideOrderQuery query) {
        if(currentPage == null || currentPage < 1 ){
            throw new FieldValidtorException("list new order, currentPage can not be null or less than 1");
        }
        if(pageSize == null || pageSize < 1 ){
            throw new FieldValidtorException("list new order, pageSize can not be null or less than 1");
        }
        Page<OrderCooperationInfo> orderCooperationInfoPage = orderCooperationInfoManageService.listNewOrder(currentPage, pageSize, query);
        return createPageViewModel(orderCooperationInfoPage);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public OrderCooperationInfoViewModel findById(@PathVariable Long id) {
        OrderCooperationInfo orderCooperationInfo=orderCooperationInfoManageService.findById(id);
        if(orderCooperationInfo==null){
            return null;
        }
        return OrderCooperationInfoViewModel.createViewModel(orderCooperationInfo,resourceService);
    }

    @Override
    public void setProperties(OrderCooperationInfo orderCooperationInfo, OrderCooperationInfoViewModel viewModel) {
    }

    /**
     * 指派出单机构
     * */
    @RequestMapping(value = "/appoint",method = RequestMethod.PUT)
    public ResultModel appointInstitution(@RequestParam(value = "id",required = true) Long id,
                                          @RequestParam(value = "institutionId",required = true) Long institutionId,
                                          @RequestParam(value = "commercialRebate",required = true) Double commercialRebate,
                                          @RequestParam(value = "compulsoryRebate",required = true) Double compulsoryRebate){
        if(id==null||institutionId==null||commercialRebate==null||compulsoryRebate==null){
            return new ResultModel(false,"指派出单机构失败");
        }
        if(orderCooperationInfoManageService.appointInstitution(id,institutionId,commercialRebate,compulsoryRebate)){
            return new ResultModel(true,"操作成功");
        }
        return new ResultModel(false,"指派出单机构失败");
    }

    /**
     * 查询报价
     * */
    @RequestMapping(value = "/{orderNo}/getQuote",method = RequestMethod.GET)
    public InstitutionQuoteRecordViewModel getQuote(@PathVariable String orderNo){
        if(orderNo==null){
            return null;
        }
        PurchaseOrder purchaseOrder=purchaseOrderService.getFirstPurchaseOrderByNo(orderNo);
        InstitutionQuoteRecord quoteRecord=institutionQuoteRecordService.getByPurchaseOrder(purchaseOrder);
        if(quoteRecord!=null){
           return InstitutionQuoteRecordViewModel.createViewModel(quoteRecord);
        }else{
            return new InstitutionQuoteRecordViewModel();
        }
    }




    /**
     * 保存报价
     * */
    @RequestMapping(value = "/saveQuote",method = RequestMethod.POST)
    public ResultModel saveQuote(@RequestBody InstitutionQuoteRecordViewModel viewModel){
        try{
            PurchaseOrder purchaseOrder=purchaseOrderService.getFirstPurchaseOrderByNo(viewModel.getOrderNo());
            OrderCooperationInfo orderCooperationInfo=orderCooperationInfoManageService.findByPurchaseOrder(purchaseOrder);
            orderCooperationInfo.setQuoteStatus(true);
            orderCooperationInfo.setMatchStatus(true);
            orderCooperationInfo.setIncomeStatus(true);

            //判断险种匹配
            InstitutionQuoteRecord institutionQuoteRecord = createInstitutionQuoteRecord(viewModel);
            setMatchStatus(institutionQuoteRecord, orderCooperationInfo);

            //判断收益异常
            if(purchaseOrder.getPayableAmount()<=(viewModel.getPremium()-viewModel.getRebate())){
                orderCooperationInfo.setIncomeStatus(false);
            }

            orderCooperationInfoManageService.save(orderCooperationInfo);
            OrderCooperationStatus cooperationStatus = OrderCooperationStatus.Enum.format(OrderCooperationStatus.Enum.QUOTE_NO_AUDIT.getId());
            orderCooperationStatusHandler.request(orderCooperationInfo,cooperationStatus, null);
            return new ResultModel(true,"保存成功!");
        }catch(Exception ex){
            logger.error("save quote record has error", ex);
            return new ResultModel(false,"保存失败!");
        }
    }

    private void setMatchStatus(InstitutionQuoteRecord institutionQuoteRecord, OrderCooperationInfo orderCooperationInfo) {
        QuoteRecord quoteRecord = orderManageService.getQuoteRecordByPurchaseOrder(orderCooperationInfo.getPurchaseOrder());
        if(!quoteRecord.getInsurancePackage().getUniqueString().equals(institutionQuoteRecord.getInsurancePackage().getUniqueString())){
            // 默认情况下险种匹配为false
            orderCooperationInfo.setMatchStatus(false);
            String quoteRecordUniqueString = quoteRecord.getInsurancePackage().getUniqueString();
            String institutionUniqueString = institutionQuoteRecord.getInsurancePackage().getUniqueString();
            String tempQuoteRecordUniqueString = quoteRecordUniqueString.substring(0, 15);
            String tempInstitutionUniqueString = institutionUniqueString.substring(0, 15);
            // 排除掉玻璃险和玻璃类型判断险种是否匹配
            if (tempQuoteRecordUniqueString.equals(tempInstitutionUniqueString)) {
                // 在排除掉玻璃险和玻璃类型以及不计免赔险种匹配的前提下，先判断玻璃险和玻璃类型是否匹配
                if (!quoteRecord.getInsurancePackage().isGlass() && !institutionQuoteRecord.getInsurancePackage().isGlass()) {
                    orderCooperationInfo.setMatchStatus(true);
                } else if (quoteRecord.getInsurancePackage().isGlass() && institutionQuoteRecord.getInsurancePackage().isGlass()) {
                    Long quoteRecordGlassType = quoteRecord.getInsurancePackage().getGlassType() == null ?
                       0 : quoteRecord.getInsurancePackage().getGlassType().getId();
                    Long institutionGlassType = institutionQuoteRecord.getInsurancePackage().getGlassType() == null ?
                        0 : institutionQuoteRecord.getInsurancePackage().getGlassType().getId();
                    if (quoteRecordGlassType.equals(institutionGlassType)) {
                        orderCooperationInfo.setMatchStatus(true);
                    }
                }
                // 在排除掉玻璃险和玻璃类型以及不计免赔险种匹配的前提下，再判断不计免赔险种是否匹配
                if(orderCooperationInfo.getMatchStatus()) {
                    boolean blnIOPQuoteRecordUniqueString = quoteRecordUniqueString.substring(17).contains("1");
                    boolean blnIOPInstitutionUniqueString = institutionUniqueString.substring(17).contains("1");
                    if(blnIOPQuoteRecordUniqueString == blnIOPInstitutionUniqueString) {
                        orderCooperationInfo.setMatchStatus(true);
                    } else {
                        orderCooperationInfo.setMatchStatus(false);
                    }
                }
            }
        }
    }


    private InstitutionQuoteRecord createInstitutionQuoteRecord(InstitutionQuoteRecordViewModel viewModel){
        InstitutionQuoteRecord institutionQuoteRecord=new InstitutionQuoteRecord();
        if(viewModel.getId()!=null){
            institutionQuoteRecord=institutionQuoteRecordService.getById(viewModel.getId());
        }
        PurchaseOrder purchaseOrder=purchaseOrderService.getFirstPurchaseOrderByNo(viewModel.getOrderNo());
        institutionQuoteRecord.setPurchaseOrder(purchaseOrder);
        institutionQuoteRecord.setInsurancePackage(createInsurancePackage(viewModel));
        institutionQuoteRecord.setCommercialPremium(viewModel.getCommercialPremium());
        institutionQuoteRecord.setCompulsoryPremium(viewModel.getCompulsoryPremium());
        institutionQuoteRecord.setAutoTax(viewModel.getAutoTax());
        institutionQuoteRecord.setCreateTime(new Date());
        institutionQuoteRecord.setUpdateTime(new Date());
        institutionQuoteRecord.setOperator(orderCenterInternalUserManageService.getCurrentInternalUser());
        return institutionQuoteRecordService.save(institutionQuoteRecord);
    }


    private InsurancePackage createInsurancePackage(InstitutionQuoteRecordViewModel model) {
        InsurancePackage insurancePackage=model.getInsurancePackage();
        insurancePackage.calculateUniqueString();
        InsurancePackage old =insurancePackageService.findByUniqueString(insurancePackage.getUniqueString());
        if (null != old)
            return old;
        return insurancePackageService.saveInsurancePackage(insurancePackage);
    }

}
