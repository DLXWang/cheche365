package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.mongodb.repository.MoMarketingDetailRepository;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.scheduletask.model.AutoFinancingInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AutoFinancingMarketingService {
    Logger logger = LoggerFactory.getLogger(AutoFinancingMarketingService.class);

    @Autowired
    MarketingSuccessRepository marketingSuccessRepository;
    @Autowired
    AreaRepository areaRepository;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    AutoTypeRepository autoTypeRepository;
    @Autowired
    MarketingRepository marketingRepository;
    @Autowired
    MoMarketingDetailRepository moMarketingDetailRepository;
    private static final String EMAIL_KEY = "autoFinancingData";

    public Map<String, List<AutoFinancingInfo>> getContentParam(Date maxCreateTime) {
        List result = marketingSuccessRepository.findAutoFinancingMarketingList(maxCreateTime);
        if (result.size() != 0) {
            logger.debug("从{}开始，获取到汽车金融活动订单数量为{}", maxCreateTime, result.size());
            Map<String, List<AutoFinancingInfo>> map = getEmptyExcelMap();
            pushOfferDataToEmail(result, map);
            return map;
        } else {
            Map<String, List<AutoFinancingInfo>> nullMap = new HashMap<>();
            return nullMap;
        }
    }

    private Map<String, List<AutoFinancingInfo>> getEmptyExcelMap() {
        Map<String, List<AutoFinancingInfo>> autoFinancingListMap = new HashMap<>();
        List<AutoFinancingInfo> orderInfos = new ArrayList();
        autoFinancingListMap.put(EMAIL_KEY, orderInfos);
        return autoFinancingListMap;
    }

    private void pushOfferDataToEmail(List result, Map<String, List<AutoFinancingInfo>> autoFinancingListMap) {
        List<AutoFinancingInfo> autoFinancingInfos = autoFinancingListMap.get(EMAIL_KEY);
        List<AutoFinancingInfo> dataInfos = new ArrayList<AutoFinancingInfo>();
        for (int i = 0; i < result.size(); i++) {
            Object[] temp = (Object[]) result.get(i);
            AutoFinancingInfo info = new AutoFinancingInfo();
            Integer id = i + 1;
            info.setId(id.toString());
            info.setOwner(temp[10] != null ? temp[10].toString() : "");//用户姓名
            info.setMobile(temp[3] != null ? temp[3].toString() : "");//手机号
            info.setIdentity(temp[11] != null ? temp[11].toString() : "");
            ;//身份证号
//            if (null != temp[12]) {
//                AutoType autoType = autoTypeRepository.findByLicensePlateNo(temp[12].toString());
//                info.setBrand(autoType.getBrand());//车辆品牌
//                info.setModel(autoType.getModel());//车辆型号
//            } else {
//                info.setBrand("");//车辆品牌
//                info.setModel("");//车辆型号
//            }
            //指导价
            Marketing marketing = marketingRepository.findOne(Long.parseLong(temp[1].toString()));
            MoMarketingDetail moMarketingDetail = moMarketingDetailRepository.findByMarketingCode(marketing.getCode());
            Map<String, Map<String, Object>> message = (Map<String, Map<String, Object>>) moMarketingDetail.getMessage();

            if (null == message || null == temp[19]) {
                info.setPrice("");
                info.setModel("");
                info.setBrand("");
            } else {
                String brand = "";
                for (Map.Entry<String, Map<String, Object>> value : message.entrySet()) {
                    Map<String, Object> valueMapValue = value.getValue();
                    for (Map.Entry<String, Object> listEntry : valueMapValue.entrySet()) {
                        String listKey = listEntry.getKey();
                        if (StringUtils.equals(listKey, "title")) {
                            String getValue = listEntry.getValue().toString();
                            brand = getValue.replace("专区", "");//车辆品牌
                        } else if (StringUtils.equals(listKey, "list")) {
                            List listValue = (List) listEntry.getValue();
                            for (int j = 0; j < listValue.size(); j++) {
                                Map lastMap = (Map) listValue.get(j);
                                if (lastMap.get("id").toString().equals(temp[19].toString())) {
                                    info.setPrice(lastMap.get("price").toString());
                                    info.setModel(lastMap.get("model").toString());//车辆型号
                                    info.setBrand(brand);//车辆品牌
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (null != temp[13]) {
                Area area = areaRepository.findOne(Long.parseLong(temp[13].toString()));
                info.setArea(area.getName());//所在城市
            } else {
                info.setArea("");//所在城市
            }
            if (null != temp[16]) {
                Channel channel = channelRepository.findOne(Long.parseLong(temp[16].toString()));
                info.setChannel(channel.getName());//渠道
            } else {
                info.setChannel("");//渠道
            }
            info.setCreateTime(temp[20] != null ? temp[20].toString() : "");//提交时间
            dataInfos.add(info);
        }
        logger.debug("最终拼装到邮件中汽车金融活动报表的信息数据条目为{}", dataInfos.size());
        autoFinancingInfos.addAll(dataInfos);
        autoFinancingInfos.add(new AutoFinancingInfo());
        autoFinancingListMap.put(EMAIL_KEY, autoFinancingInfos);
    }

}
