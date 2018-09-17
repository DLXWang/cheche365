package com.cheche365.cheche.ordercenter.service.telMarketingCenter;

import com.cheche365.cheche.common.math.NumberUtils;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.repository.AreaRepository;
import com.cheche365.cheche.core.repository.ChannelRepository;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterAssignBatch;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterAssignBatchData;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterAssignBatchDataRepository;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterAssignBatchRepository;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.ordercenter.constants.TelMarketingCenterType;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.TelMarketingCenterRequestParams;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luly on 2017/03/25.
 */
@Service
public class TelMarketingCenterAssignBatchService extends BaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TelMarketingCenterAssignBatchRepository telMarketingCenterAssignBatchRepository;
    @Autowired
    private BaseService baseService;
    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository;
    @Autowired
    private TelMarketingCenterAssignBatchDataRepository telMarketingCenterAssignBatchDataRepository;
    @Autowired
    private RedisTemplate redisTemplate;

    public Page<TelMarketingCenterAssignBatch> getTelMarketingCenterAssignBatchByPage(TelMarketingCenterRequestParams params) {
        return findDataBySpecAndPaginate(baseService.buildPageable(params.getCurrentPage(), params.getPageSize(),
            Sort.Direction.DESC, "id"), params);
    }

    public Page<TelMarketingCenterAssignBatch> findDataBySpecAndPaginate(Pageable pageable, TelMarketingCenterRequestParams params) {
        return telMarketingCenterAssignBatchRepository.findAll((root, query, cb) -> {
            CriteriaQuery<TelMarketingCenterAssignBatch> criteriaQuery = cb.createQuery(TelMarketingCenterAssignBatch.class);
            //条件构造
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isBlank(params.getStartTime()) || StringUtils.isBlank(params.getEndTime())) {
                logger.info("查询时间不能为空!");
                return null;
            }
            /*当前用户*/
            predicateList.add(cb.equal(root.get("operator").get("id"), orderCenterInternalUserManageService.getCurrentInternalUser().getId()));

            /*时间查询*/
            predicateList.add(cb.between(root.get("createTime"),
                cb.literal(DateUtils.getDayStartTime(DateUtils.getDate(params.getStartTime(), DateUtils.DATE_SHORTDATE_PATTERN))),
                cb.literal(DateUtils.getDayEndTime(DateUtils.getDate(params.getEndTime(), DateUtils.DATE_SHORTDATE_PATTERN)))));

            Predicate[] predicates = new Predicate[predicateList.size()];
            predicates = predicateList.toArray(predicates);
            return criteriaQuery.where(predicates).getRestriction();
        }, pageable);
    }

    public List<Map<String, String>> findDataByBatchId(Long batchId, int startIndex, int pageSize) {
        List<Object[]> detailList = telMarketingCenterAssignBatchRepository.findPageByBatchId(batchId, startIndex, pageSize);
        logger.info("获取数据分配历史记录详情{}条", detailList.size());
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        if (!CollectionUtils.isEmpty(detailList)) {
            for (Object[] obj : detailList) {
                Map<String, String> detailMap = new HashMap();
                detailMap.put("assignNum", obj[0].toString());
                detailMap.put("targetName", obj[1].toString());
                dataList.add(detailMap);
            }
        }
        return dataList;
    }

    public Long countByBatchId(Long batchId) {
        return telMarketingCenterAssignBatchRepository.countByBatchId(batchId);
    }

    public List<TelMarketingCenterAssignBatch> handleDataList(List<TelMarketingCenterAssignBatch> paramList) {
        logger.info("开始进行渠道、类型、地区转换，获取数据分配历史记录{}条", paramList.size());
        for (TelMarketingCenterAssignBatch tmcab : paramList) {
            if (!StringUtil.isNull(tmcab.getChannel())) {
                String channels = "";
                List<String> channelList = java.util.Arrays.asList(tmcab.getChannel().split(","));
                if (channelList.contains("0")) {
                    channels = ",站内";
                }
                for (Channel channel : channelRepository.findByIds(channelList)) {
                    channels += "," + channel.getDescription();
                }
                tmcab.setChannel(channels.substring(1));
            } else {
                tmcab.setChannel("");
            }

            if (!StringUtil.isNull(tmcab.getSourceType())) {
                String types = ",";
                List<String> typeList = java.util.Arrays.asList(tmcab.getSourceType().split(","));
                for (String sourceType : typeList) {
                    types += TelMarketingCenterType.Enum.All_TYPE_MAP.get(Integer.parseInt(sourceType)).getName() + ",";
                }
                tmcab.setSourceType(types.substring(1, types.length() - 1));
            } else {
                tmcab.setSourceType("");
            }

            if (!StringUtil.isNull(tmcab.getArea())) {
                String areas = ",";
                List<String> areaList = java.util.Arrays.asList(tmcab.getArea().split(","));
                for (Area area : areaRepository.findByIds(areaList)) {
                    areas += area.getName() + ",";
                }
                tmcab.setArea(areas.substring(1, areas.length() - 1));
            } else {
                tmcab.setArea("");
            }
        }
        return paramList;
    }

    /**
     * 统计数据分配信息
     */
