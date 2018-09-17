package com.cheche365.cheche.operationcenter.service.partner

import com.alibaba.fastjson.JSONObject
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.Partner
import com.cheche365.cheche.core.repository.InternalUserRepository
import com.cheche365.cheche.core.repository.PartnerRepository
import com.cheche365.cheche.manage.common.model.PartnerActionLogHistory
import com.cheche365.cheche.manage.common.repository.PartnerActionLogRepository
import com.cheche365.cheche.manage.common.service.BaseService
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import groovy.util.logging.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

/**
 * Created by zhangpengcheng on 2018/4/14.
 */
@Service
@Slf4j
class PartnerActionLogService extends BaseService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private PartnerActionLogRepository partnerActionLogRepository;

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private InternalUserManageService internalUserManageService;
    //储存操作
    public saveChangeInActionLog(Partner partner, Map params, Channel channel){
        PartnerActionLogHistory actionlog =new PartnerActionLogHistory();
        try {
        if(partner!=null){
            actionlog.setPartner(partner)
        }
        if(channel == null){
            actionlog.setOperationContent("新增合作商")
            if(params.get("comment") != null){
                actionlog.setOperationContent("修改注释")
            }
        }else{
            actionlog.setChannel(channel)
            if(params.get("disabledChannel") == false){
                actionlog.setOperationContent("上线渠道")
            }else if(params.get("disabledChannel") == true){
                actionlog.setOperationContent("下线渠道")
            }else{
                actionlog.setOperationContent("test")
            }
        }
        actionlog.setOperator(internalUserManageService.getCurrentInternalUser())
        actionlog.setStatus(1);
        }
        catch (Exception e){
            logger.error("update partner comment has error", e);
        }
        partnerActionLogRepository.save(actionlog)
    }
    //获得备注
    public JSONObject getCommentInPartner(long partner){
        String comment;
        String partnerName;
        try {
            Partner partnerEntity = partnerRepository.findOne(partner);
            comment = partnerEntity.getComment();
            partnerName = partnerEntity.getName();
        }
        catch (Exception e){
            logger.error("update partner comment has error", e);
        }
        JSONObject json = new JSONObject()
        json.put("comment", comment)
        json.put("partnerName",partnerName)
        return json;
    }
    //修改备注
    public modifyCommentInPartner(String comment,long partner){
        try { Partner partnerEntity = partnerRepository.findOne(partner);
            partnerEntity.setComment(comment);
            partnerRepository.save(partnerEntity);
            Map params = new HashMap();
            params.put("comment",comment);
            saveChangeInActionLog(partnerEntity,params,null)
        }
        catch (Exception e){
            logger.error("update partner comment has error", e);
        }
    }

    Page<PartnerActionLogHistory> getActionLogHistory(Channel channel, Partner partner, String operator, Pageable pageable) {
        if(channel != null && operator != null){
            return partnerActionLogRepository.findByPartnerChannelOperator(partner,channel,operator,pageable)
        }else if(channel != null){
            return partnerActionLogRepository.findByPartnerAndChannel(partner,channel,pageable)
        }else if(operator != null){
            return partnerActionLogRepository.findByPartnerOperator(partner,operator,pageable)
        }else
            return partnerActionLogRepository.findByPartner(partner,pageable)

    }




}
