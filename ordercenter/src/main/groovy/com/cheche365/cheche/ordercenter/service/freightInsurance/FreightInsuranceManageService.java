package com.cheche365.cheche.ordercenter.service.freightInsurance;

import com.cheche365.cheche.common.math.NumberUtils;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.ordercenter.exception.OrderCenterException;
import com.cheche365.cheche.ordercenter.web.model.freightInsurance.FreightInsuranceOrderRequestModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yinJianBin on 2017/8/22.
 */
@Service
public class FreightInsuranceManageService {
    private Logger logger = LoggerFactory.getLogger(FreightInsuranceManageService.class);


    /**
     * 运费险订单列表
     *
     * @param requestModel
     * @return
     */
    public DataTablePageViewModel<Map<String, Object>> listOrder(FreightInsuranceOrderRequestModel requestModel) throws JsonProcessingException {
        String urlPath = "/bfidata/orders";
        requestModel.setPageNumber(requestModel.getCurrentPage());
        Map paramMap = BeanUtil.beanToMap(requestModel);
        String response = FreightRequestHandler.doGetRequest(urlPath, paramMap);
        Map resultMap = CacheUtil.doJacksonDeserialize(response, Map.class);
        String code = resultMap.get("code").toString();
        if (!"200".equals(code)) {
            logger.info("获取运费险订单列表失败,返回结果-->({})", resultMap.toString());
            throw new OrderCenterException(code, resultMap.toString());
        }
        Map<String, Object> data = (Map<String, Object>) resultMap.get("data");
        List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");

        DataTablePageViewModel<Map<String, Object>> viewModel = new DataTablePageViewModel<>();
        viewModel.setAaData(content);
        Long totalElements = NumberUtils.toLong(data.get("totalOrderCount") + "");
        viewModel.setiTotalRecords(totalElements);
        viewModel.setiTotalDisplayRecords(totalElements);
        viewModel.setDraw(requestModel.getDraw());

        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put("totalPremium", data.get("totalPremium"));
        extraMap.put("totalOrderCount", data.get("totalOrderCount"));
        extraMap.put("compensationRate", data.get("compensationRate"));
        viewModel.setData(extraMap);

        return viewModel;
    }

    /**
     * 运费险理赔列表
     *
     * @param requestModel
     * @return
     */
    public DataTablePageViewModel<Map<String, Object>> listClaim(FreightInsuranceOrderRequestModel requestModel) throws JsonProcessingException {
        String urlPath = "/bfidata/claims";
        requestModel.setPageNumber(requestModel.getCurrentPage());
        Map paramMap = BeanUtil.beanToMap(requestModel);
        String response = FreightRequestHandler.doGetRequest(urlPath, paramMap);
        Map resultMap = CacheUtil.doJacksonDeserialize(response, Map.class);
        String code = resultMap.get("code").toString();
        if (!"200".equals(code)) {
            logger.info("获取运费险理赔列表失败,返回结果-->({})", resultMap.toString());
            throw new OrderCenterException(code, resultMap.toString());
        }
        Map<String, Object> data = (Map<String, Object>) resultMap.get("data");
        List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");

        DataTablePageViewModel<Map<String, Object>> viewModel = new DataTablePageViewModel<>();
        viewModel.setAaData(content);
        Long totalElements = NumberUtils.toLong(data.get("totalClaimCount") + "");
        viewModel.setiTotalRecords(totalElements);
        viewModel.setiTotalDisplayRecords(totalElements);
        viewModel.setDraw(requestModel.getDraw());

        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put("totalClaimCount", data.get("totalClaimCount"));
        extraMap.put("totalCompensation", data.get("totalCompensation"));
        viewModel.setData(extraMap);

        return viewModel;
    }


    /**
     * 运费险承保详细接口
     *
     * @param orderId
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public Map<String, String> getOrderDetail(Long orderId) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String urlPath = "/bfidata/orderDetail/" + orderId;
        String response = FreightRequestHandler.doGetRequest(urlPath);
        Map resultMap = CacheUtil.doJacksonDeserialize(response, Map.class);
        return resultMap;
    }

    /**
     * 渠道列表
     *
     * @return
     */
    public List<Map> getChannels() {
        String urlPath = "/dict/channel";
        String response = FreightRequestHandler.doGetRequest(urlPath);
        Map map = CacheUtil.doJacksonDeserialize(response, Map.class);
        List<Map> channels = (List<Map>) map.get("data");
        return channels;
    }

    /**
     * 根据channel获取商品类型
     *
     * @param channel
     * @return
     */
    public List<Map<String, String>> getCategorysByChannel(Long channel) {
        String urlPath = "/dict/category/" + channel;
        String response = FreightRequestHandler.doGetRequest(urlPath);
        Map map = CacheUtil.doJacksonDeserialize(response, Map.class);
        List<Map<String, String>> categoryMapList = (List<Map<String, String>>) map.get("data");
        return categoryMapList;
    }

    /**
     * 获取订单预理赔记录
     *
     * @param orderId
     * @return
     */
    public List<Map<String, String>> getPreClaimList(Long orderId) {
        String urlPath = "/bfidata/preClaim/" + orderId;
        String response = FreightRequestHandler.doGetRequest(urlPath);
        Map map = CacheUtil.doJacksonDeserialize(response, Map.class);
        List<Map<String, String>> categoryMapList = (List<Map<String, String>>) map.get("data");
        return categoryMapList;
    }
}
