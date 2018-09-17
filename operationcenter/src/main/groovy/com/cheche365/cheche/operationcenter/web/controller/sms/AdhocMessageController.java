package com.cheche365.cheche.operationcenter.web.controller.sms;

import com.cheche365.cheche.core.model.AdhocMessage;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.manage.common.constants.AdhocMessageEnum;
import com.cheche365.cheche.manage.common.model.PublicQuery;
import com.cheche365.cheche.operationcenter.service.sms.AdhocMessageService;
import com.cheche365.cheche.manage.common.service.sms.SendMessageService;
import com.cheche365.cheche.operationcenter.web.model.DataTablesPageViewModel;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.manage.common.web.model.sms.AdhocMessageViewModel;
import org.apache.commons.lang3.StringUtils;
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
 * Created by lyh on 2015/10/8.
 */
@RestController
@RequestMapping("/operationcenter/sms/adhoc")
public class AdhocMessageController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AdhocMessageService adhocMessageService;

    @Autowired
    private SendMessageService sendMessageService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    /**
     * 新增主动短信
     *
     * @param model
     * @param result
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
//    @VisitorPermission("op030501")
    public ResultModel add(@Valid AdhocMessageViewModel model, BindingResult result) {
        if (result.hasErrors())
            return new ResultModel(false, "请将信息填写完整");
        adhocMessageService.addAdhocMessage(model);
        return new ResultModel();
    }

    /**
     * 修改主动短信
     *
     * @param adhocMessageId
     * @param model
     * @param result
     * @return
     */
    @RequestMapping(value = "/{adhocMessageId}", method = RequestMethod.PUT)
//    @VisitorPermission("op030504")
    public ResultModel update(@PathVariable Long adhocMessageId, @Valid AdhocMessageViewModel model, BindingResult result) {
        if (logger.isDebugEnabled()) {
            logger.debug("update adhoc message info by id -> {}", adhocMessageId);
        }
        if (result.hasErrors())
            return new ResultModel(false, "请将信息填写完整");
        adhocMessageService.updateAdhocMessage(model);
        return new ResultModel();
    }

    /**
     * 根据条件查询主动短信
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
//    @VisitorPermission("op0305")
    public DataTablesPageViewModel<AdhocMessageViewModel> search(PublicQuery query) {
        try {
            Page<AdhocMessage> page = adhocMessageService.getAdhocMessageByPage(query);
            List<AdhocMessageViewModel> modelList = new ArrayList<>();
            for (AdhocMessage scheduleMessageLog : page.getContent()) {
                modelList.add(adhocMessageService.createViewModel(scheduleMessageLog));
            }
            PageInfo pageInfo = adhocMessageService.createPageInfo(page);
            return new DataTablesPageViewModel<>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), query.getDraw(), modelList);
        } catch (Exception e) {
            logger.error("ScheduleMessageLogController search has error", e);
        }
        return null;
    }

    /**
     * 获取主动短信详情
     *
     * @param adhocMessageId
     * @return
     */
    @RequestMapping(value = "/{adhocMessageId}", method = RequestMethod.GET)
//    @VisitorPermission("op030504")
    public AdhocMessageViewModel findOne(@PathVariable Long adhocMessageId) {
        if (logger.isDebugEnabled()) {
            logger.debug("get adhoc message detail,id:{}", adhocMessageId);
        }
        if (adhocMessageId == null || adhocMessageId < 1) {
            throw new FieldValidtorException("find adhoc message detail, id can not be null or less than 1");
        }
        return adhocMessageService.findById(adhocMessageId);
    }

    /**
     * 审核通过
     *
     * @param adhocMessageId
     * @return
     */
    @RequestMapping(value = "/{adhocMessageId}/success", method = RequestMethod.PUT)
//    @VisitorPermission("op030502")
    public ResultModel reviewSuccess(@PathVariable Long adhocMessageId) {
        if (adhocMessageId == null || adhocMessageId < 1)
            throw new FieldValidtorException("review success for adhoc message, id can not be null or less than 1");

        adhocMessageService.review(adhocMessageId, AdhocMessageEnum.REVIEW_MESSAGE_SUCCESS.ordinal());
        return new ResultModel();
    }

    /**
     * 审核不通过
     *
     * @param adhocMessageId
     * @return
     */
    @RequestMapping(value = "/{adhocMessageId}/fail", method = RequestMethod.PUT)
//    @VisitorPermission("op030503")
    public ResultModel reviewFail(@PathVariable Long adhocMessageId) {
        if (adhocMessageId == null || adhocMessageId < 1)
            throw new FieldValidtorException("revew fail for adhoc message, id can not be null or less than 1");

        adhocMessageService.review(adhocMessageId, AdhocMessageEnum.REVIEW_MESSAGE_FAIL.ordinal());
        return new ResultModel();
    }

    /**
     * 拼接页面显示的短信内容
     *
     * @return
     */
    @RequestMapping(value = "/smsContentHtml", method = RequestMethod.GET)
    public AdhocMessageViewModel getSmsContentHtml(@RequestParam(value = "smsTemplateId", required = true) Long smsTemplateId) {
        return adhocMessageService.getSmsContentHtml(smsTemplateId);
    }

    /**
     * 报价主动发送短信
     *
     * @param quoteRecord
     * @return
     */
    @RequestMapping(value = "/manual/quote", method = RequestMethod.POST)
    public ResultModel sendQuoteMsg(@RequestBody @Valid QuoteRecord quoteRecord) {
        if (quoteRecord == null)
            return new ResultModel(false, "报价信息不能为空");
        if (quoteRecord.getInsuranceCompany() == null || StringUtils.isEmpty(quoteRecord.getInsuranceCompany().getName()))
            return new ResultModel(false, "保险公司名称不能为空");
        if (quoteRecord.getApplicant() == null || StringUtils.isEmpty(quoteRecord.getApplicant().getMobile()))
            return new ResultModel(false, "手机号不能为空");
        try {
            int result = adhocMessageService.sendQuoteAdhocMessage(quoteRecord);
            String msg = sendMessageService.getSmsResultDetail(result);
            return new ResultModel(result == 0, msg);
        } catch (Exception e) {
            logger.error("manual quote error", e);
            return new ResultModel(false, "人工报价异常");
        }

    }

    /**
     * 人工报价提交订单主动发送短信
     *
     * @param mobile
     * @return
     */
    @RequestMapping(value = "/manual/order", method = RequestMethod.POST)
    public ResultModel sendQuoteMsg(@RequestParam(value = "mobile", required = true) String mobile,
                                    @RequestParam(value = "orderNo", required = true) String orderNo) {
        if (StringUtils.isBlank(mobile)) {
            return new ResultModel(false, "手机号不能为空");
        }
        if (purchaseOrderService.countByOrderNo(orderNo) == 0) {
            return new ResultModel(false, "该订单号不存在");
        }
        try {
            int result = adhocMessageService.sendOrderAdhocMessage(mobile, orderNo);
            String msg = sendMessageService.getSmsResultDetail(result);
            return new ResultModel(result == 0, msg);
        } catch (Exception ex) {
            logger.error("send commit order message has error.", ex);
            return new ResultModel(false, "人工报价发送提交订单短信失败");
        }
    }
}
