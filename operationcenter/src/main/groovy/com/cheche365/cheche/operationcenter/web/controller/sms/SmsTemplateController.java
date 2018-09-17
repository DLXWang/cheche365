package com.cheche365.cheche.operationcenter.web.controller.sms;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.SmsTemplate;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.manage.common.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.sms.SmsTemplateService;
import com.cheche365.cheche.operationcenter.web.model.DataTablesPageViewModel;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.manage.common.web.model.sms.SmsTemplateViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guoweifu on 2015/10/8.
 */

@RestController
@RequestMapping("/operationcenter/sms/template")
public class SmsTemplateController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SmsTemplateService smsTemplateService;

    /**
     * 新增短信模板
     * @param model
     * @param result
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.POST)
    @VisitorPermission("op030101")
    public ResultModel add(@Valid SmsTemplateViewModel model, BindingResult result) {
        logger.info("add new smsTemplate start...");
        if (result.hasErrors())
            return new ResultModel(false, "请将信息填写完整");

        return smsTemplateService.add(model);
    }

    /**
     * 修改短信模板备注
     * @param smsTemplateId
     * @param comment
     * @return
     */
    @RequestMapping(value = "/comment",method = RequestMethod.PUT)
    @VisitorPermission("op030103")
    public ResultModel updateComment(@RequestParam(value = "smsTemplateId",required = true) Long smsTemplateId, @RequestParam(value = "comment",required = false) String comment) {
        logger.info("update smsTemplate's info by id -> {}", smsTemplateId);
//        if (result.hasErrors())
//            return new ResultModel(false, "请将信息填写完整");

        smsTemplateService.updateComment(smsTemplateId,comment);
        return new ResultModel();
    }

    /**
     * 根据条件查询短信模板
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
    @VisitorPermission("op0301")
    public DataTablesPageViewModel<SmsTemplateViewModel> search(PublicQuery query) {
        try {
            Page<SmsTemplate> smsTemplatePage = smsTemplateService.getSmsTemplateByPage(query);
            List<SmsTemplateViewModel> modelList = new ArrayList<>();
            for(SmsTemplate smsTemplate:smsTemplatePage.getContent()){
                modelList.add(SmsTemplateViewModel.createViewData(smsTemplate));
            }
            PageInfo pageInfo = smsTemplateService.createPageInfo(smsTemplatePage);
            return new DataTablesPageViewModel<>(pageInfo.getTotalElements(),pageInfo.getTotalElements(),query.getDraw(),modelList);
        } catch (Exception e) {
            logger.error("find SmsTemplate info by page has error", e);
        }
        return null;
    }

    /**
     * 获取短信模板详情
     * @param smsTemplateId
     * @return
     */
    @RequestMapping(value = "/{smsTemplateId}",method = RequestMethod.GET)
    public SmsTemplateViewModel findOne(@PathVariable Long smsTemplateId) {
        if(logger.isDebugEnabled()) {
            logger.debug("get smsTemplate detail,id:" + smsTemplateId);
        }

        if(smsTemplateId == null || smsTemplateId < 1){
            throw new FieldValidtorException("find smsTemplate detail, id can not be null or less than 1");
        }

        return SmsTemplateViewModel.createViewData(smsTemplateService.findById(smsTemplateId));
    }


    /**
     * 启用或禁用短信模板
     * @param smsTemplateId
     * @param operationType，1-启用，0-禁用
     * @return
     */
    @RequestMapping(value = "/{smsTemplateId}/{operationType}",method = RequestMethod.PUT)
    @VisitorPermission("op030102")
    public ResultModel operation(@PathVariable Long smsTemplateId, @PathVariable Integer operationType) {
        logger.info("switch smsTemplate to enable or disable by id -> {}, operationType -> {}", smsTemplateId, operationType);
        if (smsTemplateId == null || smsTemplateId < 1)
            throw new FieldValidtorException("operation smsTemplate, id can not be null or less than 1");

        smsTemplateService.changeStatus(smsTemplateId, operationType);
        return new ResultModel();
    }
}
