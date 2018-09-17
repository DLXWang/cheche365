package com.cheche365.cheche.operationcenter.service.userManager

import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.model.agent.ChecheAgentInviteCode
import com.cheche365.cheche.core.model.agent.ProfessionApprove
import com.cheche365.cheche.core.repository.ChannelRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository
import com.cheche365.cheche.manage.common.service.BaseService
import com.cheche365.cheche.operationcenter.model.ChannelAgentQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

@Service
class UserManagerService extends BaseService<ChannelAgent, Object> {

    @Autowired
    private ChannelAgentRepository channelAgentRepository
    @Autowired
    ChannelRepository channelRepository

    /**
     * create pageable
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    static Pageable buildPageable(Integer currentPage, Integer pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id")
        return new PageRequest(currentPage - 1, pageSize, sort)
    }

    Page<ChannelAgent> findChannelAgentList(ChannelAgentQuery dataQuery, Pageable pageable) {

        channelAgentRepository.findAll(new Specification<ChannelAgent>() {
            @Override
            Predicate toPredicate(Root<ChannelAgent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                def predicate = cb.conjunction()
                def expressions = predicate.getExpressions()

                if (dataQuery.userName) {
                    expressions << cb.like(root.get("user").get("name"), dataQuery.userName + "%")
                }
                if (dataQuery.agentLevel != null) {
                    expressions << cb.equal(root.get("agentLevel").get('id'), dataQuery.agentLevel)
                }
                if (dataQuery.channel) {
                    expressions << cb.equal(root.get('shopType').get('id'), dataQuery.channel)
                }
                if (dataQuery.shop) {
                    expressions << cb.equal(root.get('shop'), dataQuery.shop)
                }
                if (dataQuery.identity) {
                    expressions << cb.like(root.get("user").get("identity"), dataQuery.identity + "%")
                }
                if (dataQuery.mobile) {
                    expressions << cb.like(root.get("user").get("mobile"), dataQuery.mobile + "%")
                }
                if (dataQuery.inviter) {
                    expressions << cb.like(root.get("parent").get("user").get("name"), dataQuery.inviter + "%")
                }
                if (dataQuery.topLevelInviter) {
                    Root<ChecheAgentInviteCode> inviteCodeRoot = query.from(ChecheAgentInviteCode.class)
                    expressions << cb.equal(inviteCodeRoot.get('channelAgent').get('id'), root.get('id'))
                    expressions << cb.like(inviteCodeRoot.get("applicantName"), dataQuery.topLevelInviter + "%")
                }
                if (dataQuery.inviteCode) {
                    expressions << cb.equal(root.get('inviteCode'), dataQuery.inviteCode)
                }
                if (dataQuery.registerTimeStart) {
                    expressions << cb.greaterThanOrEqualTo(root.get('createTime'), dataQuery.registerTimeStart)
                }
                if (dataQuery.registerTimeEnd) {
                    expressions << cb.lessThanOrEqualTo(root.get('createTime'), dataQuery.registerTimeEnd)
                }
                if (dataQuery.approveStatus) {
                    Root<ProfessionApprove> professionApproveRoot = query.from(ProfessionApprove.class)
                    expressions << cb.equal(professionApproveRoot.get('channelAgent').get('id'), root.get('id'))
                    expressions << cb.equal(professionApproveRoot.get("approveStatus"), dataQuery.approveStatus)
                }
                predicate
            }
        }, pageable)
    }

    def findChildren(Long parentId, List idList) {
        channelAgentRepository.findChildrenIdsByParentId(parentId).each {
            idList << it
            findChildren(it, idList)
        }
    }

    /**
     * 通过parent获取邀请人
     *
     * @param channelAgent
     * @return
     */
    ChannelAgent findOne(Long id) {
        channelAgentRepository.findOne(id)
    }

    List<Object[]> findOrderCountAndTotleAmount(Long channelAgentId) {
        List<Object[]> objectS = channelAgentRepository.findOrderCountAndTotleAmount(channelAgentId)
        return objectS
    }

    def getChannels() {
        channelAgentRepository.getChannels().with {
            if (it) {
                channelRepository.findByIds(it)
            } else {
                []
            }
        }
    }
}
