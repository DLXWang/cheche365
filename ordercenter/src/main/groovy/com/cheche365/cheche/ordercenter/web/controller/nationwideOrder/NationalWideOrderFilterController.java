package com.cheche365.cheche.ordercenter.web.controller.nationwideOrder;

import com.cheche365.cheche.core.model.OrderCooperationInfo;
import com.cheche365.cheche.core.model.PaymentStatus;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.NationalWideOrderFilterService;
import com.cheche365.cheche.ordercenter.service.nationwideOrder.OrderCooperationInfoManageService;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.ordercenter.web.model.nationwideOrder.OrderCooperationInfoViewModel;
import com.cheche365.cheche.ordercenter.web.model.order.OrderFilterRequestParams;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * 合作出单订单筛选
 * Created by taguangyao on 2015/11/20.
 */
@RestController
@RequestMapping("/orderCenter/nationwide/")
public class NationalWideOrderFilterController extends NationalWideOrderController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NationalWideOrderFilterService nationalWideOrderFilterService;

    @Autowired
    private OrderCooperationInfoManageService orderCooperationInfoManageService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @RequestMapping(value = "/filter",method = RequestMethod.GET)
    public PageViewModel<OrderCooperationInfoViewModel> search(OrderFilterRequestParams requestParams){
        if(requestParams.getCurrentPage() == null || requestParams.getCurrentPage() < 1 ){
            throw new FieldValidtorException("national wide order filter list, currentPage can not be null or less than 1");
        }
        if(requestParams.getPageSize() == null || requestParams.getPageSize() < 1 ){
            throw new FieldValidtorException("national wide order filter list, pageSize can not be null or less than 1");
        }
        Page<OrderCooperationInfo> orderCooperationInfoPage = nationalWideOrderFilterService.filterOrders(requestParams);
        return createPageViewModel(orderCooperationInfoPage);
    }

    @RequestMapping(value = "/export",method = RequestMethod.GET)
    public void export(OrderFilterRequestParams requestParams, HttpServletResponse response) {
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");

        OutputStream out = null;
        try {
            response.setHeader("Content-Disposition", "attachment; filename=" + new String(("合作订单查询结果.xls").getBytes(), "iso-8859-1"));
            HSSFWorkbook workbook = nationalWideOrderFilterService.createExportExcel(requestParams);
            out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
        } catch (Exception ex) {
            logger.error("export result excel has error", ex);
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception ex) {
                logger.error("export result, close OutputStream has error", ex);
            }
        }
    }

    @Override
    public void setProperties(OrderCooperationInfo orderCooperationInfo, OrderCooperationInfoViewModel viewModel) {
        // 支付状态
        PurchaseOrder purchaseOrder = orderCooperationInfo.getPurchaseOrder();
        PaymentStatus paymentStatus = orderCooperationInfoManageService.getPaymentStatus(purchaseOrder);
        setPaymentStatus(orderCooperationInfo, paymentStatus, viewModel);
        // 用户来源
        viewModel.setSource(purchaseOrderService.getUserSource(purchaseOrder));
    }
}