/*

    public Map<String, Integer> findDataByTime(Date startTime, Date endTime) {

        Map<String, Integer> countMap = new HashMap();
        int inputNum = telMarketingCenterAssignBatchRepository.findInputNumByTime(startTime, endTime);
        Integer assignNum = telMarketingCenterAssignBatchRepository.findAssignNumByUserAndTime(internalUserManageService.getCurrentInternalUser().getId(),startTime, endTime);
        int newDataNum = telMarketingCenterAssignBatchRepository.findNewDataNumByUserAndTime(startTime, endTime);

        countMap.put("inputNum", inputNum);
        countMap.put("assignNum", assignNum != null ? assignNum : 0);
        countMap.put("newDataNum", newDataNum);
        countMap.put("oldDataNum", assignNum != null ? assignNum - newDataNum : 0);
        return countMap;
    }
*/

    /**
     * 将数据平均分配给参数中的值
     *
     * @param params
     * @param assignerIds
     */
    @Transactional
    public void averageAssign(TelMarketingCenterRequestParams params, String[] assignerIds) {
        InternalUser currentInternalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        List<String> idList = (List<String>) CacheUtil.getValueToObject(redisTemplate, currentInternalUser.getId() + "assign");
        List<TelMarketingCenter> dataList = telMarketingCenterRepository.findByIds(idList);
        logger.info("开始平均分配数据,被分配人id{},总共分配数量{}", Arrays.toString(assignerIds), dataList.size());
        int batchSize = dataList.size() / assignerIds.length;
        int userNum = assignerIds.length;
        for (int i = 0; i < userNum; i++) {
            String assignerId = assignerIds[i];
            List<TelMarketingCenter> dataListTemp;
            if (i == userNum - 1) {
                dataListTemp = dataList.subList(i * batchSize, dataList.size());
            } else {
                dataListTemp = dataList.subList(i * batchSize, (i + 1) * batchSize);
            }

            this.assignData(params, assignerId, dataListTemp, currentInternalUser);
        }
    }

    /**
     * 给制定用户分配制定数量的数据
     *
     * @param params
     */
    @Transactional
    public void customAssign(TelMarketingCenterRequestParams params) {
        String[] assignerIds = params.getOperatorIds();
        InternalUser currentInternalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        List<String> idList = (List<String>) CacheUtil.getValueToObject(redisTemplate, currentInternalUser.getId() + "assign");
        List<TelMarketingCenter> dataList = telMarketingCenterRepository.findByIds(idList);
        String[] operatorAssignNums = params.getOperatorAssignNums();
        logger.info("开始按指定人分配数据,被分配人id{},分别被分配的个数{},总共分配数量[{}]", Arrays.toString(assignerIds), Arrays.toString(operatorAssignNums), dataList.size());
        int userNum = assignerIds.length;
        int index = 0;
        for (int i = 0; i < userNum; i++) {
            String assignerId = assignerIds[i];
            List<TelMarketingCenter> dataListTemp;
            dataListTemp = dataList.subList(index, index += NumberUtils.toInt(operatorAssignNums[i]));

            this.assignData(params, assignerId, dataListTemp, currentInternalUser);
        }
    }

    public void assignData(TelMarketingCenterRequestParams params, String userId, List<TelMarketingCenter> dataList, InternalUser currentInternalUser) {
        InternalUser newOperator = new InternalUser();
        newOperator.setId(NumberUtils.toLong(userId));

        TelMarketingCenterAssignBatch assignBatch = new TelMarketingCenterAssignBatch();
        assignBatch.setOperator(currentInternalUser);

        if (ArrayUtils.isNotEmpty(params.getAreaId())) {
            StringBuffer areaIdsString = new StringBuffer();
            for (Long areaId : params.getAreaId()) {
                areaIdsString.append(areaId + ",");
            }
            assignBatch.setArea(areaIdsString.toString());
        }

        if (ArrayUtils.isNotEmpty(params.getChannelIds())) {
            StringBuffer channelIdsString = new StringBuffer();
            for (String channelId : params.getChannelIds()) {
                channelIdsString.append(channelId + ",");
            }
            assignBatch.setChannel(channelIdsString.toString());
        }

        if (ArrayUtils.isNotEmpty(params.getTelTypes())) {
            StringBuffer telTypeIdsString = new StringBuffer();
            for (String telType : params.getTelTypes()) {
                telTypeIdsString.append(telType + ",");
            }
            assignBatch.setSourceType(telTypeIdsString.toString());
        }

        if (StringUtils.isNotEmpty(params.getDataLevel())) {
            StringBuffer telTypeIdsString = new StringBuffer();
            List<Integer> telTypes = TelMarketingCenterType.Enum.DATA_TYPE_MAP.get(params.getDataLevel());
            for (Integer telType : telTypes) {
                telTypeIdsString.append(telType + ",");
            }
            assignBatch.setSourceType(telTypeIdsString.toString());
        }

        assignBatch.setAssignNum(Long.valueOf(dataList.size()));
        assignBatch.setCreateTime(new Date());
        assignBatch.setParent(null);
        InternalUser sourceAssigner = dataList.get(0).getOperator();
        assignBatch.setSourceAssigner(sourceAssigner);
        assignBatch.setTargetAssigner(newOperator);

        telMarketingCenterAssignBatchRepository.save(assignBatch);

        List<TelMarketingCenterAssignBatchData> batchDataList = new ArrayList<>(dataList.size());
        TelMarketingCenterAssignBatchData batchData;
        for (TelMarketingCenter telMarketingCenter : dataList) {
            if (telMarketingCenter.getOperator() == null) {
                logger.debug("无跟进人数据ID：{}被批次分配给跟进人{}", telMarketingCenter.getId(), newOperator.getId());
            }
            telMarketingCenter.setOperator(newOperator);
            telMarketingCenter.setDisplay(true);
            batchData = new TelMarketingCenterAssignBatchData();
            batchData.setBatch(assignBatch);
            batchData.setTelMarketingCenter(telMarketingCenter);
            batchDataList.add(batchData);
        }

        telMarketingCenterRepository.save(dataList);
        telMarketingCenterAssignBatchDataRepository.save(batchDataList);
        logger.info("本次批量分配数据成功,被分配人id[{}],分配数量[{}]", userId, dataList.size());
    }

}
