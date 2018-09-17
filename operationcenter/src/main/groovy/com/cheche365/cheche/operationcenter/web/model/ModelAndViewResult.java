package com.cheche365.cheche.operationcenter.web.model;

import com.cheche365.cheche.manage.common.web.model.PageInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 该对象为Web Model对象，有以下几点要求：
 * 不带JPA标签
 * 可能带JSON标签  jaxb
 * object对象可能是持久化对象也可能使Web Model对象，一般情况下都是Web Model对象
 * @author sunhuazhong
 *
 */
public class ModelAndViewResult {
	// 返回结果：success，fail
	private String result;
	// 返回结果编号：101-199成功；201-299失败
	private String code;
	// 返回结果说明
	private String message;
    // 分页对象
    private PageInfo pageInfo;
	// 返回结果中的对象
	private Map<String, Object> objectMap = new HashMap<String, Object>();

    public ModelAndViewResult() {}

    public ModelAndViewResult(String result, String message) {
        this.result = result;
        this.message = message;
    }

    public ModelAndViewResult(String result, String message, Map<String, Object> objectMap) {
        this.result = result;
        this.message = message;
        this.objectMap = objectMap;
    }

    public String getResult() {
		return result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

    public Map<String, Object> getObjectMap() {
        return objectMap;
    }

    public void setObjectMap(Map<String, Object> objectMap) {
        this.objectMap = objectMap;
    }

    public static final String RESULT_SUCCESS = "success";
    public static final String RESULT_SUCCESS_COMMON_CODE = "101";


    public static final String RESULT_FAIL = "fail";
    public static final String RESULT_FAIL_STATUS_TRANSITION_CODE = "201";
    public static final String RESULT_FAIL_STATUS_TRANSITION_MESSAGE = "订单状态变更失败。";

    public static final String RESULT_FAIL_INSURANCE_COMPANY_CODE = "301";
    public static final String RESULT_FAIL_INSURANCE_COMPANY_MESSAGE = "获取保险公司失败。";

    public static final String RESULT_FAIL_CUSTOMER_CODE = "302";
    public static final String RESULT_FAIL_CUSTOMER_MESSAGE = "获取客服人员失败。";

    public static final String RESULT_FAIL_ORDER_TRANSMISSION_STATUS_CODE = "303";
    public static final String RESULT_FAIL_ORDER_TRANSMISSION_STATUS_MESSAGE = "获取出单状态失败。";

    public static final String RESULT_FAIL_FILTER_ORDER_CODE = "304";
    public static final String RESULT_FAIL_FILTER_ORDER_MESSAGE = "根据条件过滤订单失败。";

    public static final String RESULT_FAIL_ORDER_INIT_INFO_CODE = "401";
    public static final String RESULT_FAIL_ORDER_INIT_INFO_MESSAGE = "获取订单详细信息失败。";

    public static final String RESULT_FAIL_CHECK_PREMIUM_CODE = "402";
    public static final String RESULT_FAIL_CHECK_PREMIUM_MESSAGE = "验证保费金额错误。";

    public static final String RESULT_FAIL_SAVE_INSURANCE_CODE = "403";
    public static final String RESULT_FAIL_SAVE_INSURANCE_MESSAGE = "保存保单失败。";

    public static final String RESULT_FAIL_NO_IDENTITY_CODE = "404";
    public static final String RESULT_FAIL_NO_IDENTITY_MESSAGE = "该身份证号不存在。";

    public static final String RESULT_FAIL_NO_LICENSE_NO_CODE = "405";
    public static final String RESULT_FAIL_NO_LICENSE_NO_MESSAGE = "该车牌号不存在。";

    public static final String RESULT_FAIL_NO_INSURANCE_COMPANY_CODE = "406";
    public static final String RESULT_FAIL_NO_INSURANCE_COMPANY_MESSAGE = "该保险公司暂不支持。";
	
    public static final String RESULT_FAIL_NO_PURCHASE_ORDER_CODE = "407";
    public static final String RESULT_FAIL_NO_PURCHASE_ORDER_MESSAGE = "该订单不存在。";
	
    public static final String RESULT_FAIL_SAVE_QUOTE_CODE = "408";
    public static final String RESULT_FAIL_SAVE_QUOTE_MESSAGE = "保存报价失败。";

    public static final String RESULT_FAIL_DIDI_SUPPLEMENT_INSURANCE_CODE = "409";
    public static final String RESULT_FAIL_DIDI_SUPPLEMENT_INSURANCE_MESSAGE = "滴滴专车增补保险信息不存在。";

    public static final String RESULT_FAIL_SEND_QUOTE_MESSAGE_CODE = "410";
    public static final String RESULT_FAIL_SEND_QUOTE_MESSAGE_MESSAGE = "发送短信失败。";

    public static final String RESULT_FAIL_ILLEGAL_REQUEST_MESSAGE = "非法请求";

    public static final String RESULT_FAIL_ONE_KEY_PROCESS_CODE = "501";
    public static final String RESULT_FAIL_ONE_KEY_PROCESS_MESSAGE = "一键处理失败";
}
