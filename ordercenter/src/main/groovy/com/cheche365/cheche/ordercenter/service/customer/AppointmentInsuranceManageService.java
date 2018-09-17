package com.cheche365.cheche.ordercenter.service.customer;

import com.cheche365.cheche.core.model.AppointmentInsurance;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.LogType;
import com.cheche365.cheche.core.model.MoApplicationLog;
import com.cheche365.cheche.core.repository.AppointmentInsuranceRepository;
import com.cheche365.cheche.core.repository.ChannelRepository;
import com.cheche365.cheche.core.service.AppointmentInsuranceService;
import com.cheche365.cheche.core.service.DoubleDBService;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/7/24.
 */
@Service
@Transactional
public class AppointmentInsuranceManageService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AppointmentInsuranceRepository appointmentInsuranceRepository;

    @Autowired
    private DoubleDBService doubleDBService;

    @Autowired
    private IInternalUserManageService internalUserManageService;

    @Autowired
    private AppointmentInsuranceService appointmentInsuranceService;


    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private BaseService baseService;

    public Page<AppointmentInsurance> getAppointmentInsuranceByPage(PublicQuery query) {
        try {
            return findBySpecAndPaginate(baseService.buildPageable(query.getCurrentPage(), query.getPageSize(),
                Sort.Direction.DESC, baseService.SORT_CREATE_TIME), query);
        } catch (Exception e) {
            logger.error("get appointment insurance by page has error", e);
            return null;
        }
    }

    public boolean changeStatus(Integer status, Long appointmentInsuranceId) {
        try {
            AppointmentInsurance appointmentInsurance = appointmentInsuranceService.getById(appointmentInsuranceId);
            if (!appointmentInsurance.getStatus().equals(status)) {
                appointmentInsurance.setStatus(status);
                appointmentInsurance.setUpdateTime(Calendar.getInstance().getTime());
                appointmentInsuranceRepository.save(appointmentInsurance);

                // 保存操作日志
                createStatusApplicationLog(appointmentInsurance);
            }
            return true;
        } catch (Exception e) {
            logger.error("change appointment insurance status error", e);
            return false;
        }
    }

    public boolean updateComment(String comment, Long appointmentInsuranceId) {
        try {
            AppointmentInsurance appointmentInsurance = appointmentInsuranceService.getById(appointmentInsuranceId);
            String newComment = comment.replace("\n", "\\r\\n");
            logger.debug("修改客户预约备注，客户预约ID:{}，原备注:{}，新备注:{}",
                appointmentInsurance.getId(), appointmentInsurance.getComment(), newComment);
            appointmentInsurance.setComment(newComment);
            appointmentInsurance.setUpdateTime(Calendar.getInstance().getTime());
            appointmentInsuranceRepository.save(appointmentInsurance);
            return true;
        } catch (Exception e) {
            logger.error("update appointment insurance comment error", e);
            return false;
        }
    }

    private Page<AppointmentInsurance> findBySpecAndPaginate(Pageable pageable, PublicQuery publicQuery) throws Exception {
        return appointmentInsuranceRepository.findAll((root, query, cb) -> {
            CriteriaQuery<AppointmentInsurance> criteriaQuery = cb.createQuery(AppointmentInsurance.class);

            //条件构造
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(publicQuery.getKeyword())) {
                //获取实体属性
                Path<String> mobilePath = root.get("user").get("mobile");
                Path<String> licensePlateNoPath = root.get("licensePlateNo");
                Path<String> contactPath = root.get("contact");
                predicateList.add(cb.or(
                    cb.like(mobilePath, publicQuery.getKeyword() + "%"),
                    cb.like(licensePlateNoPath, publicQuery.getKeyword() + "%"),
                    cb.like(contactPath, publicQuery.getKeyword() + "%")
                ));
            }
            if (publicQuery.getChannel() != null && publicQuery.getChannel() != 0) {
                Path<Long> channelPath = root.get("sourceChannel").get("id");
                predicateList.add(cb.equal(channelPath, publicQuery.getChannel()));
            }
            Predicate[] predicates = new Predicate[predicateList.size()];
            predicates = predicateList.toArray(predicates);
            return criteriaQuery.where(predicates).getRestriction();
        }, pageable);
    }

    //保存修改状态操作日志
    @Transactional
    private void createStatusApplicationLog(AppointmentInsurance appointmentInsurance) {
        MoApplicationLog applicationLog = new MoApplicationLog();
        applicationLog.setLogLevel(2);//日志的级别 1debug  2info 3warn 4error
        applicationLog.setLogMessage("修改处理状态");//日志信息
        applicationLog.setLogType(LogType.Enum.APPOINTMENT_STATUS_CHANGE_22);//客户预约处理状态变更
        applicationLog.setObjId(appointmentInsurance.getId() + "");//对象id
        applicationLog.setObjTable("appointment_insurance");//对象表名
        applicationLog.setOpeartor(internalUserManageService.getCurrentInternalUser().getId());//操作人
        applicationLog.setCreateTime(Calendar.getInstance().getTime());//创建时间
        doubleDBService.saveApplicationLog(applicationLog);
    }

    public List<Channel> getChannels() {
        Iterable<Channel> channels = this.channelRepository.findAll();
        Iterator<Channel> channelIterator = channels.iterator();
        List<Channel> channelList = new ArrayList<>();
        while (channelIterator.hasNext()) {
            channelList.add(channelIterator.next());
        }
        return channelList;
    }
}
