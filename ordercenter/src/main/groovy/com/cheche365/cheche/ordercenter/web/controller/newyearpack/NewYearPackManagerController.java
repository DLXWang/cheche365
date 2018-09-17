package com.cheche365.cheche.ordercenter.web.controller.newyearpack;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.ordercenter.constants.NewYearPackStatusEum;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.ordercenter.service.newyearpack.NewYearPackManagerService;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.ordercenter.web.model.newYearPack.NewYearPackViewModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yang on 2015/12/30.
 */
@RestController
@RequestMapping("/orderCenter/newyearpack")
public class NewYearPackManagerController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NewYearPackManagerService newYearPackManagerService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private ResourceService resourceService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @VisitorPermission("or0801")
    public DataTablePageViewModel list(PublicQuery query) {
       return null;
    }

    @RequestMapping(value = "/cancel/{purchaseOrderId}", method = RequestMethod.GET)
    @VisitorPermission("or080101")
    public ResultModel cancel(@PathVariable Long purchaseOrderId) {
        try {
            newYearPackManagerService.cancel(purchaseOrderId);
            return new ResultModel();
        } catch (Exception e) {
            logger.error("cancel new year pack purchase order error", e);
            return new ResultModel(false, "修改状态失败");
        }
    }

    @RequestMapping(value = "/rule", method = RequestMethod.GET)
    public void rule(HttpServletResponse response) {
        try {
            response.sendRedirect(newYearPackManagerService.getNewYearRulePath());
        } catch (Exception ex) {
            logger.error("show new year pack rule error", ex);
        }
    }

    @RequestMapping(value = "/quantity", method = RequestMethod.GET)
    public ResultModel getCodeRemainQuantity() {
        try {
            String quantity = newYearPackManagerService.getCodeRemainQuantity();
            return new ResultModel(true, quantity);
        } catch (Exception e) {
            logger.error("get third party code remain quantity error", e);
            return new ResultModel(false, "修改状态失败");
        }
    }

}
