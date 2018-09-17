package com.cheche365.cheche.operationcenter.web.controller.sms;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.FilterUser;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.manage.common.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.sms.FilterUserService;
import com.cheche365.cheche.operationcenter.web.model.DataTablesPageViewModel;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.manage.common.web.model.sms.FilterUserViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guoweifu on 2015/10/8.
 */

@RestController
@RequestMapping("/operationcenter/sms/filterUser")
public class FilterUserController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FilterUserService filterUserService;

    /**
     * 新增筛选用户功能
     * @param model
     * @param result
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.POST)
    @VisitorPermission("op030201")
    public ResultModel add(@Valid FilterUserViewModel model, BindingResult result) {
        logger.info("add new filterUser start...");
        if (result.hasErrors())
            return new ResultModel(false, "请将信息填写完整");

        return filterUserService.add(model);
    }



    /**
     * 修改筛选用户功能
     * @param filterUserId
     * @param model
     * @param result
     * @return
     */
    @RequestMapping(value = "/{filterUserId}",method = RequestMethod.PUT)
    @VisitorPermission("op030203")
    public ResultModel update(@PathVariable Long filterUserId, @Valid FilterUserViewModel model, BindingResult result) {
        logger.info("update filterUser's info by id -> {}", filterUserId);
        if (result.hasErrors())
            return new ResultModel(false, "请将信息填写完整");

        return filterUserService.update(filterUserId, model);
    }

    /**
     * 根据条件查询筛选用户功能
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
    @VisitorPermission("op0302")
    public DataTablesPageViewModel<FilterUserViewModel> search(PublicQuery query) {
        try {
            Page<FilterUser> smsTemplatePage = filterUserService.getFilterUserByPage(query);
            List<FilterUserViewModel> modelList = new ArrayList<>();
            for(FilterUser smsTemplate:smsTemplatePage.getContent()){
                modelList.add(filterUserService.createViewData(smsTemplate));
            }
            PageInfo pageInfo = filterUserService.createPageInfo(smsTemplatePage);
            return new DataTablesPageViewModel<>(pageInfo.getTotalElements(),pageInfo.getTotalElements(),query.getDraw(),modelList);
        } catch (Exception e) {
            logger.error("return DataTablesPageViewModel has error", e);
        }
        return null;
    }

    /**
     * 获取筛选用户功能详情
     * @param filterUserId
     * @return
     */
    @RequestMapping(value = "/{filterUserId}",method = RequestMethod.GET)
    @VisitorPermission("op030203")
    public FilterUserViewModel findOne(@PathVariable Long filterUserId) {
        if(logger.isDebugEnabled()) {
            logger.debug("get filterUser detail,id:" + filterUserId);
        }

        if(filterUserId == null || filterUserId < 1){
            throw new FieldValidtorException("find filterUser detail, id can not be null or less than 1");
        }

        return filterUserService.findById(filterUserId);
    }

    /**
     * 启用或禁用筛选用户功能
     * @param filterUserId
     * @param operationType，1-启用，0-禁用
     * @return
     */
    @RequestMapping(value = "/{filterUserId}/{operationType}",method = RequestMethod.PUT)
    @VisitorPermission("op030202")
    public ResultModel operation(@PathVariable Long filterUserId, @PathVariable Integer operationType) {
        logger.info("switch filterUser to enable or disable by id -> {}, operationType -> {}", filterUserId, operationType);
        if (filterUserId == null || filterUserId < 1)
            throw new FieldValidtorException("operation filterUser, id can not be null or less than 1");

        filterUserService.changeStatus(filterUserId, operationType);
        return new ResultModel();
    }
}
