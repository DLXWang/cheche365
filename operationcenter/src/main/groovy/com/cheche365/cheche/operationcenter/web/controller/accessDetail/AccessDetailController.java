package com.cheche365.cheche.operationcenter.web.controller.accessDetail;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.repository.AccessDetailRepository;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.operationcenter.model.AccessDetailQuery;
import com.cheche365.cheche.operationcenter.service.accessDetail.AccessDetailService;
import com.cheche365.cheche.operationcenter.web.model.marketing.AccessDetailData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by chenxiangyin on 2017/8/29.
 */
@RestController
@RequestMapping("/operationcenter/accessDetail")
public class AccessDetailController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AccessDetailService accessDetailService;
    @Autowired
    private AccessDetailRepository repository;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @VisitorPermission("op0703")
    public DataTablePageViewModel list(AccessDetailQuery query) {
        List<Tuple> accessDetailList = accessDetailService.findAccessDetailGroup(query);
        List<AccessDetailData> modelList = new ArrayList<>();
        accessDetailList.forEach(accessDetailInfo -> modelList.add(accessDetailService.createAccessDetail(accessDetailInfo)));
        return new DataTablePageViewModel<AccessDetailData>(new Long((long)accessDetailList.size()), new Long((long)accessDetailList.size()), query.getDraw(), modelList);
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @VisitorPermission("op0703")
    public DataTablePageViewModel infoList(AccessDetailQuery query) {
        Integer totalElement = repository.accessDetailInfoNum(query.getSource());
        List<Tuple> accessDetailInfoList = accessDetailService.getOrdersByPage(query);
        List<AccessDetailData> modelList = new ArrayList<>();
        accessDetailInfoList.forEach(accessDetailInfo -> modelList.add(accessDetailService.createAccessDetailInfo(accessDetailInfo)));
        return new DataTablePageViewModel<AccessDetailData>(new Long((long)totalElement), new Long((long)totalElement), query.getDraw(),modelList);
    }

//    @RequestMapping(value = "/export", method = RequestMethod.GET)
//    //@VisitorPermission("op0701")
//    public ResultModel export(AccessDetailQuery query, HttpServletResponse response) {
//        query.setCurrentPage(1);
//        query.setPageSize(99999);
//        List<Tuple> accessDetailList = accessDetailService.findAccessDetailGroup(query);
//        return accessDetailService.export(accessDetailList, response);
//    }

    @RequestMapping(value = "/info/export", method = RequestMethod.GET)
    @VisitorPermission("op0703")
    public ResultModel exportInfo(AccessDetailQuery query, HttpServletResponse response) {
        query.setCurrentPage(1);
        query.setPageSize(99999);
        Integer totalElement = repository.accessDetailInfoNum(query.getSource());
        List<Tuple> accessDetailInfoList = accessDetailService.getOrdersByPage(query);
        logger.debug("按条件需导出数据数量[{}]条", totalElement);
        return accessDetailService.exportInfo(accessDetailInfoList, response);
    }
}
